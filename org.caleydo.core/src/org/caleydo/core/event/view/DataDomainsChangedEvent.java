package org.caleydo.core.event.view;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.view.opengl.canvas.AGLView;

/**
 * This event should be triggered when the data domain(s) of a view change(s).
 * 
 * @author Partl
 * @deprecated this is no longer supported - new datadomains open new views
 */
@Deprecated
public class DataDomainsChangedEvent
	extends AEvent {

	private AGLView view;

	public DataDomainsChangedEvent(AGLView view) {
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
