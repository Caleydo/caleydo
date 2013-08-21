/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.dvi.listener;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.event.CreateTablePerspectiveBeforeAddingEvent;

/**
 * Listener that first creates a {@link TablePerspective} and then forwards an {@link AddTablePerspectivesEvent}.
 *
 * @author Christian Partl
 *
 */
public class CreateAndAddTablePerspectiveEventListener extends AEventListener<GLDataViewIntegrator> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof CreateTablePerspectiveBeforeAddingEvent) {
			CreateTablePerspectiveBeforeAddingEvent e = (CreateTablePerspectiveBeforeAddingEvent) event;

			AddTablePerspectivesEvent addTablePerspectivesEvent = new AddTablePerspectivesEvent(e.getCreator().create());
			addTablePerspectivesEvent.to(e.getReceiver()).from(this);
			EventPublisher.trigger(addTablePerspectivesEvent);
		}

	}

}
