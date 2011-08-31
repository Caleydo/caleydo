package org.caleydo.view.datagraph;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.container.ADimensionGroupData;
import org.caleydo.core.data.container.ISegmentData;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;

public class FakeDimensionGroupData extends ADimensionGroupData {
	
	private int id;
	
	public FakeDimensionGroupData(int id) {
		this.id = id;
	}

	@Override
	public RecordVirtualArray getSummaryVA() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<RecordVirtualArray> getSegmentVAs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Group> getGroups() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public List<ISegmentData> getSegmentData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "fake";
	}

	@Override
	public ISegmentData getSummarySegementData() {
		// TODO Auto-generated method stub
		return null;
	}

}
