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
	protected EContainedViewType defaultViewType;

	public ABrickLayoutTemplate(GLBrick brick, DimensionGroup dimensionGroup) {
		this.brick = brick;
		this.dimensionGroup = dimensionGroup;
		showHandles = false;
		validViewTypes = new HashSet<EContainedViewType>();
		// setValidViewTypes();
		setPixelGLConverter(brick.getParentGLCanvas().getPixelGLConverter());
//		registerPickingListeners();
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
	 * Registers PickingListeners. Should be called after the layout has been
	 * configured by a {@link IBrickConfigurer}.
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
	 * 
	 * @param validViewTypes
	 */
	public void setValidViewTypes(HashSet<EContainedViewType> validViewTypes) {
		this.validViewTypes = validViewTypes;
	}

	/**
	 * Sets the default view type for the layout.
	 * 
	 * @param validViewTypes
	 */
	public void setDefaultViewType(EContainedViewType defaultViewType) {
		this.defaultViewType = defaultViewType;
	}

	/**
	 * @return The default view type for this layout.
	 */
	public EContainedViewType getDefaultViewType() {
		return defaultViewType;
	}

	/**
	 * @param viewType
	 * @return True, if the specified viewType is valid for this layout, false
	 *         otherwise.
	 */
	public boolean isViewTypeValid(EContainedViewType viewType) {
		return validViewTypes.contains(viewType);
	}

	/**
	 * This method should be called when the view type in the brick changed.
	 */
	public abstract void viewTypeChanged(EContainedViewType viewType);

	/**
	 * Sets, whether view switching by this brick should affect other bricks in
	 * the dimension group.
	 * 
	 * @param isGlobalViewSwitching
	 */
	public void setGlobalViewSwitching(boolean isGlobalViewSwitching) {
	}
	
	public abstract void setLockResizing(boolean lockResizing);

//	public abstract void configure(IBrickLayoutConfigurer configurer);

}
