package org.caleydo.core.data.graph.pathway.item.vertex;

import java.io.Serializable;
import org.caleydo.core.data.graph.ACaleydoGraphItem;
import org.caleydo.util.graph.EGraphItemKind;

/**
 * Pathway vertex that belongs to the overall pathway graph.
 * 
 * @author Marc Streit
 */
public class PathwayVertexGraphItem
	extends ACaleydoGraphItem
	implements Serializable
{

	private static final long serialVersionUID = 1L;

	final String sName;

	EPathwayVertexType type;

	final String sExternalLink;

	final String sReactionId;

	/**
	 * Constructor.
	 * 
	 * @param sName
	 * @param sType
	 * @param sExternalLink
	 * @param sReactionId
	 */
	public PathwayVertexGraphItem(final String sName, final String sType,
			final String sExternalLink, final String sReactionId)
	{

		super(EGraphItemKind.NODE);

		// Check if type exists - otherwise assign "other"
		try
		{
			type = EPathwayVertexType.valueOf(sType);
		}
		catch (IllegalArgumentException e)
		{
			type = EPathwayVertexType.other;
		}

		this.sName = sName;
		this.sExternalLink = sExternalLink;
		this.sReactionId = sReactionId;
	}

	public String getName()
	{

		return sName;
	}

	public EPathwayVertexType getType()
	{

		return type;
	}

	public String getExternalLink()
	{

		return sExternalLink;
	}

	public String getReactionId()
	{

		return sReactionId;
	}

	@Override
	public String toString()
	{

		return sName;
	}
}
