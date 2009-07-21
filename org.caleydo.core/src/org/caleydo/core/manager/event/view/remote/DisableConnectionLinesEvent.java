package org.caleydo.core.manager.event.view.remote;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event to signal that a connection lines should be disabled 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class DisableConnectionLinesEvent
	extends AEvent {
	
	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}
}
