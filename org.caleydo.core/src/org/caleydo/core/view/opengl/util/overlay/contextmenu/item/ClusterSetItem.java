package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.event.data.ClusterSetEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;

public class ClusterSetItem
	extends AContextMenuItem {

	public ClusterSetItem(ArrayList<ISet> sets) {
		super();
		setText("Cluster");
		ClusterSetEvent event = new ClusterSetEvent(sets);
		event.setSender(this);
		registerEvent(event);
	}
}
