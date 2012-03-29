/**
 * 
 */
package org.caleydo.core.data.container;

import java.util.ArrayList;
import java.util.HashMap;
import org.caleydo.core.data.collection.table.DataTable;

/**
 * @author alexsb
 */
public class TTest {

	HashMap<DataContainer, ArrayList<Double>> dataContainerToTwoSidedTTestResult =
		new HashMap<DataContainer, ArrayList<Double>>();

	double[] oneSidedTTestResult;

	// DataContainer container;

	public void setTwoSiddedTTestResult(DataContainer set, ArrayList<Double> resultVector) {
		dataContainerToTwoSidedTTestResult.put(set, resultVector);
	}

	public ArrayList<Double> getTwoSidedTTestResult(DataContainer dataContainer) {

		return dataContainerToTwoSidedTTestResult.get(dataContainer);
	}

	public Double getTwoSidedTTestResult(DataTable dataContainer, Integer recordID) {

		return dataContainerToTwoSidedTTestResult.get(dataContainer).get(recordID);
	}

	public void setOneSiddedTTestResult(double[] pValueVector) {
		oneSidedTTestResult = pValueVector;
	}

	public double[] getOneSidedTTestResult() {
		return oneSidedTTestResult;
	}

	public HashMap<DataContainer, ArrayList<Double>> getAllTwoSidedTTestResults() {
		return dataContainerToTwoSidedTTestResult;
	}

}
