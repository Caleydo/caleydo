package org.caleydo.core.manager.event.view.tablebased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event that En/disables the matrix zoom event in the ScatterPlot.
 * 
 * @author J�rgen Pillhofer
 */
@XmlRootElement
@XmlType
public class ToggleMatrixZoomEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
