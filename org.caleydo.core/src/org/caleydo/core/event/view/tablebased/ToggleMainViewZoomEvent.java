package org.caleydo.core.event.view.tablebased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.event.AEvent;

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
