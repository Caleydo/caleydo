package org.caleydo.core.data.selection.delta;

import org.apache.log4j.Logger;
import org.caleydo.core.manager.event.AEventContainer;
import org.caleydo.core.manager.event.EEventType;

@SuppressWarnings("unchecked")
public class DeltaEventContainer<T extends IDelta>
	extends AEventContainer {

	Logger log = Logger.getLogger(DeltaEventContainer.class.getName());
	
	T delta;

	public DeltaEventContainer(T delta) {
		super(delta instanceof ISelectionDelta ? EEventType.SELECTION_UPDATE : EEventType.VA_UPDATE);
		if (delta instanceof ISelectionDelta) {
			log.warn("tried to use old selection update mechanism, " + Thread.getAllStackTraces().get(Thread.currentThread()));
		}
		this.delta = delta;
	}

	public T getSelectionDelta() {
		return delta;
	}
}
