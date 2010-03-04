package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.event.view.OpenCompareViewEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;

public class CompareGroupsItem
	extends AContextMenuItem {

	public CompareGroupsItem(ArrayList<ISet> setsToCompare) {
		super();

		setText("Compare Groups");
		
		OpenCompareViewEvent openViewEvent = new OpenCompareViewEvent();
		openViewEvent.setViewType("org.caleydo.view.compare");
		openViewEvent.setSender(this);
		openViewEvent.setSetsToCompare(setsToCompare);
		registerEvent(openViewEvent);
	}
}
