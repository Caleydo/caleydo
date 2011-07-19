package org.caleydo.core.manager.event.data;

import java.util.ArrayList;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.manager.event.AEvent;

public class StatisticsPValueReductionEvent
	extends AEvent {

	private ArrayList<DataTable> sets;

	public StatisticsPValueReductionEvent(ArrayList<DataTable> sets) {
		this.sets = sets;
	}

	public void setSets(ArrayList<DataTable> sets) {
		this.sets = sets;
	}

	public ArrayList<DataTable> getSets() {
		return sets;
	}

	@Override
	public boolean checkIntegrity() {

		if (sets.size() == 0)
			return false;

		return true;
	}
}
