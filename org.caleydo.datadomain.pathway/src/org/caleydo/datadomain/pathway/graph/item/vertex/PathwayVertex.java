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
public class PathwayVertex implements Serializable, IUniqueObject {

	private static final long serialVersionUID = 1L;

	private int id;
	
	private final String name;

	private EPathwayVertexType type;

	private final String externalLink;
	
	private List<PathwayVertexRep> pathwayVertexReps = new ArrayList<PathwayVertexRep>();

	/**
	 * Constructor.
	 * 
	 * @param name
	 * @param type
	 * @param externalLink
	 * @param reactionId
	 */
	public PathwayVertex(final String name, final String type,
			final String externalLink) {

		id = GeneralManager.get().getIDCreator().createID(ManagedObjectType.PATHWAY_VERTEX);
		
		// Check if type exists - otherwise assign "other"
		try {
			this.type = EPathwayVertexType.valueOf(type);
		} catch (IllegalArgumentException e) {
			this.type = EPathwayVertexType.other;
		}

		this.name = name;
		this.externalLink = externalLink;
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

	@Override
	public String toString() {

		return name;
	}
	
	public void addPathwayVertexRep(PathwayVertexRep vertexRep) {
		pathwayVertexReps.add(vertexRep);
	}
	
	public List<PathwayVertexRep> getPathwayVertexReps() {
		return pathwayVertexReps;
	}
}
