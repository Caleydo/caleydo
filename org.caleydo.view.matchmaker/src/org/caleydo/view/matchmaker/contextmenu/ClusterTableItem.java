package org.caleydo.view.matchmaker.contextmenu;

import java.util.ArrayList;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.event.data.ClusterSetEvent;
import org.caleydo.core.view.contextmenu.AContextMenuItem;

public class ClusterTableItem
	extends AContextMenuItem {

	public ClusterTableItem(ArrayList<DataContainer> tables) {

		setLabel("Cluster");
		
		ClusterSetEvent event = new ClusterSetEvent(tables);
		event.setSender(this);
		registerEvent(event);
	}
}
