/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.pathway.v2.ui;

import org.caleydo.core.data.perspective.table.Average;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;

/**
 * @author Christian
 *
 */
public class StdDevBarAugmentation extends APerVertexAugmentation implements IPathwayMappingListener {

	protected static final Color BORDER_COLOR = new Color(0f, 0f, 0f, 1f);
	protected static final Color BACKGROUND_COLOR = new Color(1f, 1f, 1f, 1f);
	protected PathwayMappingHandler handler;

	/**
	 * @param pathwayRepresentation
	 */
	public StdDevBarAugmentation(APathwayElementRepresentation pathwayRepresentation, PathwayMappingHandler handler) {
		super(pathwayRepresentation);
		this.handler = handler;
		handler.addListener(this);
	}

	@Override
	public void update(PathwayMappingHandler handler) {
		repaint();
	}

	@Override
	protected void renderVertexAugmentation(GLGraphics g, float w, float h, PathwayVertexRep vertexRep, Rect bounds) {
		if (handler.getMappingPerspective() == null || vertexRep.getType() != EPathwayVertexType.gene)
			return;

		Average avg = handler.getMappingAverage(vertexRep);

		if (avg != null) {
			float stdDevWidth = bounds.width() * (float) avg.getStandardDeviation() * 2;
			float height = bounds.height() * 0.4f;
			float top = bounds.y() + bounds.height() - 0.3f * height;
			g.gl.glLineWidth(1);
			g.color(BACKGROUND_COLOR).fillRect(bounds.x(), top, bounds.width(), height);
			g.color(handler.getMappingPerspective().getDataDomain().getColor().darker()).fillRect(bounds.x(), top,
					stdDevWidth, height);
			g.color(BORDER_COLOR).renderRect(false, bounds.x(), top, bounds.width(), height);


		}

	}
}
