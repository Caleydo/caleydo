/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.data.collection.column.container;

import java.util.Iterator;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * A container for ints. Initialize with an int array or iteratively add data up to {@link #size()} once! The length can
 * not be modified after initialization.
 *
 * @author Alexander Lex
 */
public class IntContainer implements INumericalContainer<Integer> {

	/** The value used for unknown values */
	private static final int UNKNOWN_VALUE = Integer.MIN_VALUE;

	/** The actual data */
	private final int[] container;
	/** Keeps track of the next free index for adding data */
	private int nextIndex = 0;
	/** The smalles value in the ccontainer */
	private int min = Integer.MAX_VALUE;
	/** The largest value in the container */
	private int max = Integer.MIN_VALUE;

	/**
	 * Constructor Pass an int array. The length of the array can not be modified after initialization
	 *
	 * @param container
	 *            the int array
	 */
	public IntContainer(int[] container) {
		this.container = container;
	}

	public IntContainer(int size) {
		this(new int[size]);
	}

	@Override
	public EDataType getDataType() {
		return EDataType.INTEGER;
	}

	@Override
	public int size() {
		return container.length;
	}

	@Override
	public Iterator<Integer> iterator() {
		return new ContainerIterator<>(this);
	}

	@Override
	public void add(Integer value) {
		container[nextIndex++] = value;
	}

	/** Implementation of {@link #add(Integer)} with primitive type to avoid auto-boxing */
	public void add(int value) {
		container[nextIndex++] = value;
	}

	/** Equivalent to {@link IContainer#get(int)} using the primitive data type avoiding the boxing */
	public int getPrimitive(int index) {
		return container[index];
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

	@Override
	public FloatContainer normalizeWithAtrificalExtrema(double min, double max) {
		if (min > getMin() || max < getMax())
			throw new IllegalArgumentException("Provided external values are more " + "limiting than calculated ones");
		return normalize((int) min, (int) max);
	}

	@Override
	public FloatContainer normalize() {
		if (Integer.MAX_VALUE == min || Integer.MIN_VALUE == min)
			calculateMinMax();
		return normalize(min, max);
	}

	/**
	 * Does the actual normalization
	 *
	 * @param min
	 * @param max
	 * @return
	 * @throws IllegalAttributeException
	 *             when iMin is >= iMax
	 */
	private FloatContainer normalize(int min, int max) {
		if (min > max)
			throw new IllegalArgumentException("Minimum was bigger as maximum");
		if (min == max)
			Logger.log(new Status(IStatus.WARNING, this.toString(), "Min (" + min + ") was the same as max (" + max
					+ "). This is not very interesting to visualize."));
		float[] target = new float[container.length];

		for (int count = 0; count < container.length; count++) {
			if (container[count] == UNKNOWN_VALUE) {
				target[count] = FloatContainer.UNKNOWN_VALUE;
				continue;
			}
			target[count] = ((float) container[count] - min) / (max - min);
			target[count] = target[count] > 1 ? 1 : target[count];
		}
		return new FloatContainer(target);
	}

	/**
	 * The actual calculation of min and max on the local array
	 */
	private void calculateMinMax() {
		for (int current : container) {
			// Handle NaN values
			if (current == UNKNOWN_VALUE) {
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
	public FloatContainer log(int base) {
		float[] target = new float[container.length];
		final float lbase = 1.f / (float) Math.log(base);
		for (int index = 0; index < container.length; index++) {
			target[index] = (float) Math.log(container[index]) * lbase;
		}
		return new FloatContainer(target);
	}

	@Override
	public Integer get(int index) {
		return container[index];
	}

	@Override
	public void addUnknown() {
		add(UNKNOWN_VALUE);
	}

	@Override
	public boolean isUnknown(Integer value) {
		return value == UNKNOWN_VALUE;
	}
}
