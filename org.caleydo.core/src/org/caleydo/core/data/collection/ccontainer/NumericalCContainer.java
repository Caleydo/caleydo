package org.caleydo.core.data.collection.ccontainer;

import java.util.ArrayList;
import java.util.Iterator;
import org.caleydo.core.data.collection.INumericalCContainer;

/**
 * A container for numerical values. Type can be anything that implements
 * java.Number
 * 
 * @author Alexander Lex
 * @param <T> the Type, can be anything that implements java.Number
 */

public class NumericalCContainer<T extends Number>
	implements INumericalCContainer
{
	ArrayList<T> nAlContainer;

	Double dMin = Double.MAX_VALUE;
	Double dMax = Double.MIN_VALUE;

	/**
	 * Constructor Pass an arrayList of a type that extends java.Number
	 * 
	 * @param nAlContainer
	 */
	public NumericalCContainer(ArrayList<T> nAlContainer)
	{
		this.nAlContainer = nAlContainer;
	}

	/**
	 * Returns the element of type T at the index iIndex
	 * 
	 * @param iIndex the index
	 * @return the value at iIndex of type T
	 */
	public T get(int iIndex)
	{
		return nAlContainer.get(iIndex);
	}

	/**
	 * Returns an iterator on the container
	 * 
	 * @return
	 */
	public Iterator<T> iterator()
	{
		return nAlContainer.iterator();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.INumericalCContainer#getMin()
	 */
	@Override
	public double getMin()
	{
		if (dMin == Double.MAX_VALUE)
			calculateMinMax();
		return dMin;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.INumericalCContainer#getMax()
	 */
	@Override
	public double getMax()
	{
		if (dMax == Double.MIN_VALUE)
			calculateMinMax();
		return dMax;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ICContainer#normalize()
	 */
	@Override
	public PrimitiveFloatCContainer normalize()
	{
		return normalize(getMin(), getMax());
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ICContainer#size()
	 */
	@Override
	public int size()
	{
		return nAlContainer.size();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.INumericalCContainer#log10()
	 */
	@Override
	public PrimitiveFloatCContainer log10()
	{
		float[] fArTarget = new float[nAlContainer.size()];

		float fTmp;
		for (int index = 0; index < nAlContainer.size(); index++)
		{
			fTmp = nAlContainer.get(index).floatValue();
			fArTarget[index] = (float) Math.log10(fTmp);
			if (fArTarget[index] == Float.NEGATIVE_INFINITY)
				fArTarget[index] = Float.NaN;
		}

		return new PrimitiveFloatCContainer(fArTarget);
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.caleydo.core.data.collection.INumericalCContainer#
	 * normalizeWithExternalExtrema(double, double)
	 */
	@Override
	public PrimitiveFloatCContainer normalizeWithExternalExtrema(double min, double max)
	{
		return normalize(getMin(), getMax());
	}

	/**
	 * Does the actual normalization
	 * 
	 * @param dMin
	 * @param dMax
	 * @return
	 */
	private PrimitiveFloatCContainer normalize(double dMin, double dMax)
	{
		float[] fArTmpTarget = new float[nAlContainer.size()];

		for (int iCount = 0; iCount < nAlContainer.size(); iCount++)
		{
			if (Float.isNaN(nAlContainer.get(iCount).floatValue())
					|| Double.isNaN(nAlContainer.get(iCount).doubleValue()))
				fArTmpTarget[iCount] = Float.NaN;
			else
			{
				fArTmpTarget[iCount] = (nAlContainer.get(iCount).floatValue() - (float) dMin)
						/ ((float) dMax - (float) dMin);
			}
		}
		return new PrimitiveFloatCContainer(fArTmpTarget);
	}

	/**
	 * Calculates the min and max of the container and sets them to the fMin and
	 * fMax class variables
	 */
	private void calculateMinMax()
	{
		for (Number current : nAlContainer)
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
