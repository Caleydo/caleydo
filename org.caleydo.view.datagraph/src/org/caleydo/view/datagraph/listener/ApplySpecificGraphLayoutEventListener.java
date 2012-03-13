package org.caleydo.view.datagraph.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.datagraph.GLDataViewIntegrator;
import org.caleydo.view.datagraph.event.ApplySpecificGraphLayoutEvent;

public class ApplySpecificGraphLayoutEventListener extends AEventListener<GLDataViewIntegrator> {

	@Override
	public void handleEvent(AEvent event) {

		if (event instanceof ApplySpecificGraphLayoutEvent) {
			ApplySpecificGraphLayoutEvent e = (ApplySpecificGraphLayoutEvent) event;
			handler.applyGraphLayout(e.getGraphLayoutClass());
		}

	}

}
