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
package org.caleydo.view.tourguide.spi.algorithm;

import java.util.List;
import java.util.Set;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.id.IDType;

/**
 * @author Samuel Gratzl
 *
 */
public interface IStratificationAlgorithm {

	IDType getTargetType(Perspective a, Perspective b);

	/**
	 * computes the score between the two stratifications identified by their collection of group sets
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	float compute(List<Set<Integer>> a, List<Set<Integer>> b);

	/**
	 * returns the abbreviation of this algorithm
	 *
	 * @return
	 */
	String getAbbreviation();

	String getDescription();
}
