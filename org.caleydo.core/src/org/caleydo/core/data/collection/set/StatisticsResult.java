package org.caleydo.core.data.collection.set;

import java.util.ArrayList;
import java.util.HashMap;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;

public class StatisticsResult {

	/**
	 * The set for which the result is valid.
	 */
	ISet set;
	
	HashMap<ISet, ArrayList<Double>> hashSetToCompareResult;
	
	public StatisticsResult(ISet set) {
		hashSetToCompareResult = new HashMap<ISet, ArrayList<Double>>();
		this.set = set;
	}
	
	public void setCompareResultToSet(ISet set, ArrayList<Double> resultVector) {
		
		hashSetToCompareResult.put(set, resultVector);
	}
	
	public ArrayList<Double> getCompareResultToSet(ISet set) {
		
		return hashSetToCompareResult.get(set);
	}
	
	/**
	 * Calculate and return the content VA basing on a given cutoff p-value.
	 * 
	 * @param compareSet The set for which the VA comparison result needs to be calculated.
	 * @param cutOffPValue A cutoff p-value between 0 and 1.
	 * @return the content VA fulfilling the cutoff value.
	 */
	public ContentVirtualArray getVABasedOnCompareResult(ISet compareSet, float cutOffPValue) {
		
		ContentVirtualArray filteredVA = new ContentVirtualArray(ContentVAType.CONTENT);		
		ContentVirtualArray origVA = compareSet.getContentVA(ContentVAType.CONTENT);
	
		ArrayList<Double> compareResultVector = hashSetToCompareResult.get(compareSet);
		
		for (Integer contentIndex = 0; contentIndex < origVA.size(); contentIndex++) {
			
			Integer contentID = origVA.get(contentIndex);
			if (compareResultVector.get(contentIndex) > cutOffPValue)
				filteredVA.appendUnique(contentID);
		}
		
		return filteredVA;
	}
}
