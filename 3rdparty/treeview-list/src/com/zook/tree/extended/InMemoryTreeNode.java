package com.zook.tree.extended;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Node. It is package protected so that it cannot be used outside.
 * 
 * @param <T>
 *            type of the identifier used by the tree
 *        <S>
 *        	  type if extended params used by individual tree nodes
 */
class InMemoryTreeNode<T, S> implements Serializable {
    private static final long serialVersionUID = 1L;
    private final T id;
    private final T parent;
    private final int level;
    private boolean visible = true;
    private final List<InMemoryTreeNode<T, S>> children = new LinkedList<InMemoryTreeNode<T, S>>();
    private List<T> childIdListCache = null;
    private S params;

    public InMemoryTreeNode(final T id, final T parent, final int level, final boolean visible, S params) {
        super();
        this.id = id;
        this.parent = parent;
        this.level = level;
        this.visible = visible;
        this.params = params;
    }

    public int indexOf(final T id) {
        return getChildIdList().indexOf(id);
    }

    /**
     * Cache is built lasily only if needed. The cache is cleaned on any
     * structure change for that node!).
     * 
     * @return list of ids of children
     */
    public synchronized List<T> getChildIdList() {
        if (childIdListCache == null) {
            childIdListCache = new LinkedList<T>();
            for (final InMemoryTreeNode<T,S> n : children) {
                childIdListCache.add(n.getId());
            }
        }
        return childIdListCache;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(final boolean visible) {
        this.visible = visible;
    }

    public int getChildrenListSize() {
        return children.size();
    }

    public synchronized InMemoryTreeNode<T, S> add(final int index, final T child,
            final boolean visible, S params) {
        childIdListCache = null;
        // Note! top levell children are always visible (!)
        final InMemoryTreeNode<T, S> newNode = new InMemoryTreeNode<T, S>(child,
                getId(), getLevel() + 1, getId() == null ? true : visible, params);
        children.add(index, newNode);
        return newNode;
    }

    /**
     * Note. This method should technically return unmodifiable collection, but
     * for performance reason on small devices we do not do it.
     * 
     * @return children list
     */
    public List<InMemoryTreeNode<T, S>> getChildren() {
        return children;
    }

    public synchronized void clearChildren() {
        children.clear();
        childIdListCache = null;
    }

    public synchronized void removeChild(final T child) {
        final int childIndex = indexOf(child);
        if (childIndex != -1) {
            children.remove(childIndex);
            childIdListCache = null;
        }
    }

    @Override
    public String toString() {
        return "InMemoryTreeNode [id=" + getId() + ", parent=" + getParent()
                + ", level=" + getLevel() + ", visible=" + visible
                + ", children=" + children + ", childIdListCache="
                + childIdListCache + "]";
    }

    T getId() {
        return id;
    }

    T getParent() {
        return parent;
    }

    int getLevel() {
        return level;
    }

	public S getParams() {
		return params;
	}

	public void setParams(S params) {
		this.params = params;
	}

    
    
    

}