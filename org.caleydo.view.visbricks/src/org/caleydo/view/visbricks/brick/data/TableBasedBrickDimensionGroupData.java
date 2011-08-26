package org.caleydo.view.visbricks.brick.data;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.collection.table.DataTableDataType;
import org.caleydo.core.data.container.ISegmentData;
import org.caleydo.core.data.container.TableBasedDimensionGroupData;
import org.caleydo.core.data.container.TableBasedSegmentData;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.visbricks.brick.layout.ASetBasedDataConfigurer;
import org.caleydo.view.visbricks.brick.layout.IBrickConfigurer;
import org.caleydo.view.visbricks.brick.layout.NominalDataConfigurer;
import org.caleydo.view.visbricks.brick.layout.NumericalDataConfigurer;

public class TableBasedBrickDimensionGroupData implements
		IBrickDimensionGroupData<ATableBasedDataDomain> {

	private TableBasedDimensionGroupData dimensionGroupData;
	private ASetBasedDataConfigurer setBasedDataConfigurer;

	public TableBasedBrickDimensionGroupData(
			TableBasedDimensionGroupData dimensionGroupData) {
		this.dimensionGroupData = dimensionGroupData;
		if (dimensionGroupData.getDataDomain().getTable().getTableType()
				.equals(DataTableDataType.NUMERIC)) {
			setBasedDataConfigurer = new NumericalDataConfigurer(dimensionGroupData
					.getDataDomain().getTable());
		} else {
			setBasedDataConfigurer = new NominalDataConfigurer(dimensionGroupData
					.getDataDomain().getTable());
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
	public ATableBasedDataDomain getDataDomain() {
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
				segmentBrickData
						.add(new TableBasedBrickData((TableBasedSegmentData) data));
			}
		}

		return segmentBrickData;
	}

	@Override
	public IBrickData getSummaryBrickData() {
		TableBasedSegmentData tempSegmentData = new TableBasedSegmentData(
				getDataDomain(), dimensionGroupData.getRecordPerspective(),
				dimensionGroupData.getDimensionPerspective(), new Group(),
				dimensionGroupData);

		return new TableBasedBrickData(tempSegmentData);
	}

	@Override
	public IBrickSortingStrategy getDefaultSortingStrategy() {
		return new AverageValueSortingStrategy();
	}

}
