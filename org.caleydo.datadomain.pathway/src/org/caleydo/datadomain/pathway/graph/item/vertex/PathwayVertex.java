/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.graph.item.vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDCreator;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.IUniqueObject;
import org.caleydo.datadomain.genetic.EGeneIDTypes;

/**
 * Pathway vertex that belongs to the overall pathway graph.
 *
 * @author Marc Streit
 */
public class PathwayVertex implements IUniqueObject {

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
	public PathwayVertex(final String name, final String type, final String externalLink) {

		id = IDCreator.createVMUniqueID(PathwayVertex.class);

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

	/**
	 * @return The human readable name of this vertex. Differs from {@link #getName()} if this vertex represents a gene.
	 */
	public String getHumanReadableName() {
		if (type == EPathwayVertexType.gene) {
			IDCategory geneIDCategory = IDCategory.getIDCategory(EGeneIDTypes.GENE.name());
			IDMappingManager geneIDMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(geneIDCategory);
			IDType pathwayVertexIDType = IDType.getIDType(EGeneIDTypes.PATHWAY_VERTEX.name());
			IDType humanReadableIDType = geneIDCategory.getHumanReadableIDType();
			Set<String> ids = geneIDMappingManager.getID(pathwayVertexIDType, humanReadableIDType, getID());
			if (ids == null || ids.isEmpty())
				return "";
			return ids.iterator().next();
		}
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	public void addPathwayVertexRep(PathwayVertexRep vertexRep) {
		pathwayVertexReps.add(vertexRep);
	}

	/**
	 * @return the pathwayVertexReps, see {@link #pathwayVertexReps}
	 */
	public List<PathwayVertexRep> getPathwayVertexReps() {
		return pathwayVertexReps;
	}
}
