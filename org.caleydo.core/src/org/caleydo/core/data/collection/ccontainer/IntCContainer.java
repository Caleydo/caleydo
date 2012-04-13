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
package org.caleydo.core.data.collection.ccontainer;

import org.caleydo.core.data.virtualarray.VirtualArray;

/**
 * CContainer implementation for int A container for ints. Initialized with an int array. The length can not
 * be modified after initialization. Optimized to hold a large amount of data.
 * 
 * @author Alexander Lex
 */
public class IntCContainer
	implements INumericalCContainer {

	private int[] iArContainer;

	private int iMin = Integer.MAX_VALUE;

	private int iMax = Integer.MIN_VALUE;

	/**
	 * Constructor Pass an int array. The length of the array can not be modified after initialization
	 * 
	 * @param iArContainer
	 *            the int array
	 */
	public IntCContainer(int[] iArContainer) {
		this.iArContainer = iArContainer;
	}

	@Override
	public int size() {

		return iArContainer.length;
	}

	/**
	 * Returns the value associated with the index at the variable
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if index is out of specified range
	 * @param iIndex
	 *            the index of the variable
	 * @return the variable associated with the index
	 */
	public int get(int iIndex) {
		return iArContainer[iIndex];
	}

	@Override
	public double getMin() {
		if (Integer.MAX_VALUE == iMin) {
			calculateMinMax();
		}
		return iMin;
	}

	@Override
	public double getMax() {

		if (Integer.MIN_VALUE == iMax) {
			calculateMinMax();
		}
		return iMax;
	}

	/**
	 * Returns an iterator on the container
	 * 
	 * @return the iterator for the container
	 */
	public IntCContainerIterator iterator() {

		return new IntCContainerIterator(this);
	}

	/**
	 * Iterator which takes a virtual array into account
	 * 
	 * @param virtualArray
	 *            the virtual array
	 * @return the iterator
	 */
	public IntCContainerIterator iterator(VirtualArray<?, ?, ?> virtualArray) {
		return new IntCContainerIterator(this, virtualArray);
	}

	@Override
	public FloatCContainer normalizeWithExternalExtrema(double dMin, double dMax) {
		if (dMin > getMin() || dMax < getMax())
			throw new IllegalArgumentException("Provided external values are more "
				+ "limiting than calculated ones");
		return normalize((int) dMin, (int) dMax);
	}

	@Override
	public FloatCContainer normalize() {
		return normalize((int) getMin(), (int) getMax());
	}

	/**
	 * Does the actual normalization
	 * 
	 * @param iMin
	 * @param iMax
	 * @return
	 * @throws IllegalAttributeException
	 *             when iMin is >= iMax
	 */
	private FloatCContainer normalize(int iMin, int iMax)

	{
		if (iMin >= iMax)
			throw new IllegalArgumentException("Minimum was bigger or same as maximum");
		float[] fArTmpTarget = new float[iArContainer.length];

		for (int iCount = 0; iCount < iArContainer.length; iCount++) {
			fArTmpTarget[iCount] = ((float) iArContainer[iCount] - iMin) / (iMax - iMin);
			fArTmpTarget[iCount] = fArTmpTarget[iCount] > 1 ? 1 : fArTmpTarget[iCount];
		}
		return new FloatCContainer(fArTmpTarget);
	}

	/**
	 * The actual calculation of min and maxima on the local array
	 */
	private void calculateMinMax() {

		for (int iCurrentValue : iArContainer) {
			// Handle NaN values
			if (iCurrentValue == Integer.MIN_VALUE) {
				continue;
			}

			if (iCurrentValue < iMin) {
				iMin = iCurrentValue;
			}
			if (iCurrentValue > iMax) {
				iMax = iCurrentValue;
			}
		}
	}

	@Override
	public FloatCContainer log(int iBase) {

		float[] fArTarget = new float[iArContainer.length];

		float fTmp;
		for (int index = 0; index < iArContainer.length; index++) {
			fTmp = iArContainer[index];
			fArTarget[index] = (float) Math.log(fTmp) / (float) Math.log(iBase);
		}

		return new FloatCContainer(fArTarget);
	}

}
