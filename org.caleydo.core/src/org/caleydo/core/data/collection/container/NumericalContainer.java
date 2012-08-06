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

import java.util.ArrayList;

/**
 * A container for numerical values. Type can be anything that implements java.Number
 * 
 * @author Alexander Lex
 * @param <T>
 *            the Type, can be anything that implements java.Number
 */

public class NumericalContainer<T extends Number>
	extends ATypedContainer<T>
	implements INumericalContainer

{

	Double dMin = Double.MAX_VALUE;
	Double dMax = Double.MIN_VALUE;

	/**
	 * Constructor Pass an arrayList of a type that extends java.Number
	 * 
	 * @param alContainer
	 */
	public NumericalContainer(ArrayList<T> alContainer) {
		this.alContainer = alContainer;
	}

	@Override
	public double getMin() {
		if (dMin == Double.MAX_VALUE) {
			calculateMinMax();
		}
		return dMin;
	}

	@Override
	public double getMax() {
		if (dMax == Double.MIN_VALUE) {
			calculateMinMax();
		}
		return dMax;
	}

	@Override
	public FloatContainer normalize() {
		return normalize(getMin(), getMax());
	}

	@Override
	public FloatContainer log(int iBase) {
		float[] fArTarget = new float[alContainer.size()];

		float fTmp;
		for (int index = 0; index < alContainer.size(); index++) {
			fTmp = alContainer.get(index).floatValue();
			fArTarget[index] = (float) Math.log(fTmp) / (float) Math.log(iBase);
			if (fArTarget[index] == Float.NEGATIVE_INFINITY) {
				fArTarget[index] = Float.NaN;
			}
		}

		return new FloatContainer(fArTarget);
	}

	@Override
	public FloatContainer normalizeWithExternalExtrema(double dMin, double dMax) {
		return normalize(dMin, dMax);
	}

	/**
	 * Does the actual normalization
	 * 
	 * @param dMin
	 * @param dMax
	 * @return a new container with the normalized values
	 * @throws IllegalAttributeException
	 *             when dMin is >= dMax
	 */
	private FloatContainer normalize(double dMin, double dMax) {

		if (dMin >= dMax)
			throw new IllegalArgumentException("Minimum was bigger or same as maximum");

		float[] fArTmpTarget = new float[alContainer.size()];

		for (int iCount = 0; iCount < alContainer.size(); iCount++) {
			if (Float.isNaN(alContainer.get(iCount).floatValue())
				|| Double.isNaN(alContainer.get(iCount).doubleValue())) {
				fArTmpTarget[iCount] = Float.NaN;
			}
			else {

				fArTmpTarget[iCount] =
					(alContainer.get(iCount).floatValue() - (float) dMin) / ((float) dMax - (float) dMin);
				if (fArTmpTarget[iCount] > 1) {
					fArTmpTarget[iCount] = 1;
				}
				else if (fArTmpTarget[iCount] < 0) {
					fArTmpTarget[iCount] = 0;
				}
			}
		}
		return new FloatContainer(fArTmpTarget);
	}

	/**
	 * Calculates the min and max of the container and sets them to the fMin and fMax class variables
	 */
	private void calculateMinMax() {
		for (Number current : alContainer) {
			if (Float.isNaN(current.floatValue()) || Double.isNaN(current.doubleValue())) {
				continue;
			}

			if (current.doubleValue() < dMin) {
				dMin = current.doubleValue();
				continue;
			}
			if (current.doubleValue() > dMax) {
				dMax = current.doubleValue();
			}
		}
		return;
	}

}
