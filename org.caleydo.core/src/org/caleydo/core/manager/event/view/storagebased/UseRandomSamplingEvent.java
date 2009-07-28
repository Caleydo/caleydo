package org.caleydo.core.manager.event.view.storagebased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AFlagSetterEvent;

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
