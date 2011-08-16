package org.caleydo.core.data.virtualarray;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.virtualarray.group.Group;

public abstract class ADimensionGroupData {

	public abstract RecordVirtualArray getSummaryVA();
	
	public abstract ArrayList<RecordVirtualArray> getSegmentVAs();
	
	public abstract IDataDomain getDataDomain();
	
	public abstract ArrayList<Group> getGroups();
	
	public abstract int getID();
	
	public abstract List<ISegmentData> getSegmentData();
	
	public abstract String getLabel();
	
}
