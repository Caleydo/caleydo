package org.caleydo.core.manager.event.data;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.event.AEvent;

public class StatisticsFoldChangeReductionEvent
	extends AEvent {

	private ISet set1;
	private ISet set2;
	
	public StatisticsFoldChangeReductionEvent(ISet set1, ISet set2) {
		this.set1 = set1;
		this.set2 = set2;
	}
	
	public void setSet1(ISet set1) {
		this.set1 = set1;
	}
	
	public void setSet2(ISet set2) {
		this.set2 = set2;
	}
	
	public ISet getSet1() {
		return set1;
	}
	
	public ISet getSet2() {
		return set2;
	}
	
	@Override
	public boolean checkIntegrity() {
		
		if (set1 == null || set2 == null)
			return false;
		
		return true;
	}
}
