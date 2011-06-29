package org.caleydo.view.visbricks.event;

import java.util.ArrayList;

import org.caleydo.core.data.virtualarray.ADimensionGroupData;
import org.caleydo.core.manager.event.AEvent;

public class AddGroupsToVisBricksEvent extends AEvent {
	
	private ArrayList<ADimensionGroupData> dimensionGroupData;

	@Override
	public boolean checkIntegrity() {
		return true;
	}
	


	public void setDimensionGroupData(ArrayList<ADimensionGroupData> dimensionGroupData) {
		this.dimensionGroupData = dimensionGroupData;
	}
	


	public ArrayList<ADimensionGroupData> getDimensionGroupData() {
		return dimensionGroupData;
	}

}
