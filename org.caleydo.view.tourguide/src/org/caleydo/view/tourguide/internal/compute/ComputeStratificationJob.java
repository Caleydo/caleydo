package org.caleydo.view.tourguide.internal.compute;

import java.util.Collection;
import java.util.Iterator;

import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;
import org.caleydo.view.tourguide.spi.compute.IComputedReferenceStratificationScore;
import org.caleydo.view.tourguide.spi.compute.IComputedStratificationScore;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.google.common.base.Stopwatch;

public class ComputeStratificationJob extends AScoreJob {
	private static final Logger log = Logger.create(ComputeStratificationJob.class);

	private final Collection<IComputeElement> data;

	private final Collection<IComputedStratificationScore> stratMetrics;
	private final Collection<IComputedReferenceStratificationScore> stratScores;

	public ComputeStratificationJob(Collection<IComputeElement> data, Collection<IComputedStratificationScore> scores,
			Object receiver) {
		super(receiver);
		this.data = data;
		Pair<Collection<IComputedStratificationScore>, Collection<IComputedReferenceStratificationScore>> strats = partition(
				scores, IComputedReferenceStratificationScore.class);
		this.stratMetrics = strats.getFirst();
		this.stratScores = strats.getSecond();
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		if (data.isEmpty() || (stratScores.isEmpty() && stratMetrics.isEmpty()))
			return Status.OK_STATUS;

		final int total = data.size();
		monitor.beginTask("Compute Tour Guide Scores", data.size());
		log.info(
				"computing similarity of %d against %d stratification scores, %d stratification metrics",
				data.size(), stratScores.size(), stratMetrics.size());
		Stopwatch w = new Stopwatch().start();

		Iterator<IComputeElement> it = this.data.iterator();
		int c = 0;
		// first time the one run to compute the progress frequency interval
		{
			IComputeElement as = it.next();
			if (!run(monitor, as))
				return Status.CANCEL_STATUS;
			monitor.worked(c++);
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

			monitor.worked(c++);
		}
		System.out.println("done in " + w);
		monitor.done();
		return Status.OK_STATUS;
	}

	private boolean run(IProgressMonitor monitor, IComputeElement a) {
		if (Thread.interrupted() || monitor.isCanceled())
			return false;

		if (computeStratificationScores(monitor, a, stratMetrics, stratScores) != null)
			return false;

		// cleanup cache
		for (Group g : a.getGroups()) {
			clear(g);
		}
		clear(a);
		return true;
	}
}