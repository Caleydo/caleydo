package org.caleydo.core.view.opengl.canvas.glyph.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.glyph.GlyphSelectionBrushEvent;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;

public class GlyphSelectionBrushListener
	extends AEventListener<GLGlyph> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof GlyphSelectionBrushEvent) {
			GlyphSelectionBrushEvent glyphSelectionBrushEvent = (GlyphSelectionBrushEvent) event;

			if (handler.getID() == glyphSelectionBrushEvent.getViewID())
				handler.setSelectionBrush(glyphSelectionBrushEvent.getBrushSize());
		}
	}

}
