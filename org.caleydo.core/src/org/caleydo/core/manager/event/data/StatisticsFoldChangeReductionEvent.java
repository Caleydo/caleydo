package org.caleydo.core.manager.event.data;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.manager.event.AEvent;

public class StatisticsFoldChangeReductionEvent
	extends AEvent {

	private DataTable set1;
	private DataTable set2;

	public StatisticsFoldChangeReductionEvent(DataTable set1, DataTable set2) {
		this.set1 = set1;
		this.set2 = set2;
	}

	public void setSet1(DataTable set1) {
		this.set1 = set1;
	}

	public void setSet2(DataTable set2) {
		this.set2 = set2;
	}

	public DataTable getSet1() {
		return set1;
	}

	public DataTable getSet2() {
		return set2;
	}

	@Override
	public boolean checkIntegrity() {

		if (set1 == null || set2 == null)
			return false;

		return true;
	}
}
