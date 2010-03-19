package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.event.data.ClusterSetEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;

public class ClusterSetItem
	extends AContextMenuItem {

	public ClusterSetItem(ISet set) {
		super();
		setText("Cluster");
		ClusterSetEvent event = new ClusterSetEvent(set);
		event.setSender(this);
		registerEvent(event);
	}
}
