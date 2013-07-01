/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.pathway;

import org.caleydo.core.view.opengl.layout.util.multiform.IEmbeddedVisualizationInfo;

/**
 * VisInfo for {@link GLPathway}
 *
 * @author Christian Partl
 *
 */
public class PathwayVisInfo implements IEmbeddedVisualizationInfo {

	public PathwayVisInfo() {
	}

	@Override
	public EScalingEntity getPrimaryWidthScalingEntity() {
		return null;
	}

	@Override
	public EScalingEntity getPrimaryHeightScalingEntity() {
		return null;
	}

}
