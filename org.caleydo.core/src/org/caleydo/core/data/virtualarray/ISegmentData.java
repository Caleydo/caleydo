package org.caleydo.core.data.virtualarray;

import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.manager.datadomain.IDataDomain;

public interface ISegmentData {

	public IDataDomain getDataDomain();
	
	public RecordVirtualArray getRecordVA();
	
	public Group getGroup();
	
	public String getLabel();
	
}
