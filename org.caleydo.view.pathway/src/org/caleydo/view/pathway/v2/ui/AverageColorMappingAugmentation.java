/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.pathway.v2.ui;

import java.util.Arrays;
import java.util.List;

import org.caleydo.core.data.perspective.table.Average;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.mapping.UpdateColorMappingEvent;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.listener.ESampleMappingMode;
import org.caleydo.datadomain.pathway.listener.PathwayMappingEvent;
import org.caleydo.datadomain.pathway.listener.SampleMappingModeEvent;

/**
 * @author Christian
 *
 */
public class AverageColorMappingAugmentation extends GLElement {

	protected static final int NO_MAPPING_ICON_SIZE = 8;
	protected static final Color NO_MAPPING_ICON_COLOR = new Color(0.3f, 0.3f, 0.3f, 0.7f);

	protected APathwayElementRepresentation pathwayRepresentation;
	protected TablePerspective mappingPerspective;
	protected ESampleMappingMode sampleMappingMode = ESampleMappingMode.ALL;
	protected String eventSpace;

	public AverageColorMappingAugmentation(APathwayElementRepresentation pathwayRepresentation) {
		this.pathwayRepresentation = pathwayRepresentation;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		if (mappingPerspective == null)
			return;

		List<PathwayGraph> pathways = pathwayRepresentation.getPathways();
		for (PathwayGraph pathway : pathways) {
			for (PathwayVertexRep vertexRep : pathway.vertexSet()) {
				if (vertexRep.getType() == EPathwayVertexType.gene) {
					List<Rect> boundsList = pathwayRepresentation.getVertexRepsBounds(vertexRep);
					for (Rect bounds : boundsList) {
						Average avg = getAverageMapping(vertexRep);

						// g.color(1f, 0f, 0f).fillRect(bounds.x(), bounds.y(), bounds.width(), bounds.height());
						if (avg != null) {
							g.color(mappingPerspective.getDataDomain().getTable().getColorMapper()
									.getColor((float) avg.getArithmeticMean()));
							g.fillRect(bounds.x(), bounds.y(), bounds.width(), bounds.height());
						} else {
							float size = bounds.height() * 0.5f;
							g.color(NO_MAPPING_ICON_COLOR).fillRect(bounds.x() + bounds.width() - size,
									bounds.y() + bounds.height() - size, size, size);
						}
					}
				}
			}
		}
	}

	/**
	 * Calculates the average value of the selected samples (taken from {@link #selectedSamplesVA}) selectedSamplesVA.
	 *
	 *
	 * @param vertexRep
	 * @return
	 */
	private Average getAverageMapping(PathwayVertexRep vertexRep) {

		if (mappingPerspective == null)
			return null;

		Average average = null;
		// if (sampleMappingMode == ESampleMappingMode.ALL) {
		average = mappingPerspective.getContainerStatistics().getAverage(PathwayVertexRep.getIdType(),
				Arrays.asList(vertexRep.getID()));
		// } else {

		// Set<Integer> selectedSamples = pathRenderer.getSampleSelectionManager()
		// .getElements(SelectionType.SELECTION);
		// List<Integer> selectedSamplesArray = new ArrayList<Integer>();
		//
		// selectedSamplesArray.addAll(selectedSamples);
		// if (!selectedSamplesArray.isEmpty()) {
		//
		// VirtualArray selectedSamplesVA = new VirtualArray(pathRenderer.getSampleSelectionManager().getIDType(),
		// selectedSamplesArray);
		// GroupList groupList = new GroupList();
		// groupList.append(new Group(selectedSamplesVA.size()));
		// selectedSamplesVA.setGroupList(groupList);
		//
		// average = TablePerspectiveStatistics.calculateAverage(selectedSamplesVA,
		// mappingPerspective.getDataDomain(), idType, ids);
		// if (Double.isNaN(average.getArithmeticMean()))
		// average = null;
		// }
		// }
		return average;
	}

	@ListenTo
	public void onMapTablePerspective(PathwayMappingEvent event) {
		if (event.getEventSpace().equals(eventSpace)) {
			mappingPerspective = event.getTablePerspective();
			repaintAll();
		}
	}

	@ListenTo
	public void onUpdateColorMapping(UpdateColorMappingEvent event) {
		repaint();
	}

	@ListenTo
	public void onSampleMappingModeChanged(SampleMappingModeEvent event) {
		if (event.getEventSpace().equals(eventSpace)) {
			sampleMappingMode = event.getSampleMappingMode();
			repaint();
		}
	}

	/**
	 * @param sampleMappingMode
	 *            setter, see {@link sampleMappingMode}
	 */
	public void setSampleMappingMode(ESampleMappingMode sampleMappingMode) {
		this.sampleMappingMode = sampleMappingMode;
		repaint();
	}

	/**
	 * @param mappingPerspective
	 *            setter, see {@link mappingPerspective}
	 */
	public void setMappingPerspective(TablePerspective mappingPerspective) {
		this.mappingPerspective = mappingPerspective;
		repaintAll();
	}

	/**
	 * @param eventSpace
	 *            setter, see {@link eventSpace}
	 */
	public void setEventSpace(String eventSpace) {
		this.eventSpace = eventSpace;
	}

	/**
	 * @return the eventSpace, see {@link #eventSpace}
	 */
	public String getEventSpace() {
		return eventSpace;
	}

}
