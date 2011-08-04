package org.caleydo.view.grouper.contextmenu;

import java.util.Set;

import org.caleydo.core.view.contextmenu.ContextMenuItem;
import org.caleydo.view.grouper.event.CreateGroupEvent;

public class CreateGroupItem extends ContextMenuItem {

	public CreateGroupItem(Set<Integer> setContainedGroups, String dataDomainID) {
		setLabel("Create Group");

		CreateGroupEvent event = new CreateGroupEvent(setContainedGroups);
		event.setSender(this);
		event.setDataDomainID(dataDomainID);
		registerEvent(event);
	}
}
