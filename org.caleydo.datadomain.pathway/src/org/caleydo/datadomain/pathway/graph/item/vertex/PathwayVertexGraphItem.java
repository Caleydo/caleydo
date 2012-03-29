package org.caleydo.datadomain.pathway.graph.item.vertex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.id.ManagedObjectType;
import org.caleydo.core.manager.GeneralManager;

/**
 * Pathway vertex that belongs to the overall pathway graph.
 * 
 * @author Marc Streit
 */
public class PathwayVertexGraphItem implements Serializable, IUniqueObject {

	private static final long serialVersionUID = 1L;

	private int id;
	
	private final String name;

	private EPathwayVertexType type;

	private final String externalLink;

	private final String reactionId;
	
	private List<PathwayVertexGraphItemRep> pathwayVertexReps = new ArrayList<PathwayVertexGraphItemRep>();

	/**
	 * Constructor.
	 * 
	 * @param sName
	 * @param sType
	 * @param sExternalLink
	 * @param sReactionId
	 */
	public PathwayVertexGraphItem(final String sName, final String sType,
			final String sExternalLink, final String sReactionId) {

		id = GeneralManager.get().getIDCreator().createID(ManagedObjectType.PATHWAY_VERTEX);
		
		// Check if type exists - otherwise assign "other"
		try {
			type = EPathwayVertexType.valueOf(sType);
		} catch (IllegalArgumentException e) {
			type = EPathwayVertexType.other;
		}

		this.name = sName;
		this.externalLink = sExternalLink;
		this.reactionId = sReactionId;
	}

	@Override
	public int getID() {
		return id;
	}
	
	public String getName() {

		return name;
	}

	public EPathwayVertexType getType() {

		return type;
	}

	public String getExternalLink() {

		return externalLink;
	}

	public String getReactionId() {

		return reactionId;
	}

	@Override
	public String toString() {

		return name;
	}
	
	public void addPathwayVertexRep(PathwayVertexGraphItemRep vertexRep) {
		pathwayVertexReps.add(vertexRep);
	}
	
	public List<PathwayVertexGraphItemRep> getPathwayVertexReps() {
		return pathwayVertexReps;
	}
}
