/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.pathway.v2.ui.augmentation;

import gleem.linalg.Vec2f;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.pathway.v2.ui.APathwayElementRepresentation;

/**
 * Augments a pathway vertex by a small triangle if that vertex represents more than one gene/protein.
 * 
 * @author Christian
 * 
 */
public class MultiMappingIndicatorAugmentation extends APerVertexAugmentation {

	protected static final Color MULTI_MAPPING_INDICATOR_COLOR = new Color(0.3f, 0.3f, 0.3f, 1f);

	public MultiMappingIndicatorAugmentation(APathwayElementRepresentation pathwayRepresentation) {
		super(pathwayRepresentation);
	}

	@Override
	protected void renderVertexAugmentation(GLGraphics g, float w, float h, PathwayVertexRep vertexRep, Rect bounds) {
		if (vertexRep.getType() == EPathwayVertexType.gene && vertexRep.getPathwayVertices().size() > 1) {
			float size = bounds.height() * 0.5f;
			g.color(MULTI_MAPPING_INDICATOR_COLOR).fillPolygon(new Vec2f(bounds.x(), bounds.y()),
					new Vec2f(bounds.x() + size, bounds.y()), new Vec2f(bounds.x(), bounds.y() + size));
		}

	}

}
