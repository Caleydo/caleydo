package org.caleydo.core.view.opengl.canvas.glyph.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.view.glyph.event.GlyphUpdatePositionModelEvent;
import org.caleydo.view.glyph.gridview.GLGlyph;

public class GlyphUpdatePositionModelListener
	extends AEventListener<GLGlyph> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof GlyphUpdatePositionModelEvent) {
			GlyphUpdatePositionModelEvent glyphUpdatePositionModelEvent =
				(GlyphUpdatePositionModelEvent) event;
			if (handler.getID() == glyphUpdatePositionModelEvent.getViewID())
				handler.setPositionModelAxis(glyphUpdatePositionModelEvent.getPositionModel(),
					glyphUpdatePositionModelEvent.getAxis(), glyphUpdatePositionModelEvent.getValue());

		}

	}

}
