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

import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.util.conversion.ConversionTools;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.Status;

/**
 * A container for floats. Initialized with a float array. The length can not be
 * modified after initialization. Optimized to hold a large amount of data.
 * 
 * @author Alexander Lex
 */
public class FloatContainer implements INumericalContainer {

	private float[] container;

	private float min = Float.NaN;

	private float max = Float.NaN;

	/**
	 * Constructor Pass a float array. The length of the array can not be
	 * modified after initialization
	 * 
	 * @param container
	 *            the float array
	 */
	public FloatContainer(final float[] container) {

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
	public FloatContainerIterator iterator() {
		return new FloatContainerIterator(this);
	}

	/**
	 * Returns an iterator on the container
	 * 
	 * @return the iterator for the container
	 */
	public FloatContainerIterator iterator(VirtualArray<?, ?, ?> virtualArray) {
		return new FloatContainerIterator(this, virtualArray);
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
		if (Float.isNaN(max)) {
			calculateMinMax();
		}
		return max;
	}

	@Override
	public FloatContainer normalize() {

		return new FloatContainer(ConversionTools.normalize(container, (int) getMin(),
				(int) getMax()));
	}

	@Override
	public FloatContainer normalizeWithExternalExtrema(final double min, final double max) {
		if (min > max)
			throw new IllegalArgumentException("Minimum was bigger as maximum");
		if(min == max)
			Logger.log(new Status(Status.WARNING, this.toString(), "Min was the same as max. This is not very interesting to visualize."));

		return new FloatContainer(ConversionTools.normalize(container, (float) min,
				(float) max));

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
	public FloatContainer log(int base) {
		float[] target = new float[container.length];

		float tmp;
		for (int index = 0; index < container.length; index++) {
			tmp = container[index];

			target[index] = (float) Math.log(tmp) / (float) Math.log(base);

			if (target[index] == Float.NEGATIVE_INFINITY) {
				target[index] = 0;
			}
		}

		return new FloatContainer(target);
	}

	/**
	 * Calculates the min and max of the container and sets them to the fMin and
	 * fMax class variables
	 */
	private void calculateMinMax() {
		min = Float.MAX_VALUE;
		max = Float.MIN_VALUE;
		int counter = -1;
		for (float current : container) {
			counter++;
			if (Float.isNaN(current)) {
				continue;
			}

			if (Float.isInfinite(current)) {
				Logger.log(new Status(Status.WARNING, this.toString(),
						"Value for normalization was infinity at index " + counter + ": "
								+ current));
				continue;
			}
			if (current < min) {
				min = current;
				continue;
			}
			if (current > max) {
				max = current;
			}
		}

		// Needed if all values in array are NaN
		if (min == Float.MAX_VALUE)
			min = Float.NaN;
		if (max == Float.MIN_VALUE)
			max = Float.NaN;

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
