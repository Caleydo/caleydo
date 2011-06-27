package org.caleydo.core.data.virtualarray;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.manager.datadomain.IDataDomain;

public interface IDimensionGroupData {

	public ContentVirtualArray getSummaryVA();
	
	public ArrayList<ContentVirtualArray> getSegmentVAs();
	
	public IDataDomain getDataDomain();
	
	public ArrayList<Group> getGroups();
	
	public int getID();
	
	public List<ISegmentData> getSegmentData();
	
}
