/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.pathway.v2.ui.augmentation;

import org.caleydo.core.data.perspective.table.Average;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.pathway.v2.ui.APathwayElementRepresentation;
import org.caleydo.view.pathway.v2.ui.IPathwayMappingListener;
import org.caleydo.view.pathway.v2.ui.PathwayDataMappingHandler;

/**
 * Renders a bar indicating the standard deviation of all mapped samples for each pathway vertex. The samples are taken
 * from the mapping perspective of the {@link PathwayDataMappingHandler}.
 *
 * @author Christian
 *
 */
public class StdDevBarAugmentation extends APerVertexAugmentation implements IPathwayMappingListener {

	protected static final Color BORDER_COLOR = new Color(0f, 0f, 0f, 1f);
	protected static final Color BACKGROUND_COLOR = new Color(1f, 1f, 1f, 1f);
	protected PathwayDataMappingHandler handler;

	/**
	 * @param pathwayRepresentation
	 */
	public StdDevBarAugmentation(APathwayElementRepresentation pathwayRepresentation, PathwayDataMappingHandler handler) {
		super(pathwayRepresentation);
		this.handler = handler;
		handler.addListener(this);
	}

	@Override
	public void update(PathwayDataMappingHandler handler) {
		repaint();
	}

	@Override
	protected void renderVertexAugmentation(GLGraphics g, float w, float h, PathwayVertexRep vertexRep, Rect bounds) {
		if (handler.getMappingPerspective() == null || vertexRep.getType() != EPathwayVertexType.gene)
			return;

		Average avg = handler.getMappingAverage(vertexRep);

		if (avg != null) {
			Rect barBounds = getStdDevBarBounds(bounds);
			float stdDevWidth = bounds.width() * (float) avg.getStandardDeviation() * 2;
			g.gl.glLineWidth(1);
			g.color(BACKGROUND_COLOR).fillRect(barBounds);
			g.color(handler.getMappingPerspective().getDataDomain().getColor().darker()).fillRect(barBounds.x(),
					barBounds.y(), stdDevWidth, barBounds.height());
			g.color(BORDER_COLOR).drawRect(barBounds.x(), barBounds.y(), barBounds.width(), barBounds.height());

		}

	}

	public static Rect getStdDevBarBounds(Rect bounds) {
		float height = bounds.height() * 0.4f;
		float top = bounds.y() + bounds.height() - 0.3f * height;

		return new Rect(bounds.x(), top, bounds.width(), height);

	}
}
