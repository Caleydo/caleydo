package org.caleydo.core.manager.event.view;

import java.util.List;

import org.caleydo.core.manager.event.AEvent;

public abstract class ViewEvent 
	extends AEvent {

	/** list of view-ids the event is related to */
	protected List<Integer> viewIDs = null;
	
	public List<Integer> getViewIDs() {
		return viewIDs;
	}

	public void setViewIDs(List<Integer> viewIDs) {
		this.viewIDs = viewIDs;
	}
	
	@Override
	public boolean checkIntegrity()
	{
		if(viewIDs == null)
			return false;
		
		return true;
	}

}