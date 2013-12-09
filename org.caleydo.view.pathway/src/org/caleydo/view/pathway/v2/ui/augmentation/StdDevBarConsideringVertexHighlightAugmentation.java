/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.pathway.v2.ui.augmentation;

import gleem.linalg.Vec2f;

import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.pathway.v2.ui.PathwayDataMappingHandler;
import org.caleydo.view.pathway.v2.ui.PathwayElement;

/**
 * @author Christian
 *
 */
public class StdDevBarConsideringVertexHighlightAugmentation extends VertexHighlightAugmentation {

	protected StdDevBarAugmentation stdDevBarAugmentation;
	protected PathwayDataMappingHandler handler;

	public StdDevBarConsideringVertexHighlightAugmentation(PathwayElement pathwayElement) {
		super(pathwayElement.getPathwayRepresentation());
		handler = pathwayElement.getMappingHandler();
	}

	@Override
	protected void renderVertexAugmentation(GLGraphics g, float w, float h, PathwayVertexRep vertexRep, Rect bounds) {
		Rect newBounds = bounds;
		if (vertexRep.getType() == EPathwayVertexType.gene && handler.getMappingPerspective() != null
				&& handler.getMappingAverage(vertexRep) != null) {
			Rect barBounds = StdDevBarAugmentation.getStdDevBarBounds(bounds);
			Vec2f position = new Vec2f(Math.min(bounds.x(), barBounds.x()), Math.min(bounds.y(), barBounds.y()));
			float right1 = bounds.x() + bounds.width();
			float right2 = barBounds.x() + barBounds.width();
			float width = Math.max(right1, right2) - position.x();

			float bottom1 = bounds.y() + bounds.height();
			float bottom2 = barBounds.y() + barBounds.height();
			float height = Math.max(bottom1, bottom2) - position.y();

			newBounds = new Rect(position.x(), position.y(), width, height);
		}
		super.renderVertexAugmentation(g, w, h, vertexRep, newBounds);
	}

}
