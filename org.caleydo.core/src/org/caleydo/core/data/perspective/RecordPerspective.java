package org.caleydo.core.data.perspective;

import java.util.List;
import java.util.UUID;

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

	public RecordPerspective() {
	}

	public RecordPerspective(ATableBasedDataDomain dataDomain) {
		super(dataDomain);
	}

	@Override
	protected void init() {
		// if this perspective is de-serialized the perspectiveID is already set.
		if (perspectiveID == null)
			perspectiveID = "RecordPerspective_" + UUID.randomUUID();
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
		return new RecordVirtualArray(idType, indexList);
	}

	@Override
	protected String getElementLabel(Integer id) {
		return dataDomain.getRecordLabel(id);
	}

	@Override
	protected List<Integer> getIDList() {
		return dataDomain.getTable().getRowIDList();
	}

}
