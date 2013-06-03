package org.caleydo.view.tourguide.internal.compute;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;
import org.caleydo.view.tourguide.spi.algorithm.IGroupAlgorithm;
import org.caleydo.view.tourguide.spi.compute.IComputedGroupScore;
import org.caleydo.view.tourguide.spi.compute.IComputedReferenceGroupScore;
import org.caleydo.view.tourguide.spi.compute.IComputedReferenceStratificationScore;
import org.caleydo.view.tourguide.spi.compute.IComputedStratificationScore;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

public class ComputeScoreJob extends AScoreJob {
	private static final Logger log = Logger.create(ComputeScoreJob.class);

	private final Multimap<IComputeElement, Group> data;

	private final Collection<IComputedStratificationScore> stratMetrics;
	private final Collection<IComputedReferenceStratificationScore> stratScores;

	private final Collection<IComputedGroupScore> groupMetrics;
	private final Collection<IComputedReferenceGroupScore> groupScores;

	public ComputeScoreJob(Multimap<IComputeElement, Group> data, Collection<IComputedStratificationScore> stratScores,
			Collection<IComputedGroupScore> groupScores, Object receiver) {
		super(receiver);
		Pair<Collection<IComputedStratificationScore>, Collection<IComputedReferenceStratificationScore>> strats = partition(
				stratScores, IComputedReferenceStratificationScore.class);
		this.stratMetrics = strats.getFirst();
		this.stratScores = strats.getSecond();

		Pair<Collection<IComputedGroupScore>, Collection<IComputedReferenceGroupScore>> groups = partition(groupScores,
				IComputedReferenceGroupScore.class);
		this.groupMetrics = groups.getFirst();
		this.groupScores = groups.getSecond();
		this.data = data;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		if (data.isEmpty()
				|| (groupMetrics.isEmpty() && groupScores.isEmpty() && stratScores.isEmpty() && stratMetrics.isEmpty()))
			return Status.OK_STATUS;

		final int total = data.keySet().size() + 1;
		monitor.beginTask("Compute Tour Guide Scores", total);
		log.info(
				"computing group similarity of %d against %d group scores, %d group metrics, %d stratification scores and %d stratification metrics",
				data.size(), groupScores.size(), groupMetrics.size(), stratScores.size(), stratMetrics.size());
		Stopwatch w = new Stopwatch().start();

		progress(0, "Initializing...");
		for (IComputedStratificationScore score : Iterables.concat(stratMetrics, stratScores)) {
			score.getAlgorithm().init(monitor);
			if (Thread.interrupted() || monitor.isCanceled())
				return Status.CANCEL_STATUS;
		}
		for (IComputedGroupScore score : Iterables.concat(groupMetrics, groupScores)) {
			score.getAlgorithm().init(monitor);
			if (Thread.interrupted() || monitor.isCanceled())
				return Status.CANCEL_STATUS;
		}
		int c = 0;
		monitor.worked(1);
		progress(c++ / (float) total, "Computing...");

		Iterator<IComputeElement> it = this.data.keySet().iterator();
		// first time the one run to compute the progress frequency interval
		{
			IComputeElement as = it.next();
			if (!run(monitor, as))
				return Status.CANCEL_STATUS;
			monitor.worked(1);
			c++;
		}
		final int fireEvery = fireEvery(w.elapsedMillis());

		int f = fireEvery - 1;

		while (it.hasNext()) {
			IComputeElement as = it.next();
			if (f == 0) {
				progress(c / (float) total, "Computing " + as.getLabel());
				f = fireEvery;
			}
			f--;

			if (!run(monitor, as))
				return Status.CANCEL_STATUS;

			monitor.worked(1);
			c++;
		}
		System.out.println("done in " + w);
		monitor.done();
		return Status.OK_STATUS;
	}

	private boolean run(IProgressMonitor monitor, IComputeElement as) {
		if (Thread.interrupted() || monitor.isCanceled())
			return false;

		if (computeStratificationScores(monitor, as, stratMetrics, stratScores) != null)
			return false;

		if (computeGroupScores(monitor, as) != null)
			return false;

		// cleanup cache
		for (Group targetGroup : data.get(as)) {
			clear(targetGroup);
		}
		clear(as);
		return true;
	}

	private IStatus computeGroupScores(IProgressMonitor monitor, IComputeElement as) {
		final IDType aType = as.getIdType();
		// all metrics
		for (IComputedGroupScore metric : this.groupMetrics) {
			IGroupAlgorithm algorithm = metric.getAlgorithm();
			if (Thread.interrupted() || monitor.isCanceled())
				return Status.CANCEL_STATUS;
			IDType target = algorithm.getTargetType(as, as);
			for (Group ag : this.data.get(as)) {
				if (Thread.interrupted() || monitor.isCanceled())
					return Status.CANCEL_STATUS;
				if (metric.contains(as, ag) || !metric.getFilter().doCompute(as, ag, as, null))
					continue;
				Set<Integer> reference = get(as, target, target);
				Set<Integer> tocompute = get(as, ag, target, target);

				if (Thread.interrupted() || monitor.isCanceled())
					return Status.CANCEL_STATUS;
				float v = algorithm.compute(tocompute, reference, monitor);
				if (Thread.interrupted() || monitor.isCanceled())
					return Status.CANCEL_STATUS;
				metric.put(ag, v);
			}
		}

		// all scores
		for (IComputedReferenceGroupScore score : this.groupScores) {
			final IComputeElement rs = score.asComputeElement();
			final IDType sType = rs.getIdType();

			IGroupAlgorithm algorithm = score.getAlgorithm();

			IDType target = algorithm.getTargetType(as, rs);
			for (Group ag : this.data.get(as)) {
				if (Thread.interrupted() || monitor.isCanceled())
					return Status.CANCEL_STATUS;
				if (score.contains(as, ag) || !score.getFilter().doCompute(as, ag, rs, score.getGroup()))
					continue;
				Set<Integer> tocompute = get(as, ag, target, sType);
				Set<Integer> reference = get(rs, score.getGroup(), target, aType);

				if (Thread.interrupted() || monitor.isCanceled())
					return Status.CANCEL_STATUS;
				float v = algorithm.compute(tocompute, reference, monitor);
				if (Thread.interrupted() || monitor.isCanceled())
					return Status.CANCEL_STATUS;
				score.put(ag, v);
			}
		}
		return null;
	}
}