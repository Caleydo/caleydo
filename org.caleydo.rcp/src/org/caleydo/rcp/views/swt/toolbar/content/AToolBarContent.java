package org.caleydo.rcp.views.swt.toolbar.content;

import java.util.List;

/**
 * Abstract super class for toolbar content classes to provide lists of toolbar-actions.
 * @author Werner Puff
 *
 */
public abstract class AToolBarContent {

	/** number of toolbar-icons per row */
	public final static int TOOLBAR_WRAP_COUNT = 4;

	/** specifies that the toolbar should contain actions for standard rendering of the related view */
	public static final int STANDARD_CONTENT = 1;

	/** specifies that the toolbar should contain actions for remote rendering of the related view */
	public static final int REMOTE_RENDERED_CONTENT = 2;

	/** FIXME view-id of the target view for the actions contained within this toolbar content */
	protected int targetViewID = -1;
	
	/** specifies the type of content to render. sub classes may define their own content types */ 
	protected int contentType = STANDARD_CONTENT;
	
	/**
	 * Returns the related view type for this toolbar content
	 * @return class object of the view related to this toolbar content
	 */
	public abstract Class<?> getViewClass();

	/**
	 * Implementing classes should return a list of toolbar-actions that are added to a toolbar.
	 * Usually this method is used to show view specific toolbars.
	 * @return list of actions for a toolbar
	 */
	public abstract List<ToolBarContainer> getDefaultToolBar();
	
	/**
	 * Sets the id of the target view for the actions in this toolbar content.
	 * The id must be set before retrieving any toolbar content.
	 * @param viewID as used by IViewManager of the target view  
	 */
	public void setTargetViewID(int viewID) {
		targetViewID = viewID;
	}

	/**
	 * returns the target view id for actions provided by this tool bar content
	 * @return the target view-id as used by IViewManager
	 */
	public int getTargetViewID() {
		return targetViewID;
	}
	
	public int getContentType() {
		return contentType;
	}

	public void setContentType(int contentType) {
		this.contentType = contentType;
	}
}
