package org.caleydo.view.datagraph.contextmenu;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.event.AddGroupsToVisBricksEvent;

public class AddGroupToVisBricksItem extends AContextMenuItem {

	public AddGroupToVisBricksItem(GLVisBricks view, DataContainer dataContainer) {

		setLabel("Add to " + view.getViewLabel());

		List<DataContainer> dataContainers = new ArrayList<DataContainer>();
		dataContainers.add(dataContainer);

		AddGroupsToVisBricksEvent event = new AddGroupsToVisBricksEvent(dataContainers);
		event.setReceiver(view);
		event.setSender(this);
		registerEvent(event);
	}

}
