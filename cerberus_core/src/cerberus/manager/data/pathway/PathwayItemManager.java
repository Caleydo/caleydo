package cerberus.manager.data.pathway;

import java.util.HashMap;

import org.geneview.graph.EGraphItemHierarchy;
import org.geneview.graph.EGraphItemProperty;
import org.geneview.graph.IGraph;
import org.geneview.graph.IGraphItem;

import cerberus.data.graph.item.edge.PathwayReactionEdgeGraphItem;
import cerberus.data.graph.item.edge.PathwayReactionEdgeGraphItemRep;
import cerberus.data.graph.item.edge.PathwayRelationEdgeGraphItem;
import cerberus.data.graph.item.edge.PathwayRelationEdgeGraphItemRep;
import cerberus.data.graph.item.vertex.PathwayVertexGraphItem;
import cerberus.data.graph.item.vertex.PathwayVertexGraphItemRep;
import cerberus.manager.IGeneralManager;
import cerberus.manager.base.AAbstractManager;
import cerberus.manager.data.IPathwayItemManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.type.ManagerType;

/**
 * The element manager is in charge for handling the items. Items are
 * vertices and edges. The class is implemented as a Singleton.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 */
public class PathwayItemManager
extends AAbstractManager
implements IPathwayItemManager {

	private HashMap<String, IGraphItem> hashPathwayNameToGraphItem;
	
	/**
	 * Constructor
	 * 
	 */
	public PathwayItemManager(final IGeneralManager refGeneralManager) {
		
		super( refGeneralManager, 
				IGeneralManager.iUniqueId_TypeOffset_Pathways_Vertex,
				ManagerType.DATA_PATHWAY_ELEMENT );
	
		hashPathwayNameToGraphItem = new HashMap<String, IGraphItem>();
	}
	
	public IGraphItem createVertex(
			final String sName,
			final String sType,
			final String sExternalLink,
			final String sReactionId) {
		
		// Check if same vertex is already contained
		if (hashPathwayNameToGraphItem.containsKey(sName))
		{
			// Return existing vertex
			return hashPathwayNameToGraphItem.get(sName);
		}

		int iGeneratedId = createId(ManagerObjectType.PATHWAY_VERTEX);
		
    	IGraphItem pathwayVertex = new PathwayVertexGraphItem(
    			iGeneratedId, sName, sType, sExternalLink, sReactionId);
    	
    	refGeneralManager.getSingelton().getPathwayManager()
				.getRootPathway().addItem(pathwayVertex);
    	
    	hashPathwayNameToGraphItem.put(sName, pathwayVertex);
    	
    	return pathwayVertex;
	}
	
	public IGraphItem createVertexRep(
			final IGraph parentPathway,
			final IGraphItem  pathwayVertex,
			final String sName, 
			final String sShapeType, 
			final int iHeight, 
			final int iWidth,
			final int iXPosition, 
			final int iYPosition) {
		
		int iGeneratedId = createId(ManagerObjectType.PATHWAY_VERTEX_REP);
		IGraphItem pathwayVertexRep = new PathwayVertexGraphItemRep(
				iGeneratedId, sName, sShapeType, iHeight, iWidth,
				iXPosition, iYPosition);
		
		parentPathway.addItem(pathwayVertexRep);
		
		pathwayVertexRep.addItem(pathwayVertex, 
				EGraphItemProperty.ALIAS_PARENT);
		pathwayVertexRep.addGraph(parentPathway, 
				EGraphItemHierarchy.GRAPH_PARENT);
		
		pathwayVertex.addItem(pathwayVertexRep, 
				EGraphItemProperty.ALIAS_CHILD);
		
		return pathwayVertexRep;
	}
	
	public IGraphItem createRelationEdge(
			final IGraphItem graphItemIn, 
			final IGraphItem graphItemOut,
			final String sType) {
		
		int iGeneratedId = createId(ManagerObjectType.PATHWAY_EDGE);
		IGraphItem pathwayRelationEdge = new PathwayRelationEdgeGraphItem(
				iGeneratedId, sType);
		
		IGraph rootPathway = refGeneralManager.getSingelton()
				.getPathwayManager().getRootPathway();
		
		// Add edge to root pathway
		rootPathway.addItem(pathwayRelationEdge);
		
		// Add root pathway to edge
		pathwayRelationEdge.addGraph(rootPathway, 
				EGraphItemHierarchy.GRAPH_PARENT);
		
		// Add connection to incoming and outgoing items
		pathwayRelationEdge.addItemDoubleLinked(graphItemIn, 
				EGraphItemProperty.INCOMING);
		pathwayRelationEdge.addItemDoubleLinked(graphItemOut, 
				EGraphItemProperty.OUTGOING);
		
		return pathwayRelationEdge;
	}
	
	public void createRelationEdgeRep(
			final IGraph parentPathway,
			final IGraphItem pathwayRelationEdge,
			final IGraphItem graphItemIn, 
			final IGraphItem graphItemOut) {
		
		int iGeneratedId = createId(ManagerObjectType.PATHWAY_EDGE_REP);
		IGraphItem pathwayRelationEdgeRep = 
			new PathwayRelationEdgeGraphItemRep(iGeneratedId);

		// Add edge to pathway representation
		parentPathway.addItem(pathwayRelationEdgeRep);
		
		// Add pathway representation to created edge
		pathwayRelationEdgeRep.addGraph(parentPathway, 
				EGraphItemHierarchy.GRAPH_PARENT);
		
		// Add edge data to representation as ALIAS_PARENT
		pathwayRelationEdgeRep.addItem(pathwayRelationEdge, 
				EGraphItemProperty.ALIAS_PARENT);

		// Add edge representation to data as ALIAS_CHILD
		pathwayRelationEdge.addItem(pathwayRelationEdgeRep, 
				EGraphItemProperty.ALIAS_CHILD);
		
		// Add connection to incoming and outgoing items
		pathwayRelationEdgeRep.addItemDoubleLinked(graphItemIn, 
				EGraphItemProperty.INCOMING);
		pathwayRelationEdgeRep.addItemDoubleLinked(graphItemOut, 
				EGraphItemProperty.OUTGOING);	
	}
	
	public IGraphItem createReactionEdge(
			final IGraph parentPathway,
			final String sReactionName, 
			final String sReactionType) {
		
		// Create edge (data)
		int iGeneratedId = createId(ManagerObjectType.PATHWAY_EDGE_REP);
		IGraphItem pathwayReactionEdge =
			new PathwayReactionEdgeGraphItem(iGeneratedId, 
					sReactionName, sReactionType);
		
		// Create edge representation
		iGeneratedId = createId(ManagerObjectType.PATHWAY_EDGE_REP);
		IGraphItem pathwayReactionEdgeRep =
			new PathwayReactionEdgeGraphItemRep(iGeneratedId);
	
		IGraph rootPathway = refGeneralManager.getSingelton()
			.getPathwayManager().getRootPathway();
		
		// Add edge to root pathway
		rootPathway.addItem(pathwayReactionEdge);
		
		// Add root pathway to edge
		pathwayReactionEdge.addGraph(rootPathway, 
				EGraphItemHierarchy.GRAPH_PARENT);
		
		// Add edge to pathway representation
		parentPathway.addItem(pathwayReactionEdgeRep);
		
		// Add pathway representation to created edge
		pathwayReactionEdgeRep.addGraph(parentPathway, 
				EGraphItemHierarchy.GRAPH_PARENT);
		
		// Add edge data to representation as ALIAS_PARENT
		pathwayReactionEdgeRep.addItem(pathwayReactionEdge, 
				EGraphItemProperty.ALIAS_PARENT);

		// Add edge representation to data as ALIAS_CHILD
		pathwayReactionEdge.addItem(pathwayReactionEdgeRep, 
				EGraphItemProperty.ALIAS_CHILD);
		
		return pathwayReactionEdgeRep;
	}

	@Override
	public Object getItem(int itemId) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasItem(int itemId) {

		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean registerItem(Object registerItem, int itemId,
			ManagerObjectType type) {

		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int size() {

		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean unregisterItem(int itemId, ManagerObjectType type) {

		// TODO Auto-generated method stub
		return false;
	}
}
