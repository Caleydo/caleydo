package org.caleydo.view.differenceplot;

import org.caleydo.core.view.opengl.layout.util.multiform.IEmbeddedVisualizationInfo;

public class DifferencePlotVisInfo implements IEmbeddedVisualizationInfo {

	public DifferencePlotVisInfo() {
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
