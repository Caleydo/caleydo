package org.caleydo.core.manager.event.view.hyperbolic;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

@XmlRootElement
@XmlType
public class ChangeTreeTypeEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}