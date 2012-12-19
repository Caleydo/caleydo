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
package org.caleydo.view.tourguide.data.compute;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.tourguide.algorithm.IGroupAlgorithm;
import org.caleydo.view.tourguide.data.score.IRegisteredScore;
import org.caleydo.view.tourguide.data.score.IScore;

/**
 * declares that the given {@link IScore} must be computed on a group base
 *
 * @author Samuel Gratzl
 *
 */
public interface IComputedGroupScore extends IRegisteredScore {
	public boolean contains(TablePerspective a, Group ag);

	public void put(Group ag, float value);

	public IGroupAlgorithm getAlgorithm();

	public IComputeScoreFilter getFilter();
}
