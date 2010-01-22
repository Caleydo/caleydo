package org.caleydo.core.manager.event.view;

import java.util.List;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.view.IView;

public abstract class ViewEvent
	extends AEvent {

	/** list of view-ids the event is related to */
	protected List<IView> views = null;

	public List<IView> getViewIDs() {
		return views;
	}

	public void setViews(List<IView> views) {
		this.views = views;
	}

	@Override
	public boolean checkIntegrity() {
		if (views == null)
			return false;

		return true;
	}

}