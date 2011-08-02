package org.caleydo.view.grouper.contextmenu;

import java.util.Set;

import org.caleydo.core.view.opengl.util.overlay.contextmenu.ContextMenuItem;
import org.caleydo.view.grouper.event.CopyGroupsEvent;

public class CopyGroupsItem extends ContextMenuItem {

	public CopyGroupsItem(Set<Integer> setGroupsToCopy) {

		setLabel("Copy");
		
		CopyGroupsEvent event = new CopyGroupsEvent(setGroupsToCopy);
		event.setSender(this);
		registerEvent(event);
	}
}
