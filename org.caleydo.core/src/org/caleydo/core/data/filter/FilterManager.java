package org.caleydo.core.data.filter;

import java.util.ArrayList;

import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.selection.IVAType;
import org.caleydo.core.data.selection.VirtualArray;
import org.caleydo.core.data.selection.delta.VirtualArrayDelta;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.event.IListenerOwner;

public class FilterManager<VAType extends IVAType, DeltaType extends VirtualArrayDelta<?, VAType>, FilterType extends Filter<VAType, DeltaType>, VA extends VirtualArray<?, VAType, DeltaType, ?>>
	implements IListenerOwner {

	ArrayList<FilterType> filterPipe;
	VA baseVA;
	VA currentVA;
	Set set;

	// private ReplaceContentVAInUseCaseListener replaceContentVirtualArrayInUseCaseListener;
	// private ReplaceStorageVAInUseCaseListener replaceStorageVirtualArrayInUseCaseListener;

	EventPublisher eventPublisher = GeneralManager.get().getEventPublisher();

	public FilterManager(Set set) {
		filterPipe = new ArrayList<FilterType>();
		eventPublisher = GeneralManager.get().getEventPublisher();
	}

	public VA addFilter(FilterType filter) {
		filterPipe.add(filter);
		currentVA.setDelta(filter.getVADelta());
		return currentVA;
	}

	@Override
	public void registerEventListeners() {
		// TODO Auto-generated method stub
	}

	// TODO this is never called!
	@Override
	public void unregisterEventListeners() {

	}

	@Override
	public void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event) {
		// TODO Auto-generated method stub

	}
}
