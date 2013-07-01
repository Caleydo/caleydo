/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.contextmenu.item;

import java.util.ArrayList;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.data.StatisticsTwoSidedTTestReductionEvent;
import org.caleydo.core.view.contextmenu.AContextMenuItem;

public class StatisticsTwoSidedTTestReductionItem
	extends AContextMenuItem {

	public StatisticsTwoSidedTTestReductionItem(ArrayList<TablePerspective> tablePerspectives) {

		setLabel("Two-Sided T-Test Filter");

		StatisticsTwoSidedTTestReductionEvent event =
			new StatisticsTwoSidedTTestReductionEvent(tablePerspectives);
		event.setSender(this);
		registerEvent(event);
	}
}
