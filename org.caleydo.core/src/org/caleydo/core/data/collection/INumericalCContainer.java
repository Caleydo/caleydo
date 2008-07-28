package org.caleydo.core.data.collection;

/**
 * Extension of the ICContainer interface for numerical values
 *
 * @author Alexander Lex
 */
public interface INumericalCContainer 
extends ICContainer 
{
	/**
	 * Execute the normalize method, where values in the container are normalized
	 * to values between 0 and 1, but do not take min max from the range calculated 
	 * internally, but use those specified in dMin, dMax
	 * 
	 * Take care that dMin and dMax are smaller resp. bigger than the smallest resp. 
	 * biggest value in the data.
	 * 
	 * @param dMin the minimum
	 * @param dMax the maximum
	 * @return
	 */
	public ICContainer normalizeWithExternalExtrema(double dMin, double dMax);
	
	public double getMin();
	
	public double getMax();
	
	

}
