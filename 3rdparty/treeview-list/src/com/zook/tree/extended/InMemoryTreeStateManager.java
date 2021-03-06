package com.zook.tree.extended;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.database.DataSetObserver;
import android.util.Log;

/**
 * In-memory manager of tree state.
 * 
 * @param <T>
 *            type of identifier
 *        <S>
 *            type of params for extension of tree node
 */
public class InMemoryTreeStateManager<T, S> implements TreeStateManager<T, S> {
    private static final String TAG = InMemoryTreeStateManager.class
            .getSimpleName();
    private static final long serialVersionUID = 1L;
    private final Map<T, InMemoryTreeNode<T, S>> allNodes = new HashMap<T, InMemoryTreeNode<T, S>>();
    private final InMemoryTreeNode<T, S> topSentinel = new InMemoryTreeNode<T, S>(null, null, -1, true, null);
    private transient List<T> visibleListCache = null; // lasy initialised
    private transient List<T> unmodifiableVisibleList = null;
    private boolean visibleByDefault = true;
    private final transient Set<DataSetObserver> observers = new HashSet<DataSetObserver>();

    private synchronized void internalDataSetChanged() {
        visibleListCache = null;
        unmodifiableVisibleList = null;
        for (final DataSetObserver observer : observers) {
            observer.onChanged();
        }
    }

    /**
     * If true new nodes are visible by default.
     * 
     * @param visibleByDefault
     *            if true, then newly added nodes are expanded by default
     */
    public void setVisibleByDefault(final boolean visibleByDefault) {
        this.visibleByDefault = visibleByDefault;
    }

    private InMemoryTreeNode<T, S> getNodeFromTreeOrThrow(final T id) {
        if (id == null) {
            throw new NodeNotInTreeException("(null)");
        }
        final InMemoryTreeNode<T, S> node = allNodes.get(id);
        if (node == null) {
            throw new NodeNotInTreeException(id.toString());
        }
        return node;
    }

    private InMemoryTreeNode<T, S> getNodeFromTreeOrThrowAllowRoot(final T id) {
        if (id == null) {
            return topSentinel;
        }
        return getNodeFromTreeOrThrow(id);
    }

    private void expectNodeNotInTreeYet(final T id) {
        final InMemoryTreeNode<T, S> node = allNodes.get(id);
        if (node != null) {
            throw new NodeAlreadyInTreeException(id.toString(), node.toString());
        }
    }

    
    public synchronized TreeNodeInfo<T,S> getNodeInfo(final T id) {
        final InMemoryTreeNode<T,S> node = getNodeFromTreeOrThrow(id);
        final List<InMemoryTreeNode<T, S>> children = node.getChildren();
        boolean expanded = false;
        if (!children.isEmpty() && children.get(0).isVisible()) {
            expanded = true;
        }
        return new TreeNodeInfo<T,S>(id, node.getLevel(), !children.isEmpty(),
                node.isVisible(), expanded, node.getParams());
    }

    
    public synchronized List<T> getChildren(final T id) {
        final InMemoryTreeNode<T,S> node = getNodeFromTreeOrThrowAllowRoot(id);
        return node.getChildIdList();
    }

    
    public synchronized T getParent(final T id) {
        final InMemoryTreeNode<T,S> node = getNodeFromTreeOrThrowAllowRoot(id);
        return node.getParent();
    }

    private boolean getChildrenVisibility(final InMemoryTreeNode<T,S> node) {
        boolean visibility;
        final List<InMemoryTreeNode<T,S>> children = node.getChildren();
        if (children.isEmpty()) {
            visibility = visibleByDefault;
        } else {
            visibility = children.get(0).isVisible();
        }
        return visibility;
    }

    
    public synchronized void addBeforeChild(final T parent, final T newChild, final T beforeChild, S params) {
        expectNodeNotInTreeYet(newChild);
        final InMemoryTreeNode<T,S> node = getNodeFromTreeOrThrowAllowRoot(parent);
        final boolean visibility = getChildrenVisibility(node);
        // top nodes are always expanded.
        if (beforeChild == null) {
            final InMemoryTreeNode<T,S> added = node.add(0, newChild, visibility, params);
            allNodes.put(newChild, added);
        } else {
            final int index = node.indexOf(beforeChild);
            final InMemoryTreeNode<T,S> added = node.add(index == -1 ? 0 : index,
                    newChild, visibility, params);
            allNodes.put(newChild, added);
        }
        if (visibility) {
            internalDataSetChanged();
        }
    }

    
    public synchronized void addAfterChild(final T parent, final T newChild,
            final T afterChild, final S params) {
        expectNodeNotInTreeYet(newChild);
        final InMemoryTreeNode<T, S> node = getNodeFromTreeOrThrowAllowRoot(parent);
        final boolean visibility = getChildrenVisibility(node);
        if (afterChild == null) {
            final InMemoryTreeNode<T, S> added = node.add(
                    node.getChildrenListSize(), newChild, visibility, params);
            allNodes.put(newChild, added);
        } else {
            final int index = node.indexOf(afterChild);
            final InMemoryTreeNode<T, S> added = node.add(
                    index == -1 ? node.getChildrenListSize() : index + 1, newChild,
                    visibility, params);
            allNodes.put(newChild, added);
        }
        if (visibility) {
            internalDataSetChanged();
        }
    }

    
    public synchronized void removeNodeRecursively(final T id) {
        final InMemoryTreeNode<T, S> node = getNodeFromTreeOrThrowAllowRoot(id);
        final boolean visibleNodeChanged = removeNodeRecursively(node);
        final T parent = node.getParent();
        final InMemoryTreeNode<T, S> parentNode = getNodeFromTreeOrThrowAllowRoot(parent);
        parentNode.removeChild(id);
        if (visibleNodeChanged) {
            internalDataSetChanged();
        }
    }

    private boolean removeNodeRecursively(final InMemoryTreeNode<T, S> node) {
        boolean visibleNodeChanged = false;
        for (final InMemoryTreeNode<T, S> child : node.getChildren()) {
            if (removeNodeRecursively(child)) {
                visibleNodeChanged = true;
            }
        }
        node.clearChildren();
        if (node.getId() != null) {
            allNodes.remove(node.getId());
            if (node.isVisible()) {
                visibleNodeChanged = true;
            }
        }
        return visibleNodeChanged;
    }

    private void setChildrenVisibility(final InMemoryTreeNode<T, S> node,
            final boolean visible, final boolean recursive) {
        for (final InMemoryTreeNode<T, S> child : node.getChildren()) {
            child.setVisible(visible);
            if (recursive) {
                setChildrenVisibility(child, visible, true);
            }
        }
    }

    
    public synchronized void expandDirectChildren(final T id) {
        Log.d(TAG, "Expanding direct children of " + id);
        final InMemoryTreeNode<T, S> node = getNodeFromTreeOrThrowAllowRoot(id);
        setChildrenVisibility(node, true, false);
        internalDataSetChanged();
    }

    
    public synchronized void expandEverythingBelow(final T id) {
        Log.d(TAG, "Expanding all children below " + id);
        final InMemoryTreeNode<T, S> node = getNodeFromTreeOrThrowAllowRoot(id);
        setChildrenVisibility(node, true, true);
        internalDataSetChanged();
    }

    
    public synchronized void collapseChildren(final T id) {
        final InMemoryTreeNode<T, S> node = getNodeFromTreeOrThrowAllowRoot(id);
        if (node == topSentinel) {
            for (final InMemoryTreeNode<T, S> n : topSentinel.getChildren()) {
                setChildrenVisibility(n, false, true);
            }
        } else {
            setChildrenVisibility(node, false, true);
        }
        internalDataSetChanged();
    }

    
    public synchronized T getNextSibling(final T id) {
        final T parent = getParent(id);
        final InMemoryTreeNode<T, S> parentNode = getNodeFromTreeOrThrowAllowRoot(parent);
        boolean returnNext = false;
        for (final InMemoryTreeNode<T, S> child : parentNode.getChildren()) {
            if (returnNext) {
                return child.getId();
            }
            if (child.getId().equals(id)) {
                returnNext = true;
            }
        }
        return null;
    }

    
    public synchronized T getPreviousSibling(final T id) {
        final T parent = getParent(id);
        final InMemoryTreeNode<T, S> parentNode = getNodeFromTreeOrThrowAllowRoot(parent);
        final T previousSibling = null;
        for (final InMemoryTreeNode<T, S> child : parentNode.getChildren()) {
            if (child.getId().equals(id)) {
                return previousSibling;
            }
        }
        return null;
    }

    
    public synchronized boolean isInTree(final T id) {
        return allNodes.containsKey(id);
    }

    
    public synchronized int getVisibleCount() {
        return getVisibleList().size();
    }

    
    public synchronized List<T> getVisibleList() {
        T currentId = null;
        if (visibleListCache == null) {
            visibleListCache = new ArrayList<T>(allNodes.size());
            do {
                currentId = getNextVisible(currentId);
                if (currentId == null) {
                    break;
                } else {
                    visibleListCache.add(currentId);
                }
            } while (true);
        }
        if (unmodifiableVisibleList == null) {
            unmodifiableVisibleList = Collections
                    .unmodifiableList(visibleListCache);
        }
        return unmodifiableVisibleList;
    }

    public synchronized T getNextVisible(final T id) {
        final InMemoryTreeNode<T, S> node = getNodeFromTreeOrThrowAllowRoot(id);
        if (!node.isVisible()) {
            return null;
        }
        final List<InMemoryTreeNode<T, S>> children = node.getChildren();
        if (!children.isEmpty()) {
            final InMemoryTreeNode<T, S> firstChild = children.get(0);
            if (firstChild.isVisible()) {
                return firstChild.getId();
            }
        }
        final T sibl = getNextSibling(id);
        if (sibl != null) {
            return sibl;
        }
        T parent = node.getParent();
        do {
            if (parent == null) {
                return null;
            }
            final T parentSibling = getNextSibling(parent);
            if (parentSibling != null) {
                return parentSibling;
            }
            parent = getNodeFromTreeOrThrow(parent).getParent();
        } while (true);
    }

    
    public synchronized void registerDataSetObserver(
            final DataSetObserver observer) {
        observers.add(observer);
    }

    
    public synchronized void unregisterDataSetObserver(
            final DataSetObserver observer) {
        observers.remove(observer);
    }

    
    public int getLevel(final T id) {
        return getNodeFromTreeOrThrow(id).getLevel();
    }

    
    public Integer[] getHierarchyDescription(final T id) {
        final int level = getLevel(id);
        final Integer[] hierarchy = new Integer[level + 1];
        int currentLevel = level;
        T currentId = id;
        T parent = getParent(currentId);
        while (currentLevel >= 0) {
            hierarchy[currentLevel--] = getChildren(parent).indexOf(currentId);
            currentId = parent;
            parent = getParent(parent);
        }
        return hierarchy;
    }

    private void appendToSb(final StringBuilder sb, final T id) {
        if (id != null) {
            final TreeNodeInfo<T,S> node = getNodeInfo(id);
            final int indent = node.getLevel() * 4;
            final char[] indentString = new char[indent];
            Arrays.fill(indentString, ' ');
            sb.append(indentString);
            sb.append(node.toString());
            sb.append(Arrays.asList(getHierarchyDescription(id)).toString());
            sb.append("\n");
        }
        final List<T> children = getChildren(id);
        for (final T child : children) {
            appendToSb(sb, child);
        }
    }

    @Override
    public synchronized String toString() {
        final StringBuilder sb = new StringBuilder();
        appendToSb(sb, null);
        return sb.toString();
    }

    
    public synchronized void clear() {
        allNodes.clear();
        topSentinel.clearChildren();
        internalDataSetChanged();
    }

    
    public void refresh() {
        internalDataSetChanged();
    }

}
