package org.geneview.core.manager.data.pathway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.geneview.util.graph.EGraphItemHierarchy;
import org.geneview.util.graph.EGraphItemProperty;
import org.geneview.util.graph.IGraph;
import org.geneview.util.graph.IGraphItem;

import org.geneview.core.data.graph.item.edge.PathwayReactionEdgeGraphItem;
import org.geneview.core.data.graph.item.edge.PathwayReactionEdgeGraphItemRep;
import org.geneview.core.data.graph.item.edge.PathwayRelationEdgeGraphItem;
import org.geneview.core.data.graph.item.edge.PathwayRelationEdgeGraphItemRep;
import org.geneview.core.data.graph.item.vertex.EPathwayVertexType;
import org.geneview.core.data.graph.item.vertex.PathwayVertexGraphItem;
import org.geneview.core.data.graph.item.vertex.PathwayVertexGraphItemRep;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.base.AAbstractManager;
import org.geneview.core.manager.data.IPathwayItemManager;
import org.geneview.core.manager.type.ManagerObjectType;
import org.geneview.core.manager.type.ManagerType;
import org.geneview.core.util.system.StringConversionTool;
//import org.geneview.core.manager.ILoggerManager.LoggerType;

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

	private HashMap<Integer, IGraphItem> hashVertexIdToGraphItem;
	
	private HashMap<String, IGraphItem> hashPathwayNameToGraphItem;
	
	private HashMap<Integer, Integer> hashNCBIGeneIdToPathwayVertexGraphItemId;
	private boolean bHashNCBIGeneIdToPathwayVertexGraphItemIdInvalid = true;
	
	/**
	 * Constructor
	 * 
	 */
	public PathwayItemManager(final IGeneralManager refGeneralManager) {
		
		super( refGeneralManager, 
				IGeneralManager.iUniqueId_TypeOffset_Pathways_Vertex,
				ManagerType.DATA_PATHWAY_ELEMENT );
	
		hashVertexIdToGraphItem = new HashMap<Integer, IGraphItem>();
		hashPathwayNameToGraphItem = new HashMap<String, IGraphItem>();
		hashNCBIGeneIdToPathwayVertexGraphItemId = new HashMap<Integer, Integer>();
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
    	
		hashVertexIdToGraphItem.put(iGeneratedId, pathwayVertex);
    	
    	refGeneralManager.getSingelton().getPathwayManager()
				.getRootPathway().addItem(pathwayVertex);
    	
    	hashPathwayNameToGraphItem.put(sName, pathwayVertex);
    	
    	//Check if vertex is gene 
    	if (sType.equals(EPathwayVertexType.gene.toString()))
    	{
//    		assert bHashNCBIGeneIdToPathwayVertexGraphItemIdInvalid == false : 
//    			"HashMap NCBI GeneId to PathwayVertexGraphItemId might be invalid!";
    		
    		int iGeneId = StringConversionTool.convertStringToInt( 
    				sName.substring(4), 
    				0 );
    		
//    		if ( bHashNCBIGeneIdToPathwayVertexGraphItemIdInvalid ) 
//    		{
//    			refSingelton.logMsg("HashMap NCBI GeneId to PathwayVertexGraphItemId might be invalid! NCBI_GeneId=["
//    					+ sName + "] <key,value>= <" +
//    					iGeneId +"," +
//    					iGeneratedId +">",
//    					LoggerType.MINOR_ERROR_XML);
//    		}
    		
    		// Extract NCBI gene ID and add element 
        	hashNCBIGeneIdToPathwayVertexGraphItemId.put(
        			iGeneId, iGeneratedId);
    	}
    	
    	return pathwayVertex;
	}
	
	public IGraphItem createVertexRep(
			final IGraph parentPathway,
			final ArrayList<IGraphItem> alVertexGraphItem,
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
		
		hashVertexIdToGraphItem.put(iGeneratedId, pathwayVertexRep);
		
		parentPathway.addItem(pathwayVertexRep);
		
		pathwayVertexRep.addGraph(parentPathway, 
				EGraphItemHierarchy.GRAPH_PARENT);

		Iterator<IGraphItem> iterVertexGraphItem = alVertexGraphItem.iterator();
		IGraphItem pathwayVertex;
		while (iterVertexGraphItem.hasNext())
		{
			pathwayVertex = iterVertexGraphItem.next();
			
			pathwayVertexRep.addItem(pathwayVertex, 
				EGraphItemProperty.ALIAS_PARENT);
				
			pathwayVertex.addItem(pathwayVertexRep, 
				EGraphItemProperty.ALIAS_CHILD);
		}
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
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.manager.IGeneralManager#getItem(int)
	 */
	public Object getItem(int iItemId) {

		if (!hashVertexIdToGraphItem.containsKey(iItemId))
			return null;
		
		return hashVertexIdToGraphItem.get(iItemId);
	}
	
	public final HashMap<Integer, Integer> getHashNCBIGeneIdToPathwayVertexGraphItemId() {
		
		assert false : "should not call!!";
		bHashNCBIGeneIdToPathwayVertexGraphItemIdInvalid = false;
		return hashNCBIGeneIdToPathwayVertexGraphItemId;
	}
	
	// TODO: throw exception
	public final int getPathwayVertexGraphItemIdByNCBIGeneId (final int iNCBIGeneId) {
		
		if (hashNCBIGeneIdToPathwayVertexGraphItemId.containsKey(iNCBIGeneId))
			return hashNCBIGeneIdToPathwayVertexGraphItemId.get(iNCBIGeneId);
		
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.manager.IGeneralManager#hasItem(int)
	 */
	public boolean hasItem(int iItemId) {

		if (hashVertexIdToGraphItem.containsKey(iItemId))
			return true;
		
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
