package org.caleydo.view.visbricks.brick.data;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.view.visbricks.brick.layout.IBrickConfigurer;

public interface IBrickDimensionGroupData {

	public RecordVirtualArray getSummaryBrickVA();
	
	public ArrayList<RecordVirtualArray> getSegmentBrickVAs();
	
	public IDataDomain getDataDomain();
	
	public IBrickConfigurer getBrickConfigurer();
	
	public ArrayList<Group> getGroups();
	
	public int getID();
	
	public List<IBrickData> getSegmentBrickData();
	
	public IBrickData getSummaryBrickData();
	
	public IBrickSortingStrategy getDefaultSortingStrategy();
	
}
