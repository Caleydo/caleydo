package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.event.view.OpenViewEvent;
import org.caleydo.core.manager.event.view.grouper.CompareGroupsEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;

public class CompareGroupsItem
	extends AContextMenuItem {

	public CompareGroupsItem(ArrayList<ISet> setsToCompare) {
		super();

		setText("Compare Groups");
		
		OpenViewEvent openViewEvent = new OpenViewEvent();
		openViewEvent.setViewType("org.caleydo.view.compare");
		openViewEvent.setSender(this);
		registerEvent(openViewEvent);
		
		CompareGroupsEvent event = new CompareGroupsEvent(setsToCompare);
		event.setSender(this);
		registerEvent(event);
	}
}
