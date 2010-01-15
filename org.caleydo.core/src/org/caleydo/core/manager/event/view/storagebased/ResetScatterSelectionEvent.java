package org.caleydo.core.manager.event.view.storagebased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event that Resets all Selections in the Scatterplot.
 * 
 * @author Jürgen Pillhofer
 */
@XmlRootElement
@XmlType
public class ResetScatterSelectionEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
