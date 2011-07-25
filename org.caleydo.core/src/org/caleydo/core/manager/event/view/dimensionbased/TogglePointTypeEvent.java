package org.caleydo.core.manager.event.view.dimensionbased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event that Changes the Point-Type of a Scatterplot.
 * 
 * @author J�rgen Pillhofer
 */
@XmlRootElement
@XmlType
public class TogglePointTypeEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
