/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.stratomex.brick.layout;

import java.util.ArrayList;
import java.util.HashSet;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutConfiguration;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.layout.util.BorderedAreaRenderer;
import org.caleydo.view.stratomex.brick.EContainedViewType;
import org.caleydo.view.stratomex.brick.GLBrick;
import org.caleydo.view.stratomex.brick.configurer.IBrickConfigurer;
import org.caleydo.view.stratomex.column.BrickColumn;

/**
 * Base class are intended to specify the elements and their
 * layout for different appearances of a brick.
 * 
 * @author Christian Partl
 * 
 */
public abstract class ABrickLayoutConfiguration extends LayoutConfiguration {

	protected static final int SPACING_PIXELS = 4;
	protected static final int DEFAULT_GUI_ELEMENT_SIZE_PIXELS = 16;

	protected GLBrick brick;
	protected LayoutRenderer viewRenderer;
	protected ElementLayout viewLayout;
	protected BrickColumn dimensionGroup;
	protected HashSet<EContainedViewType> validViewTypes;
	protected EContainedViewType defaultViewType;
	protected ArrayList<IViewTypeChangeListener> viewTypeChangeListeners;
	protected BorderedAreaRenderer borderedAreaRenderer;

	public ABrickLayoutConfiguration(GLBrick brick, BrickColumn dimensionGroup) {
		this.brick = brick;
		this.dimensionGroup = dimensionGroup;
		validViewTypes = new HashSet<EContainedViewType>();
		viewTypeChangeListeners = new ArrayList<IViewTypeChangeListener>();
		borderedAreaRenderer = new BorderedAreaRenderer();
		// setValidViewTypes();

		// registerPickingListeners();
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
	public abstract ABrickLayoutConfiguration getCollapsedLayoutTemplate();

	/**
	 * Gets the layout that represents the expanded version of the current
	 * layout. If no further expansion is possible, the current layout is
	 * returned.
	 * 
	 * @return
	 */
	public abstract ABrickLayoutConfiguration getExpandedLayoutTemplate();

	// public abstract void configure(IBrickLayoutConfigurer configurer);

	/**
	 * @return Default height in pixels required by the brick with the current
	 *         layout and view.
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

	public BrickColumn getDimensionGroup() {
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
					pixelSize = brick.getPixelGLConverter().getPixelWidthForGLWidth(
							glSize);
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
					pixelSize = brick.getPixelGLConverter().getPixelHeightForGLHeight(
							glSize);
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
					pixelSize = brick.getPixelGLConverter().getPixelHeightForGLHeight(
							glSize);
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
					pixelSize = brick.getPixelGLConverter().getPixelWidthForGLWidth(
							glSize);
				}
			}
			if (max < pixelSize)
				max = pixelSize;
		}

		return max;
	}

	public void setSelected(boolean selected) {
		if (selected) {
			float[] color = new float[4];
			float[] selectionColor = SelectionType.SELECTION.getColor();
			color[0] = selectionColor[0] * 0.4f + BorderedAreaRenderer.DEFAULT_COLOR[0]
					* 0.6f;
			color[1] = selectionColor[1] * 0.4f + BorderedAreaRenderer.DEFAULT_COLOR[1]
					* 0.6f;
			color[2] = selectionColor[2] * 0.4f + BorderedAreaRenderer.DEFAULT_COLOR[2]
					* 0.6f;
			color[3] = 1;
			borderedAreaRenderer.setColor(color);
		} else {
			borderedAreaRenderer.setColor(BorderedAreaRenderer.DEFAULT_COLOR);
		}
	}

	/**
	 * This method should be called if the layout template is no longer needed.
	 */
	public void destroy() {
		if (viewLayout != null) {
			viewLayout.destroy();
			viewLayout = null;
		}
	}
	
	/**
	 * @return the viewLayout, see {@link #viewLayout}
	 */
	public ElementLayout getViewLayout() {
		return viewLayout;
	}
}
