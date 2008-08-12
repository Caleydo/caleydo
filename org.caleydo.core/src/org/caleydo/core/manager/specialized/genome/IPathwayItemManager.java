package org.caleydo.core.manager.specialized.genome;

import java.util.ArrayList;
import java.util.HashMap;
import org.caleydo.core.data.graph.ICaleydoGraphItem;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItemRep;
import org.caleydo.core.manager.IManager;
import org.caleydo.util.graph.IGraph;
import org.caleydo.util.graph.IGraphItem;

/**
 * Pathway item manager handles creation of all pathway vertices and edges.
 * 
 * @author Marc Streit
 */
public interface IPathwayItemManager
	extends IManager<ICaleydoGraphItem>
{

	public IGraphItem createVertex(final String sName, final String sType,
			final String sExternalLink, final String sReactionId);

	public IGraphItem createVertexGene(final String sName, final String sType,
			final String sExternalLink, final String sReactionId, final int iDavidId);

	public IGraphItem createVertexRep(final IGraph parentPathway,
			final ArrayList<IGraphItem> alVertexGraphItem, final String sName,
			final String sShapeType, final short shHeight, final short shWidth,
			final short shXPosition, final short shYPosition);

	public IGraphItem createVertexRep(final IGraph parentPathway,
			final IGraphItem parentVertex, final String sName, final String sShapeType,
			final String sCoords);

	public IGraphItem createRelationEdge(final IGraphItem graphItemIn,
			final IGraphItem graphItemOut, final String sType);

	public void createRelationEdgeRep(final IGraph parentPathway,
			final IGraphItem pathwayRelationEdge, final IGraphItem graphItemIn,
			final IGraphItem graphItemOut);

	public IGraphItem createReactionEdge(final IGraph parentPathway,
			final String sReactionName, final String sReactionType);

	public int getPathwayVertexGraphItemIdByDavidId(final int iDavidId);

	public int getDavidIdByPathwayVertexGraphItemId(final int iPathwayVertexGraphItemId);
	
	public PathwayVertexGraphItemRep getPathwayVertexRep(int iID);
}
