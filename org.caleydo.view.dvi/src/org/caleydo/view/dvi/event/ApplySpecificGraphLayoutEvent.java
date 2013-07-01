/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.event;

import org.caleydo.core.event.AEvent;
import org.caleydo.view.dvi.layout.AGraphLayout;

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
