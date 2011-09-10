package org.caleydo.core.data.perspective;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.filter.RecordFilterManager;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;

/**
 * Implementation of {@link ADataPerspective} for records.
 * 
 * @author Alexander Lex
 */
@XmlRootElement
public class RecordPerspective
	extends ADataPerspective<RecordVirtualArray, RecordGroupList, RecordVADelta, RecordFilterManager> {

	private static int recordDataRunningNumber = 0;

	public RecordPerspective() {
	}

	public RecordPerspective(ATableBasedDataDomain dataDomain) {
		super(dataDomain);
	}

	@Override
	protected void init() {
		perspectiveID = "RecordPerspective_" + recordDataRunningNumber;
		recordDataRunningNumber++;
		filterManager = new RecordFilterManager(dataDomain, this);
		idType = dataDomain.getRecordIDType();
	}

	@Override
	protected RecordGroupList createGroupList() {
		return new RecordGroupList();
	}

	@Override
	protected void createFilterManager() {
		filterManager = new RecordFilterManager(dataDomain, this);
	}

	@Override
	protected RecordVirtualArray newConcreteVirtualArray(List<Integer> indexList) {
		return new RecordVirtualArray(perspectiveID, indexList);
	}

	@Override
	protected String getLabel(Integer id) {
		return dataDomain.getRecordLabel(id);
	}
	
	@Override
	protected List<Integer> getIDList()
	{
		return dataDomain.getTable().getRowIDList();
	}
}
