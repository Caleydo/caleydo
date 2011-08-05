package org.caleydo.view.grouper.contextmenu;

import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.grouper.event.RenameGroupEvent;

public class RenameGroupItem extends AContextMenuItem {

	public RenameGroupItem(int groupID, String dataDomainID) {

		setLabel("Rename Group");

		RenameGroupEvent event = new RenameGroupEvent(groupID);
		event.setSender(this);
		event.setDataDomainID(dataDomainID);
		registerEvent(event);
	}
}
