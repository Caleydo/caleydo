package org.caleydo.core.data.collection;


/**
 * INumericalStorage is a specialization of IStorage. It is meant for numerical data of a continuous range,
 * equivalent to the set of real numbers. In terms of scales it can be interpreted as a data structure for an
 * absolute scale. As a consequence raw data for a numerical set can only be of a number format, such as int
 * or float
 * 
 * @author Alexander Lex
 */

public interface INumericalStorage
	extends IStorage {

	/**
	 * <p>
	 * If you want to consider extremas for normalization which do not occur in the dataset, use this method
	 * instead of normalize(). This is e.g. useful if other sets need to be comparable, but contain larger or
	 * smaller elements.
	 * </p>
	 * If dMin is smaller respectively dMax bigger than the actual minimum the values that are bigger are set
	 * to 0 (minimum) or 1 (maximum) in the normalized data. However, the raw data stays the way it is.
	 * Therefore elements that are drawn at 1 or 0 can have different raw values associated.
	 * <p>
	 * Normalize operates on the raw data, except if you previously called log, then the logarithmized data is
	 * used.
	 * 
	 * @param dMin
	 *            the minimum
	 * @param dMax
	 *            the maximum
	 * @throws IlleagalAttributeStateException
	 *             if dMin >= dMax
	 */
	public void normalizeWithExternalExtrema(double dMin, double dMax);

	/**
	 * Get the minimum of the raw data, respectively the logarithmized data if log was applied
	 * 
	 * @return the minimum - a double since it can contain all values
	 */
	public double getMin();

	/**
	 * Get the maximum of the raw data, respectively the logarithmized data if log was applied
	 * 
	 * @return the maximum - a double since it can contain all values
	 */
	public double getMax();

	/**
	 * Calculates a raw value based on min and max from a normalized value.
	 * 
	 * @param dNormalized
	 *            a value between 0 and 1
	 * @return a value between min and max
	 */
	public double getRawForNormalized(double dNormalized);

	/**
	 * Calculates the log10 of the raw data. Log data can be retrieved by using the get methods with
	 * EDataRepresentation.LOG10. Call normalize after this operation if you want to display the result
	 * Normalize then uses the log data instead of the raw data
	 */
	public void log10();

	/**
	 * Calculates the log2 of the raw data. Log data can be retrieved by using the get methods with
	 * EDataRepresentation.LOG10. Call normalize after this operation if you want to display the result
	 * Normalize then uses the log data instead of the raw data
	 */
	public void log2();

	/**
	 * Remove log and normalized data. Normalize has to be called again.
	 */
	public void reset();

	/**
	 * Returns a histogram of the values in the storage for all values (not considering VAs). The number of
	 * the bins is sqrt(numberOfElements)
	 * 
	 * @return
	 */
	public Histogram getHistogram();

}
