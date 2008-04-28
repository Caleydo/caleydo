package org.caleydo.core.manager.data;

import java.util.ArrayList;
import java.util.HashMap;

import org.caleydo.core.manager.IManager;
import org.caleydo.util.graph.IGraph;
import org.caleydo.util.graph.IGraphItem;

public interface IPathwayItemManager 
extends IManager
{
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
			final short shHeight, 
			final short shWidth,
			final short shXPosition, 
			final short shYPosition);
	
	public IGraphItem createVertexRep(
			final IGraph parentPathway,
			final IGraphItem parentVertex,
			final String sName, 
			final String sShapeType, 
			final String sCoords);
	
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
	
	/**
	 * Expose HashMap NCBI_GENE_ID ==> PathwayVertexGraphItem
	 * 
	 * @see org.caleydo.core.view.opengl.canvas.heatmap.GLCanvasHeatmap2DColumn
	 * 
	 * @return HashMap NCBI_GENE_ID ==> PathwayVertexGraphItem
	 */
	public HashMap<Integer, Integer> getHashNCBIGeneIdToPathwayVertexGraphItemId();
	
	public int getPathwayVertexGraphItemIdByNCBIGeneId (final int iNCBIGeneId);
}
