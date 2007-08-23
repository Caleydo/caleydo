package cerberus.manager.data.pathway;

import java.util.HashMap;

import org.geneview.graph.EGraphItemHierarchy;
import org.geneview.graph.EGraphItemProperty;
import org.geneview.graph.IGraph;
import org.geneview.graph.IGraphItem;

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
			final String sExternalLink) {
		
		// Check if same vertex is already contained
		if (hashPathwayNameToGraphItem.containsKey(sName))
		{
			// Return existing vertex
			return hashPathwayNameToGraphItem.get(sName);
		}

		int iGeneratedId = createId(ManagerObjectType.PATHWAY_VERTEX);
		
    	IGraphItem pathwayVertex = new PathwayVertexGraphItem(
    			iGeneratedId, sName, sType, sExternalLink);
    	
    	refGeneralManager.getSingelton().getPathwayManager()
				.getRootPathway().addItem(pathwayVertex);
    	
    	hashPathwayNameToGraphItem.put(sName, pathwayVertex);
    	
    	return pathwayVertex;
	}
	
	public void createVertexRep(
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
		
		pathwayVertexRep.addItem(pathwayVertex, EGraphItemProperty.ALIAS_PARENT);
		pathwayVertexRep.addGraph(parentPathway, EGraphItemHierarchy.GRAPH_PARENT);
		
		pathwayVertex.addItem(pathwayVertexRep, EGraphItemProperty.ALIAS_CHILD);
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
