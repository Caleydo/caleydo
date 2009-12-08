package org.caleydo.core.manager.event.view.glyph;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

@XmlRootElement
@XmlType
public class RemoveUnselectedGlyphsEvent
	extends AEvent {

	int viewID;

	public RemoveUnselectedGlyphsEvent() {
		// nothing to initialize here
	}

	public RemoveUnselectedGlyphsEvent(int iViewID) {
		super();
		this.viewID = iViewID;
	}

	public int getViewID() {
		return viewID;
	}

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}

	public void setViewID(int viewID) {
		this.viewID = viewID;
	}

}
