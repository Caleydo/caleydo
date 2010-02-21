package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.event.view.grouper.CompareGroupsEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;

public class CompareGroupsItem
	extends AContextMenuItem {

	public CompareGroupsItem(ArrayList<ISet> setsToCompare) {
		super();

		// Trigger lazy plugin loading and creation of statistics performer
		GeneralManager.get().getRStatisticsPerformer();

		setText("Compare Groups");
		CompareGroupsEvent event = new CompareGroupsEvent(setsToCompare);
		event.setSender(this);
		registerEvent(event);
	}
}
