package org.caleydo.core.data.collection;

import java.util.ArrayList;

import org.caleydo.core.data.collection.ccontainer.EDataKind;

/**
 * @author Alexander Lex The INominalStorage interface is an extension of the
 *         IStorage interface. It is meant for data which has no discrete
 *         numerical values, such as nominal or ordinal data. Example cases are
 *         illness classifications, ratings such as good, OK, bad etc.
 *         Normalization converts the entities into numerical values between 0
 *         and 1 One can provide a list of possible values, which is useful, if
 *         a list does not contain all possible values, but you want to have the
 *         others represented anyway. If no such list is provided it is
 *         generated from the available values.
 */

public interface INominalStorage<T>
	extends IStorage
{

	/**
	 * Set the raw data Currently supported: String
	 * 
	 * @param alData
	 *            the ArrayList containing the data
	 */
	public void setRawData(ArrayList<T> alData);

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

	// TODO
	// public int getNumberOfDistinctValues();
	
	public T getRawForNormalized();

}
