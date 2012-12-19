/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.data.collection.container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

/**
 * Container for nominal string values. Provides access to the values, can create discrete values for the
 * nominal values You can provide a list of all possible values, otherwise such a list will be constructed
 * when you call normalize, or request a mapping for the first time.
 * 
 * @author Alexander Lex
 */
public class NominalContainer<T>
	extends ATypedContainer<T>
	implements INominalContainer<T> {

	private HashMap<T, Float> hashNominalToDiscrete;

	private HashMap<Float, T> hashDiscreteToNominal;

	private boolean areHashMapsInitialized = false;

	/**
	 * Constructor
	 * 
	 * @param sAlContainer
	 *            The complete list of all Strings in the dataset
	 */
	public NominalContainer(ArrayList<T> tAlContainer) {
		this.alContainer = tAlContainer;
		hashNominalToDiscrete = new HashMap<T, Float>();
		hashDiscreteToNominal = new HashMap<Float, T>();
	}

	/**
	 * Provide a list with all possible values on the nominal scale. Useful when the data set does not contain
	 * all values by itself. Take care that every value in the data set is also in this list, otherwise an
	 * exception will occur
	 * 
	 * @param sAlPossibleValues
	 *            the List
	 */
	@Override
	public void setPossibleValues(ArrayList<T> alPossibleValues) {
		// TODO: check if all values in the raw list are also in the other list
		setUpMapping(alPossibleValues);
	}

	/**
	 * Creates a float array of discrete data values for every nominal value. The same string always has the
	 * same value. If no list of possible values has been specified beforehand, a list is created.
	 */
	@Override
	public FloatContainer normalize() {

		if (!areHashMapsInitialized) {
			setUpMapping(alContainer);
		}

		float[] fArNormalized = new float[alContainer.size()];

		int iCount = 0;
		for (T tContent : alContainer) {
			Float fTemp = hashNominalToDiscrete.get(tContent);
			if (fTemp == null)
				throw new IllegalStateException("Requested string is not in the possible list of strings. "
					+ "This happens if you have set the possible list with setPossibleValues "
					+ "but a Value in the data set is not in this list.");
			fArNormalized[iCount] = fTemp.floatValue();
			iCount++;
		}

		return new FloatContainer(fArNormalized);
	}

	/**
	 * When providing a float value following the rules of the normalization (0 >= x <= 1) the associated raw
	 * nominal value is returned
	 * 
	 * @param fDiscrete
	 * @return the string associated with the discrete value, or null if no such value exists
	 */
	public T getNominalForDiscreteValue(Float fDiscrete) {
		if (!areHashMapsInitialized) {
			setUpMapping(alContainer);
		}
		return hashDiscreteToNominal.get(fDiscrete);
	}

	/**
	 * When providing a nominal value that is in the initially provided list, the assoziated normalized value
	 * is returned
	 * 
	 * @param sNominal
	 * @return
	 */
	public Float getDiscreteForNominalValue(T tNominal) {
		if (!areHashMapsInitialized) {
			setUpMapping(alContainer);
		}
		return hashNominalToDiscrete.get(tNominal);
	}

	/**
	 * Initialize the mapping of nominal to discrete values. Call it either with the member sAlDimension, or
	 * with a list provided externally
	 * 
	 * @param sAlDimension
	 */
	@SuppressWarnings("unchecked")
	private void setUpMapping(ArrayList<T> tAlDimension) {
		for (T tContent : tAlDimension) {
			hashNominalToDiscrete.put(tContent, new Float(0));
		}

		float fDivisor = 1.0f / (hashNominalToDiscrete.size() - 1);

		// float[] fArNormalized = new float[sAlDimension.size()];

		int iCount = 0;

		Set<T> keySet = hashNominalToDiscrete.keySet();
		Object[] sortedArray = new Object[keySet.size()];
		keySet.toArray(sortedArray);
		Arrays.sort(sortedArray, 0, sortedArray.length - 1);

		for (Object record : sortedArray) {
			Float fDiscrete = hashNominalToDiscrete.get(record);
			fDiscrete = fDivisor * iCount;
			T tContent = (T) record;
			hashNominalToDiscrete.put(tContent, fDiscrete);
			hashDiscreteToNominal.put(fDiscrete, tContent);

			iCount++;
		}
		areHashMapsInitialized = true;
	}

	@Override
	public HashMap<T, Float> getHistogram() {
		HashMap<T, Float> hashTypeToCounter = new HashMap<T, Float>();
		Float fTemp;

		float fMax = Float.MIN_VALUE;

		for (T tContent : alContainer) {
			fTemp = hashTypeToCounter.get(tContent);
			if (fTemp == null) {
				fTemp = new Float(0);
			}
			++fTemp;
			if (fTemp > fMax) {
				fMax = fTemp;
			}
			hashTypeToCounter.put(tContent, fTemp);
		}

		for (T tContent : hashTypeToCounter.keySet()) {
			fTemp = hashTypeToCounter.get(tContent);
			fTemp = fTemp / (fMax - 1);
		}
		return hashTypeToCounter;
	}

}
