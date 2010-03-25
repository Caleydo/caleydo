package org.caleydo.core.manager.event.data;

import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.event.AEvent;

public class StatisticsTwoSidedTTestReductionEvent
	extends AEvent {

	private ArrayList<ISet> sets;

	public StatisticsTwoSidedTTestReductionEvent(ArrayList<ISet> sets) {
		this.sets = sets;
	}

	public void setSets(ArrayList<ISet> sets) {
		this.sets = sets;
	}

	public ArrayList<ISet> getSets() {
		return sets;
	}

	@Override
	public boolean checkIntegrity() {

		if (sets.size() == 0)
			return false;

		return true;
	}
}
