package org.caleydo.view.visbricks.brick.layout;

import java.util.HashSet;

import org.caleydo.core.view.opengl.layout.LayoutTemplate;
import org.caleydo.view.visbricks.brick.EContainedViewType;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.ui.AContainedViewRenderer;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroup;

/**
 * Subclasses of this class are intended to specify the elements and their
 * layout for different appearences of a brick.
 * 
 * @author Christian Partl
 * 
 */
public abstract class ABrickLayoutTemplate extends LayoutTemplate {

	protected static final int SPACING_PIXELS = 4;

	protected GLBrick brick;
	protected AContainedViewRenderer viewRenderer;
	protected boolean showHandles;
	protected DimensionGroup dimensionGroup;
	protected HashSet<EContainedViewType> validViewTypes;

	public ABrickLayoutTemplate(GLBrick brick, DimensionGroup dimensionGroup) {
		this.brick = brick;
		this.dimensionGroup = dimensionGroup;
		showHandles = false;
		validViewTypes = new HashSet<EContainedViewType>();
		setValidViewTypes();
		setPixelGLConverter(brick.getParentGLCanvas().getPixelGLConverter());
		registerPickingListeners();
	}

	/**
	 * Sets the renderer for the view element of a brick.
	 * 
	 * @param viewRenderer
	 */
	public void setViewRenderer(AContainedViewRenderer viewRenderer) {
		this.viewRenderer = viewRenderer;
	}

	/**
	 * @return True, if handles are shown.
	 */
	public boolean isShowHandles() {
		return showHandles;
	}

	/**
	 * Sets whether handles shall be shown.
	 * 
	 * @param showHandles
	 */
	public void setShowHandles(boolean showHandles) {
		this.showHandles = showHandles;
	}

	/**
	 * Registers PickingListeners. Automatically called upon Object creation.
	 */
	protected abstract void registerPickingListeners();

	/**
	 * @return Minimum height in pixels required by the brick with the current
	 *         layout and view
	 */
	public abstract int getMinHeightPixels();

	/**
	 * @return Minimum width in pixels required by the brick with the current
	 *         layout and view
	 */
	public abstract int getMinWidthPixels();

	/**
	 * Sets the view types that are valid for the layout.
	 */
	protected abstract void setValidViewTypes();

	/**
	 * @return The default view type for this layout.
	 */
	public abstract EContainedViewType getDefaultViewType();

	/**
	 * @param viewType
	 * @return True, if the specified viewType is valid for this layout, false
	 *         otherwise.
	 */
	public boolean isViewTypeValid(EContainedViewType viewType) {
		return validViewTypes.contains(viewType);
	}

}
