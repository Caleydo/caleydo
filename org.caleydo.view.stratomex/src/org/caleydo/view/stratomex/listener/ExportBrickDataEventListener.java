/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.stratomex.brick.GLBrick;
import org.caleydo.view.stratomex.event.ExportBrickDataEvent;

/**
 * Listener for {@link ExportBrickDataEvent}.
 *
 * @author Christian Partl
 *
 */
public class ExportBrickDataEventListener extends AEventListener<GLBrick> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ExportBrickDataEvent) {
			ExportBrickDataEvent exportBrickDataEvent = (ExportBrickDataEvent) event;
			GLBrick brick = exportBrickDataEvent.getBrick();
			if (handler == brick) {
				handler.exportData(exportBrickDataEvent.isExportIdentifiersOnly());
			}
		}
	}

}
