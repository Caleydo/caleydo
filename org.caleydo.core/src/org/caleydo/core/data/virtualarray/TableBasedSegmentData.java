package org.caleydo.core.data.virtualarray;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.virtualarray.group.Group;

/**
 * Implementation of {@link ISegmentData} for table based data sets ({@link ATableBasedDataDomain}).
 * 
 * @author Partl
 *
 */
public class TableBasedSegmentData implements ISegmentData {

	private ATableBasedDataDomain dataDomain;
	private RecordVirtualArray recordVA;
	private Group group;
	private DataTable table;
	private TableBasedDimensionGroupData dimensionGroupData;

	public TableBasedSegmentData(ATableBasedDataDomain dataDomain, DataTable table,
			RecordVirtualArray recordVA, Group group,
			TableBasedDimensionGroupData dimensionGroupData) {
		this.table = table;
		this.dataDomain = dataDomain;
		this.recordVA = recordVA;
		this.group = group;
		this.dimensionGroupData = dimensionGroupData;
	}

	@Override
	public IDataDomain getDataDomain() {
		// TODO Auto-generated method stub
		return dataDomain;
	}

	@Override
	public RecordVirtualArray getRecordVA() {
		// TODO Auto-generated method stub
		return recordVA;
	}

	@Override
	public Group getGroup() {
		// TODO Auto-generated method stub
		return group;
	}

	@Override
	public String getLabel() {
		return "Group " + group.getGroupID() + " in " + table.getLabel();
	}

	/**
	 * @return The data table this segment group is based on.
	 */
	public DataTable getTable() {
		return table;
	}

}
