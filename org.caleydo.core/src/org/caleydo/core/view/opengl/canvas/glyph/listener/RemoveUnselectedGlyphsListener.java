package org.caleydo.core.view.opengl.canvas.glyph.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.glyph.RemoveUnselectedGlyphsEvent;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;

public class RemoveUnselectedGlyphsListener
	extends AEventListener<GLGlyph> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof RemoveUnselectedGlyphsEvent) {
			RemoveUnselectedGlyphsEvent casted_event = (RemoveUnselectedGlyphsEvent) event;

			if (handler.getID() == casted_event.getViewID())
				handler.removeUnselected();
		}

	}

}