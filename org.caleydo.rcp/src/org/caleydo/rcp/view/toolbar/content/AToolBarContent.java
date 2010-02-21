package org.caleydo.rcp.view.toolbar.content;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.rcp.view.toolbar.ToolBarContainer;

/**
 * Abstract super class for toolbar content classes to provide lists of toolbar-actions.
 * 
 * @author Werner Puff
 */
public abstract class AToolBarContent {

	/** number of toolbar-icons per row */
	public final static int TOOLBAR_WRAP_COUNT = 4;

	/**
	 * specifies that the toolbar should contain actions for standard rendering of the related view
	 */
	public static final int STANDARD_RENDERING = 1;

	/**
	 * specifies that the toolbar should contain actions for rendering contextual information only
	 */
	public static final int CONTEXT_ONLY_RENDERING = 2;

	/**
	 * FIXME view-id of the target view for the actions contained within this toolbar content
	 */
	protected ASerializedView targetViewData = null;

	/**
	 * specifies the type of content to render. sub classes may define their own content types
	 */
	protected int renderType = STANDARD_RENDERING;

	/**
	 * specifies the if the related view is attached or detached to caleydo's main window
	 */
	protected boolean attached = false;

	/**
	 * Returns the related view type for this toolbar content
	 * 
	 * @return class object of the view related to this toolbar content
	 */
	public abstract Class<?> getViewClass();

	/**
	 * Delivers the toolbar content. sub classes should return a list of toolbar-actions that are added to a
	 * toolbar.
	 * 
	 * @return list of actions for a toolbar
	 */
	protected abstract List<ToolBarContainer> getToolBarContent();

	/**
	 * Delivers the content for the toolbar view for special behaviour sub classes should override this method
	 * 
	 * @return list of actions for a toolbar
	 */
	public List<ToolBarContainer> getDefaultToolBar() {
		if (attached) {
			return getToolBarContent();
		}
		else {
			return new ArrayList<ToolBarContainer>();
		}
	}

	/**
	 * Delivers the content for the view-inline toolbar for special behaviour sub classes should override this
	 * method
	 * 
	 * @return list of actions for a toolbar
	 */
	public List<ToolBarContainer> getInlineToolBar() {
		if (!attached) {
			return getToolBarContent();
		}
		else {
			return new ArrayList<ToolBarContainer>();
		}
	}

	/**
	 * Called when the object is not needed anymore to release resources that might be obtained during
	 * initialization. Inherited objects should override the default implementation if needed.
	 */
	public void dispose() {
		// nothing to do in this default implementation
	}

	/**
	 * Sets the id of the target view for the actions in this toolbar content. The id must be set before
	 * retrieving any toolbar content.
	 * 
	 * @param viewID
	 *            as used by IViewManager of the target view
	 */
	public void setTargetViewData(ASerializedView serializedView) {
		targetViewData = serializedView;
	}

	public ASerializedView getTargetViewData() {
		return targetViewData;
	}

	public int getRenderType() {
		return renderType;
	}

	public void setRenderType(int renderType) {
		this.renderType = renderType;
	}

	public boolean isAttached() {
		return attached;
	}

	public void setAttached(boolean attached) {
		this.attached = attached;
	}

}
