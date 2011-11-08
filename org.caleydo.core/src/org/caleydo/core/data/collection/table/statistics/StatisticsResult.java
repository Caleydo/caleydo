package org.caleydo.core.data.collection.table.statistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.collection.table.statistics.FoldChangeSettings.FoldChangeEvaluator;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.conversion.ConversionTools;

@Deprecated
public class StatisticsResult {

	/**
	 * The set for which the result is valid.
	 */
	DataTable set;

	HashMap<DataTable, ArrayList<Double>> setToTwoSidedTTestResult;

	double[] oneSidedTTestResult;

	

	//FIXME: just for uncertainty paper. this needs to be calculated here and not inside the view
	double[] aggregatedUncertainty;

	public StatisticsResult(DataTable set) {
		setToTwoSidedTTestResult = new HashMap<DataTable, ArrayList<Double>>();
				// oneSidedTTestResult = new double[0];
		this.set = set;
	}

	public void setTwoSiddedTTestResult(DataTable set, ArrayList<Double> resultVector) {
		setToTwoSidedTTestResult.put(set, resultVector);
	}

	public ArrayList<Double> getTwoSidedTTestResult(DataTable set) {

		return setToTwoSidedTTestResult.get(set);
	}

	public Double getTwoSidedTTestResult(DataTable compareSet, Integer recordID) {

		return setToTwoSidedTTestResult.get(compareSet).get(recordID);
	}

	public void setOneSiddedTTestResult(double[] pValueVector) {
		oneSidedTTestResult = pValueVector;
	}

	public double[] getOneSidedTTestResult() {
		return oneSidedTTestResult;
	}

	

	public HashMap<DataTable, ArrayList<Double>> getAllTwoSidedTTestResults() {
		return setToTwoSidedTTestResult;
	}

	


	
	

	

	public void clearStatisticsResults() {
		setToTwoSidedTTestResult.clear();
		oneSidedTTestResult = null;
	}


	
	@Deprecated
	public void setAggregatedUncertainty(double[] aggregatedUncertainty) {
		this.aggregatedUncertainty = aggregatedUncertainty;
	}
	
	public double[] getAggregatedUncertainty() {
		return aggregatedUncertainty;
	}

}
