package org.caleydo.view.datagraph.event;

import org.caleydo.core.event.AEvent;
import org.caleydo.view.datagraph.layout.AGraphLayout;

public class ApplySpecificGraphLayoutEvent extends AEvent {

	private Class<? extends AGraphLayout> graphLayoutClass;

	@Override
	public boolean checkIntegrity() {
		return graphLayoutClass != null;
	}

	public Class<? extends AGraphLayout> getGraphLayoutClass() {
		return graphLayoutClass;
	}

	public void setGraphLayoutClass(Class<? extends AGraphLayout> graphLayoutClass) {
		this.graphLayoutClass = graphLayoutClass;
	}

}
