/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.event.data;

import java.util.ArrayList;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.AEvent;

public class StatisticsTwoSidedTTestReductionEvent
	extends AEvent {

	private ArrayList<TablePerspective> tablePerspectives;

	public StatisticsTwoSidedTTestReductionEvent(ArrayList<TablePerspective> tablePerspectives) {
		this.tablePerspectives = tablePerspectives;
	}

	public void setTablePerspectives(ArrayList<TablePerspective> tablePerspectives) {
		this.tablePerspectives = tablePerspectives;
	}

	public ArrayList<TablePerspective> getTablePerspectives() {
		return tablePerspectives;
	}

	@Override
	public boolean checkIntegrity() {

		if (tablePerspectives == null || tablePerspectives.size() == 0)
			return false;

		return true;
	}
}
