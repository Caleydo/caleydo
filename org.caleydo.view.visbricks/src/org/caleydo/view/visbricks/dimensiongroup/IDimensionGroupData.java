package org.caleydo.view.visbricks.dimensiongroup;

import java.util.ArrayList;

import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.view.visbricks.brick.layout.IBrickConfigurer;

public interface IDimensionGroupData {

	public ContentVirtualArray getSummaryBrickVA();
	
	public ArrayList<ContentVirtualArray> getSegmentBrickVAs();
	
	public IDataDomain getDataDomain();
	
	public IBrickConfigurer getBrickConfigurer();
	
}
