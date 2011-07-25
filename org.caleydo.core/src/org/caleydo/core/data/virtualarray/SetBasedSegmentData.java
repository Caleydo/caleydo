package org.caleydo.core.data.virtualarray;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomain;

public class SetBasedSegmentData implements ISegmentData {

	private ATableBasedDataDomain dataDomain;
	private RecordVirtualArray recordVA;
	private Group group;
	private DataTable dataTable;
	private SetBasedDimensionGroupData dimensionGroupData;

	public SetBasedSegmentData(ATableBasedDataDomain dataDomain, DataTable dataTable,
			RecordVirtualArray recordVA, Group group,
			SetBasedDimensionGroupData dimensionGroupData) {
		this.dataTable = dataTable;
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
		return "Group " + group.getGroupID() + " in " + dataTable.getLabel();
	}

	public DataTable getDataTable() {
		return dataTable;
	}

}
