package org.caleydo.view.visbricks.brick.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.container.ADimensionGroupData;
import org.caleydo.core.data.container.ISegmentData;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.data.PathwayDimensionGroupData;
import org.caleydo.datadomain.pathway.data.PathwaySegmentData;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexGraphItem;
import org.caleydo.datadomain.pathway.manager.PathwayItemManager;
import org.caleydo.util.graph.EGraphItemKind;
import org.caleydo.util.graph.EGraphItemProperty;
import org.caleydo.util.graph.IGraphItem;
import org.caleydo.view.visbricks.brick.layout.IBrickConfigurer;
import org.caleydo.view.visbricks.brick.layout.PathwayDataConfigurer;

public class PathwayBrickDimensionGroupData extends PathwayDimensionGroupData implements
		IBrickDimensionGroupData {

	private PathwayDataConfigurer pathwayDataConfigurer;

	public PathwayBrickDimensionGroupData(ATableBasedDataDomain dataDomain,
			PathwayDataDomain pathwayDataDomain,
			DimensionPerspective dimensionPerspective, ArrayList<PathwayGraph> pathways,
			String label) {
		super(dataDomain, pathwayDataDomain, dimensionPerspective, pathways, label);

		pathwayDataConfigurer = new PathwayDataConfigurer();
	}

	@Override
	public RecordVirtualArray getSummaryBrickVA() {
		return getSummaryVA();
	}

	@Override
	public ArrayList<RecordVirtualArray> getSegmentBrickVAs() {

		return getSegmentVAs();
	}

	@Override
	public IBrickConfigurer getBrickConfigurer() {
		return pathwayDataConfigurer;
	}

	// @Override
	// public List<IBrickData> getSegmentBrickData() {
	//
	// List<ISegmentData> segmentData = dimensionGroupData.getSegmentData();
	//
	// List<IBrickData> segmentBrickData = new ArrayList<IBrickData>();
	//
	// for (ISegmentData data : segmentData) {
	// segmentBrickData.add(new PathwayBrickData((PathwaySegmentData) data));
	// }
	//
	// return segmentBrickData;
	// }
	@Override
	public List<IBrickData> getSegmentBrickData() {

		List<IBrickData> segmentData = new ArrayList<IBrickData>();

		int groupID = 0;
		int startIndex = 0;
		for (PathwayGraph pathway : pathways) {

			Group group = new Group();
			List<Integer> ids = new ArrayList<Integer>();
			group.setGroupID(groupID);
			group.setStartIndex(startIndex);

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
						groupSize++;
						Set<Integer> recordIDs = GeneralManager
								.get()
								.getIDMappingManager()
								.getIDAsSet(IDType.getIDType("DAVID"),
										dataDomain.getRecordIDType(), davidId);

						if (recordIDs != null && recordIDs.size() > 0) {
							ids.addAll(recordIDs);
						}
					}
				}
			}

			group.setSize(groupSize);
			RecordPerspective recordPerspective = new RecordPerspective(dataDomain);
			recordPerspective.createVA((ArrayList<Integer>) ids);
			segmentData.add(new PathwayBrickData(dataDomain, pathwayDataDomain,
					recordPerspective, dimensionPerspective, group, pathway, this));

			startIndex += groupSize;
			groupID++;
		}

		return segmentData;
	}

	@Override
	public IBrickData getSummaryBrickData() {
		PathwayBrickData tempSegmentData = new PathwayBrickData(dataDomain,
				pathwayDataDomain, recordPerspective, dimensionPerspective, new Group(),
				null, this);
		return tempSegmentData;
	}

	@Override
	public IBrickSortingStrategy getDefaultSortingStrategy() {
		return new AlphabeticalDataLabelSortingStrategy();
	}

}
