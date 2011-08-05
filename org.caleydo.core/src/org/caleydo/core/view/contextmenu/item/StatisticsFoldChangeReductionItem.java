package org.caleydo.core.view.contextmenu.item;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.manager.event.data.StatisticsFoldChangeReductionEvent;
import org.caleydo.core.view.contextmenu.AContextMenuItem;

public class StatisticsFoldChangeReductionItem
	extends AContextMenuItem {

	public StatisticsFoldChangeReductionItem(DataTable table1, DataTable table2) {

		setLabel("Fold Change Filter");
		StatisticsFoldChangeReductionEvent event = new StatisticsFoldChangeReductionEvent(table1, table2);
		event.setSender(this);
		registerEvent(event);
	}
}
