package org.caleydo.core.view;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.ISetBasedDataDomain;
import org.caleydo.core.manager.event.view.NewSetEvent;
import org.caleydo.core.view.opengl.canvas.listener.NewSetListener;

public class ASetBasedView
{

	ISetBasedDataDomain useCase;

	public ASetBasedView() {
	
		registerEventListeners();

	}

	/**
	 * Data set which the view operates on.
	 */
	protected ISet set;

	private NewSetListener newSetListener;

	
	public void setSet(ISet set) {
		this.set = set;
	}

	
	public ISet getSet() {
		return set;
	}

	
	public void registerEventListeners() {
		newSetListener = new NewSetListener();
		newSetListener.setHandler(this);
		eventPublisher.addListener(NewSetEvent.class, newSetListener);
		// default implementations does not react on events
	}

	@Override
	public void unregisterEventListeners() {
		if (newSetListener != null) {
			eventPublisher.removeListener(newSetListener);
			newSetListener = null;
		}
	}
}
