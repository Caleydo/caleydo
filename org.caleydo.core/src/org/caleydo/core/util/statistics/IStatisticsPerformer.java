package org.caleydo.core.util.statistics;

import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;

public interface IStatisticsPerformer {
	public void init();

	public void performTest();
	
	public void twoSidedTTest(ArrayList<ISet> setsToCompare);

	public void foldChange(ISet set1, ISet set2);
}
