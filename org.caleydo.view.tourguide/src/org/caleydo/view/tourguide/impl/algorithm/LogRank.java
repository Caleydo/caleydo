/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.tourguide.impl.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.statistics.Statistics;
import org.caleydo.view.tourguide.spi.algorithm.IGroupAlgorithm;

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
	public IDType getTargetType(Perspective a, Perspective b) {
		return clinical.getRecordIDType();
	}

	public static float getPValue(float logRankScore) {
		if (Float.isNaN(logRankScore))
			return Float.NaN;
		double r = Statistics.chiSquaredProbability(logRankScore, 1); // see #983
		return (float) r;
	}

	@Override
	public float compute(Set<Integer> a, Set<Integer> b) {
		return compute((Iterable<Integer>) a, b);
	}

	public float compute(Iterable<Integer> a, Iterable<Integer> b) {
		// http://en.wikipedia.org/wiki/Logrank_test and
		// Survival Analysis: A Self-Learning Text
		// 1. resolve data
		Pair<List<Float>, Integer> asp = getValues(a, this.clinicalVariable);
		List<Float> as = asp.getFirst();
		int asurvived = asp.getSecond(); // still there after end

		Pair<List<Float>, Integer> bsp = getValues(b, this.clinicalVariable);
		List<Float> bs = bsp.getFirst();
		int bsurvived = bsp.getSecond();
		return Statistics.logRank(as, asurvived, bs, bsurvived);
	}

	private Pair<List<Float>, Integer> getValues(Iterable<Integer> a, Integer col) {
		int survived = 0;
		List<Float> r = new ArrayList<>();
		for (Integer row : a) {
			Number v = clinical.getTable().getRaw(col, row);
			if (v == null || Float.isNaN(v.floatValue())) {
				survived++;
				continue;
			}
			r.add(v.floatValue());
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
