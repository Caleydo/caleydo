package org.caleydo.core.data.virtualarray.group;

import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.delta.RecordVADelta;

@XmlType
public class RecordGroupList
	extends GroupList<RecordGroupList, RecordVirtualArray, RecordVADelta> {
	public RecordGroupList createInstance() {
		return new RecordGroupList();
	}
}
