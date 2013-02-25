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

import java.util.Set;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.statistics.Statistics;
import org.caleydo.view.tourguide.spi.algorithm.IGroupAlgorithm;

/**
 * @author Samuel Gratzl
 *
 */
public class JaccardIndex implements IGroupAlgorithm {
	private static final JaccardIndex instance = new JaccardIndex();

	public static JaccardIndex get() {
		return instance;
	}

	private JaccardIndex() {

	}

	@Override
	public String getAbbreviation() {
		return "JI";
	}

	@Override
	public String getDescription() {
		return "Jaccard Index against ";
	}

	@Override
	public IDType getTargetType(Perspective a, Perspective b) {
		return a.getIdType();
	}

	@Override
	public float compute(Set<Integer> a, Set<Integer> b) {
		return Statistics.jaccardIndex(a, b);
	}
}
