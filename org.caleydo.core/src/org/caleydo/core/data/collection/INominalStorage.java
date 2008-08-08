package org.caleydo.core.data.collection;

import java.util.ArrayList;
import java.util.HashMap;
import org.caleydo.core.data.collection.storage.EDataRepresentation;

/**
 * The INominalStorage interface is an extension of the IStorage interface. It
 * is meant for data which has no discrete numerical values, such as nominal or
 * ordinal data. Example cases are illness classifications, ratings such as
 * good, OK, bad etc.
 * 
 * Normalization converts the entities into evenly spaced numerical values
 * between 0 and 1.
 * 
 * One can provide a list of possible values, which is useful, if a list does
 * not contain all possible values, but you want to have the others represented
 * anyway. If no such list is provided it is generated from the available
 * values.
 * 
 * @author Alexander Lex
 */

public interface INominalStorage<T>
	extends IStorage
{

	/**
	 * Set the raw data Currently supported: String
	 * 
	 * @param alData the ArrayList containing the data
	 */
	public void setRawNominalData(ArrayList<T> alData);

	/**
	 * @return
	 */
	public T getRaw(int iIndex);

	/**
	 * Provide a list of possible values, which must include all values
	 * specified in the raw data
	 * 
	 * @param sAlPossibleValues
	 */
	public void setPossibleValues(ArrayList<T> sAlPossibleValues);

	/**
	 * Create a histogram off all elements that actually occur in the storage
	 * The values in the histogram are normalized between 0 and 1, where 0 means
	 * one occurrence and 1 corresponds to the maximum number of occurrences
	 * 
	 * @return a hash map mapping the nominal value to it's histogram value
	 */
	public HashMap<T, Float> getHistogram();

}
