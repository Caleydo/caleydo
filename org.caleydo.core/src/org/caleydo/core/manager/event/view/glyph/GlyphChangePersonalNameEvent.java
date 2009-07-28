package org.caleydo.core.manager.event.view.glyph;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

@XmlRootElement
@XmlType
public class GlyphChangePersonalNameEvent
	extends AEvent {

	private String personalName = null;
	int iViewID;

	public GlyphChangePersonalNameEvent() {
		// nothing to initialize here
	}
	
	public GlyphChangePersonalNameEvent(int iViewID, String personalName) {
		super();
		this.iViewID = iViewID;
		this.personalName = personalName;
	}

	public String getPersonalName() {
		return personalName;
	}
	
	public int getViewID() {
		return iViewID;
	}

	@Override
	public boolean checkIntegrity() {
		if (personalName == null)
			return false;
		return true;
	}

	public int getIViewID() {
		return iViewID;
	}

	public void setIViewID(int viewID) {
		iViewID = viewID;
	}

	public void setPersonalName(String personalName) {
		this.personalName = personalName;
	}

}
