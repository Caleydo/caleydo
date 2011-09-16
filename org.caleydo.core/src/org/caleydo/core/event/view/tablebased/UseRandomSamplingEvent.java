package org.caleydo.core.event.view.tablebased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.event.AFlagSetterEvent;

@XmlRootElement
@XmlType
public class UseRandomSamplingEvent
	extends AFlagSetterEvent {

	public UseRandomSamplingEvent() {
		// nothing to initialize here
	}

	public UseRandomSamplingEvent(boolean flag) {
		super(flag);
	}
}
