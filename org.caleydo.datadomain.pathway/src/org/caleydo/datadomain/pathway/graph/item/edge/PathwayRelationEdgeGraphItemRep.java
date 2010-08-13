package org.caleydo.datadomain.pathway.graph.item.edge;

import java.io.Serializable;

import org.caleydo.core.data.graph.ACaleydoGraphItem;
import org.caleydo.util.graph.EGraphItemKind;

/**
 * Pathway relation edge representation belonging to the overall pathway graph. Used for KEGG pathways.
 * 
 * @author Marc Streit
 */
public class PathwayRelationEdgeGraphItemRep
	extends ACaleydoGraphItem
	implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public PathwayRelationEdgeGraphItemRep() {
		super(EGraphItemKind.EDGE);
	}
}