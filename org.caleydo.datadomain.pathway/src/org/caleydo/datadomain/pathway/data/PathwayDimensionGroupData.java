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

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;

/**
 * Specialization of {@link TablePerspective} for pathway dimension groups. A
 * {@link PathwayDimensionGroupData} can be used to hold small-multiple pathways
 * or multiple different pathways in the same dimension group.
 *
 * @author Christian Partl
 * @author Alexander Lex
 * @author Marc Streit
 *
 */
public class PathwayDimensionGroupData
	extends TablePerspective {

	protected PathwayDataDomain pathwayDataDomain;
	protected ArrayList<PathwayGraph> pathways;

	private List<TablePerspective> recordSubTablePerspectives = new ArrayList<TablePerspective>();

	public PathwayDimensionGroupData(ATableBasedDataDomain dataDomain, PathwayDataDomain pathwayDataDomain,
			Perspective recordPerspective, Perspective dimensionPerspective,
			ArrayList<PathwayGraph> pathways, String label) {
		this.dataDomain = dataDomain;
		this.pathwayDataDomain = pathwayDataDomain;
		this.dimensionPerspective = dimensionPerspective;
		this.recordPerspective = recordPerspective;
		this.pathways = pathways;
		this.label = label;

	}

	/**
	 * @return All pathways of this dimension group.
	 */
	public ArrayList<PathwayGraph> getPathways() {
		return pathways;
	}

	/**
	 * Sets the pathways of this dimension group.
	 *
	 * @param pathways
	 */
	public void setPathways(ArrayList<PathwayGraph> pathways) {
		this.pathways = pathways;
		initializeData();
	}

	private void initializeData() {

		ArrayList<Integer> sampleElements = new ArrayList<Integer>();

		IDType geneIDType = null;
		if (dataDomain.isColumnDimension())
			geneIDType = dataDomain.getRecordIDType();
		else
			geneIDType = dataDomain.getDimensionIDType();

		IDMappingManager geneIDMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(geneIDType);
		int startIndex = 0;

		IIDTypeMapper<Integer, Integer> mapper = geneIDMappingManager.getIDTypeMapper(PathwayVertexRep.getIdType(),
				geneIDType);

		for (PathwayGraph pathway : pathways) {
			List<Integer> idsInPathway = new ArrayList<Integer>();

			int groupSize = 0;
			for (PathwayVertexRep vertexRep : pathway.vertexSet()) {
				Set<Integer> geneIDs = mapper.apply(vertexRep.getID());
				idsInPathway.addAll(geneIDs);
				groupSize += geneIDs.size();
			}

			sampleElements.add(startIndex);
			startIndex += groupSize;

			PerspectiveInitializationData data = new PerspectiveInitializationData();
			data.setData(idsInPathway);
			PathwayTablePerspective pathwayTablePerspective;
			if (dataDomain.isColumnDimension()) {
				Perspective pathwayRecordPerspective = new Perspective(dataDomain,
						dataDomain.getRecordIDType());
				pathwayRecordPerspective.init(data);

				pathwayTablePerspective = new PathwayTablePerspective(dataDomain, pathwayDataDomain,
						pathwayRecordPerspective, dimensionPerspective, pathway);
			}
			else {
				Perspective pathwayDimensionPerspective = new Perspective(dataDomain,
						dataDomain.getDimensionIDType());
				pathwayDimensionPerspective.init(data);

				pathwayTablePerspective = new PathwayTablePerspective(dataDomain, pathwayDataDomain, recordPerspective,
						pathwayDimensionPerspective, pathway);
			}

			pathwayTablePerspective.setRecordGroup(recordPerspective.getVirtualArray().getGroupList().get(0));

			recordSubTablePerspectives.add(pathwayTablePerspective);
		}
	}


	/**
	 * @return the pathwayDataDomain, see {@link #pathwayDataDomain}
	 */
	public PathwayDataDomain getPathwayDataDomain() {
		return pathwayDataDomain;
	}

	@Override
	public List<TablePerspective> getRecordSubTablePerspectives() {

		List<TablePerspective> recordSubTablePerspectives = new ArrayList<TablePerspective>();

		VirtualArray recordVA = recordPerspective.getVirtualArray();

		if (recordVA.getGroupList() == null)
			return null;

		GroupList groupList = recordVA.getGroupList();
		groupList.updateGroupInfo();

		for (Group group : groupList) {

			List<Integer> indices = recordVA.getIDsOfGroup(group.getGroupIndex());
			Perspective recordPerspective = new Perspective(dataDomain, recordVA.getIdType());
			PerspectiveInitializationData data = new PerspectiveInitializationData();
			data.setData(indices);
			recordPerspective.init(data);

			// FIXME: currently only the first pathway is taken from the list
			PathwayTablePerspective subTablePerspective = new PathwayTablePerspective(dataDomain, pathwayDataDomain,
					recordPerspective, dimensionPerspective, pathways.get(0));
			subTablePerspective.setRecordGroup(group);
			recordSubTablePerspectives.add(subTablePerspective);

		}

		return recordSubTablePerspectives;
	}
}
