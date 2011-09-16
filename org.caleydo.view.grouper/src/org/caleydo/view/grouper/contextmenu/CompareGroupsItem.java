package org.caleydo.view.grouper.contextmenu;

import java.util.ArrayList;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.event.view.OpenMatchmakerViewEvent;
import org.caleydo.core.view.contextmenu.AContextMenuItem;

public class CompareGroupsItem extends AContextMenuItem {

	public CompareGroupsItem(ArrayList<DataTable> setsToCompare) {

		setLabel("Compare Groups in Matchmaker");

		OpenMatchmakerViewEvent openViewEvent = new OpenMatchmakerViewEvent();
		openViewEvent.setViewType("org.caleydo.view.matchmaker");
		openViewEvent.setSender(this);
		openViewEvent.setTablesToCompare(setsToCompare);
		registerEvent(openViewEvent);
	}
}
