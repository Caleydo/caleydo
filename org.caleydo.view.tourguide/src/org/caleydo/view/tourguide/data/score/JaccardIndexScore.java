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
import java.util.Collections;
import java.util.Map;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.tourguide.data.compute.IDSet;
import org.caleydo.view.tourguide.data.compute.IDSetScoreComputes;
import org.caleydo.view.tourguide.data.compute.IDSetScoreComputes.IIDSetGroupScoreFun;

import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * implementation of the jaccard score to compare groups
 *
 * @author Samuel Gratzl
 *
 */
public class JaccardIndexScore extends AGroupScore implements IBatchComputedGroupScore {
	private final static IIDSetGroupScoreFun jaccardIndex = new IIDSetGroupScoreFun() {
		@Override
		public float apply(IDSet a, IDSet b, Predicate<Integer> inB) {
			int mappabledifference = 0;
			int intersection = 0;
			for (Integer ai : a) {
				if (b.contains(ai))
					intersection++;
				else if (inB.apply(ai))
					mappabledifference++;
			}
			// bs elements + those of a that are mappable to the other one
			int union = b.size() + mappabledifference;

			float score = union == 0 ? 0.f : (float) intersection / union;
			return score;
		}
	};


	public JaccardIndexScore(TablePerspective stratification, Group group) {
		this(null, stratification, group);
	}

	public JaccardIndexScore(String label, TablePerspective stratification, Group group) {
		super(label, stratification, group);
	}

	@Override
	public void apply(Multimap<TablePerspective, Group> stratNGroups) {
		apply(Collections.<IBatchComputedGroupScore> singleton(this), stratNGroups);
	}

	@Override
	public void apply(Collection<IBatchComputedGroupScore> batch, Multimap<TablePerspective, Group> stratNGroups) {
		applyImpl(batch, stratNGroups);
	}

	private static void applyImpl(Collection<IBatchComputedGroupScore> batch,
			Multimap<TablePerspective, Group> stratNGroups) {
		Multimap<TablePerspective, AGroupScore> against = ArrayListMultimap.create();
		for (IBatchComputedGroupScore b : batch) {
			AGroupScore s = (AGroupScore) b;
			// not all cached?
			for (Map.Entry<TablePerspective, Group> g : stratNGroups.entries()) {
				if (!s.contains(g.getKey(), g.getValue())) {
					against.put(s.stratification, s);
					break;
				}
			}
		}
		new IDSetScoreComputes(stratNGroups, against, jaccardIndex).run();
	}


}
