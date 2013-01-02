package org.caleydo.view.tourguide.internal.compute;

import java.util.Collection;

import org.caleydo.core.data.perspective.variable.ARecordPerspective;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.view.tourguide.spi.compute.IComputedReferenceStratificationScore;
import org.caleydo.view.tourguide.spi.compute.IComputedStratificationScore;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.google.common.base.Stopwatch;

public class ComputeStratificationJob extends AScoreJob {
	private static final Logger log = Logger.create(ComputeStratificationJob.class);

	private final Collection<ARecordPerspective> data;

	private final Collection<IComputedStratificationScore> stratMetrics;
	private final Collection<IComputedReferenceStratificationScore> stratScores;

	public ComputeStratificationJob(Collection<ARecordPerspective> data, Collection<IComputedStratificationScore> scores) {
		super("Compute Tour Guide Scores");
		this.data = data;
		Pair<Collection<IComputedStratificationScore>, Collection<IComputedReferenceStratificationScore>> strats = partition(
				scores, IComputedReferenceStratificationScore.class);
		this.stratMetrics = strats.getFirst();
		this.stratScores = strats.getSecond();
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		if (data.isEmpty() || (stratScores.isEmpty() && stratMetrics.isEmpty()))
			return Status.OK_STATUS;

		monitor.beginTask("Compute Tour Guide Scores", data.size());
		log.info(
				"computing similarity of %d against %d stratification scores, %d stratification metrics",
				data.size(), stratScores.size(), stratMetrics.size());
		Stopwatch w = new Stopwatch().start();

		for (ARecordPerspective a : this.data) {
			if (Thread.interrupted() || monitor.isCanceled())
				return Status.CANCEL_STATUS;

			if (computeStratificationScores(monitor, a, stratMetrics, stratScores) != null)
				return Status.CANCEL_STATUS;

			// cleanup cache
			for (Group g : a.getVirtualArray().getGroupList()) {
				clear(g);
			}
			clear(a);
		}
		System.out.println("done in " + w);
		monitor.done();
		return Status.OK_STATUS;
	}
}