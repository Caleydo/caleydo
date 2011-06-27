package org.caleydo.view.visbricks.event;

import java.util.ArrayList;

import org.caleydo.core.data.virtualarray.IDimensionGroupData;
import org.caleydo.core.manager.event.AEvent;

public class AddGroupsToVisBricksEvent extends AEvent {
	
	private ArrayList<IDimensionGroupData> dimensionGroupData;

	@Override
	public boolean checkIntegrity() {
		return true;
	}
	


	public void setDimensionGroupData(ArrayList<IDimensionGroupData> dimensionGroupData) {
		this.dimensionGroupData = dimensionGroupData;
	}
	


	public ArrayList<IDimensionGroupData> getDimensionGroupData() {
		return dimensionGroupData;
	}

}
