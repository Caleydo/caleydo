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
package org.caleydo.view.tourguide.internal.model;

import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.tourguide.spi.score.IGroupScore;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.view.tourguide.spi.score.IStratificationScore;
import org.caleydo.vis.rank.data.AFloatFunction;
import org.caleydo.vis.rank.model.IRow;


/**
 * the current strategy up to now is to show just a single stratification but here the maximal value for a group score,
 * 
 * therefore if we have a group score
 * 
 * @author Samuel Gratzl
 * 
 */
public class MaxGroupCombiner extends AFloatFunction<IRow> {

	private final IScore score;

	/**
	 * @param score
	 */
	public MaxGroupCombiner(IScore score) {
		this.score = score;
	}

	@Override
	public float applyPrimitive(IRow in) {
		AScoreRow row = (AScoreRow) in;
		if (score instanceof IStratificationScore && !(score instanceof IGroupScore)) {
			return score.apply(row, null); // as group independent
		}
		float v = Float.NaN;
		for(Group g : row.getGroups()) {
			float vg = score.apply(row, g);
			if (Float.isNaN(vg))
				continue;
			if (Float.isNaN(v) || vg > v)
				v = vg;
		}
		return v;
	}

	public static Group getMax(IRow in, IScore score) {
		if (score == null)
			return null;
		AScoreRow row = (AScoreRow) in;
		if (score instanceof IStratificationScore && !(score instanceof IGroupScore)) {
			return null;
		}

		// combine groups
		float v = Float.NaN;
		Group gm = null;
		for (Group g : row.getGroups()) {
			float vg = score.apply(row, g);
			if (Float.isNaN(vg))
				continue;
			if (Float.isNaN(v) || vg > v) {
				v = vg;
				gm = g;
			}
		}
		return gm;
	}

}
