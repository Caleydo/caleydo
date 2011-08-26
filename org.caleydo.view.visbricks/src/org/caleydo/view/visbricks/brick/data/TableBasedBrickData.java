package org.caleydo.view.visbricks.brick.data;

import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.container.TableBasedSegmentData;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.visbricks.brick.GLBrick;

/**
 * Data structure for an individual brick
 * 
 * @author Partl
 * @author Alexander Lex
 * 
 */
public class TableBasedBrickData implements IBrickData<ATableBasedDataDomain> {

	private TableBasedSegmentData segmentData;
	private double averageValue;

	public TableBasedBrickData(TableBasedSegmentData segmentData) {
		this.segmentData = segmentData;
		calculateAverageValue();
	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return segmentData.getDataDomain();
	}

	@Override
	public RecordVirtualArray getRecordVA() {
		return segmentData.getRecordPerspective().getVirtualArray();
	}

	@Override
	public Group getGroup() {
		return segmentData.getGroup();
	}

	@Override
	public void setBrickData(GLBrick brick) {
		brick.setDataDomain(getDataDomain());
		brick.setRecordVA(getGroup(), getRecordVA());
	}

	private void calculateAverageValue() {
		int count = 0;
		// if (recordVA == null)
		// throw new IllegalStateException("recordVA was null");
		for (Integer contenID : getRecordVA()) {
			DimensionPerspective dimensionData = segmentData.getDimensionPerspective();
			if (dimensionData == null) {
				averageValue = 0;
				return;
			}

			DimensionVirtualArray dimensionVA = dimensionData.getVirtualArray();

			if (dimensionVA == null) {
				averageValue = 0;
				return;
			}
			for (Integer dimensionID : dimensionVA) {
				float value = segmentData.getDataDomain().getTable().get(dimensionID)
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

	@Override
	public DimensionVirtualArray getDimensionVA() {
		return segmentData.getDimensionPerspective().getVirtualArray();
	}

}
