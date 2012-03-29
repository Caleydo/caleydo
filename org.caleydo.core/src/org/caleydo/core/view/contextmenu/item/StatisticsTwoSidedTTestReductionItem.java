package org.caleydo.core.view.contextmenu.item;

import java.util.ArrayList;
import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.event.data.StatisticsTwoSidedTTestReductionEvent;
import org.caleydo.core.view.contextmenu.AContextMenuItem;

public class StatisticsTwoSidedTTestReductionItem
	extends AContextMenuItem {

	public StatisticsTwoSidedTTestReductionItem(ArrayList<DataContainer> dataContainers) {

		setLabel("Two-Sided T-Test Filter");

		StatisticsTwoSidedTTestReductionEvent event =
			new StatisticsTwoSidedTTestReductionEvent(dataContainers);
		event.setSender(this);
		registerEvent(event);
	}
}
