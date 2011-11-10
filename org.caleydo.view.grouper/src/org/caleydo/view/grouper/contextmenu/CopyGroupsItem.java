package org.caleydo.view.grouper.contextmenu;

import java.util.Set;

import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.grouper.event.CopyGroupsEvent;

public class CopyGroupsItem extends AContextMenuItem {

	public CopyGroupsItem(Set<Integer> setGroupsToCopy, String dataDomainID) {

		setLabel("Copy");

		CopyGroupsEvent event = new CopyGroupsEvent(setGroupsToCopy);
		event.setSender(this);
		event.setDataDomainID(dataDomainID);
		registerEvent(event);
	}
}
