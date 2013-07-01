/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.event.CreateTablePerspectiveEvent;

public class CreateTablePerspectiveEventListener extends AEventListener<GLDataViewIntegrator> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof CreateTablePerspectiveEvent) {
			CreateTablePerspectiveEvent e = (CreateTablePerspectiveEvent) event;
			handler.createTablePerspective(e.getDataDomain(), e.getRecordPerspectiveID(),
					e.isCreateRecordPerspective(), e.getRecordVA(), e.getRecordGroup(),
					e.getDimensionPerspectiveID(), e.isCreateDimensionPerspective(),
					e.getDimensionVA(), e.getDimensionGroup());
		}

	}

}
