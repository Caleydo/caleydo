package org.caleydo.core.view.opengl.canvas.grouper.listeners;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.grouper.CopyGroupsEvent;
import org.caleydo.core.manager.event.view.grouper.PasteGroupsEvent;
import org.caleydo.core.view.opengl.canvas.grouper.GLGrouper;

public class PasteGroupsEventListener
	extends AEventListener<GLGrouper> {

	@Override
	public void handleEvent(AEvent event) {
		PasteGroupsEvent pasteGroupsEvent = null;
		if (event instanceof CopyGroupsEvent) {
			pasteGroupsEvent = (PasteGroupsEvent) event;
			handler.pasteGroups(pasteGroupsEvent.getParentGroupID());
		}
		
	}

}
