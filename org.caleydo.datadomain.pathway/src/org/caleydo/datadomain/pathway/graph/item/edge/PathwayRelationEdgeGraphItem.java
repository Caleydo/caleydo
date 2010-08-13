package org.caleydo.datadomain.pathway.graph.item.edge;

import java.io.Serializable;

import org.caleydo.core.data.graph.ACaleydoGraphItem;
import org.caleydo.util.graph.EGraphItemKind;

/**
 * Pathway relation edge belonging to the overall pathway graph. Used for KEGG pathways.
 * 
 * @author Marc Streit
 */
public class PathwayRelationEdgeGraphItem
	extends ACaleydoGraphItem
	implements Serializable {

	private static final long serialVersionUID = 1L;

	final EPathwayRelationEdgeType type;

	/**
	 * Constructor.
	 * 
	 * @param sType
	 */
	public PathwayRelationEdgeGraphItem(final String sType) {
		super(EGraphItemKind.EDGE);

		type = EPathwayRelationEdgeType.valueOf(sType);
	}

	public EPathwayRelationEdgeType getType() {
		return type;
	}
}