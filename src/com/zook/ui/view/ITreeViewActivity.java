package com.zook.ui.view;

import java.io.Serializable;

/**
 * Interface for all tree view type activities
 * Defines an enum used to clarify which adapter has been injected into the activity's display.
 * 
 * @author declan.wilson
 *
 */
public interface ITreeViewActivity {

	public enum TreeType implements Serializable {
        SIMPLE,
        FANCY
    }
	
	public void setTreeAdapter(final TreeType newTreeType);
	public void setCollapsible(final boolean newCollapsible);
}
