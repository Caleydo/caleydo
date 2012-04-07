package org.caleydo.core.util.statistics;

import java.util.ArrayList;

import org.caleydo.core.data.container.DataContainer;

public interface IStatisticsPerformer {
	public void init();

	public void performTest();

	public void twoSidedTTest(ArrayList<DataContainer> setsToCompare);

	public void foldChange(DataContainer container1, DataContainer container2, boolean betweenRecords);
}
