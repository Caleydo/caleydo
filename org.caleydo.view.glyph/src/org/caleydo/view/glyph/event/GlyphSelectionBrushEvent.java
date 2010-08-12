package org.caleydo.view.glyph.event;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

@XmlRootElement
@XmlType
public class GlyphSelectionBrushEvent
	extends AEvent {

	private int brushSize = -1;
	int iViewID;

	public GlyphSelectionBrushEvent() {
		// nothing to initialize here
	}

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

	public int getIViewID() {
		return iViewID;
	}

	public void setIViewID(int viewID) {
		iViewID = viewID;
	}

	public void setBrushSize(int brushSize) {
		this.brushSize = brushSize;
	}

}
