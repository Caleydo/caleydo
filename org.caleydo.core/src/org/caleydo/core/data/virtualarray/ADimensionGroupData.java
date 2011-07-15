package org.caleydo.core.data.virtualarray;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.manager.datadomain.IDataDomain;

public abstract class ADimensionGroupData {

	public abstract ContentVirtualArray getSummaryVA();
	
	public abstract ArrayList<ContentVirtualArray> getSegmentVAs();
	
	public abstract IDataDomain getDataDomain();
	
	public abstract ArrayList<Group> getGroups();
	
	public abstract int getID();
	
	public abstract List<ISegmentData> getSegmentData();
	
	public abstract String getLabel();
	
}
