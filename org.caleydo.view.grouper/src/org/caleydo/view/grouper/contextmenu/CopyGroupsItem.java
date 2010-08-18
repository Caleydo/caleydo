package org.caleydo.view.grouper.contextmenu;

import java.util.Set;

import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;
import org.caleydo.view.grouper.event.CopyGroupsEvent;

public class CopyGroupsItem extends AContextMenuItem {

	public CopyGroupsItem(Set<Integer> setGroupsToCopy) {
		super();
		setText("Copy");
		CopyGroupsEvent event = new CopyGroupsEvent(setGroupsToCopy);
		event.setSender(this);
		registerEvent(event);
	}
}
