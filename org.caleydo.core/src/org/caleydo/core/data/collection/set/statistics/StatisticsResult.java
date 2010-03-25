package org.caleydo.core.data.collection.set.statistics;

import java.util.ArrayList;
import java.util.HashMap;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.statistics.FoldChangeSettings.FoldChangeEvaluator;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.util.collection.Pair;

public class StatisticsResult {

	/**
	 * The set for which the result is valid.
	 */
	ISet set;

	HashMap<ISet, ArrayList<Double>> setToTwoSidedTTestResult;

	double[] oneSidedTTestResult;

	HashMap<ISet, Pair<double[], FoldChangeSettings>> setToFoldChangeResult;

	public StatisticsResult(ISet set) {
		setToTwoSidedTTestResult = new HashMap<ISet, ArrayList<Double>>();
		setToFoldChangeResult = new HashMap<ISet, Pair<double[], FoldChangeSettings>>();
		// oneSidedTTestResult = new double[0];
		this.set = set;
	}

	public void setTwoSiddedTTestResult(ISet set, ArrayList<Double> resultVector) {

		setToTwoSidedTTestResult.put(set, resultVector);
	}

	public ArrayList<Double> getTwoSidedTTestResult(ISet set) {

		return setToTwoSidedTTestResult.get(set);
	}

	public Double getTwoSidedTTestResult(ISet compareSet, Integer contentID) {

		return setToTwoSidedTTestResult.get(compareSet).get(contentID);
	}

	public void setOneSiddedTTestResult(double[] pValueVector) {
		oneSidedTTestResult = pValueVector;
	}
	
	public double[] getOneSidedTTestResult() {
		return oneSidedTTestResult;
	}

	@SuppressWarnings("unchecked")
	public void setFoldChangeResult(ISet set, double[] resultVector) {

		setToFoldChangeResult.put(set, new Pair(resultVector, -1d));
	}

	public HashMap<ISet, Pair<double[], FoldChangeSettings>> getAllFoldChangeResults() {
		return setToFoldChangeResult;
	}
	
	public HashMap<ISet, ArrayList<Double>> getAllTwoSidedTTestResults() {
		return setToTwoSidedTTestResult;
	}

	public Pair<double[], FoldChangeSettings> getFoldChangeResult(ISet set) {

		return setToFoldChangeResult.get(set);
	}

	public void setFoldChangeSettings(ISet set, FoldChangeSettings foldChangeSettings) {
		setToFoldChangeResult.get(set).setSecond(foldChangeSettings);
	}
	
	public void clearStatisticsResults() {
		setToFoldChangeResult.clear();
		setToTwoSidedTTestResult.clear();
		oneSidedTTestResult = null;
	}

	// /**
	// * Calculate and return the content VA based on a given cutoff p-value.
	// *
	// * @param set The set for which the VA comparison result needs to be calculated.
	// * @param cutOffPValue A cutoff p-value between 0 and 1.
	// * @return the content VA fulfilling the cutoff value.
	// */
	// public ContentVirtualArray getVABasedOnTwoSidedTTestResult(ISet compareSet, float cutOffPValue) {
	//		
	// ContentVirtualArray filteredVA = new ContentVirtualArray(ContentVAType.CONTENT);
	// ContentVirtualArray origVA = compareSet.getContentVA(ContentVAType.CONTENT);
	//	
	// ArrayList<Double> compareResultVector = setToTwoSidedTTestResult.get(compareSet);
	//		
	// for (Integer contentIndex = 0; contentIndex < origVA.size(); contentIndex++) {
	//			
	// Integer contentID = origVA.get(contentIndex);
	// if (compareResultVector.get(contentIndex) < cutOffPValue)
	// filteredVA.appendUnique(contentID);
	// }
	//		
	// return filteredVA;
	// }
	//	
//	/**
//	 * Calculate and return the content VA based on a given cutoff fold change.
//	 * 
//	 * @param set
//	 *            The set for which the VA comparison result needs to be calculated.
//	 * @param foldChange
//	 *            A cutoff foldChange value.
//	 * @return the content VA greater than the cutoff fold change value.
//	 */
//	public ContentVirtualArray getVABasedOnFoldChangeResult(ISet compareSet) {
//
//		ContentVirtualArray filteredVA = new ContentVirtualArray(ContentVAType.CONTENT);
//		ContentVirtualArray origVA = compareSet.getContentVA(ContentVAType.CONTENT);
//
//		double[] resultVector = setToFoldChangeResult.get(compareSet).getFirst();
//		double foldChangeRatio = setToFoldChangeResult.get(compareSet).getSecond().getRatio();
//
//		for (Integer contentIndex = 0; contentIndex < origVA.size(); contentIndex++) {
//
//			Integer contentID = origVA.get(contentIndex);
//			if (resultVector[contentIndex] > foldChangeRatio)
//				filteredVA.appendUnique(contentID);
//		}
//
//		return filteredVA;
//	}
	
	public int getElementNumberOfFoldChangeReduction(ISet compareSet) {
		
		int numberOfElements = 0;
		ContentVirtualArray origVA = compareSet.getContentVA(ContentVAType.CONTENT);

		double[] resultVector = setToFoldChangeResult.get(compareSet).getFirst();
		FoldChangeSettings settings = setToFoldChangeResult.get(compareSet).getSecond();
		
		double foldChangeRatio = settings.getRatio();
		FoldChangeEvaluator foldChangeEvaluator = settings.getEvaluator();

		for (Integer contentIndex = 0; contentIndex < origVA.size(); contentIndex++) {
			
			switch (foldChangeEvaluator) {
				case LESS:
					if (resultVector[contentIndex] > -foldChangeRatio)
						continue;
					break;
				case GREATER:
					if (resultVector[contentIndex] < foldChangeRatio)
						continue;
					break;
				case SAME:
					if (Math.abs(resultVector[contentIndex]) > foldChangeRatio)
						continue;
					break;
			}
			
			numberOfElements++;
		}

		return numberOfElements;
	}
}
