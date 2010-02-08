package org.caleydo.core.manager.event.view.storagebased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event that En/disables the matrix zoom event in the ScatterPlot.
 * 
 * @author Jürgen Pillhofer
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
