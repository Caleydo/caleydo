package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.event.data.StatisticsPValueReductionEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;

public class StatisticsPValueReductionItem
	extends AContextMenuItem {

	public StatisticsPValueReductionItem(ISet set) {
		super();
		setText("P-Value Reduction");
		StatisticsPValueReductionEvent event = new StatisticsPValueReductionEvent(set);
		event.setSender(this);
		registerEvent(event);
	}
}
