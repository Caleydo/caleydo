package org.caleydo.view.tourguide.data.compute;

import java.util.Collection;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.tourguide.data.score.IBatchComputedGroupScore;

import com.google.common.collect.Multimap;

public class ComputeBatchGroupScore implements Runnable {
	private final Collection<IBatchComputedGroupScore> scores;
	private final Multimap<TablePerspective, Group> data;

	public ComputeBatchGroupScore(Collection<IBatchComputedGroupScore> scores,
			Multimap<TablePerspective, Group> data) {
		this.scores = scores;
		this.data = data;
	}

	@Override
	public void run() {
		scores.iterator().next().apply(scores, data);
	}
}