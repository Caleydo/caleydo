package org.caleydo.view.heatmap.heatmap;

import org.caleydo.core.view.opengl.layout.util.multiform.IEmbeddedVisualizationInfo;

public class HeatMapVisInfo implements IEmbeddedVisualizationInfo {

	public HeatMapVisInfo() {
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
