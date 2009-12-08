package org.caleydo.core.view.opengl.canvas.glyph.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.glyph.SetPositionModelEvent;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;

public class SetPositionModelListener
	extends AEventListener<GLGlyph> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof SetPositionModelEvent) {
			SetPositionModelEvent positionModelEvent = (SetPositionModelEvent) event;
			if (handler.getID() == positionModelEvent.getViewID())
				handler.setPositionModel(positionModelEvent.getPositionModel());
		}

	}

}
