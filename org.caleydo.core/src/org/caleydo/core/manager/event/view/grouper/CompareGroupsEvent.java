package org.caleydo.core.manager.event.view.grouper;

import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.event.AEvent;

public class CompareGroupsEvent
	extends AEvent {

	private ArrayList<ISet> setsToCompare;

	public CompareGroupsEvent(ArrayList<ISet> setsToCompare) {

		this.setsToCompare = setsToCompare;
	}

	@Override
	public boolean checkIntegrity() {
		return setsToCompare != null;
	}

	public ArrayList<ISet> getSets() {
		return setsToCompare;
	}

	public void setSets(ArrayList<ISet> setsToCompare) {
		this.setsToCompare = setsToCompare;
	}
}
