package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import java.util.Set;

import org.caleydo.core.manager.event.view.grouper.DeleteGroupsEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;

public class DeleteGroupsItem
	extends AContextMenuItem {

	public DeleteGroupsItem(Set<Integer> setGroupsToDelete) {
		super();
		setText("Delete");
		DeleteGroupsEvent event = new DeleteGroupsEvent(setGroupsToDelete);
		event.setSender(this);
		registerEvent(event);
	}
}
