package org.caleydo.core.event.view.infoarea;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.event.AEvent;

/**
 * Event for updating the info area. The event holds the update-information as payload.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class InfoAreaUpdateEvent
	extends AEvent {

	/** additional information about the selection, e.g. to display in the info-box */
	private String info;

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Override
	public boolean checkIntegrity() {
		if (info == null)
			throw new NullPointerException("info is null");
		return true;
	}
}
