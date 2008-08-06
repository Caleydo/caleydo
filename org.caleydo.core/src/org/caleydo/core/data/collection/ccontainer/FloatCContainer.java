package org.caleydo.core.data.collection.ccontainer;

import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * A container for floats. Initialized with a float array. The length can not be
 * modified after initialization. Optimized to hold a large amount of data.
 * 
 * @author Alexander Lex
 */
public class FloatCContainer
	implements INumericalCContainer
{

	private float[] fArContainer;

	private float fMin = Float.NaN;

	private float fMax = Float.NaN;

	/**
	 * Constructor Pass a float array. The length of the array can not be
	 * modified after initialization
	 * 
	 * @param fArContainer the float array
	 */
	public FloatCContainer(final float[] fArContainer)
	{

		this.fArContainer = fArContainer;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ICContainer#size()
	 */
	@Override
	public int size()
	{
		return fArContainer.length;
	}

	/**
	 * Returns the value associated with the index
	 * 
	 * @throws IndexOutOfBoundsException if index out of range
	 * @param iIndex index of element to return
	 * @return the element at the specified position in this list
	 */
	public float get(final int iIndex)
	{

		return fArContainer[iIndex];
	}

	/**
	 * Returns an iterator on the container
	 * 
	 * @return the iterator for the container
	 */
	public FloatCContainerIterator iterator()
	{
		return new FloatCContainerIterator(this);
	}

	/**
	 * Returns an iterator on the container
	 * 
	 * @return the iterator for the container
	 */
	public FloatCContainerIterator iterator(IVirtualArray virtualArray)
	{
		return new FloatCContainerIterator(this, virtualArray);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.INumericalCContainer#getMin()
	 */
	@Override
	public double getMin()
	{
		if (Float.isNaN(fMin))
			calculateMinMax();
		return fMin;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.INumericalCContainer#getMax()
	 */
	@Override
	public double getMax()
	{
		if (Float.isNaN(fMax))
			calculateMinMax();
		return fMax;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ICContainer#normalize()
	 */
	@Override
	public FloatCContainer normalize()
	{
		return normalize((float) getMin(), (float) getMax());
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.caleydo.core.data.collection.INumericalStorage#
	 * normalizeWithExternalExtrema(double, double)
	 */
	@Override
	public FloatCContainer normalizeWithExternalExtrema(final double dMin, final double dMax)
	{

		if (dMin > getMin() || dMax < getMax())
		{
			throw new CaleydoRuntimeException("Provided external values are more "
					+ "limiting than calculated ones",
					CaleydoRuntimeExceptionType.DATAHANDLING);
		}
		return normalize((float) dMin, (float) dMax);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.INumericalCContainer#log10()
	 */
	@Override
	public FloatCContainer log10()
	{
		float[] fArTarget = new float[fArContainer.length];

		float fTmp;
		for (int index = 0; index < fArContainer.length; index++)
		{
			fTmp = fArContainer[index];
			fArTarget[index] = (float) Math.log10(fTmp);
			if (fArTarget[index] == Float.NEGATIVE_INFINITY)
				fArTarget[index] = Float.NaN;
		}

		return new FloatCContainer(fArTarget);
	}

	/**
	 * Does the actual normalization between 0 and 1 values that are NaN in the
	 * input are kept to be NaN
	 * 
	 * @param fMin the minimum considered in the normalization
	 * @param fMax the maximum considered in the normalization
	 * @return
	 */
	private FloatCContainer normalize(final float fMin, final float fMax)
	{
		float[] fArTmpTarget = new float[fArContainer.length];
		if (fArContainer.length > 1)
		{

			for (int iCount = 0; iCount < fArContainer.length; iCount++)
			{
				if (Float.isNaN(fArContainer[iCount]))
					fArTmpTarget[iCount] = Float.NaN;
				else
				{
					fArTmpTarget[iCount] = (fArContainer[iCount] - fMin) / (fMax - fMin);
				}
			}
		}
		return new FloatCContainer(fArTmpTarget);
	}

	/**
	 * Calculates the min and max of the container and sets them to the fMin and
	 * fMax class variables
	 */
	private void calculateMinMax()
	{
		fMin = Float.MAX_VALUE;
		fMax = Float.MIN_VALUE;
		for (int iCount = 0; iCount < fArContainer.length; iCount++)
		{
			float fCurrent = fArContainer[iCount];

			if (Float.isNaN(fCurrent))
				continue;

			if (fCurrent < fMin)
			{
				fMin = fCurrent;
				continue;
			}
			if (fCurrent > fMax)
				fMax = fCurrent;
		}
		return;
	}
}
