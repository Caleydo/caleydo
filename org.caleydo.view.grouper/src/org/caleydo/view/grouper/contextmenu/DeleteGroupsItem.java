package org.caleydo.view.grouper.contextmenu;

import java.util.Set;

import org.caleydo.core.view.contextmenu.ContextMenuItem;
import org.caleydo.view.grouper.event.DeleteGroupsEvent;

public class DeleteGroupsItem extends ContextMenuItem {

	public DeleteGroupsItem(Set<Integer> setGroupsToDelete, String dataDomainID) {

		setLabel("Delete");
		
		DeleteGroupsEvent event = new DeleteGroupsEvent(setGroupsToDelete);
		event.setSender(this);
		event.setDataDomainID(dataDomainID);
		registerEvent(event);
	}
}
