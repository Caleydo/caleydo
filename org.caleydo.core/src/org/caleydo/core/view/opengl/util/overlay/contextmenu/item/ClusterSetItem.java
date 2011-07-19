package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import java.util.ArrayList;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.manager.event.data.ClusterSetEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;

public class ClusterSetItem
	extends AContextMenuItem {

	public ClusterSetItem(ArrayList<DataTable> sets) {
		super();
		setText("Cluster");
		ClusterSetEvent event = new ClusterSetEvent(sets);
		event.setSender(this);
		registerEvent(event);
	}
}
