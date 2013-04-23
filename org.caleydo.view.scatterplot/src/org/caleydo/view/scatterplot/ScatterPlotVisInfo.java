package org.caleydo.view.scatterplot;

import org.caleydo.core.view.opengl.layout.util.multiform.IEmbeddedVisualizationInfo;

public class ScatterPlotVisInfo implements IEmbeddedVisualizationInfo {

	public ScatterPlotVisInfo() {
	}

	@Override
	public EScalingEntity getPrimaryWidthScalingEntity() {
		return EScalingEntity.DIMENSION;
	}

	@Override
	public EScalingEntity getPrimaryHeightScalingEntity() {
		return EScalingEntity.RECORD;
	}

}
