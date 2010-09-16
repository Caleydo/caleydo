package org.caleydo.core.data.filter;

import java.util.ArrayList;
import java.util.Iterator;

import org.caleydo.core.data.filter.event.FilterUpdatedEvent;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;
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
public abstract class FilterManager<DeltaType extends VirtualArrayDelta<?>, FilterType extends Filter<DeltaType>, VA extends VirtualArray<?, DeltaType, ?>>
	implements IListenerOwner {

	private final IFilterFactory<FilterType> factory;
	private ArrayList<FilterType> filterPipe;
	private VA baseVA;
	protected VA currentVA;
	protected ASetBasedDataDomain dataDomain;

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
	@SuppressWarnings("unchecked")
	public VA addFilter(FilterType filter) {
		filterPipe.add(filter);

		if (!(filter instanceof MetaFilter)) {
			currentVA.setDelta(filter.getVADelta());
			triggerVAUpdateEvent(filter.getVADelta());
		}
		else {

			for (FilterType subFilter : ((MetaFilter<FilterType>) filter).getFilterList()) {
				currentVA.setDelta(subFilter.getVADelta());
				triggerVAUpdateEvent(subFilter.getVADelta());
			}
		}

		triggerFilterUpdatedEvent();

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
		if (!(vaDelta.getVAType().equals(baseVA.getVaType())))
			return;
		FilterType filter = factory.create();
		filter.setDelta(vaDelta);
		filterPipe.add(filter);
		currentVA.setDelta(vaDelta);

		FilterUpdatedEvent event = new FilterUpdatedEvent();
		event.setDataDomainType(dataDomain.getDataDomainType());
		eventPublisher.triggerEvent(event);
	}

	@Override
	public void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event) {
		listener.handleEvent(event);
	}

	/**
	 * Returns all current filters.
	 * 
	 * @return a list of all filters
	 */
	public ArrayList<FilterType> getFilterPipe() {
		return filterPipe;
	}

	public void handleRemoveFilter(Filter<?> filter) {

		Iterator<FilterType> filterIterator = filterPipe.iterator();
		while (filterIterator.hasNext()) {
			if (filterIterator.next() == filter) {

				filterIterator.remove();
			}
		}

		reEvaluateFilters();
		triggerFilterUpdatedEvent();

	}

	private void triggerFilterUpdatedEvent() {
		FilterUpdatedEvent event = new FilterUpdatedEvent();
		event.setDataDomainType(dataDomain.getDataDomainType());
		eventPublisher.triggerEvent(event);
	}

	@SuppressWarnings("unchecked")
	public void reEvaluateFilters() {
		currentVA = (VA) baseVA.clone();
		for (FilterType filter : filterPipe) {
			if (filter instanceof MetaFilter) {
				for (Filter<DeltaType> subFilter : ((MetaFilter<FilterType>) filter).getFilterList()) {
					currentVA.setDelta(subFilter.getVADelta());
				}
			}
			else
				currentVA.setDelta(filter.getVADelta());
		}
		triggerReplaceVAEvent();
	}

	protected abstract void triggerReplaceVAEvent();

	@SuppressWarnings("unchecked")
	public VA getBaseVA() {
		return (VA) baseVA.clone();
	}
}
