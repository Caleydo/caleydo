package org.caleydo.view.tourguide.data.compute;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.view.tourguide.algorithm.IStratificationAlgorithm;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.google.common.base.Stopwatch;

public class ComputeStratificationJob extends AScoreJob {
	private static final Logger log = Logger.create(ComputeStratificationJob.class);

	private final Collection<TablePerspective> data;
	private final Collection<IComputedReferenceStratificationScore> scores;

	public ComputeStratificationJob(Collection<TablePerspective> data,
			Collection<IComputedReferenceStratificationScore> scores) {
		super("Compute Tour Guide Scores");
		this.data = data;
		this.scores = scores;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		if (data.isEmpty() || scores.isEmpty())
			return Status.OK_STATUS;

		monitor.beginTask("Compute Tour Guide Scores", data.size() * scores.size());
		log.info("computing stratification similarity of " + data.size() + " against " + scores.size() + " others");
		Stopwatch w = new Stopwatch().start();
		int c = 0;
		for (TablePerspective a : this.data) {
			if (Thread.interrupted() || monitor.isCanceled())
				return Status.CANCEL_STATUS;

			// all scores
			for (IComputedReferenceStratificationScore score : this.scores) {
				IStratificationAlgorithm algorithm = score.getAlgorithm();
				final TablePerspective rs = score.getStratification();
				IDType target = algorithm.getTargetType(a, rs);
				if (score.contains(a) || !score.getFilter().doCompute(a, null, rs, null)) {
					monitor.worked(c++);
					continue;
				}
				List<Set<Integer>> compute = getAll(a, target, target);
				List<Set<Integer>> reference = getAll(rs, target, target);

				if (Thread.interrupted() || monitor.isCanceled())
					return Status.CANCEL_STATUS;

				float v = algorithm.compute(compute, reference);
				score.put(a, v);
				monitor.worked(c++);
			}

			// cleanup cache
			for (Group g : a.getRecordPerspective().getVirtualArray().getGroupList()) {
				clear(g);
			}
			clear(a);
		}
		System.out.println("done in " + w);
		monitor.done();
		return Status.OK_STATUS;
	}
}