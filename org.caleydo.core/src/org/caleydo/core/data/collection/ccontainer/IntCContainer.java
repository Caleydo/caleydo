package org.caleydo.core.data.collection.ccontainer;

import javax.management.InvalidAttributeValueException;
import org.caleydo.core.data.selection.IVirtualArray;
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

	@Override
	public double getMin()
	{
		if (Integer.MAX_VALUE == iMin)
			calculateMinMax();
		return iMin;
	}

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

	/**
	 * Iterator which takes a virtual array into account
	 * 
	 * @param virtualArray the virtual array
	 * @return the iterator
	 */
	public IntCContainerIterator iterator(IVirtualArray virtualArray)
	{
		return new IntCContainerIterator(this, virtualArray);
	}

	@Override
	public FloatCContainer normalizeWithExternalExtrema(double dMin, double dMax)
			throws InvalidAttributeValueException
	{
		if (dMin > getMin() || dMax < getMax())
		{
			throw new CaleydoRuntimeException("Provided external values are more "
					+ "limiting than calculated ones",
					CaleydoRuntimeExceptionType.DATAHANDLING);
		}
		return normalize((int) dMin, (int) dMax);
	}

	@Override
	public FloatCContainer normalize()
	{
		try
		{
			return normalize((int) getMin(), (int) getMax());
		}
		catch (InvalidAttributeValueException e)
		{
			throw new CaleydoRuntimeException(
					"Caught InvalidAttributeValueException with automatically calculated values. "
							+ "Original Message: " + e.getMessage(),
					CaleydoRuntimeExceptionType.DATAHANDLING);
		}
	}

	/**
	 * Does the actual normalization
	 * 
	 * @param iMin
	 * @param iMax
	 * @return
	 * @throws InvalidAttributeValueException when iMin is >= iMax
	 */
	private FloatCContainer normalize(int iMin, int iMax)
			throws InvalidAttributeValueException
	{
		if (iMin >= iMax)
			throw new InvalidAttributeValueException("Minimum was bigger or same as maximum");
		float[] fArTmpTarget = new float[iArContainer.length];

		for (int iCount = 0; iCount < iArContainer.length; iCount++)
		{
			fArTmpTarget[iCount] = ((float) iArContainer[iCount] - iMin) / (iMax - iMin);
			fArTmpTarget[iCount] = (fArTmpTarget[iCount] > 1) ? 1 : fArTmpTarget[iCount];
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
