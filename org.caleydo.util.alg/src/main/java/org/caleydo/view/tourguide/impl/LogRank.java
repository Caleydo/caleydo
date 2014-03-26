/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.collection.column.container.CategoricalContainer;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;
import org.caleydo.view.tourguide.spi.algorithm.IGroupAlgorithm;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Samuel Gratzl
 *
 */
public class LogRank implements IGroupAlgorithm {
	private final ATableBasedDataDomain clinical;
	private final Integer clinicalVariable;

	public static LogRank get(Integer clinicalVariable, ATableBasedDataDomain clinical) {
		return new LogRank(clinicalVariable, clinical);
	}

	private LogRank(Integer clinicalVariable, ATableBasedDataDomain clinical) {
		this.clinicalVariable = clinicalVariable;
		this.clinical = clinical;
	}

	@Override
	public String getAbbreviation() {
		return "LR";
	}

	@Override
	public String getDescription() {
		return "Log Rank of ";
	}


	@Override
	public IDType getTargetType(IComputeElement a, IComputeElement b) {
		return clinical.getRecordIDType();
	}

	public static double getPValue(double logRankScore) {
		if (Double.isNaN(logRankScore) || Double.isInfinite(logRankScore))
			return Float.NaN;
		double r = Statistics.chiSquaredProbability(logRankScore, 1); // see #983
		return r;
	}

	@Override
	public void init(IProgressMonitor monitor) {
		// nothing todo
	}

	@Override
	public double compute(Set<Integer> a, Group ag, Set<Integer> b, Group bg, IProgressMonitor monitor) {
		return compute(a, b, monitor);
	}

	public double compute(Iterable<Integer> a, Iterable<Integer> b, IProgressMonitor monitor) {
		// http://en.wikipedia.org/wiki/Logrank_test and
		// Survival Analysis: A Self-Learning Text
		// 1. resolve data
		Pair<List<Double>, Integer> asp = getValues(a, this.clinicalVariable);
		List<Double> as = asp.getFirst();
		int asurvived = asp.getSecond(); // still there after end
		if (monitor.isCanceled())
			return Float.NaN;

		Pair<List<Double>, Integer> bsp = getValues(b, this.clinicalVariable);
		List<Double> bs = bsp.getFirst();
		int bsurvived = bsp.getSecond();
		if (monitor.isCanceled())
			return Float.NaN;
		double r = Statistics.logRank(as, asurvived, bs, bsurvived);
		if (Double.isInfinite(r))
			return Double.NaN;
		return (float) r;
	}

	private Pair<List<Double>, Integer> getValues(Iterable<Integer> a, Integer col) {
		int survived = 0;
		List<Double> r = new ArrayList<>();
		for (Integer row : a) {
			Number v = clinical.getTable().getRaw(col, row);
			if (v == null || Double.isNaN(v.doubleValue()) || CategoricalContainer.UNKNOWN_CATEGORY_INT.equals(v)) {
				survived++;
				continue;
			}
			r.add(v.doubleValue());
		}
		Collections.sort(r);
		return Pair.make(r, survived);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clinicalVariable == null) ? 0 : clinicalVariable.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogRank other = (LogRank) obj;
		if (clinicalVariable == null) {
			if (other.clinicalVariable != null)
				return false;
		} else if (!clinicalVariable.equals(other.clinicalVariable))
			return false;
		return true;
	}
}
