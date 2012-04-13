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
import org.caleydo.core.util.conversion.ConversionTools;

/**
 * A container for floats. Initialized with a float array. The length can not be
 * modified after initialization. Optimized to hold a large amount of data.
 * 
 * @author Alexander Lex
 */
public class FloatCContainer implements INumericalCContainer {

	private float[] container;

	private float min = Float.NaN;

	private float fMax = Float.NaN;

	/**
	 * Constructor Pass a float array. The length of the array can not be
	 * modified after initialization
	 * 
	 * @param container
	 *            the float array
	 */
	public FloatCContainer(final float[] container) {

		this.container = container;
	}

	@Override
	public int size() {
		return container.length;
	}

	/**
	 * Returns the value associated with the index
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if index out of range
	 * @param index
	 *            index of element to return
	 * @return the element at the specified position in this list
	 */
	public float get(final int index) {

		return container[index];
	}

	/**
	 * Returns an iterator on the container
	 * 
	 * @return the iterator for the container
	 */
	public FloatCContainerIterator iterator() {
		return new FloatCContainerIterator(this);
	}

	/**
	 * Returns an iterator on the container
	 * 
	 * @return the iterator for the container
	 */
	public FloatCContainerIterator iterator(VirtualArray<?, ?, ?> virtualArray) {
		return new FloatCContainerIterator(this, virtualArray);
	}

	@Override
	public double getMin() {
		if (Float.isNaN(min)) {
			calculateMinMax();
		}
		return min;
	}

	@Override
	public double getMax() {
		if (Float.isNaN(fMax)) {
			calculateMinMax();
		}
		return fMax;
	}

	@Override
	public FloatCContainer normalize() {
		return new FloatCContainer(ConversionTools.normalize(container, (int) getMin(),
				(int) getMax()));
	}

	@Override
	public FloatCContainer normalizeWithExternalExtrema(final double dMin,
			final double dMax) {
		if (dMin >= dMax)
			throw new IllegalArgumentException("Minimum was bigger or same as maximum");

		return new FloatCContainer(ConversionTools.normalize(container, (float) dMin,
				(float) dMax));

	}

	// @Override
	// public FloatCContainer log10()
	// {
	// float[] fArTarget = new float[fArContainer.length];
	//
	// float fTmp;
	// for (int index = 0; index < fArContainer.length; index++)
	// {
	// fTmp = fArContainer[index];
	// fArTarget[index] = (float) Math.log10(fTmp);
	// if (fArTarget[index] == Float.NEGATIVE_INFINITY)
	// fArTarget[index] = Float.NaN;
	// }
	//
	// return new FloatCContainer(fArTarget);
	// }

	@Override
	public FloatCContainer log(int base) {
		float[] fArTarget = new float[container.length];

		float fTmp;
		for (int index = 0; index < container.length; index++) {
			fTmp = container[index];

			fArTarget[index] = (float) Math.log(fTmp) / (float) Math.log(base);

			if (fArTarget[index] == Float.NEGATIVE_INFINITY) {
				fArTarget[index] = 0;
			}
		}

		return new FloatCContainer(fArTarget);
	}

	/**
	 * Calculates the min and max of the container and sets them to the fMin and
	 * fMax class variables
	 */
	private void calculateMinMax() {
		min = Float.MAX_VALUE;
		fMax = Float.MIN_VALUE;
		for (float fCurrent : container) {
			if (Float.isNaN(fCurrent)) {
				continue;
			}

			if (fCurrent < min) {
				min = fCurrent;
				continue;
			}
			if (fCurrent > fMax) {
				fMax = fCurrent;
			}
		}
		return;
	}

	@Override
	public String toString() {
		String string = "[";
		for (float value : container)
			string += value + ", ";
		return string;
	}
}
