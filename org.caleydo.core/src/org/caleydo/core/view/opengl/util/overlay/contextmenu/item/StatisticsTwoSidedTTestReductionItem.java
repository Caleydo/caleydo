package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import java.util.ArrayList;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.manager.event.data.StatisticsTwoSidedTTestReductionEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.ContextMenuItem;

public class StatisticsTwoSidedTTestReductionItem
	extends ContextMenuItem {

	public StatisticsTwoSidedTTestReductionItem(ArrayList<DataTable> sets) {

		setLabel("Two-Sided T-Test Filter");

		StatisticsTwoSidedTTestReductionEvent event = new StatisticsTwoSidedTTestReductionEvent(sets);
		event.setSender(this);
		registerEvent(event);
	}
}
