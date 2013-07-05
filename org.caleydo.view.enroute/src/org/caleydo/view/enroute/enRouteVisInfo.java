/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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

	@Override
	public String getLabel() {
		return GLEnRoutePathway.VIEW_NAME;
	}

}
