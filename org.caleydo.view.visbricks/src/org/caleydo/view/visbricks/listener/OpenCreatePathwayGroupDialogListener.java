package org.caleydo.view.visbricks.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.event.OpenCreatePathwayGroupDialogEvent;

/**
 * Listener for the event {@link OpenCreatePathwayGroupDialogEvent}.
 * 
 * @author Partl
 * 
 */
public class OpenCreatePathwayGroupDialogListener extends
		AEventListener<GLBrick> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof OpenCreatePathwayGroupDialogEvent) {
			OpenCreatePathwayGroupDialogEvent openCreatePathwayGroupDialogEvent = (OpenCreatePathwayGroupDialogEvent) event;
			handler.openCreatePathwayGroupDialog(
					openCreatePathwayGroupDialogEvent.getSourceDataDomain(),
					openCreatePathwayGroupDialogEvent.getSourceRecordVA());
		}
	}

}
