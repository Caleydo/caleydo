/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation.wilcoxon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.collect.Lists;

/**
 *
 * @author Christian
 *
 */
public class AdjustedPValue {

	public double p;
	public double adjustedP;

	public AdjustedPValue() {

	}

	public AdjustedPValue(double p) {
		this.p = p;
	}

	public AdjustedPValue(double p, double adjustedP) {
		this.p = p;
		this.adjustedP = adjustedP;
	}

	@Override
	public String toString() {
		return "P: " + p + ", AdjustedP: " + adjustedP;
	}

	/**
	 * Calculates the adjusted P values using False Discovery Rate. Fills the {@link #adjustedP} fields of the specified
	 * list.
	 *
	 * @param pValues
	 */
	public static void calcAdjustedPwithFDR(List<? extends AdjustedPValue> pValues) {

		List<AdjustedPValue> orderedPValues = new ArrayList<AdjustedPValue>(pValues);

		Collections.sort(orderedPValues, new Comparator<AdjustedPValue>() {

			@Override
			public int compare(AdjustedPValue arg0, AdjustedPValue arg1) {
				return Double.compare(arg0.p, arg1.p);
			}
		});

		int numElements = orderedPValues.size();

		for (int i = numElements - 1; i >= 0; i--) {
			AdjustedPValue v = orderedPValues.get(i);
			if (i == numElements - 1) {
				v.adjustedP = v.p;
			} else {
				double adjP = ((double) numElements / (i + 1)) * v.p;
				v.adjustedP = Math.min(orderedPValues.get(i + 1).adjustedP, adjP);
			}
		}
	}

	public static void main(String[] args) {
		List<AdjustedPValue> values = Lists.newArrayList(new AdjustedPValue(0.01), new AdjustedPValue(0.01),
				new AdjustedPValue(0.078), new AdjustedPValue(0.05));
		calcAdjustedPwithFDR(values);

		System.out.println(values);
	}
}
