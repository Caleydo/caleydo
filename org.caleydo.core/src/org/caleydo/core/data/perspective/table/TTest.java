/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 * 
 */
package org.caleydo.core.data.perspective.table;

import java.util.ArrayList;
import java.util.HashMap;

import org.caleydo.core.data.collection.table.Table;

/**
 * @author alexsb
 */
public class TTest {

	HashMap<TablePerspective, ArrayList<Double>> tablePerspectiveToTwoSidedTTestResult =
		new HashMap<TablePerspective, ArrayList<Double>>();

	double[] oneSidedTTestResult;

	// TablePerspective container;

	public void setTwoSiddedTTestResult(TablePerspective set, ArrayList<Double> resultVector) {
		tablePerspectiveToTwoSidedTTestResult.put(set, resultVector);
	}

	public ArrayList<Double> getTwoSidedTTestResult(TablePerspective tablePerspective) {

		return tablePerspectiveToTwoSidedTTestResult.get(tablePerspective);
	}

	public Double getTwoSidedTTestResult(Table tablePerspective, Integer recordID) {

		return tablePerspectiveToTwoSidedTTestResult.get(tablePerspective).get(recordID);
	}

	public void setOneSiddedTTestResult(double[] pValueVector) {
		oneSidedTTestResult = pValueVector;
	}

	public double[] getOneSidedTTestResult() {
		return oneSidedTTestResult;
	}

	public HashMap<TablePerspective, ArrayList<Double>> getAllTwoSidedTTestResults() {
		return tablePerspectiveToTwoSidedTTestResult;
	}

}
