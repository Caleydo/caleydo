package org.caleydo.view.scatterplot;

import org.caleydo.core.view.opengl.layout.util.multiform.IEmbeddedVisualizationInfo;

public class ScatterPlotVisInfo implements IEmbeddedVisualizationInfo {
	
	public static int MIN_HEIGHT_PIXELS = 200;
	public static int MIN_WIDTH_PIXELS = 200;

	public ScatterPlotVisInfo() {
	}

	@Override
	public EScalingEntity getPrimaryWidthScalingEntity() {
		//return EScalingEntity.DIMENSION;
		return null;
	}

	@Override
	public EScalingEntity getPrimaryHeightScalingEntity() {
		//return EScalingEntity.RECORD;
		return null;
	}
	
	/**
	 * To be overridden by subclass if needed.
	 *
	 * @return The minimum height in pixels required by the renderer.
	 */
	public int getMinHeightPixels() {
		return MIN_HEIGHT_PIXELS;
	}

	/**
	 * To be overridden by subclass if needed.
	 *
	 * @return The minimum width in pixels required by the renderer.
	 */
	public int getMinWidthPixels() {
		return MIN_WIDTH_PIXELS;
	}

}
