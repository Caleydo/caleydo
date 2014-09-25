/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.pathway.v2.ui.augmentation;

import org.caleydo.core.data.perspective.table.Average;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.pathway.v2.ui.APathwayElementRepresentation;
import org.caleydo.view.pathway.v2.ui.IPathwayMappingListener;
import org.caleydo.view.pathway.v2.ui.PathwayDataMappingHandler;

/**
 * Augments a vertex by an exclamation mark if its mapped samples of any table perspective of the
 * {@link PathwayDataMappingHandler} exhibit high variance.
 *
 * @author Christian
 *
 */
public class HighVarianceIndicatorAugmentation extends APerVertexAugmentation implements IPathwayMappingListener {

	protected PathwayDataMappingHandler handler;

	/**
	 * @param pathwayRepresentation
	 */
	public HighVarianceIndicatorAugmentation(APathwayElementRepresentation pathwayRepresentation,
			PathwayDataMappingHandler handler) {
		super(pathwayRepresentation);
		this.handler = handler;
		handler.addListener(this);
	}

	@Override
	protected void renderVertexAugmentation(GLGraphics g, float w, float h, PathwayVertexRep vertexRep, Rect bounds) {

		if (vertexRep.getType() != EPathwayVertexType.gene)
			return;

		float threshold = 0.2f;
		Pair<TablePerspective, Average> highestAverage = null;
		Average average;
		for (TablePerspective tablePerspective : handler.getTablePerspectives()) {
			average = handler.getCachedAverage(tablePerspective, vertexRep);
			if (average == null)
				continue;
			if (average.getStandardDeviation() > threshold) {
				if (highestAverage == null
						|| average.getStandardDeviation() > highestAverage.getSecond().getStandardDeviation()) {
					highestAverage = new Pair<>(tablePerspective, average);
				}
			}
		}

		if (highestAverage != null) {
			float exclMarkWidth = 0.1f * bounds.width();
			// float exclMarkLowerWidth = 0.8f * exclMarkWidth;
			float exclMarkTopElementHeight = bounds.height() - exclMarkWidth - bounds.height() * 0.1f;
			// float exclMarkBottomElementHeight = 0.2f * bounds.height();

			g.color(highestAverage.getFirst().getDataDomain().getColor().darker());
			g.fillRect(bounds.x() + bounds.width() - exclMarkWidth, bounds.y(), exclMarkWidth, exclMarkTopElementHeight);

			g.fillRect(bounds.x() + bounds.width() - exclMarkWidth, bounds.y() + bounds.height() - exclMarkWidth,
					exclMarkWidth, exclMarkWidth);
			// g.fillPolygon(new Vec2f(bounds.x() + bounds.width(), bounds.y()), new Vec2f(bounds.x() + bounds.width()
			// - exclMarkWidth, bounds.y()), new Vec2f(bounds.x() + bounds.width() - exclMarkWidth
			// + (exclMarkWidth - exclMarkLowerWidth) / 2.0f, bounds.y() + exclMarkTopElementHeight), new Vec2f(
			// bounds.x() + bounds.width() - (exclMarkWidth - exclMarkLowerWidth) / 2.0f, bounds.y()
			// + exclMarkTopElementHeight));
			//
			// g.fillRect(bounds.x() + bounds.width() - exclMarkLowerWidth, bounds.y() + exclMarkTopElementHeight
			// + (bounds.height() - exclMarkBottomElementHeight - exclMarkTopElementHeight) / 2.0f, exclMarkWidth,
			// exclMarkBottomElementHeight);

		}
	}

	@Override
	public void update(PathwayDataMappingHandler handler) {
		repaint();
	}

}
