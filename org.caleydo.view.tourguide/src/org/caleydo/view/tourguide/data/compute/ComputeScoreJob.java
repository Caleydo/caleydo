package org.caleydo.view.tourguide.data.compute;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.view.tourguide.data.score.IComputedGroupScore;
import org.caleydo.view.tourguide.data.score.IComputedReferenceGroupScore;
import org.caleydo.view.tourguide.data.score.IComputedReferenceStratificationScore;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class ComputeScoreJob extends AScoreJob {
	private static final Logger log = Logger.create(ComputeScoreJob.class);

	private final Multimap<TablePerspective, Group> data;
	private final Collection<IComputedReferenceStratificationScore> stratScores;
	private final Collection<IComputedGroupScore> groupMetrics;
	private final Collection<IComputedReferenceGroupScore> groupScores;

	public ComputeScoreJob(Multimap<TablePerspective, Group> data,
			Collection<IComputedReferenceStratificationScore> stratScores, Collection<IComputedGroupScore> groupScores) {
		super("Compute Tour Guide Scores");
		this.stratScores = stratScores;
		this.groupScores = Lists.newArrayList(Iterables.transform(
				Iterables.filter(groupScores, Predicates.instanceOf(IComputedReferenceGroupScore.class)),
				new Function<IComputedGroupScore, IComputedReferenceGroupScore>() {
					@Override
					public IComputedReferenceGroupScore apply(IComputedGroupScore a) {
						return (IComputedReferenceGroupScore) a;
					}
				}));
		this.groupMetrics = Collections2.filter(groupScores,
				Predicates.not(Predicates.instanceOf(IComputedReferenceGroupScore.class)));
		this.data = data;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		if (data.isEmpty() || (groupMetrics.isEmpty() && groupScores.isEmpty() && stratScores.isEmpty()))
			return Status.OK_STATUS;

		monitor.beginTask("Compute Tour Guide Scores", data.keySet().size());
		log.info(
				"computing group similarity of %d against %d group scores, %d group metrics and %d stratification scores",
				data.size(), groupScores.size(), groupMetrics.size(), stratScores.size());
		Stopwatch w = new Stopwatch().start();

		int c = 0;
		for (TablePerspective as : this.data.keySet()) {
			final IDType aType = as.getRecordPerspective().getIdType();
			if (Thread.interrupted() || monitor.isCanceled())
				return Status.OK_STATUS;

			// all stratification scores
			for (IComputedReferenceStratificationScore score : this.stratScores) {
				IDType target = score.getTargetType(as);
				if (score.contains(as)) {
					continue;
				}
				List<Set<Integer>> compute = getAll(as, target, target);
				List<Set<Integer>> reference = getAll(score.getStratification(), target, target);

				if (Thread.interrupted() || monitor.isCanceled())
					return Status.CANCEL_STATUS;

				float v = score.compute(compute, reference);
				score.put(as, v);
			}

			// all metrics
			for (IComputedGroupScore metric : this.groupMetrics) {
				IDType target = metric.getTargetType(as);
				for (Group ag : this.data.get(as)) {
					if (Thread.interrupted() || monitor.isCanceled())
						return Status.CANCEL_STATUS;
					if (metric.contains(as, ag))
						continue;
					Set<Integer> reference = get(as, target, target);
					Set<Integer> tocompute = get(as, ag, target, target);
					float v = metric.compute(tocompute, reference);
					metric.put(ag, v);
				}
			}

			// all scores
			for (IComputedReferenceGroupScore score : this.groupScores) {
				final IDType sType = score.getStratification().getRecordPerspective().getIdType();

				IDType target = score.getTargetType(as);
				for (Group ag : this.data.get(as)) {
					if (Thread.interrupted() || monitor.isCanceled())
						return Status.CANCEL_STATUS;
					if (score.contains(as, ag))
						continue;
					Set<Integer> tocompute = get(as, ag, target, sType);
					Set<Integer> reference = get(score.getStratification(), score.getGroup(), target, aType);
					float v = score.compute(tocompute, reference);
					score.put(ag, v);
				}
			}
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
}