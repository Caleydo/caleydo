package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import java.util.Set;

import org.caleydo.core.manager.event.view.grouper.CopyGroupsEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;

public class CopyGroupsItem
	extends AContextMenuItem {

	public CopyGroupsItem(Set<Integer> setGroupsToCopy) {
		super();
		setText("Copy");
		CopyGroupsEvent event = new CopyGroupsEvent(setGroupsToCopy);
		event.setSender(this);
		registerEvent(event);
	}
}
