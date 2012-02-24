package org.caleydo.view.datagraph.contextmenu;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.event.AddGroupsToVisBricksEvent;

public class AddGroupToVisBricksItem extends AContextMenuItem {

	public AddGroupToVisBricksItem(GLVisBricks view, DataContainer dataContainer) {

		setLabel("Add to " + view.getViewLabel());

		AddGroupsToVisBricksEvent event = new AddGroupsToVisBricksEvent(dataContainer);
		event.setReceiver(view);
		event.setSender(this);
		registerEvent(event);
	}

}
