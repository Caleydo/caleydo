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
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;

/**
 * Base class for pathway augmentations that are rendered for each {@link PathwayVertexRep}.
 *
 * @author Christian
 *
 */
public abstract class APerVertexAugmentation extends GLElement {
	protected IPathwayRepresentation pathwayRepresentation;

	public APerVertexAugmentation(IPathwayRepresentation pathwayRepresentation) {
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

	/**
	 * Renders an augmentation for an individual {@link PathwayVertexRep}.
	 *
	 * @param g
	 * @param w
	 *            Width of this element.
	 * @param h
	 *            Height of this element
	 * @param vertexRep
	 *            The vertex to augment.
	 * @param bounds
	 *            The bounds of the vertex to augment as given by the pathway representation.
	 */
	protected abstract void renderVertexAugmentation(GLGraphics g, float w, float h, PathwayVertexRep vertexRep,
			Rect bounds);
}
