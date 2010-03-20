package org.caleydo.core.manager.event.data;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.event.AEvent;

public class StatisticsPValueReductionEvent
	extends AEvent {

	private ISet set;
	
	public StatisticsPValueReductionEvent(ISet set) {
		this.set = set;
	}
	
	public void setSet(ISet set) {
		this.set = set;
	}
	
	public ISet getSet() {
		return set;
	}
	
	@Override
	public boolean checkIntegrity() {
		
		if (set == null)
			return false;
		
		return true;
	}
}
