/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.pathway.v2.ui.augmentation;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;

/**
 * @author Christian
 *
 */
public class PathwayTextureBackgroundColorAugmentation extends GLElement {

	protected IPathwayRepresentation pathwayRepresentation;

	public PathwayTextureBackgroundColorAugmentation(IPathwayRepresentation pathwayRepresentation) {
		this.pathwayRepresentation = pathwayRepresentation;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.color(1f, 1f, 0f, 1f).fillRect(pathwayRepresentation.getPathwayBounds());
	}

}
