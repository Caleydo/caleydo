package org.caleydo.view.visbricks.brick.layout;

import java.util.ArrayList;
import java.util.HashSet;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.layout.LayoutTemplate;
import org.caleydo.view.visbricks.brick.EContainedViewType;
import org.caleydo.view.visbricks.brick.GLBrick;
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
	protected static final int DEFAULT_GUI_ELEMENT_SIZE_PIXELS = 16;

	protected GLBrick brick;
	protected LayoutRenderer viewRenderer;
	protected boolean showHandles;
	protected DimensionGroup dimensionGroup;
	protected HashSet<EContainedViewType> validViewTypes;
	protected EContainedViewType defaultViewType;
	protected ArrayList<IViewTypeChangeListener> viewTypeChangeListeners;

	public ABrickLayoutTemplate(GLBrick brick, DimensionGroup dimensionGroup) {
		this.brick = brick;
		this.dimensionGroup = dimensionGroup;
		showHandles = false;
		validViewTypes = new HashSet<EContainedViewType>();
		viewTypeChangeListeners = new ArrayList<IViewTypeChangeListener>();
		// setValidViewTypes();
		setPixelGLConverter(brick.getParentGLCanvas().getPixelGLConverter());
		// registerPickingListeners();
	}

	public PixelGLConverter getPixelGLConverter() {
		return pixelGLConverter;
	}

	/**
	 * Sets the renderer for the view element of a brick.
	 * 
	 * @param viewRenderer
	 */
	public void setViewRenderer(LayoutRenderer viewRenderer) {
		this.viewRenderer = viewRenderer;
	}

	/**
	 * Gets the renderer for the view element of a brick.
	 * 
	 * @return viewRenderer
	 */
	public LayoutRenderer getViewRenderer() {
		return viewRenderer;
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
	public void viewTypeChanged(EContainedViewType viewType) {
		for (IViewTypeChangeListener viewTypeChangeListener : viewTypeChangeListeners) {
			viewTypeChangeListener.viewTypeChanged(viewType);
		}
	}

	/**
	 * Sets, whether view switching by this brick should affect other bricks in
	 * the dimension group.
	 * 
	 * @param isGlobalViewSwitching
	 */
	public void setGlobalViewSwitching(boolean isGlobalViewSwitching) {
	}

	/**
	 * Sets whether the brick resizing is currently locked.
	 * 
	 * @param lockResizing
	 */
	public abstract void setLockResizing(boolean lockResizing);

	/**
	 * Gets the layout that represents the collapsed version of the current
	 * layout. If no further collapsing is possible, the current layout is
	 * returned.
	 * 
	 * @return
	 */
	public abstract ABrickLayoutTemplate getCollapsedLayoutTemplate();

	/**
	 * Gets the layout that represents the expanded version of the current
	 * layout. If no further expansion is possible, the current layout is
	 * returned.
	 * 
	 * @return
	 */
	public abstract ABrickLayoutTemplate getExpandedLayoutTemplate();

	// public abstract void configure(IBrickLayoutConfigurer configurer);

	/**
	 * @return Default height in pixels required by the brick with the current
	 *         layout and view
	 */
	public int getDefaultHeightPixels() {
		return getMinHeightPixels();
	}

	/**
	 * @return Default width in pixels required by the brick with the current
	 *         layout and view
	 */
	public int getDefaultWidthPixels() {
		return getMinWidthPixels();
	}

	public GLBrick getBrick() {
		return brick;
	}

	public DimensionGroup getDimensionGroup() {
		return dimensionGroup;
	}

	public void registerViewTypeChangeListener(
			IViewTypeChangeListener viewTypeChangeListener) {
		viewTypeChangeListeners.add(viewTypeChangeListener);
	}

	protected int calcSumPixelWidth(ArrayList<ElementLayout> elementLayouts) {
		int sum = 0;
		for (ElementLayout elementLayout : elementLayouts) {
			int pixelSize = elementLayout.getPixelSizeX();
			if (pixelSize == Integer.MIN_VALUE) {
				float glSize = elementLayout.getAbsoluteSizeX();
				if (Float.isNaN(glSize)) {
					pixelSize = DEFAULT_GUI_ELEMENT_SIZE_PIXELS;
				} else {
					pixelSize = pixelGLConverter
							.getPixelWidthForGLWidth(glSize);
				}
			}
			sum += pixelSize;
		}

		return sum;
	}
	
	protected int calcSumPixelHeight(ArrayList<ElementLayout> elementLayouts) {
		int sum = 0;
		for (ElementLayout elementLayout : elementLayouts) {
			int pixelSize = elementLayout.getPixelSizeY();
			if (pixelSize == Integer.MIN_VALUE) {
				float glSize = elementLayout.getAbsoluteSizeY();
				if (glSize == Float.NaN) {
					pixelSize = DEFAULT_GUI_ELEMENT_SIZE_PIXELS;
				} else {
					pixelSize = pixelGLConverter
							.getPixelHeightForGLHeight(glSize);
				}
			}
			sum += pixelSize;
		}

		return sum;
	}
	
	protected int getMaxPixelHeight(ArrayList<ElementLayout> elementLayouts) {
		int max = Integer.MIN_VALUE;
		for (ElementLayout elementLayout : elementLayouts) {
			int pixelSize = elementLayout.getPixelSizeY();
			if (pixelSize == Integer.MIN_VALUE) {
				float glSize = elementLayout.getAbsoluteSizeY();
				if (glSize != Float.NaN) {
					pixelSize = pixelGLConverter
							.getPixelHeightForGLHeight(glSize);
				}
			}
			if(max < pixelSize)
				max = pixelSize;
		}

		return max;
	}
	
	protected int getMaxPixelWidth(ArrayList<ElementLayout> elementLayouts) {
		int max = Integer.MIN_VALUE;
		for (ElementLayout elementLayout : elementLayouts) {
			int pixelSize = elementLayout.getPixelSizeX();
			if (pixelSize == Integer.MIN_VALUE) {
				float glSize = elementLayout.getAbsoluteSizeX();
				if (glSize != Float.NaN) {
					pixelSize = pixelGLConverter
							.getPixelWidthForGLWidth(glSize);
				}
			}
			if(max < pixelSize)
				max = pixelSize;
		}

		return max;
	}

}
