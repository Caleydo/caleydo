package org.caleydo.view.visbricks.brick.ui;

import org.caleydo.core.manager.picking.PickingType;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;

public class PathwaysSummaryRenderer extends LabelRenderer {

	public PathwaysSummaryRenderer(AGLView view, String caption,
			PickingType pickingType, int id) {
		super(view, caption, pickingType, id);
	}
	
	@Override
	public int getMinHeightPixels() {
		return 20;
	}

	@Override
	public int getMinWidthPixels() {
		return 110;
	}

}
