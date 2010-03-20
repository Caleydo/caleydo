package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.event.data.StatisticsPValueReductionEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;

public class StatisticsPValueReductionItem
	extends AContextMenuItem {

	public StatisticsPValueReductionItem(ArrayList<ISet> sets) {
		super();
		setText("P-Value Reduction");
		StatisticsPValueReductionEvent event = new StatisticsPValueReductionEvent(sets);
		event.setSender(this);
		registerEvent(event);
	}
}
