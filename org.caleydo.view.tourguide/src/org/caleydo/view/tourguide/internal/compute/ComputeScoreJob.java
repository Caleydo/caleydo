package org.caleydo.view.tourguide.internal.compute;

import java.util.Collection;
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
import com.google.common.collect.Multimap;

public class ComputeScoreJob extends AScoreJob {
	private static final Logger log = Logger.create(ComputeScoreJob.class);

	private final Multimap<IComputeElement, Group> data;

	private final Collection<IComputedStratificationScore> stratMetrics;
	private final Collection<IComputedReferenceStratificationScore> stratScores;

	private final Collection<IComputedGroupScore> groupMetrics;
	private final Collection<IComputedReferenceGroupScore> groupScores;


	public ComputeScoreJob(Multimap<IComputeElement, Group> data,
			Collection<IComputedStratificationScore> stratScores, Collection<IComputedGroupScore> groupScores) {
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

		monitor.beginTask("Compute Tour Guide Scores", data.keySet().size());
		log.info(
				"computing group similarity of %d against %d group scores, %d group metrics, %d stratification scores and %d stratification metrics",
				data.size(), groupScores.size(), groupMetrics.size(), stratScores.size(), stratMetrics.size());
		Stopwatch w = new Stopwatch().start();

		int c = 0;
		for (IComputeElement as : this.data.keySet()) {
			if (Thread.interrupted() || monitor.isCanceled())
				return Status.OK_STATUS;

			if (computeStratificationScores(monitor, as, stratMetrics, stratScores) != null)
				return Status.CANCEL_STATUS;

			if (computeGroupScores(monitor, as) != null)
				return Status.CANCEL_STATUS;

			// cleanup cache
			for (Group targetGroup : data.get(as)) {
				clear(targetGroup);
			}
			clear(as);
			monitor.worked(c++);
		}
		System.out.println("done in " + w);
		monitor.done();
		return Status.OK_STATUS;
	}

	private IStatus computeGroupScores(IProgressMonitor monitor, IComputeElement as) {
		final IDType aType = as.getIdType();
		// all metrics
		for (IComputedGroupScore metric : this.groupMetrics) {
			IGroupAlgorithm algorithm = metric.getAlgorithm();
			IDType target = algorithm.getTargetType(as, as);
			for (Group ag : this.data.get(as)) {
				if (Thread.interrupted() || monitor.isCanceled())
					return Status.CANCEL_STATUS;
				if (metric.contains(as, ag) || !metric.getFilter().doCompute(as, ag, as, null))
					continue;
				Set<Integer> reference = get(as, target, target);
				Set<Integer> tocompute = get(as, ag, target, target);
				float v = algorithm.compute(tocompute, reference);
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
				float v = algorithm.compute(tocompute, reference);
				score.put(ag, v);
			}
		}
		return null;
	}
}