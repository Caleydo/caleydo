package org.caleydo.vis.rank.ui.column;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.ui.detail.ColoredValueElement;

public class RankColumnUI extends ColumnUI {

	public RankColumnUI(ARankColumnModel model) {
		super(model);
	}

	@Override
	public void layout(int deltaTimeMs) {
		super.layout(deltaTimeMs);
		OrderColumnUI ranker = getColumnParent().getRanker(model);
		if (ranker.haveRankDeltas()) {
			for (GLElement elem : this) {
				IRow row = elem.getLayoutDataAs(IRow.class, null);
				if (row == null)
					continue;
				int delta = ranker.getRankDelta(row);
				if (delta == 0)
					continue;
				((ColoredValueElement) elem).setRankDelta(delta);
			}
		}
	}
}