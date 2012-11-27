package org.caleydo.view.tourguide.data.compute;

import java.util.Collection;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.view.tourguide.data.score.IComputedStratificationScore;

public class ComputeStratificationScore implements Runnable {
	private final IComputedStratificationScore score;
	private final Collection<TablePerspective> data;

	public ComputeStratificationScore(IComputedStratificationScore score, Collection<TablePerspective> data) {
		this.score = score;
		this.data = data;
	}

	@Override
	public void run() {
		score.apply(data);
	}
}

