package org.caleydo.core.manager.event.view.storagebased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AFlagSetterEvent;

@XmlRootElement
@XmlType
public class ChangeOrientationParallelCoordinatesEvent
	extends AFlagSetterEvent {

	public ChangeOrientationParallelCoordinatesEvent() {
		// nothing to initialize
	}

	public ChangeOrientationParallelCoordinatesEvent(boolean flag) {
		super(flag);
	}

}
