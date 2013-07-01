/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.parcoords;
import org.caleydo.core.view.opengl.layout.util.multiform.IEmbeddedVisualizationInfo;


/**
 * Visualization Info for {@link GLParallelCoordinates}.
 *
 * @author Christian Partl
 *
 */
public class ParCoordsVisInfo implements IEmbeddedVisualizationInfo {

	public ParCoordsVisInfo() {
	}

	@Override
	public EScalingEntity getPrimaryWidthScalingEntity() {
		return EScalingEntity.DIMENSION;
	}

	@Override
	public EScalingEntity getPrimaryHeightScalingEntity() {
		return null;
	}

}
