/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.contextmenu.item;

import java.util.ArrayList;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.data.StatisticsPValueReductionEvent;
import org.caleydo.core.view.contextmenu.AContextMenuItem;

public class StatisticsPValueReductionItem
	extends AContextMenuItem {

	public StatisticsPValueReductionItem(ArrayList<TablePerspective> tablePerspectives) {

		setLabel("Variance Filter");

		StatisticsPValueReductionEvent event = new StatisticsPValueReductionEvent(tablePerspectives);
		event.setSender(this);
		registerEvent(event);
	}
}
