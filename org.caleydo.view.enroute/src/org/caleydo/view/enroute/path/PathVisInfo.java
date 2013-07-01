/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.enroute.path;

import org.caleydo.core.view.opengl.layout.util.multiform.IEmbeddedVisualizationInfo;

/**
 * VisInfo for {@link APathwayPathRenderer}.
 *
 * @author Christian Partl
 *
 */
public class PathVisInfo implements IEmbeddedVisualizationInfo {

	public PathVisInfo() {
	}

	@Override
	public EScalingEntity getPrimaryWidthScalingEntity() {
		return null;
	}

	@Override
	public EScalingEntity getPrimaryHeightScalingEntity() {
		return EScalingEntity.PATHWAY_VERTEX;
	}

}
