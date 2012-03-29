package org.caleydo.core.view.contextmenu.item;

import java.util.ArrayList;
import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.event.data.StatisticsPValueReductionEvent;
import org.caleydo.core.view.contextmenu.AContextMenuItem;

public class StatisticsPValueReductionItem
	extends AContextMenuItem {

	public StatisticsPValueReductionItem(ArrayList<DataContainer> dataContainers) {

		setLabel("Variance Filter");

		StatisticsPValueReductionEvent event = new StatisticsPValueReductionEvent(dataContainers);
		event.setSender(this);
		registerEvent(event);
	}
}
