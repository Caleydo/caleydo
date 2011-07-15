package org.caleydo.core.manager.event.view;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.view.ViewManager;
import org.caleydo.core.view.opengl.canvas.AGLView;

/**
 * This event is triggered when a view is unregistered at the {@link ViewManager}.
 * 
 * @author Partl
 *
 */
public class ViewClosedEvent
	extends AEvent {

	private AGLView view;

	public ViewClosedEvent(AGLView view) {
		this.setView(view);
	}

	@Override
	public boolean checkIntegrity() {
		// TODO Auto-generated method stub
		return true;
	}

	public void setView(AGLView view) {
		this.view = view;
	}

	public AGLView getView() {
		return view;
	}

}
