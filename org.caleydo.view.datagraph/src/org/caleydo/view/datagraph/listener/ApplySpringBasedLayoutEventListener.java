package org.caleydo.view.datagraph.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.datagraph.GLDataGraph;
import org.caleydo.view.datagraph.event.ApplySpringBasedLayoutEvent;

public class ApplySpringBasedLayoutEventListener extends AEventListener<GLDataGraph> {

	@Override
	public void handleEvent(AEvent event) {
		
		if(event instanceof ApplySpringBasedLayoutEvent) {
			handler.applySpringBasedLayout();
		}
		
	}

}
