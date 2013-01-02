/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.datadomain.pathway.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.perspective.variable.ARecordPerspective;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;

/**
 * a special kind of a recordperspective for a pathway
 *
 * @author Samuel Gratzl
 *
 */
public class PathwayRecordPerspective extends ARecordPerspective {
	private final PathwayGraph pathway;

	/**
	 * @param pathway
	 */
	public PathwayRecordPerspective(PathwayGraph pathway, PathwayDataDomain dataDomain) {
		super(dataDomain);
		this.pathway = pathway;
		setLabel(pathway.getTitle());

	}

	/**
	 * @return the pathway, see {@link #pathway}
	 */
	public PathwayGraph getPathway() {
		return pathway;
	}

	@Override
	protected void init() {
		super.init();
		idType = getDataDomain().getDavidIDType();
	}

	@Override
	public PathwayDataDomain getDataDomain() {
		return (PathwayDataDomain) super.getDataDomain();
	}

	@Override
	protected String getElementLabel(Integer id) {
		Set<String> ids = IDMappingManagerRegistry.get().getIDMappingManager(idType)
				.getIDAsSet(idType, idType.getIDCategory().getHumanReadableIDType(), id);
		String label = "No Mapping";
		if (ids != null && ids.size() > 0) {
			label = ids.iterator().next();
		}
		return label;
	}

	@Override
	protected List<Integer> getIDList() {
		IDMappingManager geneIDMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(idType);
		IIDTypeMapper<Integer, Integer> mapper = geneIDMappingManager.getIDTypeMapper(PathwayVertexRep.getIdType(),
				idType);
		List<Integer> idsInPathway = new ArrayList<Integer>();
		for (PathwayVertexRep vertexRep : pathway.vertexSet()) {
			Set<Integer> ids = mapper.apply(vertexRep.getID());
			if (ids != null)
				idsInPathway.addAll(ids);
		}
		return idsInPathway;
	}
}
