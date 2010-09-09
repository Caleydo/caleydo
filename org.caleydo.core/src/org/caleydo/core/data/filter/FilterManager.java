package org.caleydo.core.data.filter;

import java.util.ArrayList;

import org.caleydo.core.data.selection.IVAType;
import org.caleydo.core.data.selection.VirtualArray;
import org.caleydo.core.data.selection.delta.VirtualArrayDelta;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.view.storagebased.VirtualArrayUpdateEvent;

/**
 * <p>
 * Managing class for {@link Filter}s. A Filter manages changes in virtual arrays, based on virtual array
 * deltas. The FilterManager handles the succession of Filters and thereby allows to remove previous filters,
 * allowing undo functionality.
 * </p>
 * <p>
 * The FilterManager is the base for a statically typed FilterManager sub-class, such as {@link ContentFilter}
 * .
 * </p>
 * 
 * @author Alexander Lex
 * @param <VAType>
 * @param <DeltaType>
 * @param <FilterType>
 * @param <VA>
 */
public abstract class FilterManager<VAType extends IVAType, DeltaType extends VirtualArrayDelta<?, VAType>, FilterType extends Filter<VAType, DeltaType>, VA extends VirtualArray<?, VAType, DeltaType, ?>>
	implements IListenerOwner {

	private final IFilterFactory<FilterType> factory;
	ArrayList<FilterType> filterPipe;
	VA baseVA;
	VA currentVA;
	ASetBasedDataDomain dataDomain;

	// private ReplaceContentVAInUseCaseListener replaceContentVirtualArrayInUseCaseListener;
	// private ReplaceStorageVAInUseCaseListener replaceStorageVirtualArrayInUseCaseListener;

	EventPublisher eventPublisher = GeneralManager.get().getEventPublisher();

	/**
	 * Pass the dataDomain, the initial VA, and a factory to create filters of the specified type.
	 * 
	 * @param dataDomain
	 * @param baseVA
	 * @param factory
	 */
	@SuppressWarnings("unchecked")
	public FilterManager(ASetBasedDataDomain dataDomain, VA baseVA, IFilterFactory<FilterType> factory) {
		this.dataDomain = dataDomain;
		this.baseVA = baseVA;
		currentVA = (VA) baseVA.clone();
		this.factory = factory;
		filterPipe = new ArrayList<FilterType>();
		eventPublisher = GeneralManager.get().getEventPublisher();
		registerEventListeners();
	}

	/**
	 * Adds a filter and triggers a {@link VirtualArrayUpdateEvent} with the {@link VirtualArrayDelta} in the
	 * filter.
	 * 
	 * @param filter
	 * @return
	 */
	public VA addFilter(FilterType filter) {
		filterPipe.add(filter);
		currentVA.setDelta(filter.getVADelta());

		triggerVAUpdateEvent(filter.getVADelta());

		return currentVA;
	}

	/**
	 * Triggers event signalling a virtual array update. Has to be implemented in sub-classes, because only
	 * there the type is known.
	 * 
	 * @param delta
	 */
	protected abstract void triggerVAUpdateEvent(DeltaType delta);

	@Override
	public void registerEventListeners() {
	}

	// TODO this is never called!
	@Override
	public void unregisterEventListeners() {

	}

	/**
	 * To also support legacy VA update events, this listener creates a filter for every incoming vaDelta
	 * event.
	 * 
	 * @param vaDelta
	 * @param info
	 */
	public void handleVAUpdate(DeltaType vaDelta, String info) {
		FilterType filter = factory.create();
		filter.setDelta(vaDelta);
		filterPipe.add(filter);
		currentVA.setDelta(vaDelta);
	}

	@Override
	public void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event) {
		listener.handleEvent(event);
	}
}
