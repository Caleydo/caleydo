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
package org.caleydo.view.tourguide.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.util.collection.Pair;

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
	public float compute(Set<Integer> a, Set<Integer> b) {
		// http://en.wikipedia.org/wiki/Logrank_test and
		// Survival Analysis: A Self-Learning Text
		// 1. resolve data
		Pair<List<Float>, Integer> asp = getValues(a, this.clinicalVariable);
		List<Float> as = asp.getFirst();
		int asurvived = asp.getSecond(); // still there after end

		Pair<List<Float>, Integer> bsp = getValues(b, this.clinicalVariable);
		List<Float> bs = bsp.getFirst();
		int bsurvived = bsp.getSecond();
		SortedSet<Float> distinct = new TreeSet<>(as);
		distinct.addAll(bs);
		int ai = 0, bi = 0;

		float nom = 0, denom = 0;

		for (float j : distinct) {
			// 1
			float o1j = 0;
			while (ai < as.size() && as.get(ai) == j) {
				o1j++; // find act
				ai++;
			}
			float n1j = as.size() + asurvived - ai; // rest
			// 2
			float o2j = 0;
			while (bi < bs.size() && bs.get(bi) == j) {
				o2j++; // find act
				bi++;
			}
			float n2j = bs.size() + bsurvived - bi; // rest

			float e1j = n1j == 0 ? 0 : (o1j + o2j) * n1j / (n1j + n2j);
			float vj = (n1j == 0 || n2j == 0) ? 0 : (n1j * n2j * (o1j + o2j) * (n1j + n2j - o1j - o2j))
					/ ((n1j + n2j) * (n1j + n2j) * (n1j + n2j - 1));

			nom += o1j - e1j;
			denom += vj;
		}
		float z = (nom * nom) / denom;
		return z;
	}

	private Pair<List<Float>, Integer> getValues(Iterable<Integer> a, Integer col) {
		int survived = 0;
		List<Float> r = new ArrayList<>();
		for (Integer row : a) {
			Float v = clinical.getTable().getFloat(DataRepresentation.RAW, row, col);
			if (v == null || v.isNaN()) {
				survived++;
				continue;
			}
			r.add(v);
		}
		Collections.sort(r);
		return Pair.make(r, survived);
	}
}
