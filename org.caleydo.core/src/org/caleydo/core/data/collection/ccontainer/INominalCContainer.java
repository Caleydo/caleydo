package org.caleydo.core.data.collection.ccontainer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Extension of the ICContainer interface for handling nominal data.
 * 
 * @author Alexander Lex
 */
public interface INominalCContainer<T>
	extends ICContainer
{

	/**
	 * Provide a list with all possible values on the nominal scale. Useful when
	 * the data set does not contain all values by itself. Take care that every
	 * value in the data set is also in this list, otherwise an exception will
	 * occur
	 * 
	 * @param sAlPossibleValues the List
	 */
	public void setPossibleValues(ArrayList<T> tAlPossibleValues);

	/**
	 * Create a histogram for the values in the container
	 * 
	 * @return the
	 */
	public HashMap<T, Float> getHistogram();

}
