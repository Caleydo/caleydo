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
package org.caleydo.view.tourguide.data.score;

import java.util.Collection;

import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.view.tourguide.data.ScoringElement;

/**
 * @author Samuel Gratzl
 *
 */
public enum ECollapseOperator implements ILabelProvider {
	NONE, MUTUTAL_EXCLUSIVE;

	@Override
	public String getLabel() {
		switch (this) {
		case NONE:
			return "None";
		case MUTUTAL_EXCLUSIVE:
			return "Mutual Exclusive";

		default:
			throw new IllegalStateException("invalid enum: " + this);
		}
	}

	@Override
	public String getProviderName() {
		return null;
	}

	public float apply(ScoringElement elem, IScore current, Collection<IScore> all) {
		switch (this) {
		case NONE:
			return current.getScore(elem);
		case MUTUTAL_EXCLUSIVE:
			// see #984: high score to the current one and a low score to all the others
			float base = current.getScore(elem);
			float[] others = new float[all.size() - 1];
			int i = 0;
			for (IScore s : all) {
				if (s == current)
					continue;
				others[i++] = s.getScore(elem);
			}
			return base - ECombinedOperator.MEAN.apply(others);
		default:
			throw new IllegalStateException("invalid enum: " + this);
		}
	}

}

