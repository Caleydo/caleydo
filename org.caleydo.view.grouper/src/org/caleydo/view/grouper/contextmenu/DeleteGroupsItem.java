package org.caleydo.view.grouper.contextmenu;

import java.util.Set;

import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;
import org.caleydo.view.grouper.event.DeleteGroupsEvent;

public class DeleteGroupsItem extends AContextMenuItem {

	public DeleteGroupsItem(Set<Integer> setGroupsToDelete) {
		super();
		setText("Delete");
		DeleteGroupsEvent event = new DeleteGroupsEvent(setGroupsToDelete);
		event.setSender(this);
		registerEvent(event);
	}
}
