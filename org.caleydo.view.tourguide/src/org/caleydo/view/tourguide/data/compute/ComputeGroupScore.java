package org.caleydo.view.tourguide.data.compute;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.tourguide.data.score.IComputedGroupScore;

import com.google.common.collect.Multimap;

public class ComputeGroupScore implements Runnable {
	private final IComputedGroupScore score;
	private final Multimap<TablePerspective, Group> data;

	public ComputeGroupScore(IComputedGroupScore score, Multimap<TablePerspective, Group> data) {
		this.score = score;
		this.data = data;
	}

	@Override
	public void run() {
		score.apply(data);
	}
}

