package org.caleydo.view.visbricks.brick.data;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.collection.table.DataTableDataType;
import org.caleydo.core.data.virtualarray.ISegmentData;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.SetBasedSegmentData;
import org.caleydo.core.data.virtualarray.TableBasedDimensionGroupData;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.view.visbricks.brick.layout.ASetBasedDataConfigurer;
import org.caleydo.view.visbricks.brick.layout.IBrickConfigurer;
import org.caleydo.view.visbricks.brick.layout.NominalDataConfigurer;
import org.caleydo.view.visbricks.brick.layout.NumericalDataConfigurer;

public class SetBasedBrickDimensionGroupData implements
		IBrickDimensionGroupData {

	private TableBasedDimensionGroupData dimensionGroupData;
	private ASetBasedDataConfigurer setBasedDataConfigurer;

	public SetBasedBrickDimensionGroupData(
			TableBasedDimensionGroupData dimensionGroupData) {
		this.dimensionGroupData = dimensionGroupData;
		if (dimensionGroupData.getTable().getTableType()
				.equals(DataTableDataType.NUMERIC)) {
			setBasedDataConfigurer = new NumericalDataConfigurer(
					dimensionGroupData.getTable());
		} else {
			setBasedDataConfigurer = new NominalDataConfigurer(
					dimensionGroupData.getTable());
		}
	}

	@Override
	public RecordVirtualArray getSummaryBrickVA() {
		return dimensionGroupData.getSummaryVA();
	}

	@Override
	public ArrayList<RecordVirtualArray> getSegmentBrickVAs() {
		return dimensionGroupData.getSegmentVAs();
	}

	@Override
	public IDataDomain getDataDomain() {
		return dimensionGroupData.getDataDomain();
	}

	@Override
	public IBrickConfigurer getBrickConfigurer() {
		return setBasedDataConfigurer;
	}

	@Override
	public ArrayList<Group> getGroups() {

		return dimensionGroupData.getGroups();
	}

	@Override
	public int getID() {
		return dimensionGroupData.getID();
	}

	@Override
	public List<IBrickData> getSegmentBrickData() {

		List<ISegmentData> segmentData = dimensionGroupData.getSegmentData();

		List<IBrickData> segmentBrickData = new ArrayList<IBrickData>();

		if (segmentData != null) {
			for (ISegmentData data : segmentData) {
				segmentBrickData.add(new SetBasedBrickData(
						(SetBasedSegmentData) data));
			}
		}

		return segmentBrickData;
	}

	@Override
	public IBrickData getSummaryBrickData() {
		SetBasedSegmentData tempSegmentData = new SetBasedSegmentData(
				(ATableBasedDataDomain) getDataDomain(),
				dimensionGroupData.getTable(), getSummaryBrickVA(), new Group(),
				dimensionGroupData);
		return new SetBasedBrickData(tempSegmentData);
	}

	@Override
	public IBrickSortingStrategy getDefaultSortingStrategy() {
		return new AverageValueSortingStrategy();
	}

}
