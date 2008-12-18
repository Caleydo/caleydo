package org.caleydo.core.data.collection.ccontainer;

/**
 * Extension of the ICContainer interface for numerical values
 * 
 * @author Alexander Lex
 */
public interface INumericalCContainer
	extends ICContainer
{

	/**
	 * Execute the normalize method, where values in the container are
	 * normalized to values between 0 and 1, but do not take min max from the
	 * range calculated internally, but use those specified in dMin, dMax Take
	 * care that dMin and dMax are smaller resp. bigger than the smallest resp.
	 * biggest value in the data.
	 * 
	 * @param dMin the minimum
	 * @param dMax the maximum
	 * @return a container with the normalized values
	 * @throws IllegalAttributeException when iMin is >= iMax
	 */
	public FloatCContainer normalizeWithExternalExtrema(double dMin, double dMax);

	/**
	 * Returns the minimum of the container, double to fit all datatypes
	 * 
	 * @return the minimum
	 */
	public double getMin();

	/**
	 * Returns the maximum of the container, double to fit all datatypes
	 * 
	 * @return the maximum
	 */
	public double getMax();

	// /**
	// * Calculates a logarithmic representation (logarithm to the base of 10)
	// for
	// * the data, which it returns as a new ICContainer
	// *
	// * @return
	// */
	// public FloatCContainer log10();

	/**
	 * Calculates a logarithmic representation of a base for the data, which it
	 * returns as a new ICContainer
	 * 
	 * @return
	 */
	public FloatCContainer log(int iBase);

}
