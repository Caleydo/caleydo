/**
 * 
 */
package org.caleydo.core.data.container;

/**
 * @author Alexander Lex
 */
public class AverageRecord {
	
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

}
