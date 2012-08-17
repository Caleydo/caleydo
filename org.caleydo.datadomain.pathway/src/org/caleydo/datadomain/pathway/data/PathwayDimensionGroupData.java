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
import org.caleydo.core.data.perspective.variable.DimensionPerspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.core.id.IDType;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertex;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.manager.PathwayItemManager;

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
public class PathwayDimensionGroupData extends TablePerspective {

	protected PathwayDataDomain pathwayDataDomain;
	protected ArrayList<PathwayGraph> pathways;

	private List<TablePerspective> recordSubTablePerspectives = new ArrayList<TablePerspective>();

	public PathwayDimensionGroupData(ATableBasedDataDomain dataDomain,
			PathwayDataDomain pathwayDataDomain, RecordPerspective recordPerspective,
			DimensionPerspective dimensionPerspective, ArrayList<PathwayGraph> pathways,
			String label) {
		this.dataDomain = dataDomain;
		this.pathwayDataDomain = pathwayDataDomain;
		this.dimensionPerspective = dimensionPerspective;
		this.recordPerspective = recordPerspective;
		this.pathways = pathways;
		this.label = label;

		// initializeData();
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

		ArrayList<Integer> groups = new ArrayList<Integer>();
		ArrayList<Integer> sampleElements = new ArrayList<Integer>();
		List<Integer> allIDsInPathwayDimensionGroup = new ArrayList<Integer>();

		IDType geneIDType = null;
		if (dataDomain.isColumnDimension())
			geneIDType = dataDomain.getRecordIDType();
		else
			geneIDType = dataDomain.getDimensionIDType();

		// if (dataDomain.isColumnDimension()) {
		// recordPerspective = new RecordPerspective(dataDomain);
		// PerspectiveInitializationData data = new
		// PerspectiveInitializationData();
		// data.setData(allIDsInPathwayDimensionGroup, groups, sampleElements);
		// recordPerspective.init(data);
		// } else {
		// dimensionPerspective = new DimensionPerspective(dataDomain);
		// PerspectiveInitializationData data = new
		// PerspectiveInitializationData();
		// data.setData(allIDsInPathwayDimensionGroup, groups, sampleElements);
		// dimensionPerspective.init(data);
		// }

		int startIndex = 0;
		for (PathwayGraph pathway : pathways) {
			List<Integer> idsInPathway = new ArrayList<Integer>();

			int groupSize = 0;

			for (PathwayVertexRep vertexRep : pathway.vertexSet()) {

				for (PathwayVertex vertex : vertexRep.getPathwayVertices()) {
					Integer davidId = PathwayItemManager.get().getDavidIdByPathwayVertex(
							vertex);

					if (davidId != null) {

						Set<Integer> ids = pathwayDataDomain.getGeneIDMappingManager()
								.getIDAsSet(pathwayDataDomain.getDavidIDType(),
										geneIDType, davidId);

						if (ids != null && ids.size() > 0) {
							groupSize++;
							allIDsInPathwayDimensionGroup.addAll(ids);
							idsInPathway.addAll(ids);
						}
					}
				}
			}

			groups.add(groupSize);
			sampleElements.add(startIndex);
			startIndex += groupSize;

			PerspectiveInitializationData data = new PerspectiveInitializationData();
			data.setData(idsInPathway);
			PathwayTablePerspective pathwayTablePerspective;
			if (dataDomain.isColumnDimension()) {
				RecordPerspective pathwayRecordPerspective = new RecordPerspective(
						dataDomain);
				pathwayRecordPerspective.init(data);

				pathwayTablePerspective = new PathwayTablePerspective(dataDomain,
						pathwayDataDomain, pathwayRecordPerspective,
						dimensionPerspective, pathway);
			} else {
				DimensionPerspective pathwayDimensionPerspective = new DimensionPerspective(
						dataDomain);
				pathwayDimensionPerspective.init(data);

				pathwayTablePerspective = new PathwayTablePerspective(dataDomain,
						pathwayDataDomain, recordPerspective,
						pathwayDimensionPerspective, pathway);
			}

			pathwayTablePerspective.setRecordGroup(recordPerspective.getVirtualArray()
					.getGroupList().get(0));

			recordSubTablePerspectives.add(pathwayTablePerspective);
		}
	}

	// @Override
	// public ArrayList<Group> getGroups() {
	// ArrayList<Group> groups = new ArrayList<Group>();
	//
	// int groupID = 0;
	// int startIndex = 0;
	// for (PathwayGraph pathway : pathways) {
	//
	// Group group = new Group();
	// group.setGroupID(groupID);
	// group.setStartIndex(startIndex);
	//
	// List<IGraphItem> vertexGraphItemReps = pathway
	// .getAllItemsByKind(EGraphItemKind.NODE);
	//
	// int groupSize = 0;
	// for (IGraphItem itemRep : vertexGraphItemReps) {
	//
	// List<IGraphItem> vertexGraphItems = itemRep
	// .getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT);
	//
	// for (IGraphItem item : vertexGraphItems) {
	// int davidId = PathwayItemManager.get()
	// .getDavidIdByPathwayVertexGraphItem(
	// (PathwayVertexGraphItem) item);
	//
	// if (davidId != -1) {
	// Set<Integer> recordIDs = pathwayDataDomain
	// .getGeneIDMappingManager().getIDAsSet(
	// IDType.getIDType("DAVID"),
	// dataDomain.getRecordIDType(), davidId);
	//
	// if (recordIDs != null && recordIDs.size() > 0) {
	// groupSize++;
	// }
	//
	// }
	// }
	// }
	//
	// group.setSize(groupSize);
	// groups.add(group);
	// startIndex += groupSize;
	// groupID++;
	// }
	//
	// return groups;
	// }

	// @Override
	// public List<TablePerspective> getDa() {
	//
	// List<ISegmentData> segmentData = new ArrayList<ISegmentData>();
	//
	// int groupID = 0;
	// int startIndex = 0;
	// for (PathwayGraph pathway : pathways) {
	//
	// Group group = new Group();
	// List<Integer> ids = new ArrayList<Integer>();
	// group.setGroupID(groupID);
	// group.setStartIndex(startIndex);
	//
	// List<IGraphItem> vertexGraphItemReps = pathway
	// .getAllItemsByKind(EGraphItemKind.NODE);
	//
	// int groupSize = 0;
	// for (IGraphItem itemRep : vertexGraphItemReps) {
	//
	// List<IGraphItem> vertexGraphItems = itemRep
	// .getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT);
	//
	// for (IGraphItem item : vertexGraphItems) {
	// int davidId = PathwayItemManager.get()
	// .getDavidIdByPathwayVertexGraphItem(
	// (PathwayVertexGraphItem) item);
	//
	// if (davidId != -1) {
	// groupSize++;
	// Set<Integer> recordIDs = pathwayDataDomain
	// .getGeneIDMappingManager().getIDAsSet(
	// IDType.getIDType("DAVID"),
	// dataDomain.getRecordIDType(), davidId);
	//
	// if (recordIDs != null && recordIDs.size() > 0) {
	// ids.addAll(recordIDs);
	// }
	// }
	// }
	// }
	//
	// group.setSize(groupSize);
	// RecordPerspective recordPerspective = new RecordPerspective(dataDomain);
	// PerspectiveInitializationData data = new PerspectiveInitializationData();
	// data.setData(ids);
	// recordPerspective.init(data);
	// segmentData.add(new PathwaySegmentData(dataDomain, pathwayDataDomain,
	// recordPerspective, dimensionPerspective, group, pathway, this));
	//
	// startIndex += groupSize;
	// groupID++;
	// }
	//
	// return segmentData;
	// }

	/**
	 * @return the pathwayDataDomain, see {@link #pathwayDataDomain}
	 */
	public PathwayDataDomain getPathwayDataDomain() {
		return pathwayDataDomain;
	}

	@Override
	public List<TablePerspective> getRecordSubTablePerspectives() {

		List<TablePerspective> recordSubTablePerspectives = new ArrayList<TablePerspective>();

		RecordVirtualArray recordVA = recordPerspective.getVirtualArray();

		if (recordVA.getGroupList() == null)
			return null;

		RecordGroupList groupList = recordVA.getGroupList();
		groupList.updateGroupInfo();

		for (Group group : groupList) {

			List<Integer> indices = recordVA.getIDsOfGroup(group.getGroupIndex());
			RecordPerspective recordPerspective = new RecordPerspective(dataDomain);
			PerspectiveInitializationData data = new PerspectiveInitializationData();
			data.setData(indices);
			recordPerspective.init(data);

			// FIXME: currently only the first pathway is taken from the list
			PathwayTablePerspective subTablePerspective = new PathwayTablePerspective(
					dataDomain, pathwayDataDomain, recordPerspective,
					dimensionPerspective, pathways.get(0));
			subTablePerspective.setRecordGroup(group);
			recordSubTablePerspectives.add(subTablePerspective);

		}

		return recordSubTablePerspectives;
	}
}
