package org.caleydo.core.data.container;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.data.virtualarray.group.Group;

/**
 * Implementation of {@link ISegmentData} for table based data sets ({@link ATableBasedDataDomain}).
 * 
 * @author Partl
 */
public class TableBasedSegmentData
	extends ADataContainer
	implements ISegmentData {

	private Group group;
	private DataTable table;
	private TableBasedDimensionGroupData dimensionGroupData;

	public TableBasedSegmentData(ATableBasedDataDomain dataDomain, RecordPerspective recordPerspective,
		DimensionPerspective dimensionPerspective, Group group,
		TableBasedDimensionGroupData dimensionGroupData) {
		super(dataDomain, recordPerspective, dimensionPerspective);
		this.group = group;
		this.dimensionGroupData = dimensionGroupData;
	}

	@Override
	public Group getGroup() {
		return group;
	}

	@Override
	public String getLabel() {
		return "Group " + group.getGroupID() + " in " + table.getLabel();
	}
}
