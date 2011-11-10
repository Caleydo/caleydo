package org.caleydo.datadomain.pathway.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.PerspectiveInitializationData;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexGraphItem;
import org.caleydo.datadomain.pathway.manager.PathwayItemManager;
import org.caleydo.util.graph.EGraphItemKind;
import org.caleydo.util.graph.EGraphItemProperty;
import org.caleydo.util.graph.IGraphItem;

/**
 * Implementation of {@link ADimensionGroupData} for pathways. In this case a
 * dimension group consists of several pathways.
 * 
 * @author Christian Partl
 * @author Alexander Lex
 * 
 */
public class PathwayDimensionGroupData extends DataContainer {

	protected PathwayDataDomain pathwayDataDomain;
	protected GeneticDataDomain geneticDataDomain;
	protected ArrayList<PathwayGraph> pathways;

	private List<DataContainer> recordSubDataContainers = new ArrayList<DataContainer>();

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
	}

	// @Override
	// public RecordVirtualArray getSummaryVA() {
	// // TODO: Is this a good way?
	// ArrayList<RecordVirtualArray> recordVAs = getSegmentVAs();
	// ArrayList<Integer> summaryBrickIDs = new ArrayList<Integer>();
	// for (RecordVirtualArray recordVA : recordVAs) {
	// summaryBrickIDs.addAll(recordVA.getVirtualArray());
	// }
	//
	// RecordGroupList groupList = new RecordGroupList();
	// groupList.setGroups(getGroups());
	// RecordVirtualArray summaryBrickVA = new RecordVirtualArray("CONTENT",
	// summaryBrickIDs);
	// summaryBrickVA.setGroupList(groupList);
	//
	// return summaryBrickVA;
	// }

	// @Override
	// public ArrayList<RecordVirtualArray> getSegmentVAs() {
	//
	// ArrayList<RecordVirtualArray> recordVAs = new
	// ArrayList<RecordVirtualArray>();
	//
	// for (PathwayGraph pathway : pathways) {
	// List<IGraphItem> vertexGraphItemReps = pathway
	// .getAllItemsByKind(EGraphItemKind.NODE);
	//
	// List<Integer> ids = new ArrayList<Integer>();
	//
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
	// // TODO: Map to content id type (given as parameter)
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
	// recordVAs.add(new RecordVirtualArray("CONTENT", ids));
	// }
	//
	// return recordVAs;
	// }

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

		int startIndex = 0;
		for (PathwayGraph pathway : pathways) {
			List<Integer> idsInPathway = new ArrayList<Integer>();
			List<IGraphItem> vertexGraphItemReps = pathway
					.getAllItemsByKind(EGraphItemKind.NODE);

			int groupSize = 0;

			for (IGraphItem itemRep : vertexGraphItemReps) {

				List<IGraphItem> vertexGraphItems = itemRep
						.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT);

				for (IGraphItem item : vertexGraphItems) {
					int davidId = PathwayItemManager.get()
							.getDavidIdByPathwayVertexGraphItem(
									(PathwayVertexGraphItem) item);

					if (davidId != -1) {
						Set<Integer> ids = pathwayDataDomain.getGeneIDMappingManager()
								.getIDAsSet(IDType.getIDType("DAVID"),
										geneticDataDomain.getGeneIDType(), davidId);

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

			// here we create the
			PerspectiveInitializationData data = new PerspectiveInitializationData();
			data.setData(idsInPathway);
			PathwayDataContainer pathwayDataContainer;
			if (dataDomain.isColumnDimension()) {
				RecordPerspective pathwayRecordPerspective = new RecordPerspective(
						dataDomain);
				pathwayRecordPerspective.init(data);

				pathwayDataContainer = new PathwayDataContainer(dataDomain,
						pathwayDataDomain, pathwayRecordPerspective,
						dimensionPerspective, pathway);
			} else {
				DimensionPerspective pathwayDimensionPerspective = new DimensionPerspective(
						dataDomain);
				pathwayDimensionPerspective.init(data);

				pathwayDataContainer = new PathwayDataContainer(dataDomain,
						pathwayDataDomain, recordPerspective,
						pathwayDimensionPerspective, pathway);
			}
			recordSubDataContainers.add(pathwayDataContainer);
		}

		if (dataDomain.isColumnDimension()) {
			recordPerspective = new RecordPerspective(dataDomain);
			PerspectiveInitializationData data = new PerspectiveInitializationData();
			data.setData(allIDsInPathwayDimensionGroup, groups, sampleElements);
			recordPerspective.init(data);
		} else {
			dimensionPerspective = new DimensionPerspective(dataDomain);
			PerspectiveInitializationData data = new PerspectiveInitializationData();
			data.setData(allIDsInPathwayDimensionGroup, groups, sampleElements);
			dimensionPerspective.init(data);
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
	// public List<DataContainer> getDa() {
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
	public List<DataContainer> createRecordSubDataContainers() {
		return recordSubDataContainers;
	}
}
