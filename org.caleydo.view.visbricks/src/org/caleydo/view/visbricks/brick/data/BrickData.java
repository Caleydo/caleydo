package org.caleydo.view.visbricks.brick.data;

import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.container.TableBasedDimensionGroupData;
import org.caleydo.core.data.container.TableBasedSegmentData;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.RecordPerspective;
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
public class BrickData extends TableBasedSegmentData implements IBrickData {

	// private TableBasedSegmentData segmentData;
	private double averageValue;

	public BrickData(ATableBasedDataDomain dataDomain,
			RecordPerspective recordPerspective,
			DimensionPerspective dimensionPerspective, Group group,
			TableBasedDimensionGroupData dimensionGroupData) {
		super(dataDomain, recordPerspective, dimensionPerspective, group,
				dimensionGroupData);
		calculateAverageValue();
	}

	@Override
	public RecordVirtualArray getRecordVA() {
		return getRecordPerspective().getVirtualArray();
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

			if (dimensionPerspective == null) {
				averageValue = 0;
				return;
			}

			DimensionVirtualArray dimensionVA = dimensionPerspective.getVirtualArray();

			if (dimensionVA == null) {
				averageValue = 0;
				return;
			}
			for (Integer dimensionID : dimensionVA) {
				float value = dataDomain.getTable().get(dimensionID)
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
	public DimensionVirtualArray getDimensionVA() {
		return dimensionPerspective.getVirtualArray();
	}

}
