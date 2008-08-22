package org.caleydo.core.data.collection.ccontainer;

import java.util.ArrayList;
import javax.management.InvalidAttributeValueException;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * A container for numerical values. Type can be anything that implements
 * java.Number
 * 
 * @author Alexander Lex
 * @param <T> the Type, can be anything that implements java.Number
 */

public class NumericalCContainer<T extends Number>
	extends ATypedCContainer<T>
	implements INumericalCContainer

{

	Double dMin = Double.MAX_VALUE;
	Double dMax = Double.MIN_VALUE;

	/**
	 * Constructor Pass an arrayList of a type that extends java.Number
	 * 
	 * @param alContainer
	 */
	public NumericalCContainer(ArrayList<T> alContainer)
	{
		this.alContainer = alContainer;
	}

	@Override
	public double getMin()
	{
		if (dMin == Double.MAX_VALUE)
			calculateMinMax();
		return dMin;
	}

	@Override
	public double getMax()
	{
		if (dMax == Double.MIN_VALUE)
			calculateMinMax();
		return dMax;
	}

	@Override
	public FloatCContainer normalize()
	{
		try
		{
			return normalize(getMin(), getMax());
		}
		catch (InvalidAttributeValueException e)
		{
			throw new CaleydoRuntimeException(
					"Caught InvalidAttributeValueException with automatically calculated values. "
							+ "Original Message: " + e.getMessage(),
					CaleydoRuntimeExceptionType.DATAHANDLING);
		}
	}

	@Override
	public FloatCContainer log10()
	{
		float[] fArTarget = new float[alContainer.size()];

		float fTmp;
		for (int index = 0; index < alContainer.size(); index++)
		{
			fTmp = alContainer.get(index).floatValue();
			fArTarget[index] = (float) Math.log10(fTmp);
			if (fArTarget[index] == Float.NEGATIVE_INFINITY)
				fArTarget[index] = Float.NaN;
		}

		return new FloatCContainer(fArTarget);
	}

	@Override
	public FloatCContainer normalizeWithExternalExtrema(double dMin, double dMax)
			throws InvalidAttributeValueException
	{
		return normalize(dMin, dMax);
	}

	/**
	 * Does the actual normalization
	 * 
	 * @param dMin
	 * @param dMax
	 * @return a new container with the normalized values
	 * @throws InvalidAttributeValueException when dMin is >= dMax
	 */
	private FloatCContainer normalize(double dMin, double dMax)
			throws InvalidAttributeValueException
	{

		if (dMin >= dMax)
			throw new InvalidAttributeValueException("Minimum was bigger or same as maximum");

		float[] fArTmpTarget = new float[alContainer.size()];

		for (int iCount = 0; iCount < alContainer.size(); iCount++)
		{
			if (Float.isNaN(alContainer.get(iCount).floatValue())
					|| Double.isNaN(alContainer.get(iCount).doubleValue()))
				fArTmpTarget[iCount] = Float.NaN;
			else
			{
				fArTmpTarget[iCount] = (alContainer.get(iCount).floatValue() - (float) dMin)
						/ ((float) dMax - (float) dMin);
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
		for (Number current : alContainer)
		{
			if (Float.isNaN(current.floatValue()) || Double.isNaN(current.doubleValue()))
				continue;

			if (current.doubleValue() < dMin)
			{
				dMin = current.doubleValue();
				continue;
			}
			if (current.doubleValue() > dMax)
				dMax = current.doubleValue();
		}
		return;
	}

}
