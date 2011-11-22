package org.caleydo.core.event.view;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.view.opengl.canvas.AGLView;

/**
 * This event should be triggered when the {@link DataContainer}(s) of a view change(s).
 * 
 * @author Partl
 * @author Alexander Lex
 */
public class DataContainersChangedEvent
	extends AEvent {

	private AGLView view;

	public DataContainersChangedEvent(AGLView view) {
		this.setView(view);
	}

	@Override
	public boolean checkIntegrity() {
		if (view == null)
			return false;
		return true;
	}

	public void setView(AGLView view) {
		this.view = view;
	}

	public AGLView getView() {
		return view;
	}

}
