/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.pathway.v2.ui.augmentation;

import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.pathway.v2.ui.APathwayElementRepresentation;

/**
 * @author Christian
 *
 */
public abstract class APerVertexAugmentation extends GLElement {
	protected APathwayElementRepresentation pathwayRepresentation;

	public APerVertexAugmentation(APathwayElementRepresentation pathwayRepresentation) {
		this.pathwayRepresentation = pathwayRepresentation;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		for (PathwayGraph pathway : pathwayRepresentation.getPathways()) {
			for (PathwayVertexRep vertexRep : pathway.vertexSet()) {
				List<Rect> boundsList = pathwayRepresentation.getVertexRepsBounds(vertexRep);
				for (Rect bounds : boundsList) {
					renderVertexAugmentation(g, w, h, vertexRep, bounds);
				}
			}
		}
	}

	protected abstract void renderVertexAugmentation(GLGraphics g, float w, float h, PathwayVertexRep vertexRep,
			Rect bounds);
}
