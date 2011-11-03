package org.caleydo.view.datagraph.contextmenu;

import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.datagraph.event.AddDataContainerEvent;

public class AddDataContainerItem extends AContextMenuItem {

	public AddDataContainerItem(AddDataContainerEvent event) {
		setLabel("Create Data Container");
		event.setSender(this);
		registerEvent(event);
	}
}
