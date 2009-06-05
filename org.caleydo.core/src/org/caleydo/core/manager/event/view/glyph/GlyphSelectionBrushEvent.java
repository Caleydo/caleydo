package org.caleydo.core.manager.event.view.glyph;

import org.caleydo.core.manager.event.AEvent;

public class GlyphSelectionBrushEvent
	extends AEvent {

	private int brushSize = -1;
	int iViewID;

	public GlyphSelectionBrushEvent(int iViewID, int brushSize) {
		super();
		this.iViewID = iViewID;
		this.brushSize = brushSize;
	}
	
	public int getBrushSize() {
		return brushSize;
	}
	
	public int getViewID() {
		return iViewID;
	}

	@Override
	public boolean checkIntegrity() {
		if (brushSize == -1)
			return false;
		return true;
	}

}
