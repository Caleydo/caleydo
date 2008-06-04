package org.caleydo.core.data.graph.pathway.item.vertex;

import java.io.Serializable;

import org.caleydo.util.graph.EGraphItemKind;
import org.caleydo.util.graph.item.GraphItem;

/**
 * Pathway vertex that belongs to the overall pathway graph.
 * 
 * @author Marc Streit
 *
 */
public class PathwayVertexGraphItem 
extends GraphItem 
implements Serializable 
{	
	private static final long serialVersionUID = 1L;
	
	final String sName;
	
	final EPathwayVertexType type;
	
	final String sExternalLink;
	
	final String sReactionId;
	
	/**
	 * Constructor.
	 * 
	 * @param iId
	 * @param sName
	 * @param sType
	 * @param sExternalLink
	 * @param sReactionId
	 */
	public PathwayVertexGraphItem(
			final int iId,
			final String sName,
			final String sType,
			final String sExternalLink,
			final String sReactionId) {
		
		super(iId, EGraphItemKind.NODE);

		type = EPathwayVertexType.valueOf(sType);
		
		this.sName = sName;
		this.sExternalLink = sExternalLink;
		this.sReactionId = sReactionId;
	}
	
	public String getName() {
		
		return sName;
	}
	
	public EPathwayVertexType getType() {
		
		return type;
	}
	
	public String getExternalLink() {
		
		return sExternalLink;
	}
	
	public String getReactionId() {
		
		return sReactionId;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		
		return sName;
	}
}
