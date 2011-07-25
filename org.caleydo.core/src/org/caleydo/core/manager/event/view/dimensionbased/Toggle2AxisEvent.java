package org.caleydo.core.manager.event.view.dimensionbased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event that Changes enables/disables the 2-Axis_mode in ScatterPlot.
 * 
 * @author J�rgen Pillhofer
 */
@XmlRootElement
@XmlType
public class Toggle2AxisEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
