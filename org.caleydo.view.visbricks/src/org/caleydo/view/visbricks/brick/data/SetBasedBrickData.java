package org.caleydo.view.visbricks.brick.data;

import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.collection.table.DimensionData;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.SetBasedSegmentData;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.visbricks.brick.GLBrick;

public class SetBasedBrickData implements IBrickData {

	private SetBasedSegmentData segmentData;
	private double averageValue;

	public SetBasedBrickData(SetBasedSegmentData segmentData) {
		this.segmentData = segmentData;
		calculateAverageValue();
	}

	@Override
	public IDataDomain getDataDomain() {
		// TODO Auto-generated method stub
		return segmentData.getDataDomain();
	}

	@Override
	public RecordVirtualArray getRecordVA() {
		// TODO Auto-generated method stub
		return segmentData.getRecordVA();
	}

	@Override
	public Group getGroup() {
		// TODO Auto-generated method stub
		return segmentData.getGroup();
	}

	@Override
	public void setBrickData(GLBrick brick) {
		brick.setDataDomain((ATableBasedDataDomain) getDataDomain());
		brick.setRecordVA(getGroup(), getRecordVA());
	}

	private void calculateAverageValue() {
		int count = 0;
		// if (recordVA == null)
		// throw new IllegalStateException("recordVA was null");
		for (Integer contenID : getRecordVA()) {
			DimensionData dimensionData = segmentData.getTable().getDimensionData(
					DataTable.DIMENSION);
			if (dimensionData == null) {
				averageValue = 0;
				return;
			}

			DimensionVirtualArray dimensionVA = dimensionData.getDimensionVA();

			if (dimensionVA == null) {
				averageValue = 0;
				return;
			}
			for (Integer dimensionID : dimensionVA) {
				float value = segmentData.getTable().get(dimensionID)
						.getFloat(DataRepresentation.NORMALIZED, contenID);
				if (!Float.isNaN(value)) {
					averageValue += value;
					count++;
				}
			}
		}
		averageValue /= count;
	}

	public double getAverageValue() {
		return averageValue;
	}

	@Override
	public String getLabel() {
		return segmentData.getLabel();
	}

}
