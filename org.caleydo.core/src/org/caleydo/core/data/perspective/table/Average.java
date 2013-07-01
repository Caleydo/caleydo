/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.core.data.perspective.table;

/**
 * Container for average values and related information such as standard deviations
 *
 * @author Alexander Lex
 */
public class Average {

	/** The average value (mean) of the record */
	double arithmeticMean;
	/** The standard deviation from the {@link #arithmeticMean} */
	double standardDeviation;

	/**
	 * @return the arithmeticMean, see {@link #arithmeticMean}
	 */
	public double getArithmeticMean() {
		return arithmeticMean;
	}

	/**
	 * @return the standardDeviation, see {@link #standardDeviation}
	 */
	public double getStandardDeviation() {
		return standardDeviation;
	}

	/**
	 * @param arithmeticMean
	 *            setter, see {@link arithmeticMean}
	 */
	public void setArithmeticMean(double arithmeticMean) {
		this.arithmeticMean = arithmeticMean;
	}

	/**
	 * @param standardDeviation
	 *            setter, see {@link standardDeviation}
	 */
	public void setStandardDeviation(double standardDeviation) {
		this.standardDeviation = standardDeviation;
	}

}
