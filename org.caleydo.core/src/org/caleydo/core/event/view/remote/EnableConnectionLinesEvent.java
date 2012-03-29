package org.caleydo.core.event.view.remote;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.event.AEvent;

/**
 * Event to signal that a connection lines should be enabled
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class EnableConnectionLinesEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}
}
