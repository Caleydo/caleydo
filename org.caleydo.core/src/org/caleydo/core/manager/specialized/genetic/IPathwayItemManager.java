package org.caleydo.core.manager.specialized.genetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.graph.ICaleydoGraphItem;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItem;
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
	extends IManager<ICaleydoGraphItem> {

	public IGraphItem createVertex(final String sName, final String sType, final String sExternalLink,
		final String sReactionId);

	public ArrayList<IGraphItem> createVertexGene(final String sName, final String sType,
		final String sExternalLink, final String sReactionId, final Set<Integer> iSetDavidId);

	public IGraphItem createVertexRep(final IGraph parentPathway,
		final ArrayList<IGraphItem> alVertexGraphItem, final String sName, final String sShapeType,
		final short shHeight, final short shWidth, final short shXPosition, final short shYPosition);

	public IGraphItem createVertexRep(final IGraph parentPathway, ArrayList<IGraphItem> alVertexGraphItem,
		final String sName, final String sShapeType, final String sCoords);

	public IGraphItem createRelationEdge(final List<IGraphItem> alGraphItemIn,
		final List<IGraphItem> alGraphItemOut, final String sType);

	public void createRelationEdgeRep(final IGraph parentPathway, final IGraphItem pathwayRelationEdge,
		final IGraphItem graphItemIn, final IGraphItem graphItemOut);

	public IGraphItem createReactionEdge(final IGraph parentPathway, final String sReactionName,
		final String sReactionType);

	public PathwayVertexGraphItem getPathwayVertexGraphItemByDavidId(final int iDavidId);

	public int getDavidIdByPathwayVertexGraphItem(final PathwayVertexGraphItem pathwayVertexGraphItem);

	public PathwayVertexGraphItemRep getPathwayVertexRep(int iID);
}
