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
package org.caleydo.view.tourguide.impl.algorithm;

import java.util.List;
import java.util.Set;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.statistics.Statistics;
import org.caleydo.view.tourguide.spi.algorithm.IStratificationAlgorithm;

/**
 * @author Samuel Gratzl
 *
 */
public class AdjustedRandIndex implements IStratificationAlgorithm {
	private static final AdjustedRandIndex instance = new AdjustedRandIndex();

	public static AdjustedRandIndex get() {
		return instance;
	}

	private AdjustedRandIndex() {

	}

	@Override
	public String getAbbreviation() {
		return "AR";
	}

	@Override
	public String getDescription() {
		return "Adjusted Rand of ";
	}

	@Override
	public IDType getTargetType(Perspective a, Perspective b) {
		return a.getIdType();
	}

	@Override
	public float compute(List<Set<Integer>> a, List<Set<Integer>> b) {
		return Statistics.randIndex(a, b);
	}
}

