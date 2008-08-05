package org.caleydo.core.data.collection.ccontainer;

import org.caleydo.core.data.collection.INumericalCContainer;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * CContainer implementation for int A container for ints. Initialized with an
 * int array. The length can not be modified after initialization. Optimized to
 * hold a large amount of data.
 * 
 * @author Alexander Lex
 */
public class IntCContainer
	implements INumericalCContainer
{

	private int[] iArContainer;

	private int iMin = Integer.MAX_VALUE;

	private int iMax = Integer.MIN_VALUE;

	/**
	 * Constructor Pass an int array. The length of the array can not be
	 * modified after initialization
	 * 
	 * @param iArContainer the int array
	 */
	public IntCContainer(int[] iArContainer)
	{
		this.iArContainer = iArContainer;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ICContainer#size()
	 */
	@Override
	public int size()
	{

		return iArContainer.length;
	}

	/**
	 * Returns the value associated with the index at the variable
	 * 
	 * @throws IndexOutOfBoundsException if index is out of specified range
	 * @param iIndex the index of the variable
	 * @return the variable associated with the index
	 */
	public int get(int iIndex)
	{
		return iArContainer[iIndex];
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.INumericalCContainer#getMin()
	 */
	@Override
	public double getMin()
	{
		if (Integer.MAX_VALUE == iMin)
			calculateMinMax();
		return iMin;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.INumericalCContainer#getMax()
	 */
	@Override
	public double getMax()
	{

		if (Integer.MIN_VALUE == iMax)
			calculateMinMax();
		return iMax;
	}

	/**
	 * Returns an iterator on the container
	 * 
	 * @return the iterator for the container
	 */
	public IntCContainerIterator iterator()
	{

		return new IntCContainerIterator(this);
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.caleydo.core.data.collection.INumericalStorage#
	 * normalizeWithExternalExtrema(double, double)
	 */
	@Override
	public FloatCContainer normalizeWithExternalExtrema(double dMin, double dMax)
	{
		if (dMin > getMin() || dMax < getMax())
		{
			throw new CaleydoRuntimeException("Provided external values are more "
					+ "limiting than calculated ones",
					CaleydoRuntimeExceptionType.DATAHANDLING);
		}
		return normalize((int) dMin, (int) dMax);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ICContainer#normalize()
	 */
	@Override
	public FloatCContainer normalize()
	{
		return normalize(iMin, iMax);
	}

	/**
	 * Does the actual normalization
	 * 
	 * @param iMin
	 * @param iMax
	 * @return
	 */
	private FloatCContainer normalize(int iMin, int iMax)
	{

		float[] fArTmpTarget = new float[iArContainer.length];

		for (int iCount = 0; iCount < iArContainer.length; iCount++)
		{
			fArTmpTarget[iCount] = ((float) iArContainer[iCount] - iMin) / (iMax - iMin);
		}
		return new FloatCContainer(fArTmpTarget);
	}

	/**
	 * The actual calculation of min and maxima on the local array
	 */
	private void calculateMinMax()
	{

		for (int iCount = 0; iCount < iArContainer.length; iCount++)
		{
			int iCurrentValue = iArContainer[iCount];

			// Handle NaN values
			if (iCurrentValue == Integer.MIN_VALUE)
				continue;

			if (iCurrentValue < iMin)
				iMin = iCurrentValue;
			if (iCurrentValue > iMax)
				iMax = iCurrentValue;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.INumericalCContainer#log10()
	 */
	@Override
	public FloatCContainer log10()
	{

		float[] fArTarget = new float[iArContainer.length];

		float fTmp;
		for (int index = 0; index < iArContainer.length; index++)
		{
			fTmp = iArContainer[index];
			fArTarget[index] = (float) Math.log10(fTmp);
		}

		return new FloatCContainer(fArTarget);
	}

}
