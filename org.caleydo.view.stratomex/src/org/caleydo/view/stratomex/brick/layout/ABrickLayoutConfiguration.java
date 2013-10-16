/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.brick.layout;

import java.util.ArrayList;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutConfiguration;
import org.caleydo.core.view.opengl.layout.util.BorderedAreaRenderer;
import org.caleydo.datadomain.pathway.data.PathwayTablePerspective;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.brick.GLBrick;
import org.caleydo.view.stratomex.brick.configurer.IBrickConfigurer;
import org.caleydo.view.stratomex.brick.ui.HandleRenderer;
import org.caleydo.view.stratomex.brick.ui.ViewBorderRenderer;
import org.caleydo.view.stratomex.column.BrickColumn;

/**
 * Base class are intended to specify the elements and their layout for different appearances of a brick.
 *
 * @author Christian Partl
 *
 */
public abstract class ABrickLayoutConfiguration extends LayoutConfiguration {

	protected static final int SPACING_PIXELS = 2;
	protected static final int DEFAULT_GUI_ELEMENT_SIZE_PIXELS = 16;

	protected GLBrick brick;
	protected ALayoutRenderer viewRenderer;
	protected ElementLayout viewLayout;
	protected BrickColumn brickColumn;
	protected GLStratomex stratomex;
	// protected HashSet<EContainedViewType> validViewTypes;
	// protected EContainedViewType defaultViewType;
	// protected ArrayList<IViewTypeChangeListener> viewTypeChangeListeners;
	protected BorderedAreaRenderer borderedAreaRenderer;
	protected ViewBorderRenderer innerBorderedAreaRenderer;

	protected Integer handles = null;

	protected HandleRenderer handleRenderer = null;

	public ABrickLayoutConfiguration(GLBrick brick, BrickColumn brickColumn, GLStratomex stratomex) {
		this.brick = brick;
		this.brickColumn = brickColumn;
		this.stratomex = stratomex;
		// validViewTypes = new HashSet<EContainedViewType>();
		// viewTypeChangeListeners = new ArrayList<IViewTypeChangeListener>();
		borderedAreaRenderer = new BorderedAreaRenderer();
		innerBorderedAreaRenderer = new ViewBorderRenderer();

		// if (brick.isHeaderBrick())
		Color color = brick.getDataDomain().getColor();
		if (brick.getBrickColumn().getTablePerspective() instanceof PathwayTablePerspective)
			color = ((PathwayTablePerspective) brick.getBrickColumn().getTablePerspective()).getPathwayDataDomain()
					.getColor();

		if (!brick.isHeaderBrick())
			color = Color.NEUTRAL_GREY;

		borderedAreaRenderer.setColor(color);
		innerBorderedAreaRenderer.setColor(color);

		// setValidViewTypes();
		// registerPickingListeners();
	}

	/**
	 * Configures the elements of this {@link ABrickLayoutConfiguration} using the specified {@link IBrickConfigurer}.
	 *
	 * @param configurer
	 */
	public abstract void configure(IBrickConfigurer configurer);

	/**
	 * Sets the renderer for the view element of a brick.
	 *
	 * @param viewRenderer
	 */
	public void setViewRenderer(ALayoutRenderer viewRenderer) {
		this.viewRenderer = viewRenderer;
	}

	/**
	 * Gets the renderer for the view element of a brick.
	 *
	 * @return viewRenderer
	 */
	public ALayoutRenderer getViewRenderer() {
		return viewRenderer;
	}

	/**
	 * Manually override the brick handles to be shown. See {@link HandleRenderer} for possible flags
	 *
	 * @param handles
	 *            an integer that can be bit-ored with the flags defined in {@link HandleRenderer} to determine the
	 *            handles desired.
	 *
	 */
	public void setHandles(int handles) {
		this.handles = handles;
	}

	public abstract ToolBar getToolBar();

	/**
	 * Registers PickingListeners. Should be called after the layout has been configured by a {@link IBrickConfigurer}.
	 */
	protected abstract void registerPickingListeners();

	/**
	 * @return Minimum height in pixels required by the brick with the current layout and view
	 */
	public abstract int getMinHeightPixels();

	/**
	 * @return Minimum width in pixels required by the brick with the current layout and view
	 */
	public abstract int getMinWidthPixels();

	/**
	 * Sets the view types that are valid for the layout.
	 *
	 * @param validViewTypes
	 */
	// public void setValidViewTypes(HashSet<EContainedViewType> validViewTypes) {
	// this.validViewTypes = validViewTypes;
	// }
	//
	// /**
	// * Sets the default view type for the layout.
	// *
	// * @param validViewTypes
	// */
	// public void setDefaultViewType(EContainedViewType defaultViewType) {
	// this.defaultViewType = defaultViewType;
	// }
	//
	// /**
	// * @return The default view type for this layout.
	// */
	// public EContainedViewType getDefaultViewType() {
	// return defaultViewType;
	// }

	// /**
	// * @param viewType
	// * @return True, if the specified viewType is valid for this layout, false
	// * otherwise.
	// */
	// public boolean isViewTypeValid(EContainedViewType viewType) {
	// return validViewTypes.contains(viewType);
	// }

	// /**
	// * This method should be called when the view type in the brick changed.
	// */
	// public void viewTypeChanged(EContainedViewType viewType) {
	// for (IViewTypeChangeListener viewTypeChangeListener : viewTypeChangeListeners) {
	// viewTypeChangeListener.viewTypeChanged(viewType);
	// }
	// }

	/**
	 * Sets, whether view switching by this brick should affect other bricks in the dimension group.
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
	 * Gets the layout that represents the collapsed version of the current layout. If no further collapsing is
	 * possible, the current layout is returned.
	 *
	 * @return
	 */
	public abstract ABrickLayoutConfiguration getCollapsedLayoutTemplate();

	/**
	 * Gets the layout that represents the expanded version of the current layout. If no further expansion is possible,
	 * the current layout is returned.
	 *
	 * @return
	 */
	public abstract ABrickLayoutConfiguration getExpandedLayoutTemplate();

	// public abstract void configure(IBrickLayoutConfigurer configurer);

	/**
	 * @return Default height in pixels required by the brick with the current layout and view.
	 */
	public int getDefaultHeightPixels() {

		return getMinHeightPixels();
	}

	/**
	 * @return Default width in pixels required by the brick with the current layout and view
	 */
	public int getDefaultWidthPixels() {
		return getMinWidthPixels();
	}

	public GLBrick getBrick() {
		return brick;
	}

	public BrickColumn getDimensionGroup() {
		return brickColumn;
	}

	// public void registerViewTypeChangeListener(
	// IViewTypeChangeListener viewTypeChangeListener) {
	// viewTypeChangeListeners.add(viewTypeChangeListener);
	// }

	protected int calcSumPixelWidth(Iterable<ElementLayout> elementLayouts) {
		int sum = 0;
		for (ElementLayout elementLayout : elementLayouts) {
			int pixelSize = elementLayout.getPixelSizeX();
			if (pixelSize == Integer.MIN_VALUE) {
				float glSize = elementLayout.getAbsoluteSizeX();
				if (Float.isNaN(glSize)) {
					pixelSize = DEFAULT_GUI_ELEMENT_SIZE_PIXELS;
				} else {
					pixelSize = brick.getPixelGLConverter().getPixelWidthForGLWidth(glSize);
				}
			}
			sum += pixelSize;
		}

		return sum;
	}

	protected int calcSumPixelHeight(Iterable<ElementLayout> elementLayouts) {
		int sum = 0;
		for (ElementLayout elementLayout : elementLayouts) {
			int pixelSize = elementLayout.getPixelSizeY();
			if (pixelSize == Integer.MIN_VALUE) {
				float glSize = elementLayout.getAbsoluteSizeY();
				if (glSize == Float.NaN) {
					pixelSize = DEFAULT_GUI_ELEMENT_SIZE_PIXELS;
				} else {
					pixelSize = brick.getPixelGLConverter().getPixelHeightForGLHeight(glSize);
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
					pixelSize = brick.getPixelGLConverter().getPixelHeightForGLHeight(glSize);
				}
			}
			if (max < pixelSize)
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
					pixelSize = brick.getPixelGLConverter().getPixelWidthForGLWidth(glSize);
				}
			}
			if (max < pixelSize)
				max = pixelSize;
		}

		return max;
	}

	public void setSelected(boolean selected) {

		if (brick.isHeaderBrick()) {
			brickColumn.setHighlightColor(selected ? SelectionType.SELECTION.getColor() : null);
			return;
		} else {
			if (selected) {
				Color selectionColor = SelectionType.SELECTION.getColor().lessSaturated();
				borderedAreaRenderer.setColor(selectionColor);
			} else {
				borderedAreaRenderer.setColor(Color.NEUTRAL_GREY);
			}
		}
	}

	/**
	 * This method should be called if the layout template is no longer needed.
	 */
	public void destroy() {
		if (viewLayout != null) {
			// viewLayout.destroy(brick.getParentGLCanvas().asGLAutoDrawAble().getGL().getGL2());
			viewLayout = null;
		}
	}

	/**
	 * @return the viewLayout, see {@link #viewLayout}
	 */
	public ElementLayout getViewLayout() {
		return viewLayout;
	}

	/**
	 * @return the handleRenderer, see {@link #handleRenderer}
	 */
	public HandleRenderer getHandleRenderer() {
		return handleRenderer;
	}
}
