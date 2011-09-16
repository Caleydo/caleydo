package org.caleydo.core.event.view.remote;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.event.AEvent;

/**
 * Event to signal that the navigation mode should be toggled.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class ToggleNavigationModeEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}

}
