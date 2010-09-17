package org.caleydo.core.view.opengl.util.overlay.contextmenu.item;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.event.data.StatisticsFoldChangeReductionEvent;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;

public class StatisticsFoldChangeReductionItem
	extends AContextMenuItem {

	public StatisticsFoldChangeReductionItem(ISet set1, ISet set2) {
		super();
		setText("Fold Change Filter");
		StatisticsFoldChangeReductionEvent event = new StatisticsFoldChangeReductionEvent(set1, set2);
		event.setSender(this);
		registerEvent(event);
	}
}
