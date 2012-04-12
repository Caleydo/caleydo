package org.caleydo.view.visbricks.brick.ui;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;
import org.caleydo.view.visbricks.brick.ViewLabelProvider;

/**
 * Renderer for a line of text for pathway description.
 * 
 * @author Partl
 * 
 */
public class PathwaysSummaryRenderer extends LabelRenderer {

	public PathwaysSummaryRenderer(AGLView view, String caption, String pickingType,
			int id) {
		super(view, new ViewLabelProvider(view), pickingType, id);
	}

	@Override
	public int getMinHeightPixels() {
		return 20;
	}

	@Override
	public int getMinWidthPixels() {
		return 140;
	}

}
