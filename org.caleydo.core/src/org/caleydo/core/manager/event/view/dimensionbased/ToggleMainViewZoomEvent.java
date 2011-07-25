package org.caleydo.core.manager.event.view.dimensionbased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event that en/disables the MainView zoom event in the ScatterPlot.
 * 
 * @author J�rgen Pillhofer
 */
@XmlRootElement
@XmlType
public class ToggleMainViewZoomEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
