package org.caleydo.core.data.collection;

import java.util.ArrayList;

/**
 * 
 * @author Alexander Lex
 * 
 * Extension of the ICContainer interface for handling nominal data.
 */
public interface INominalCContainer extends ICContainer 
{

	/**
	 * Provide a list with all possible values on the nominal scale. Useful
	 * when the data set does not contain all values by itself.
	 * 
	 * Take care that every value in the data set is also in this list, otherwise
	 * an exception will occur
	 *  
	 * @param sAlPossibleValues the List
	 */
	public void setPossibleValues(ArrayList<String> sAlPossibleValues);
	
}
