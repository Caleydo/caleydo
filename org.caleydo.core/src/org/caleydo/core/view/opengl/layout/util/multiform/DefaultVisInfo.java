/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout.util.multiform;

/**
 * Vis info that does not scale with any entity
 *
 * @author Christian Partl
 *
 */
public class DefaultVisInfo implements IEmbeddedVisualizationInfo {

	@Override
	public EScalingEntity getPrimaryWidthScalingEntity() {
		return null;
	}

	@Override
	public EScalingEntity getPrimaryHeightScalingEntity() {
		return null;
	}

	@Override
	public String getLabel() {
		return "";
	}

}
