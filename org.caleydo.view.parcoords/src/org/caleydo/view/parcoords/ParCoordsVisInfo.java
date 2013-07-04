/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.parcoords;

import org.caleydo.core.view.opengl.layout.util.multiform.DefaultVisInfo;


/**
 * Visualization Info for {@link GLParallelCoordinates}.
 *
 * @author Christian Partl
 *
 */
public class ParCoordsVisInfo extends DefaultVisInfo {

	public ParCoordsVisInfo() {
	}

	@Override
	public EScalingEntity getPrimaryWidthScalingEntity() {
		return EScalingEntity.DIMENSION;
	}

	@Override
	public String getLabel() {
		return GLParallelCoordinates.VIEW_NAME;
	}

}
