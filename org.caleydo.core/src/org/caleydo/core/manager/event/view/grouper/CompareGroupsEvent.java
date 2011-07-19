package org.caleydo.core.manager.event.view.grouper;

import java.util.ArrayList;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.manager.event.AEvent;

public class CompareGroupsEvent
	extends AEvent {

	private ArrayList<DataTable> setsToCompare;

	public CompareGroupsEvent(ArrayList<DataTable> setsToCompare) {

		this.setsToCompare = setsToCompare;
	}

	@Override
	public boolean checkIntegrity() {
		return setsToCompare != null;
	}

	public ArrayList<DataTable> getSets() {
		return setsToCompare;
	}

	public void setSets(ArrayList<DataTable> setsToCompare) {
		this.setsToCompare = setsToCompare;
	}
}
