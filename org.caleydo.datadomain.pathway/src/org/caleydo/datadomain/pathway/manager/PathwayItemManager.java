package org.caleydo.datadomain.pathway.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.caleydo.core.manager.AManager;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.edge.PathwayReactionEdgeRep;
import org.caleydo.datadomain.pathway.graph.item.edge.PathwayRelationEdge;
import org.caleydo.datadomain.pathway.graph.item.edge.PathwayRelationEdgeRep;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertex;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * The element manager is in charge for handling the items. Items are vertices
 * and edges. The class is implemented as a Singleton.
 * 
 * @author Marc Streit
 */
public class PathwayItemManager
	extends AManager<PathwayVertex>
	implements Serializable {

	private static final long serialVersionUID = 1L;

	private static PathwayItemManager pathwayItemManager;

	// TODO: replace these hash maps by GenomeIDManager
	private HashMap<Integer, PathwayVertex> hashDavidIdToPathwayVertexGraphItem;
	private HashMap<PathwayVertex, Integer> hashPathwayVertexGraphItemToDavidId;

	private HashMap<Integer, PathwayVertexRep> hashIDToPathwayVertexGraphItemRep;

	private PathwayItemManager() {

	}

	/**
	 * Returns the pathway item manager as a singleton object. When first called
	 * the manager is created (lazy).
	 * 
	 * @return singleton PathwayItemManager instance
	 */
	public static PathwayItemManager get() {
		if (pathwayItemManager == null) {
			pathwayItemManager = new PathwayItemManager();
			pathwayItemManager.init();
		}
		return pathwayItemManager;
	}

	private void init() {
		hashDavidIdToPathwayVertexGraphItem = new HashMap<Integer, PathwayVertex>();
		hashPathwayVertexGraphItemToDavidId = new HashMap<PathwayVertex, Integer>();
		hashIDToPathwayVertexGraphItemRep = new HashMap<Integer, PathwayVertexRep>();
	}

	public PathwayVertex createVertex(final String name, final String type,
			final String externalLink) {

		PathwayVertex pathwayVertex = new PathwayVertex(name, type, externalLink);

		hashItems.put(pathwayVertex.getID(), pathwayVertex);

		PathwayManager.get().getRootPathway().addVertex(pathwayVertex);

		return pathwayVertex;
	}

	public ArrayList<PathwayVertex> createVertexGene(final String name, final String type,
			final String externalLink, final Set<Integer> DataTableDavidId) {

		ArrayList<PathwayVertex> alGraphItems = new ArrayList<PathwayVertex>();
		PathwayVertex tmpGraphItem = null;
		for (int iDavidId : DataTableDavidId) {

			// Do not create a new vertex if it is already registered
			if (hashDavidIdToPathwayVertexGraphItem.containsKey(iDavidId)) {
				tmpGraphItem = hashDavidIdToPathwayVertexGraphItem.get(iDavidId);
			}
			else {
				tmpGraphItem = createVertex(name, type, externalLink);

				hashDavidIdToPathwayVertexGraphItem
						.put(iDavidId, (PathwayVertex) tmpGraphItem);
				hashPathwayVertexGraphItemToDavidId
						.put((PathwayVertex) tmpGraphItem, iDavidId);
			}

			if (tmpGraphItem == null)
				throw new IllegalStateException("New pathway vertex is null");

			alGraphItems.add(tmpGraphItem);
		}

		return alGraphItems;
	}

	public PathwayVertexRep createVertexRep(final PathwayGraph parentPathway,
			final ArrayList<PathwayVertex> alVertexGraphItem, final String sName,
			final String sShapeType, final short shHeight, final short shWidth,
			final short shXPosition, final short shYPosition) {

		PathwayVertexRep pathwayVertexRep = new PathwayVertexRep(sName, sShapeType, shHeight,
				shWidth, shXPosition, shYPosition);

		// registerItem(pathwayVertexRep);

		parentPathway.addVertex(pathwayVertexRep);
		pathwayVertexRep.addPathway(parentPathway);

		for (PathwayVertex parentVertex : alVertexGraphItem) {
			pathwayVertexRep.addPathwayVertex(parentVertex);
			parentVertex.addPathwayVertexRep(pathwayVertexRep);
		}

		hashIDToPathwayVertexGraphItemRep.put(pathwayVertexRep.getID(),
				(PathwayVertexRep) pathwayVertexRep);

		return pathwayVertexRep;
	}

	public PathwayVertexRep createVertexRep(final PathwayGraph parentPathway,
			final ArrayList<PathwayVertex> alVertexGraphItem, final String sName,
			final String sShapeType, final String sCoords) {

		PathwayVertexRep pathwayVertexRep = new PathwayVertexRep(sName, sShapeType, sCoords);

		// registerItem(pathwayVertexRep);

		parentPathway.addVertex(pathwayVertexRep);
		pathwayVertexRep.addPathway(parentPathway);

		for (PathwayVertex parentVertex : alVertexGraphItem) {
			pathwayVertexRep.addPathwayVertex(parentVertex);
			parentVertex.addPathwayVertexRep(pathwayVertexRep);
		}

		hashIDToPathwayVertexGraphItemRep.put(pathwayVertexRep.getID(),
				(PathwayVertexRep) pathwayVertexRep);

		return pathwayVertexRep;
	}

	public PathwayRelationEdge createRelationEdge(final List<PathwayVertex> sourceVertices,
			final List<PathwayVertex> targetVertices, final String type) {

		PathwayRelationEdge pathwayRelationEdge = new PathwayRelationEdge(type);

		DirectedGraph<PathwayVertex, DefaultEdge> rootPathway = PathwayManager.get()
				.getRootPathway();

		return pathwayRelationEdge;
	}

	public void createRelationEdgeRep(final PathwayGraph parentPathway,
			final PathwayRelationEdge pathwayRelationEdge,
			final PathwayVertexRep sourceVertexRep, final PathwayVertexRep targetVertexRep) {

		PathwayRelationEdgeRep pathwayRelationEdgeRep = new PathwayRelationEdgeRep();

		// Add edge to pathway representation
		try {
			parentPathway.addEdge(sourceVertexRep, targetVertexRep, pathwayRelationEdgeRep);
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}

	public PathwayReactionEdgeRep createReactionEdgeRep(final PathwayGraph parentPathway,
			final PathwayVertexRep sourceVertexRep, final PathwayVertexRep targetVertexRep) {

		PathwayReactionEdgeRep pathwayReactionEdgeRep = new PathwayReactionEdgeRep();
		
		// Add edge to pathway representation
		try {
			parentPathway.addEdge(sourceVertexRep, targetVertexRep, pathwayReactionEdgeRep);
		}
		catch (Exception e) {
			System.out.println(e);
		}
		
		return pathwayReactionEdgeRep;
	}

	// TODO: throw exception
	public final PathwayVertex getPathwayVertexGraphItemByDavidId(final int iDavidId) {
		PathwayManager.get().waitUntilPathwayLoadingIsFinished();

		if (hashDavidIdToPathwayVertexGraphItem.containsKey(iDavidId))
			return hashDavidIdToPathwayVertexGraphItem.get(iDavidId);

		return null;
	}

	public int getDavidIdByPathwayVertex(final PathwayVertex pathwayVertexGraphItem) {
		PathwayManager.get().waitUntilPathwayLoadingIsFinished();

		if (hashPathwayVertexGraphItemToDavidId.containsKey(pathwayVertexGraphItem))
			return hashPathwayVertexGraphItemToDavidId.get(pathwayVertexGraphItem);

		return -1;
	}

	public PathwayVertexRep getPathwayVertexRep(int iID) {
		PathwayManager.get().waitUntilPathwayLoadingIsFinished();

		if (!hashIDToPathwayVertexGraphItemRep.containsKey(iID))
			throw new IllegalArgumentException("Requested pathway vertex representation ID "
					+ iID + " does not exist!");

		return hashIDToPathwayVertexGraphItemRep.get(iID);
	}
}
