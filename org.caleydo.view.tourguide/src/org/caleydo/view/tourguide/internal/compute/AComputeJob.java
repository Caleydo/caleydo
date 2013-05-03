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
package org.caleydo.view.tourguide.internal.compute;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.tourguide.internal.model.AScoreRow;
import org.caleydo.view.tourguide.internal.score.Scores;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;
import org.caleydo.view.tourguide.spi.compute.IComputedGroupScore;
import org.caleydo.view.tourguide.spi.compute.IComputedStratificationScore;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class AComputeJob extends Job {
	private final List<IComputedStratificationScore> stratScores;
	private final List<IComputedGroupScore> groupScores;
	protected Object receiver;

	public AComputeJob(Collection<IScore> scores, Object receiver) {
		super("Compute Tour Guide Scores");
		Set<IScore> flatten = Scores.flatten(scores);
		stratScores = Lists.newArrayList(Iterables.filter(flatten, IComputedStratificationScore.class));
		groupScores = Lists.newArrayList(Iterables.filter(flatten, IComputedGroupScore.class));
		this.receiver = receiver;
	}

	public boolean hasThingsToDo() {
		return !stratScores.isEmpty() || !groupScores.isEmpty();
	}

	protected IStatus runImpl(IProgressMonitor monitor, List<AScoreRow> data, BitSet mask) {
		IStatus result;
		if (!stratScores.isEmpty() && groupScores.isEmpty()) {
			Set<IComputeElement> stratifications = new HashSet<>();
			// just stratifications
			for(int i = mask.nextSetBit(0); i >= 0; i = mask.nextSetBit(i+1))
				stratifications.add(data.get(i));
			AScoreJob job = new ComputeStratificationJob(stratifications, stratScores);
			result = job.run(monitor);
		} else if (!groupScores.isEmpty()) {
			Multimap<IComputeElement, Group> d = HashMultimap.create();
			// both or just groups
			for (int i = mask.nextSetBit(0); i >= 0; i = mask.nextSetBit(i + 1)) {
				AScoreRow r = data.get(i);
				for(Group g : r.getGroups())
					d.put(r, g);
			}
			AScoreJob job = new ComputeScoreJob(d, stratScores, groupScores);
			result = job.run(monitor);
		} else {
			monitor.done();
			result = Status.OK_STATUS;
		}
		return result;
	}
}
