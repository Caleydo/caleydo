package org.caleydo.core.manager.event.view.storagebased;

import org.caleydo.core.manager.event.AFlagSetterEvent;

public class ChangeOrientationParallelCoordinatesEvent
	extends AFlagSetterEvent {

	public ChangeOrientationParallelCoordinatesEvent(boolean flag) {
		super(flag);
	}

}
