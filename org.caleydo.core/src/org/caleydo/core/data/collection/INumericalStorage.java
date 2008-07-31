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
	 * Normalize operates on the raw data, except if you previously called log,
	 * then the logarithmized data is used.
	 * 
	 * @param dMin the minimum
	 * @param dMax the maximum
	 */
	public void normalizeWithExternalExtrema(double dMin, double dMax);

	/**
	 * Get the minimum of the raw data, respectively the logarithmized data if
	 * log was applied
	 * 
	 * @return the minimum - a double since it can contain all values
	 */
	public double getMin();

	/**
	 * Get the maximum of the raw data, respectively the logarithmized data if
	 * log was applied
	 * 
	 * @return the maximum - a double since it can contain all values
	 */
	public double getMax();

	/**
	 * Calculates the log10 of the raw data. Log data can be retrieved by using
	 * the get methods with EDataRepresentation.LOG10. Call normalize after this
	 * operation if you want to display the result Normalize then uses the log
	 * data instead of the raw data
	 */
	public void log10();

	/**
	 * Remove log and normalized data. Normalize has to be called again.
	 */
	public void reset();

}
