package org.caleydo.vis.rank.ui.column;

import gleem.linalg.Vec4f;

import org.caleydo.core.view.opengl.layout2.animation.ALayoutAnimation;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.vis.rank.internal.ui.anim.ColorTransition;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.ui.RenderStyle;

public class RankColumnUI extends ColumnUI {

	public RankColumnUI(ARankColumnModel model) {
		super(model);
	}


	@Override
	protected ALayoutAnimation createMoveAnimation(IGLLayoutElement elem, Vec4f before, Vec4f after) {
		if (areValidBounds(after)) {
			OrderColumnUI ranker = getColumnParent().getRanker(model);
			IRow row = elem.getLayoutDataAs(IRow.class, null);
			int delta = ranker.getRankDelta(row);
			if (delta != 0 && delta != Integer.MAX_VALUE) {
				removeAnimationsOf(elem.asElement(), false);
				animate(elem.asElement(), RenderStyle.hightlightAnimationDuration(delta), ColorTransition.get(delta));
			}
		}
		return super.createMoveAnimation(elem, before, after);
	}
}