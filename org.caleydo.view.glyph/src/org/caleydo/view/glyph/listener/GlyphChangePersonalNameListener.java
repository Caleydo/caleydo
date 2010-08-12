package org.caleydo.core.view.opengl.canvas.glyph.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.view.glyph.event.GlyphChangePersonalNameEvent;
import org.caleydo.view.glyph.gridview.GLGlyph;

public class GlyphChangePersonalNameListener
	extends AEventListener<GLGlyph> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof GlyphChangePersonalNameEvent) {
			GlyphChangePersonalNameEvent glyphChangePersonalNameEvent = (GlyphChangePersonalNameEvent) event;

			if (handler.getID() == glyphChangePersonalNameEvent.getViewID())
				handler.setPersonalName(glyphChangePersonalNameEvent.getPersonalName());
		}
	}
}
