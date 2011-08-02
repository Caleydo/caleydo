package org.caleydo.view.matchmaker.contextmenu;

import java.util.ArrayList;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.manager.event.data.ClusterSetEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.ContextMenuItem;

public class ClusterTableItem
	extends ContextMenuItem {

	public ClusterTableItem(ArrayList<DataTable> tables) {

		setLabel("Cluster");
		
		ClusterSetEvent event = new ClusterSetEvent(tables);
		event.setSender(this);
		registerEvent(event);
	}
}
