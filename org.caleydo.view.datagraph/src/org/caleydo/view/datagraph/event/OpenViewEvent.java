package org.caleydo.view.datagraph.event;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.view.opengl.canvas.AGLView;

public class OpenViewEvent extends AEvent {

	private AGLView view;

	public OpenViewEvent(AGLView view) {
		this.view = view;
	}

	@Override
	public boolean checkIntegrity() {
		return (view != null);
	}

	public void setView(AGLView view) {
		this.view = view;
	}

	public AGLView getView() {
		return view;
	}

}
