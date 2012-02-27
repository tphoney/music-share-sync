package com.zook.tree.extended;

/**
 * This exception is thrown when the extended tree does not contain node requested.
 * 
 */
public class NodeNotInTreeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NodeNotInTreeException(final String id) {
        super("The extended tree does not contain the node specified: " + id);
    }

}
