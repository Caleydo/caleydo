package org.caleydo.core.manager.event.view.tablebased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event that switches between main and matrix-view in the Scatterplot.
 * 
 * @author J�rgen Pillhofer
 */
@XmlRootElement
@XmlType
public class SwitchMatrixViewEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
