package org.caleydo.view.visbricks.brick.data;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.collection.table.DataTableDataType;
import org.caleydo.core.data.container.TableBasedDimensionGroupData;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.data.perspective.DataPerspective;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.view.visbricks.brick.layout.ASetBasedDataConfigurer;
import org.caleydo.view.visbricks.brick.layout.IBrickConfigurer;
import org.caleydo.view.visbricks.brick.layout.NominalDataConfigurer;
import org.caleydo.view.visbricks.brick.layout.NumericalDataConfigurer;

public class TableBasedBrickDimensionGroupData extends TableBasedDimensionGroupData
		implements IBrickDimensionGroupData {

	private ASetBasedDataConfigurer setBasedDataConfigurer;

	public TableBasedBrickDimensionGroupData(ATableBasedDataDomain dataDomain,
			RecordPerspective recordPerspective,
			DimensionPerspective dimensionPerspective, ClusterNode rootNode,
			Class<? extends DataPerspective<?, ?, ?, ?>> dataPerspectiveClass) {
		super(dataDomain, recordPerspective, dimensionPerspective, rootNode,
				dataPerspectiveClass);

		if (dataDomain.getTable().getTableType().equals(DataTableDataType.NUMERIC)) {
			setBasedDataConfigurer = new NumericalDataConfigurer(dataDomain.getTable());
		} else {
			setBasedDataConfigurer = new NominalDataConfigurer(dataDomain.getTable());
		}
	}

	// @Override
	// public RecordVirtualArray getSummaryBrickVA() {
	// return dimensionG.getSummaryVA();
	// }

	@Override
	public ArrayList<RecordVirtualArray> getSegmentBrickVAs() {
		return getSegmentVAs();
	}

	@Override
	public IBrickConfigurer getBrickConfigurer() {
		return setBasedDataConfigurer;
	}

	// @Override
	// public ArrayList<Group> getGroups() {
	//
	// return dimensionGroupData.getGroups();
	// }

	// @Override
	// public int getID() {
	// return dimensionGroupData.getID();
	// }

	@Override
	public List<IBrickData> getSegmentBrickData() {

		RecordVirtualArray recordVA = recordPerspective.getVirtualArray();

		if (recordVA.getGroupList() == null)
			return null;

		RecordGroupList groupList = recordVA.getGroupList();
		groupList.updateGroupInfo();

		List<IBrickData> segmentBrickData = new ArrayList<IBrickData>();

		for (Group group : groupList) {

			ArrayList<Integer> indices = (ArrayList<Integer>) recordVA.getVirtualArray()
					.subList(group.getStartIndex(), group.getEndIndex() + 1);

			RecordPerspective recordPerspective = new RecordPerspective(dataDomain);
			recordPerspective.createVA(indices);

			segmentBrickData.add(new BrickData(dataDomain, recordPerspective,
					dimensionPerspective, group, this));

		}
		return segmentBrickData;

		// List<ISegmentData> segmentData = getSegmentData();
		//
		// List<IBrickData> segmentBrickData = new ArrayList<IBrickData>();
		//
		// if (segmentData != null) {
		// for (ISegmentData data : segmentData) {
		// segmentBrickData
		// .add(new BrickData(data));
		// }
		// }
		//
		// return segmentBrickData;
	}

	@Override
	public IBrickData getSummaryBrickData() {
		BrickData tempSegmentData = new BrickData(dataDomain, recordPerspective,
				dimensionPerspective, new Group(), this);

		return tempSegmentData;
	}

	@Override
	public IBrickSortingStrategy getDefaultSortingStrategy() {
		return new AverageValueSortingStrategy();
	}

	@Override
	public RecordVirtualArray getSummaryBrickVA() {
		return recordPerspective.getVirtualArray();
	}

}
