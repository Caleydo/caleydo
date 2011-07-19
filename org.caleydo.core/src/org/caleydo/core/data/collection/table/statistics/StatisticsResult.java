package org.caleydo.core.data.collection.table.statistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.collection.table.statistics.FoldChangeSettings.FoldChangeEvaluator;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.conversion.ConversionTools;

public class StatisticsResult {

	/**
	 * The set for which the result is valid.
	 */
	DataTable set;

	HashMap<DataTable, ArrayList<Double>> setToTwoSidedTTestResult;

	double[] oneSidedTTestResult;

	HashMap<DataTable, Pair<double[], FoldChangeSettings>> setToFoldChangeResult;

	HashMap<DataTable, double[]> setToFoldChangeUncertainty;
	
	//FIXME: just for uncertainty paper. this needs to be calculated here and not inside the view
	double[] aggregatedUncertainty;

	public StatisticsResult(DataTable set) {
		setToTwoSidedTTestResult = new HashMap<DataTable, ArrayList<Double>>();
		setToFoldChangeResult = new HashMap<DataTable, Pair<double[], FoldChangeSettings>>();
		setToFoldChangeUncertainty = new HashMap<DataTable, double[]>();
		// oneSidedTTestResult = new double[0];
		this.set = set;
	}

	public void setTwoSiddedTTestResult(DataTable set, ArrayList<Double> resultVector) {

		setToTwoSidedTTestResult.put(set, resultVector);
	}

	public ArrayList<Double> getTwoSidedTTestResult(DataTable set) {

		return setToTwoSidedTTestResult.get(set);
	}

	public Double getTwoSidedTTestResult(DataTable compareSet, Integer contentID) {

		return setToTwoSidedTTestResult.get(compareSet).get(contentID);
	}

	public void setOneSiddedTTestResult(double[] pValueVector) {
		oneSidedTTestResult = pValueVector;
	}

	public double[] getOneSidedTTestResult() {
		return oneSidedTTestResult;
	}

	@SuppressWarnings("unchecked")
	public void setFoldChangeResult(DataTable set, double[] resultVector) {

		setToFoldChangeResult.put(set, new Pair(resultVector, -1d));
	}

	public HashMap<DataTable, Pair<double[], FoldChangeSettings>> getAllFoldChangeResults() {
		return setToFoldChangeResult;
	}

	public HashMap<DataTable, ArrayList<Double>> getAllTwoSidedTTestResults() {
		return setToTwoSidedTTestResult;
	}

	public Pair<double[], FoldChangeSettings> getFoldChangeResult(DataTable set) {

		return setToFoldChangeResult.get(set);
	}

	public double[] getFoldChangeUncertainty(DataTable set) {

		return setToFoldChangeUncertainty.get(set);
	}
	
	public Collection<double[]> getAllFoldChangeUncertainties() {

		return setToFoldChangeUncertainty.values();
	}	

	public void setFoldChangeSettings(DataTable set, FoldChangeSettings foldChangeSettings) {
		setToFoldChangeResult.get(set).setSecond(foldChangeSettings);

		// Recalculate normalized uncertainty for fold change
		boolean calculateAbsolute = false;
		if (foldChangeSettings.getEvaluator() == FoldChangeEvaluator.BOTH)
			calculateAbsolute = true;

		setToFoldChangeUncertainty.put(
			set,
			ConversionTools.normalize(setToFoldChangeResult.get(set).getFirst(),
				foldChangeSettings.getRatioUncertainty(), foldChangeSettings.getRatio(), calculateAbsolute));
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
	// public ContentVirtualArray getVABasedOnTwoSidedTTestResult(DataTable compareSet, float cutOffPValue) {
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
	// /**
	// * Calculate and return the content VA based on a given cutoff fold change.
	// *
	// * @param set
	// * The set for which the VA comparison result needs to be calculated.
	// * @param foldChange
	// * A cutoff foldChange value.
	// * @return the content VA greater than the cutoff fold change value.
	// */
	// public ContentVirtualArray getVABasedOnFoldChangeResult(DataTable compareSet) {
	//
	// ContentVirtualArray filteredVA = new ContentVirtualArray(ContentVAType.CONTENT);
	// ContentVirtualArray origVA = compareSet.getContentVA(ContentVAType.CONTENT);
	//
	// double[] resultVector = setToFoldChangeResult.get(compareSet).getFirst();
	// double foldChangeRatio = setToFoldChangeResult.get(compareSet).getSecond().getRatio();
	//
	// for (Integer contentIndex = 0; contentIndex < origVA.size(); contentIndex++) {
	//
	// Integer contentID = origVA.get(contentIndex);
	// if (resultVector[contentIndex] > foldChangeRatio)
	// filteredVA.appendUnique(contentID);
	// }
	//
	// return filteredVA;
	// }

	// public int getElementNumberOfFoldChangeReduction(DataTable compareSet) {
	//
	// int numberOfElements = 0;
	// ContentVirtualArray origVA = compareSet.getContentData(ContentVAType.CONTENT).getContentVA();
	//
	// double[] resultVector = setToFoldChangeResult.get(compareSet).getFirst();
	// FoldChangeSettings settings = setToFoldChangeResult.get(compareSet).getSecond();
	//
	// double foldChangeRatio = settings.getRatio();
	// FoldChangeEvaluator foldChangeEvaluator = settings.getEvaluator();
	//
	// for (Integer contentIndex = 0; contentIndex < origVA.size(); contentIndex++) {
	//
	// switch (foldChangeEvaluator) {
	// case LESS:
	// if (resultVector[contentIndex] * -1 < foldChangeRatio)
	// continue;
	// break;
	// case GREATER:
	// if (resultVector[contentIndex] < foldChangeRatio)
	// continue;
	// break;
	// case SAME:
	// if (Math.abs(resultVector[contentIndex]) > foldChangeRatio)
	// continue;
	// break;
	// }
	//
	// // System.out.println("Found valid gene fulfilling statistics criteria: " +compareSet
	// // +" "+contentIndex);
	// numberOfElements++;
	// }
	//
	// return numberOfElements;
	// }
	
	@Deprecated
	public void setAggregatedUncertainty(double[] aggregatedUncertainty) {
		this.aggregatedUncertainty = aggregatedUncertainty;
	}
	
	public double[] getAggregatedUncertainty() {
		return aggregatedUncertainty;
	}

}
