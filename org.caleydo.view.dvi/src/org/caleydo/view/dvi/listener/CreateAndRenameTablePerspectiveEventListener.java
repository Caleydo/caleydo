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
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.event.CreateAndRenameTablePerspectiveEvent;
import org.caleydo.view.dvi.event.RenameLabelHolderEvent;

/**
 *
 *
 * @author Christian
 * 
 */
public class CreateAndRenameTablePerspectiveEventListener extends AEventListener<GLDataViewIntegrator> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof CreateAndRenameTablePerspectiveEvent) {
			CreateAndRenameTablePerspectiveEvent e = (CreateAndRenameTablePerspectiveEvent) event;
			TablePerspective tablePerspective = e.getCreator().create();

			RenameLabelHolderEvent renameLabelHolderEvent = new RenameLabelHolderEvent(tablePerspective);
			renameLabelHolderEvent.setSender(this);
			EventPublisher.trigger(renameLabelHolderEvent);
		}

	}

}
