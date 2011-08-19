package org.caleydo.core.data.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.caleydo.core.data.collection.table.DataPerspective;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.filter.event.FilterUpdatedEvent;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;
import org.caleydo.core.data.virtualarray.events.VADeltaEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.event.IListenerOwner;

/**
 * <p>
 * Managing class for {@link Filter}s. A Filter manages changes in virtual arrays, based on virtual array
 * deltas. The FilterManager handles the succession of Filters and thereby allows to remove previous filters,
 * allowing undo functionality.
 * </p>
 * <p>
 * The FilterManager is the base for a statically typed FilterManager sub-class, such as {@link RecordFilter}
 * .
 * </p>
 * 
 * @author Alexander Lex
 * @param <VAType>
 * @param <DeltaType>
 * @param <FilterType>
 * @param <VA>
 */
public abstract class FilterManager<PerspectiveType extends DataPerspective<?, ?, ?, ?>, DeltaType extends VirtualArrayDelta<?>, FilterType extends Filter<DeltaType>, VA extends VirtualArray<?, DeltaType, ?>>
	implements IListenerOwner {

	// private final IFilterFactory<FilterType> factory;
	private ArrayList<FilterType> filterPipe;
	protected PerspectiveType perspective;
	protected ATableBasedDataDomain dataDomain;

	EventPublisher eventPublisher = GeneralManager.get().getEventPublisher();

	/**
	 * Pass the dataDomain, the initial VA, and a factory to create filters of the specified type.
	 * 
	 * @param dataDomain
	 * @param virtualArray
	 * @param factory
	 */
	public FilterManager(ATableBasedDataDomain dataDomain, PerspectiveType perspective) {
		this.dataDomain = dataDomain;

		this.perspective = perspective;
		// this.factory = factory;
		filterPipe = new ArrayList<FilterType>();
		eventPublisher = GeneralManager.get().getEventPublisher();
		registerEventListeners();
	}

	/**
	 * Adds a filter and triggers a {@link VADeltaEvent} with the {@link VirtualArrayDelta} in the filter.
	 * 
	 * @param filter
	 * @return
	 */
	public void addFilter(FilterType filter) {
		filterPipe.add(filter);
		runFilter(filter);

		triggerFilterUpdatedEvent();

	}

	@SuppressWarnings("unchecked")
	private void runFilter(FilterType filter) {

		if (!(filter instanceof MetaFilter)) {
			triggerVADeltaEvent(filter.getVADelta());
		}
		else {
			for (FilterType subFilter : ((MetaFilter<FilterType>) filter).getFilterList()) {
				triggerVADeltaEvent(subFilter.getVADelta());
			}
		}
	}

	/**
	 * To also support legacy VA update events, this listener creates a filter for every incoming vaDelta
	 * event.
	 * 
	 * @param vaDelta
	 * @param info
	 */
	// public void handleVAUpdate(DeltaType vaDelta, String info) {
	// if (!(vaDelta.getVAType().equals(virtualArray.getVaType())))
	// return;
	// FilterType filter = factory.create();
	// filter.setVADelta(vaDelta);
	// filterPipe.add(filter);
	// virtualArray.setDelta(vaDelta);
	//
	// FilterUpdatedEvent event = new FilterUpdatedEvent();
	// event.setDataDomainID(dataDomain.getDataDomainID());
	// eventPublisher.triggerEvent(event);
	// }

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

	public void handleMoveFilter(Filter<?> filter, int offset) {
		int index = filterPipe.indexOf(filter);

		if (index < 0)
			throw new RuntimeException("handleMoveFilter: filter not found.");

		// move filters before/after
		if (offset < 0) {
			for (int i = index; i > index + offset; --i)
				filterPipe.set(i, filterPipe.get(i - 1));
		}
		else {
			for (int i = index; i < index + offset; ++i)
				filterPipe.set(i, filterPipe.get(i + 1));
		}

		// place filter on new position
		filterPipe.set(index + offset, (FilterType) filter);

		reEvaluateFilters();
		triggerFilterUpdatedEvent();
	}

	public void handleCombineFilter(Filter<?> filter, Collection<? extends RecordFilter> combineFilters) {
		int index = filterPipe.indexOf(filter);

		if (index < 0)
			throw new RuntimeException("handleCombineFilter: filter not found.");

		RecordMetaOrFilter metaFilter = null;

		if (filter instanceof RecordMetaOrFilter) {
			metaFilter = (RecordMetaOrFilter) filter;
		}
		else {
			metaFilter = new RecordMetaOrFilter();
			metaFilter.setDataDomain(filter.getDataDomain());
			metaFilter.getFilterList().add((RecordFilter) filter);
		}

		metaFilter.getFilterList().addAll(combineFilters);
		metaFilter.updateDelta(perspective.getPerspectiveID());

		filterPipe.set(index, (FilterType) metaFilter);
		filterPipe.removeAll(combineFilters);

		reEvaluateFilters();
		triggerFilterUpdatedEvent();
	}

	private void triggerFilterUpdatedEvent() {
		FilterUpdatedEvent event = new FilterUpdatedEvent();
		event.setDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.triggerEvent(event);
	}

	public void reEvaluateFilters() {

		for (FilterType filter : filterPipe) {
			runFilter(filter);
		}
	}

	protected abstract void resetVA();

	/**
	 * Triggers event signaling a virtual array update. Has to be implemented in sub-classes, because only
	 * there the type is known.
	 * 
	 * @param delta
	 */
	protected abstract void triggerVADeltaEvent(DeltaType delta);

	protected abstract void triggerReplaceVAEvent();

}
