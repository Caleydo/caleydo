/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.TablePerspectivesChangedEvent;
import org.caleydo.view.dvi.GLDataViewIntegrator;

/**
 * Listener for {@link TablePerspectivesChangedEvent}
 * 
 * @author Partl
 * @author Alexander Lex
 */
public class TablePerspectivesCangedListener extends AEventListener<GLDataViewIntegrator> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof TablePerspectivesChangedEvent) {
			TablePerspectivesChangedEvent tablePerspectivesChangedEvent = (TablePerspectivesChangedEvent) event;
			handler.updateView(tablePerspectivesChangedEvent.getView());
		}
	}

}
