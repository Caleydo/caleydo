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
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.Status;

/**
 * CContainer implementation for int A container for ints. Initialized with an
 * int array. The length can not be modified after initialization. Optimized to
 * hold a large amount of data.
 * 
 * @author Alexander Lex
 */
public class IntContainer
	implements INumericalContainer {

	private int[] container;

	private int min = Integer.MAX_VALUE;

	private int max = Integer.MIN_VALUE;

	/**
	 * Constructor Pass an int array. The length of the array can not be
	 * modified after initialization
	 * 
	 * @param iArContainer the int array
	 */
	public IntContainer(int[] iArContainer) {
		this.container = iArContainer;
	}

	@Override
	public int size() {

		return container.length;
	}

	/**
	 * Returns the value associated with the index at the variable
	 * 
	 * @throws IndexOutOfBoundsException if index is out of specified range
	 * @param iIndex the index of the variable
	 * @return the variable associated with the index
	 */
	public int get(int iIndex) {
		return container[iIndex];
	}

	@Override
	public double getMin() {
		if (Integer.MAX_VALUE == min) {
			calculateMinMax();
		}
		return min;
	}

	@Override
	public double getMax() {

		if (Integer.MIN_VALUE == max) {
			calculateMinMax();
		}
		return max;
	}

	/**
	 * Returns an iterator on the container
	 * 
	 * @return the iterator for the container
	 */
	public IntContainerIterator iterator() {

		return new IntContainerIterator(this);
	}

	/**
	 * Iterator which takes a virtual array into account
	 * 
	 * @param virtualArray the virtual array
	 * @return the iterator
	 */
	public IntContainerIterator iterator(VirtualArray<?, ?, ?> virtualArray) {
		return new IntContainerIterator(this, virtualArray);
	}

	@Override
	public FloatContainer normalizeWithExternalExtrema(double min, double max) {
		if (min > getMin() || max < getMax())
			throw new IllegalArgumentException("Provided external values are more "
					+ "limiting than calculated ones");
		return normalize((int) min, (int) max);
	}

	@Override
	public FloatContainer normalize() {
		return normalize((int) getMin(), (int) getMax());
	}

	/**
	 * Does the actual normalization
	 * 
	 * @param min
	 * @param max
	 * @return
	 * @throws IllegalAttributeException when iMin is >= iMax
	 */
	private FloatContainer normalize(int min, int max)

	{
		if (min > max)
			throw new IllegalArgumentException("Minimum was bigger as maximum");
		if(min == max)
			Logger.log(new Status(Status.WARNING, this.toString(), "Min was the same as max. This is not very interesting to visualize."));
		float[] target = new float[container.length];

		for (int iCount = 0; iCount < container.length; iCount++) {
			target[iCount] = ((float) container[iCount] - min) / (max - min);
			target[iCount] = target[iCount] > 1 ? 1 : target[iCount];
		}
		return new FloatContainer(target);
	}

	/**
	 * The actual calculation of min and maxima on the local array
	 */
	private void calculateMinMax() {

		for (int current : container) {
			// Handle NaN values
			if (current == Integer.MIN_VALUE) {
				continue;
			}

			if (current < min) {
				min = current;
			}
			if (current > max) {
				max = current;
			}
		}
	}

	@Override
	public FloatContainer log(int iBase) {

		float[] target = new float[container.length];

		float tmp;
		for (int index = 0; index < container.length; index++) {
			tmp = container[index];
			target[index] = (float) Math.log(tmp) / (float) Math.log(iBase);
		}

		return new FloatContainer(target);
	}

}
