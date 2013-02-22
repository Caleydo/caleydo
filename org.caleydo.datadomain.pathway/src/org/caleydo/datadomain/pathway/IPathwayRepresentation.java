/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.datadomain.pathway;

import java.awt.geom.Rectangle2D;
import java.util.List;

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
	 * primary one is returned. To retrieve all locations, use {@link #getVertexRepLocations(PathwayVertexRep)}.
	 *
	 * @param vertexRep
	 * @return The location of the specified vertexRep or null, if the vertexRep does not exist.
	 */
	public Rectangle2D getVertexRepLocation(PathwayVertexRep vertexRep);

	/**
	 * Gets all locations of the specified vertexRep as rectangles. The coordinates are specified in pixels with the
	 * origin of the coordinate system at the left top.
	 *
	 * @param vertexRep
	 * @return The locations of the specified vertexRep or null, if the vertexRep does not exist.
	 */
	public List<Rectangle2D> getVertexRepLocations(PathwayVertexRep vertexRep);

	/**
	 * Adds the specified context menu item to the context menu of all vertexReps in this pathway. The selected
	 * vertexRep is set in the item using {@link VertexRepBasedContextMenuItem#setVertexRep(PathwayVertexRep)}.
	 *
	 * @param item
	 */
	public void addVertexRepBasedContextMenuItem(VertexRepBasedContextMenuItem item);
}
