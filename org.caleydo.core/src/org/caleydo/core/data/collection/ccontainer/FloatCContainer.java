package org.caleydo.core.data.collection.ccontainer;

import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.util.conversion.ConversionTools;

/**
 * A container for floats. Initialized with a float array. The length can not be modified after
 * initialization. Optimized to hold a large amount of data.
 * 
 * @author Alexander Lex
 */
public class FloatCContainer
	implements INumericalCContainer {

	private float[] container;

	private float fMin = Float.NaN;

	private float fMax = Float.NaN;

	/**
	 * Constructor Pass a float array. The length of the array can not be modified after initialization
	 * 
	 * @param fArContainer
	 *            the float array
	 */
	public FloatCContainer(final float[] fArContainer) {

		this.container = fArContainer;
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
		if (Float.isNaN(fMin)) {
			calculateMinMax();
		}
		return fMin;
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
		return new FloatCContainer(ConversionTools.normalize(container, (int) getMin(), (int) getMax()));
	}

	@Override
	public FloatCContainer normalizeWithExternalExtrema(final double dMin, final double dMax) {
		if (dMin >= dMax)
			throw new IllegalArgumentException("Minimum was bigger or same as maximum");

		return new FloatCContainer(ConversionTools.normalize(container, (float) dMin, (float) dMax));

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
	public FloatCContainer log(int iBase) {
		float[] fArTarget = new float[container.length];

		float fTmp;
		for (int index = 0; index < container.length; index++) {
			fTmp = container[index];

			fArTarget[index] = (float) Math.log(fTmp) / (float) Math.log(iBase);

			if (fArTarget[index] == Float.NEGATIVE_INFINITY) {
				fArTarget[index] = 0;
			}
		}

		return new FloatCContainer(fArTarget);
	}

	/**
	 * Calculates the min and max of the container and sets them to the fMin and fMax class variables
	 */
	private void calculateMinMax() {
		fMin = Float.MAX_VALUE;
		fMax = Float.MIN_VALUE;
		for (float fCurrent : container) {
			if (Float.isNaN(fCurrent)) {
				continue;
			}

			if (fCurrent < fMin) {
				fMin = fCurrent;
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
