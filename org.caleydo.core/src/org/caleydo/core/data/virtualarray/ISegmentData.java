package org.caleydo.core.data.virtualarray;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.virtualarray.group.Group;

public interface ISegmentData {

	public IDataDomain getDataDomain();
	
	public RecordVirtualArray getRecordVA();
	
	public Group getGroup();
	
	public String getLabel();
	
}
