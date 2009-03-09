package org.caleydo.core.data.selection;

import org.caleydo.core.manager.event.AEventContainer;
import org.caleydo.core.manager.event.EEventType;

@SuppressWarnings("unchecked")
public class DeltaEventContainer<T extends IDelta>
	extends AEventContainer {
	T delta;

	public DeltaEventContainer(T delta) {
		super(delta instanceof ISelectionDelta ? EEventType.SELECTION_UPDATE : EEventType.VA_UPDATE);
		this.delta = delta;
	}

	public T getSelectionDelta() {
		return delta;
	}
}
