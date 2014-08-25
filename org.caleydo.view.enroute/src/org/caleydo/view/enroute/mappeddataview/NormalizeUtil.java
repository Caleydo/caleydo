/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.mappeddataview;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.function.DoubleFunctions;
import org.caleydo.core.util.function.IInvertableDoubleFunction;

/**
 * Utility methods that infer a normalize function from two pairs of raw and normalized values.
 *
 * @author Christian
 *
 */
public final class NormalizeUtil {

	public static IInvertableDoubleFunction inferNormalizeFunction(ATableBasedDataDomain dataDomain,
			VirtualArray columnVA, IDType columnIDType, Integer rowID, IDType rowIDType) {

		if (columnVA.size() <= 1)
			return null;

		double firstRaw = 0;
		double firstNormalized = 0;
		double secondRaw = Float.NaN;
		double secondNormalized = Float.NaN;
		for (int i = 0; i < columnVA.size(); i++) {
			Integer id = columnVA.get(i);
			double normalizedValue = dataDomain.getNormalizedValue(rowIDType, rowID, columnIDType, id);
			double rawValue = ((Number) dataDomain.getRaw(rowIDType, rowID, columnIDType, id)).doubleValue();

			if (i == 0) {
				firstNormalized = normalizedValue;
				firstRaw = rawValue;
			} else if (normalizedValue != firstNormalized) {
				secondNormalized = normalizedValue;
				secondRaw = rawValue;
				break;
			}
		}

		if (!Double.isNaN(secondNormalized)) {
			return inferNormalizeFunction(firstRaw, firstNormalized, secondRaw, secondNormalized);
		}

		return null;
	}

	public static IInvertableDoubleFunction inferNormalizeFunction(double[] rawValues, double[] normalizedValues) {
		if (normalizedValues.length > 1) {
			int index1 = 0;
			int index2 = -1;
			// Find two different values -> needed to infer min and max for normalize function
			for (int i = 1; i < normalizedValues.length; i++) {
				if (normalizedValues[index1] != normalizedValues[i]) {
					index2 = i;
					break;
				}
			}

			if (index2 != -1) {
				return inferNormalizeFunction(rawValues[index1], normalizedValues[index1], rawValues[index2],
						normalizedValues[index2]);
			}
		}
		return null;
	}

	public static IInvertableDoubleFunction inferNormalizeFunction(double rawVal1, double normalizedVal1,
			double rawVal2, double normalizedVal2) {
		double inferredRawMin = (normalizedVal2 * rawVal1 - normalizedVal1 * rawVal2)
				/ (normalizedVal2 - normalizedVal1);
		double delta = (rawVal2 - inferredRawMin) / normalizedVal2;
		double inferredRawMax = inferredRawMin + delta;
		return DoubleFunctions.normalize(inferredRawMin, inferredRawMax);
	}

}
