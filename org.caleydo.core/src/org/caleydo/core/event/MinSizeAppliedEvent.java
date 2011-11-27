package org.caleydo.core.event;

import org.caleydo.core.view.MinimumSizeComposite;
import org.caleydo.core.view.opengl.canvas.AGLView;

/**
 * Event that is triggered when a minimum size was successfully applied to a {@link MinimumSizeComposite}.
 * 
 * @author Christian
 */
public class MinSizeAppliedEvent
	extends AEvent {

	private AGLView view;

	@Override
	public boolean checkIntegrity() {
		return view != null;
	}

	public AGLView getView() {
		return view;
	}

	public void setView(AGLView view) {
		this.view = view;
	}

}
