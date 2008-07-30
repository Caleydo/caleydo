package org.caleydo.core.data.collection;

/**
 * INumericalStorage is a specialization of IStorage. It is meant for numerical
 * data of a continuous range, equivalent to the set of real numbers. In terms
 * of scales it can be interpreted as a data structure for an absolute scale. As
 * a consequence raw data for a numerical set can only be of a number format,
 * such as int or float
 * 
 * @author Alexander Lex
 */

public interface INumericalStorage
	extends IStorage
{

	/**
	 * If you want to consider extremas for normalization which do not occur in
	 * the dataset, use this method instead of normalize(). This is e.g. useful
	 * if other sets need to be comparable, but contain larger or smaller
	 * elements.
	 * 
	 * @param dMin
	 *            the minimum
	 * @param dMax
	 *            the maximum
	 */
	public void normalizeWithExternalExtrema(double dMin, double dMax);

	/**
	 * Get the maximum of the raw data
	 * 
	 * @return
	 */
	public double getMin();

	/**
	 * Get the maximum of the raw data
	 * 
	 * @return
	 */
	public double getMax();

	/**
	 * Calculates the log10 of the raw data. Log data can be retrieved by using
	 * the get methods with EDataKind.LOG10 Call normalize after this operation
	 * if you want to display the result
	 */
	public void log10();

	/**
	 * Remove log and normalized data. Useful when raw data is of interest again
	 * Normalize has to be called again.
	 */
	public void reset();

}
