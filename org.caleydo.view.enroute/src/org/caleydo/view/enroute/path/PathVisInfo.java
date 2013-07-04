/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.enroute.path;

import org.caleydo.core.view.opengl.layout.util.multiform.DefaultVisInfo;

/**
 * VisInfo for {@link APathwayPathRenderer}.
 *
 * @author Christian Partl
 *
 */
public class PathVisInfo extends DefaultVisInfo {

	public PathVisInfo() {
	}

	@Override
	public EScalingEntity getPrimaryHeightScalingEntity() {
		return EScalingEntity.PATHWAY_VERTEX;
	}

	@Override
	public String getLabel() {
		return "Path";
	}

}
