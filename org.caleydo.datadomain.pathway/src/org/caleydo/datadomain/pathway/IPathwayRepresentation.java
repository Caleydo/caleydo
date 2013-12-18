/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway;

import java.util.List;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
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
	 * Gets the bounds of the whole pathway representation as rectangle. The coordinates are specified in pixels with
	 * the origin of the coordinate system at the left top.
	 *
	 * @return The bounds of the pathway representation.
	 */
	public Rect getPathwayBounds();

	/**
	 * Adds the specified context menu item to the context menu of all vertexReps in this pathway. The selected
	 * vertexRep is set in the item using {@link VertexRepBasedContextMenuItem#setVertexRep(PathwayVertexRep)}.
	 *
	 * @param item
	 */
	public void addVertexRepBasedContextMenuItem(VertexRepBasedContextMenuItem item);

	/**
	 * Adds a {@link IVertexRepSelectionListener} to the representation. The representation then calls
	 * {@link IVertexRepSelectionListener#onSelect(PathwayVertexRep, org.caleydo.core.view.opengl.picking.Pick)} when a
	 * {@link PathwayVertexRep} was picked.
	 *
	 * @param listener
	 */
	public void addVertexRepSelectionListener(IVertexRepSelectionListener listener);

	/**
	 * @return The minimum width in pixels required by the pathway representation.
	 */
	public float getMinWidth();

	/**
	 * @return The minimum height in pixels required by the pathway representation.
	 */
	public float getMinHeight();

	/**
	 *
	 * @return This pathway representation as {@link GLElement}. Null if not supported.
	 */
	public GLElement asGLElement();

	/**
	 *
	 * @return This pathway representation as {@link AGLView}. Null if not supported.
	 */
	public AGLView asAGLView();

	/**
	 *
	 * @return This pathway representation as {@link ALayoutRenderer}. Null if not supported.
	 */
	public ALayoutRenderer asLayoutRenderer();

	// /**
	// * Adds a vertex rep based event that should be triggered when picking a vertex rep in the specified mode.
	// *
	// * @param eventFactory
	// * @param pickingMode
	// */
	// public void addVertexRepBasedSelectionEvent(IVertexRepBasedEventFactory eventFactory, PickingMode pickingMode);

}
