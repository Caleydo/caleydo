/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.statistics;

import java.util.ArrayList;

import org.caleydo.core.data.perspective.table.TablePerspective;

public interface IStatisticsPerformer {
	public void init();

	public void performTest();

	public void twoSidedTTest(ArrayList<TablePerspective> setsToCompare);

	public void foldChange(TablePerspective container1, TablePerspective container2, boolean betweenRecords);
}
