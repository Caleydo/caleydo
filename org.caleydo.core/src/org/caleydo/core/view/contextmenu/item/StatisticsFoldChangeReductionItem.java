package org.caleydo.core.view.contextmenu.item;

import org.caleydo.core.event.data.StatisticsFoldChangeReductionEvent;
import org.caleydo.core.view.contextmenu.AContextMenuItem;

public class StatisticsFoldChangeReductionItem
	extends AContextMenuItem {

	public StatisticsFoldChangeReductionItem(StatisticsFoldChangeReductionEvent event) {

		setLabel("Fold Change Filter");
		event.setSender(this);
		registerEvent(event);
	}
}
