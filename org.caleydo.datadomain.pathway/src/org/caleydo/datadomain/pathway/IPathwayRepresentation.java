/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway;

import java.util.List;

import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;

/**
 * Interface for classes that represent one or more pathway(s) or parts of pathways. Specifies methods for accessing
 * pathway related information and extending the visualization.
 *
 * @author Christian Partl
 *
 */
public interface IPathwayRepresentation {

	/**
	 * Gets the represented pathway. If the visualization represents multiple pathways, only the primary pathway is
	 * returned. To access all pathways use {@link #getPathways()}.
	 *
	 * @return
	 */
	public PathwayGraph getPathway();

	/**
	 * Gets all represented pathways.
	 *
	 * @return
	 */
	public List<PathwayGraph> getPathways();

	/**
	 * Gets the location of the specified vertexRep as rectangle. The coordinates are specified in pixels with the
	 * origin of the coordinate system at the left top. If there are multiple locations for the vertexRep, just the
	 * primary one is returned. To retrieve all locations, use {@link #getVertexRepsBounds(PathwayVertexRep)}.
	 *
	 * @param vertexRep
	 * @return The location of the specified vertexRep or null, if the vertexRep does not exist.
	 */
	public Rect getVertexRepBounds(PathwayVertexRep vertexRep);

	/**
	 * Gets all locations of the specified vertexRep as rectangles. The coordinates are specified in pixels with the
	 * origin of the coordinate system at the left top.
	 *
	 * @param vertexRep
	 * @return The locations of the specified vertexRep or null, if the vertexRep does not exist.
	 */
	public List<Rect> getVertexRepsBounds(PathwayVertexRep vertexRep);

	/**
	 * Adds the specified context menu item to the context menu of all vertexReps in this pathway. The selected
	 * vertexRep is set in the item using {@link VertexRepBasedContextMenuItem#setVertexRep(PathwayVertexRep)}.
	 *
	 * @param item
	 */
	public void addVertexRepBasedContextMenuItem(VertexRepBasedContextMenuItem item);

	/**
	 * Adds a vertex rep based event that should be triggered when picking a vertex rep in the specified mode.
	 *
	 * @param eventFactory
	 * @param pickingMode
	 */
	public void addVertexRepBasedSelectionEvent(IVertexRepBasedEventFactory eventFactory, PickingMode pickingMode);

}
