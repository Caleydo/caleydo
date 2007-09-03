package cerberus.manager.data;

import java.util.ArrayList;

import org.geneview.graph.IGraph;
import org.geneview.graph.IGraphItem;

import cerberus.manager.IGeneralManager;

public interface IPathwayItemManager 
extends IGeneralManager {

	public IGraphItem createVertex(
			final String sName,
			final String sType,
			final String sExternalLink,
			final String sReactionId);
	
	public IGraphItem createVertexRep(
			final IGraph parentPathway,
			final ArrayList<IGraphItem> alVertexGraphItem,
			final String sName, 
			final String sShapeType, 
			final int iHeight, 
			final int iWidth,
			final int iXPosition, 
			final int iYPosition);
	
	public IGraphItem createRelationEdge(
			final IGraphItem graphItemIn, 
			final IGraphItem graphItemOut,
			final String sType);
			
	public void createRelationEdgeRep(
			final IGraph parentPathway,
			final IGraphItem pathwayRelationEdge,
			final IGraphItem graphItemIn, 
			final IGraphItem graphItemOut);
	
	public IGraphItem createReactionEdge(
			final IGraph parentPathway,
			final String sReactionName, 
			final String sReactionType);
}
