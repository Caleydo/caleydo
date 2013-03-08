package org.caleydo.view.enroute;

import org.caleydo.core.view.opengl.layout.util.multiform.IEmbeddedVisualizationInfo;

public class enRouteVisInfo implements IEmbeddedVisualizationInfo {

	public enRouteVisInfo() {
		// TODO Auto-generated constructor stub
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
