package org.caleydo.core.util.statistics;

import java.util.ArrayList;

import org.caleydo.core.data.collection.table.DataTable;

public interface IStatisticsPerformer {
	public void init();

	public void performTest();

	public void twoSidedTTest(ArrayList<DataTable> setsToCompare);

	public void foldChange(DataTable set1, DataTable set2);
}
