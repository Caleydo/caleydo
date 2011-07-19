package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import java.util.ArrayList;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.manager.event.data.StatisticsPValueReductionEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;

public class StatisticsPValueReductionItem
	extends AContextMenuItem {

	public StatisticsPValueReductionItem(ArrayList<DataTable> sets) {
		super();
		setText("Variance Filter");
		StatisticsPValueReductionEvent event = new StatisticsPValueReductionEvent(sets);
		event.setSender(this);
		registerEvent(event);
	}
}
