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
package org.caleydo.datadomain.pathway.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertex;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexGroupRep;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * The element manager is in charge for handling the items. Items are vertices and edges. The class is implemented as a
 * Singleton.
 *
 * @author Marc Streit
 */
public class PathwayItemManager {

	private volatile static PathwayItemManager pathwayItemManager;

	/** The mapping manager for genes */
	private final IDMappingManager geneIDMappingManager;
	/** The davidIDType ID Type */
	private final IDType davidIDType;
	/** The final ID type for the {@link PathwayVertex}s */
	private final IDType pathwayVertexIDType;
	/** The id type for the {@link PathwayVertexRep}s */
	private final IDType pathwayVertexRepIDType;

	private final Map<Integer, PathwayVertexRep> hashPathwayVertexRepIDToPathwayVertexRep;
	private final Map<Integer, PathwayVertex> hashVertexIDToVertex;

	private PathwayItemManager() {
		geneIDMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(
				IDCategory.getIDCategory(EGeneIDTypes.GENE.name()));
		davidIDType = IDType.getIDType(EGeneIDTypes.DAVID.name());
		pathwayVertexIDType = IDType.getIDType(EGeneIDTypes.PATHWAY_VERTEX.name());
		pathwayVertexRepIDType = IDType.getIDType(EGeneIDTypes.PATHWAY_VERTEX_REP.name());

		if (!geneIDMappingManager.hasMapping(davidIDType, pathwayVertexIDType)) {
			geneIDMappingManager.createMap(davidIDType, pathwayVertexIDType, true, true);
		}
		if (!geneIDMappingManager.hasMapping(pathwayVertexIDType, pathwayVertexRepIDType)) {
			geneIDMappingManager.createMap(pathwayVertexIDType, pathwayVertexRepIDType, true, true);
		}
		hashPathwayVertexRepIDToPathwayVertexRep = new HashMap<Integer, PathwayVertexRep>();
		hashVertexIDToVertex = new HashMap<Integer, PathwayVertex>();
	}

	/**
	 * Returns the pathway item manager as a singleton object. When first called the manager is created (lazy).
	 *
	 * @return singleton PathwayItemManager instance
	 */
	public static PathwayItemManager get() {
		if (pathwayItemManager == null) {
			synchronized (PathwayItemManager.class) {
				if (pathwayItemManager == null)
					pathwayItemManager = new PathwayItemManager();
			}
		}
		return pathwayItemManager;
	}

	/**
	 * Creates a general (aka non-gene) pathway vertex
	 *
	 * @param name
	 *            the name of the vertex
	 * @param type
	 *            the type of the vertex, e.g. "gene"
	 * @param externalLink
	 *            a link to a web resource about the vertex
	 */
	public PathwayVertex createVertex(final String name, final String type, final String externalLink) {

		PathwayVertex pathwayVertex = new PathwayVertex(name, type, externalLink);

		hashVertexIDToVertex.put(pathwayVertex.getID(), pathwayVertex);

		PathwayManager.get().getRootPathway().addVertex(pathwayVertex);

		return pathwayVertex;
	}

	/**
	 * Creates a vertex that can be mapped to a gene.
	 *
	 * @param name
	 *            the name of the vertex
	 * @param type
	 *            the type of the vertex, e.g. "gene"
	 * @param externalLink
	 *            a link to a web resource about the vertex
	 * @param mappingDavidIDs
	 *            the davidIDType ids that map to this vertex
	 * @return
	 */
	public List<PathwayVertex> createGeneVertex(final String name, final String type, final String externalLink,
			final Set<Integer> mappingDavidIDs) {

		ArrayList<PathwayVertex> vertices = new ArrayList<PathwayVertex>();
		PathwayVertex vertex = null;
		for (int davidId : mappingDavidIDs) {

			// Do not create a new vertex if it is already registered
			Set<Integer> existingVerticeIDs = geneIDMappingManager
					.getIDAsSet(davidIDType, pathwayVertexIDType, davidId);

			if (existingVerticeIDs != null && !existingVerticeIDs.isEmpty()) {

				geneIDMappingManager.getIDAsSet(davidIDType, pathwayVertexIDType, davidId);
				if (existingVerticeIDs.size() > 1) {
					Logger.log(new Status(IStatus.WARNING, this.toString(),
							"There was a multi-mapping from vertex to davidIDType. This shouldn't happen. Using only the first hit."));
				}
				Integer vertexID = existingVerticeIDs.iterator().next();
				vertex = hashVertexIDToVertex.get(vertexID);
			} else {
				vertex = createVertex(name, type, externalLink);
				geneIDMappingManager.addMapping(davidIDType, davidId, pathwayVertexIDType, vertex.getID());
				// hashDavidIdToPathwayVertexGraphItem.put(davidId,
				// (PathwayVertex) vertex);
				// hashPathwayVertexGraphItemToDavidId.put((PathwayVertex)
				// vertex, davidId);
			}

			if (vertex == null)
				throw new IllegalStateException("New pathway vertex is null");

			vertices.add(vertex);
		}

		return vertices;
	}

	/**
	 * Creates a rectangular {@link PathwayVertexRep} and registers it in the ID manager
	 *
	 * @param parentPathway
	 *            the pathway the rep belongs to
	 * @param vertices
	 *            the vertices this rep is associated with
	 * @param name
	 * @param shapeType
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public PathwayVertexRep createVertexRep(final PathwayGraph parentPathway, final List<PathwayVertex> vertices,
			final String name, final String shapeType, final short x, final short y, final short width,
			final short height) {

		PathwayVertexRep pathwayVertexRep = new PathwayVertexRep(name, shapeType, x, y, width, height);

		// registerItem(pathwayVertexRep);

		registerRep(parentPathway, vertices, pathwayVertexRep);

		return pathwayVertexRep;
	}

	/**
	 * Creates polygonal a {@link PathwayVertexRep} and registers it in the ID manager. The shape of this vertex is
	 * specified as a list of points in the coords parameter.
	 *
	 * @param parentPathway
	 *            the pathway the rep belongs to
	 * @param vertices
	 *            the vertices this rep is associated with
	 * @param name
	 * @param shapeType
	 * @param coords
	 *            a string with the coordinates comma separated. e.g. 13,25,15,26,... alternating between x and y values
	 * @return
	 */
	public PathwayVertexRep createVertexRep(final PathwayGraph parentPathway, final List<PathwayVertex> vertices,
			final String name, final String shapeType, final String coords) {

		PathwayVertexRep pathwayVertexRep = new PathwayVertexRep(name, shapeType, coords);

		registerRep(parentPathway, vertices, pathwayVertexRep);

		return pathwayVertexRep;
	}

	/**
	 * Registers a pathwayVertexRep to it's pathway, its vertices and to the id mapping manager
	 *
	 * @param parentPathway
	 * @param vertices
	 * @param pathwayVertexRep
	 */
	private void registerRep(PathwayGraph parentPathway, final List<PathwayVertex> vertices,
			PathwayVertexRep pathwayVertexRep) {
		parentPathway.addVertex(pathwayVertexRep);
		pathwayVertexRep.setPathway(parentPathway);

		for (PathwayVertex vertex : vertices) {
			pathwayVertexRep.addPathwayVertex(vertex);
			vertex.addPathwayVertexRep(pathwayVertexRep);
			geneIDMappingManager.addMapping(pathwayVertexIDType, vertex.getID(), pathwayVertexRepIDType,
					pathwayVertexRep.getID());
		}

		hashPathwayVertexRepIDToPathwayVertexRep.put(pathwayVertexRep.getID(), pathwayVertexRep);
	}

	public PathwayVertexGroupRep createVertexGroupRep(final PathwayGraph parentPathway) {

		PathwayVertexGroupRep pathwayVertexGroupRep = new PathwayVertexGroupRep();

		// registerItem(pathwayVertexRep);

		parentPathway.addVertex(pathwayVertexGroupRep);
		pathwayVertexGroupRep.setPathway(parentPathway);

		hashPathwayVertexRepIDToPathwayVertexRep.put(pathwayVertexGroupRep.getID(), pathwayVertexGroupRep);

		return pathwayVertexGroupRep;
	}

	// TODO: throw exception
	public List<PathwayVertex> getPathwayVertexByDavidId(final int davidId) {
		PathwayManager.get().waitUntilPathwayLoadingIsFinished();

		Set<Integer> vertexIDs = geneIDMappingManager.getIDAsSet(davidIDType, pathwayVertexIDType, davidId);
		if (vertexIDs == null)
			return null;

		List<PathwayVertex> vertices = new ArrayList<PathwayVertex>();
		for (Integer vertexID : vertexIDs) {
			vertices.add(hashVertexIDToVertex.get(vertexID));
		}
		return vertices;

	}

	/**
	 * Returns a davidIDType ID for the specified <code>PathwayVertex</code>. If no davidIDType ID can be found, -1 is
	 * returned.
	 *
	 * @param pathwayVertexIDType
	 * @return the davidID or null if no mapping was found
	 */
	public Set<Integer> getDavidIdByPathwayVertex(final PathwayVertex pathwayVertex) {
		PathwayManager.get().waitUntilPathwayLoadingIsFinished();
		Set<Integer> davidIDs = geneIDMappingManager
				.getIDAsSet(pathwayVertexIDType, davidIDType, pathwayVertex.getID());

		return davidIDs;
	}

	/**
	 * Returns all davidIDType IDs of all vertices stored in the <code>PathwayVertexRep</code>. If no davidIDs can be
	 * resolved an empty list is returned.
	 */
	public ArrayList<Integer> getDavidIDsByPathwayVertexRep(PathwayVertexRep pathwayVertexRep) {
		ArrayList<Integer> davidIDs = new ArrayList<Integer>();
		for (PathwayVertex vertex : pathwayVertexRep.getPathwayVertices()) {
			Set<Integer> tempDavids = getDavidIdByPathwayVertex(vertex);
			if (tempDavids != null)
				davidIDs.addAll(tempDavids);
		}
		return davidIDs;
	}

	public PathwayVertexRep getPathwayVertexRep(int iID) {
		PathwayManager.get().waitUntilPathwayLoadingIsFinished();

		if (!hashPathwayVertexRepIDToPathwayVertexRep.containsKey(iID))
			throw new IllegalArgumentException("Requested pathway vertex representation ID " + iID + " does not exist!");

		return hashPathwayVertexRepIDToPathwayVertexRep.get(iID);
	}
}
