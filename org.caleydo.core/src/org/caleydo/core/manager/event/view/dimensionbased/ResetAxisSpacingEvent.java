package org.caleydo.core.manager.event.view.dimensionbased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

/**
 * Event that signals that the spacing between the axis should be reset.
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class ResetAxisSpacingEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
