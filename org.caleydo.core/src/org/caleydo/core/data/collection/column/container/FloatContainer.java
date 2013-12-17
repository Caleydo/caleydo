/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.collection.column.container;

import java.util.Arrays;
import java.util.Iterator;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.util.conversion.ConversionTools;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * A container for floats. Initialize with a float array or iteratively add data up to {@link #size()} once! The length
 * can not be modified after initialization.
 *
 * @author Alexander Lex
 */
public class FloatContainer implements INumericalContainer<Float> {

	/** The value used for unknown values */
	public static final float UNKNOWN_VALUE = Float.NaN;

	/** The actual data */
	private final float[] container;

	/** Keeps track of the next free index for adding data */
	private int nextIndex = 0;

	/** The smallest value in this container */
	private float min = Float.NaN;

	/** The biggest value in this container */
	private float max = Float.NaN;

	/**
	 * Constructor initializing the container without data. The length can not be changed.
	 *
	 * @param size
	 */
	public FloatContainer(int size) {
		container = new float[size];
	}

	/**
	 * Constructor setting a pre-filled container. The length can not be changed.
	 *
	 * @param container
	 *            the float array
	 */
	public FloatContainer(float[] container) {
		this.container = container;
	}

	@Override
	public int size() {
		return container.length;
	}

	@Override
	public Iterator<Float> iterator() {
		return new ContainerIterator<>(this);
	}

	@Override
	public EDataType getDataType() {
		return EDataType.FLOAT;
	}

	@Override
	public Float get(int index) {
		return container[index];
	}

	/** Equivalent to {@link IContainer#get(int)} using the primitive data type avoiding the boxing */
	public float getPrimitive(final int index) {
		if (index < 0 || index >= container.length)
			return UNKNOWN_VALUE;
		return container[index];
	}

	@Override
	public void add(Float value) {
		container[nextIndex++] = value;
	}

	/** Implementation of {@link #add(Float)} with primitive type to avoid auto-boxing */
	public void add(float value) {
		container[nextIndex++] = value;
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
		if (Float.isNaN(max) || Float.isNaN(min))
			calculateMinMax();
		return new FloatContainer(ConversionTools.normalize(container, min, max));
	}

	@Override
	public FloatContainer normalizeWithAtrificalExtrema(double min, double max) {
		if (min > max)
			throw new IllegalArgumentException("Minimum was bigger then maximum");
		if (min == max)
			Logger.log(new Status(IStatus.WARNING, this.toString(), "Min (" + min + ") was the same as max (" + max
					+ "). This is not very interesting to visualize."));

		return new FloatContainer(ConversionTools.normalize(container, (float) min, (float) max));

	}

	@Override
	public FloatContainer log(int base) {
		float[] target = new float[container.length];

		float tmp;
		for (int index = 0; index < container.length; index++) {
			tmp = container[index];

			target[index] = (float) (Math.log(tmp) / Math.log(base));

			if (target[index] == Float.NEGATIVE_INFINITY) {
				target[index] = 0;
			}
		}
		return new FloatContainer(target);
	}

	/**
	 * Calculates the min and max of the container and sets them to the min and max class variables
	 */
	private void calculateMinMax() {
		min = Float.POSITIVE_INFINITY;
		max = Float.NEGATIVE_INFINITY;
		int counter = -1;
		for (float current : container) {
			counter++;
			if (Float.isNaN(current)) {
				continue;
			}

			if (Float.isInfinite(current)) {
				Logger.log(new Status(IStatus.WARNING, this.toString(),
						"Value for normalization was infinity at index " + counter + ": " + current));
				continue;
			}
			if (current < min) {
				min = current;
			}
			if (current > max) {
				max = current;
			}
		}

		// Needed if all values in array are NaN
		if (min == Float.POSITIVE_INFINITY)
			min = Float.NaN;
		if (max == Float.NEGATIVE_INFINITY)
			max = Float.NaN;

		return;
	}

	@Override
	public String toString() {
		return Arrays.toString(container);
	}

	@Override
	public void addUnknown() {
		add(UNKNOWN_VALUE);
	}

	@Override
	public boolean isUnknown(Float value) {
		return value == null || value.isNaN();
	}

	@Override
	public void set(int index, Float value) {
		container[index] = value;

	}
}
