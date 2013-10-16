/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.compute;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.util.base.ICallback;
import org.caleydo.view.tourguide.api.model.AScoreRow;
import org.caleydo.view.tourguide.api.score.Scores;
import org.caleydo.view.tourguide.internal.event.JobStateProgressEvent;
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
 * basic job implementation that
 *
 * @author Samuel Gratzl
 *
 */
public abstract class AComputeJob extends Job implements ICallback<Boolean> {
	private final List<IComputedStratificationScore> stratScores;
	private final List<IComputedGroupScore> groupScores;
	protected Object receiver;

	public AComputeJob(Collection<IScore> scores, Object receiver) {
		super("Compute LineUp Scores");
		Set<IScore> flatten = Scores.flatten(scores);
		stratScores = Lists.newArrayList(Iterables.filter(flatten, IComputedStratificationScore.class));
		groupScores = Lists.newArrayList(Iterables.filter(flatten, IComputedGroupScore.class));
		this.receiver = receiver;
	}

	@Override
	public final void on(Boolean data) {
		if (data)
			cancel();
	}

	/**
	 * triggers a progress message
	 *
	 * @param completed
	 * @param text
	 */
	protected final void progress(float completed, String text) {
		EventPublisher.trigger(new JobStateProgressEvent(text, completed, false).to(receiver).from(this));
	}

	/**
	 * triggers an progress error message
	 *
	 * @param text
	 */
	protected final void error(String text) {
		EventPublisher.trigger(new JobStateProgressEvent(text, 1.0f, true).to(receiver).from(this));
	}

	/**
	 * whether the job has some work
	 *
	 * @return
	 */
	public boolean hasThingsToDo() {
		return !stratScores.isEmpty() || !groupScores.isEmpty();
	}

	/**
	 * computes scores of the given masked data
	 *
	 * @param monitor
	 * @param data
	 * @param mask
	 * @return
	 */
	protected IStatus runImpl(IProgressMonitor monitor, List<AScoreRow> data, BitSet mask) {
		IStatus result;
		if (!stratScores.isEmpty() && groupScores.isEmpty()) {
			Set<IComputeElement> stratifications = new HashSet<>();
			// just stratifications
			for(int i = mask.nextSetBit(0); i >= 0; i = mask.nextSetBit(i+1))
				stratifications.add(data.get(i));
			AScoreJob job = new ComputeStratificationJob(stratifications, stratScores, receiver);
			result = job.run(monitor);
		} else if (!groupScores.isEmpty()) {
			Multimap<IComputeElement, Group> d = HashMultimap.create();
			// both or just groups
			for (int i = mask.nextSetBit(0); i >= 0; i = mask.nextSetBit(i + 1)) {
				AScoreRow r = data.get(i);
				for(Group g : r.getGroups())
					d.put(r, g);
			}
			AScoreJob job = new ComputeScoreJob(d, stratScores, groupScores, receiver);
			result = job.run(monitor);
		} else {
			progress(1, "Done");
			monitor.done();
			result = Status.OK_STATUS;
		}
		return result;
	}
}
