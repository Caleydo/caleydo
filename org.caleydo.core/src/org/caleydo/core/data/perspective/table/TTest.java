/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
/**
 * 
 */
package org.caleydo.core.data.perspective.table;

import java.util.ArrayList;
import java.util.HashMap;
import org.caleydo.core.data.collection.table.DataTable;

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

	public Double getTwoSidedTTestResult(DataTable tablePerspective, Integer recordID) {

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
