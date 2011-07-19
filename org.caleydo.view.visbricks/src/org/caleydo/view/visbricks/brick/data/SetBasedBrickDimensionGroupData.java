package org.caleydo.view.visbricks.brick.data;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.collection.table.EDataTableDataType;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.ISegmentData;
import org.caleydo.core.data.virtualarray.SetBasedDimensionGroupData;
import org.caleydo.core.data.virtualarray.SetBasedSegmentData;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.view.visbricks.brick.layout.ASetBasedDataConfigurer;
import org.caleydo.view.visbricks.brick.layout.IBrickConfigurer;
import org.caleydo.view.visbricks.brick.layout.NominalDataConfigurer;
import org.caleydo.view.visbricks.brick.layout.NumericalDataConfigurer;

public class SetBasedBrickDimensionGroupData implements IBrickDimensionGroupData {

	private SetBasedDimensionGroupData dimensionGroupData;
	private ASetBasedDataConfigurer setBasedDataConfigurer;

	public SetBasedBrickDimensionGroupData(
			SetBasedDimensionGroupData dimensionGroupData) {
		this.dimensionGroupData = dimensionGroupData;
		if (dimensionGroupData.getSet().getSetType()
				.equals(EDataTableDataType.NUMERIC)) {
			setBasedDataConfigurer = new NumericalDataConfigurer(
					dimensionGroupData.getSet());
		} else {
			setBasedDataConfigurer = new NominalDataConfigurer(
					dimensionGroupData.getSet());
		}
	}

	@Override
	public ContentVirtualArray getSummaryBrickVA() {
		return dimensionGroupData.getSummaryVA();
	}

	@Override
	public ArrayList<ContentVirtualArray> getSegmentBrickVAs() {
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

		for (ISegmentData data : segmentData) {
			segmentBrickData.add(new SetBasedBrickData(
					(SetBasedSegmentData) data));
		}

		return segmentBrickData;
	}

	@Override
	public IBrickData getSummaryBrickData() {
		SetBasedSegmentData tempSegmentData = new SetBasedSegmentData(
				(ASetBasedDataDomain) getDataDomain(),
				dimensionGroupData.getSet(), getSummaryBrickVA(), new Group(),
				dimensionGroupData);
		return new SetBasedBrickData(tempSegmentData);
	}

	@Override
	public IBrickSortingStrategy getDefaultSortingStrategy() {
		return new AverageValueSortingStrategy();
	}

}
