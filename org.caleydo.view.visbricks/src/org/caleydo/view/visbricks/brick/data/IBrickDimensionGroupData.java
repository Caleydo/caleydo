package org.caleydo.view.visbricks.brick.data;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.container.ADimensionGroupData;
import org.caleydo.core.data.container.IDataContainer;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.visbricks.brick.layout.IBrickConfigurer;

public interface IBrickDimensionGroupData extends IDataContainer {

	public RecordVirtualArray getSummaryBrickVA();

	public ArrayList<RecordVirtualArray> getSegmentBrickVAs();

	public IBrickConfigurer getBrickConfigurer();

	public ArrayList<Group> getGroups();

	public int getID();

	public List<IBrickData> getSegmentBrickData();

	public IBrickData getSummaryBrickData();

	public IBrickSortingStrategy getDefaultSortingStrategy();

}
