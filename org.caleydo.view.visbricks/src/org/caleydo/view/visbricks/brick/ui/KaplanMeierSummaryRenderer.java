package org.caleydo.view.visbricks.brick.ui;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;
import org.caleydo.view.visbricks.brick.ViewLabelProvider;

/**
 * Renderer for a line of text for kaplan meier summary.
 * 
 * @author Marc Streit
 * 
 */
public class KaplanMeierSummaryRenderer extends LabelRenderer {

	public KaplanMeierSummaryRenderer(AGLView view, String caption, String pickingType,
			int id) {
		super(view, new ViewLabelProvider(view), pickingType, id);
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
