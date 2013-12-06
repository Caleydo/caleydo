/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.pathway.v2.ui.augmentation;

import org.caleydo.core.data.perspective.table.Average;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.data.DataSetSelectedEvent;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.datadomain.pathway.IVertexRepSelectionListener;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.pathway.v2.ui.APathwayElementRepresentation;
import org.caleydo.view.pathway.v2.ui.IPathwayMappingListener;
import org.caleydo.view.pathway.v2.ui.PathwayDataMappingHandler;

/**
 * @author Christian
 *
 */
public class AverageColorMappingAugmentation extends APerVertexAugmentation implements IPathwayMappingListener,
		IVertexRepSelectionListener {

	protected static final Color NO_MAPPING_ICON_COLOR = new Color(0.3f, 0.3f, 0.3f, 0.7f);
	protected PathwayDataMappingHandler handler;

	public AverageColorMappingAugmentation(APathwayElementRepresentation pathwayRepresentation,
			PathwayDataMappingHandler handler) {
		super(pathwayRepresentation);
		this.handler = handler;
		pathwayRepresentation.addVertexRepSelectionListener(this);
		handler.addListener(this);
	}

	@Override
	protected void renderVertexAugmentation(GLGraphics g, float w, float h, PathwayVertexRep vertexRep, Rect bounds) {
		if (handler.getMappingPerspective() == null || vertexRep.getType() != EPathwayVertexType.gene)
			return;

		Average avg = handler.getMappingAverage(vertexRep);

		// g.color(1f, 0f, 0f).fillRect(bounds.x(), bounds.y(), bounds.width(), bounds.height());
		if (avg != null) {
			g.color(handler.getMappingPerspective().getDataDomain().getTable().getColorMapper()
					.getColor((float) avg.getArithmeticMean()));
			g.fillRect(bounds.x(), bounds.y(), bounds.width(), bounds.height());
		} else {
			float size = bounds.height() * 0.5f;
			g.color(NO_MAPPING_ICON_COLOR).fillRect(bounds.x() + bounds.width() - size,
					bounds.y() + bounds.height() - size, size, size);
		}
	}

	@Override
	protected void takeDown() {
		handler.removeListener(this);
		super.takeDown();
	}

	@Override
	public void update(PathwayDataMappingHandler handler) {
		repaint();
	}

	@Override
	public void onSelect(PathwayVertexRep vertexRep, Pick pick) {
		TablePerspective mappingPerspective = handler.getMappingPerspective();
		if (vertexRep.getType() == EPathwayVertexType.gene && mappingPerspective != null
				&& pick.getPickingMode() == PickingMode.CLICKED) {
			DataSetSelectedEvent dataSetSelectedEvent = new DataSetSelectedEvent(mappingPerspective);
			dataSetSelectedEvent.setSender(this);
			EventPublisher.trigger(dataSetSelectedEvent);
		}
	}

}
