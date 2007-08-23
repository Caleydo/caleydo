package cerberus.manager.data;

import org.geneview.graph.IGraph;
import org.geneview.graph.IGraphItem;

import cerberus.manager.IGeneralManager;

public interface IPathwayItemManager 
extends IGeneralManager {

	public IGraphItem createVertex(
			final String sName,
			final String sType,
			final String sExternalLink);
	
	public void createVertexRep(
			final IGraph parentPathway,
			final IGraphItem  pathwayVertexGraphItem,
			final String sName, 
			final String sShapeType, 
			final int iHeight, 
			final int iWidth,
			final int iXPosition, 
			final int iYPosition);
}
