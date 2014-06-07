/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.datadomain.UnRegisterListenersOnEvent;
import org.caleydo.core.data.filter.event.FilterUpdatedEvent;
import org.caleydo.core.data.filter.event.MoveFilterEvent;
import org.caleydo.core.data.filter.event.MoveFilterListener;
import org.caleydo.core.data.filter.event.NewFilterEvent;
import org.caleydo.core.data.filter.event.NewFilterListener;
import org.caleydo.core.data.filter.event.ReEvaluateFilterListEvent;
import org.caleydo.core.data.filter.event.ReEvaluateFilterListListener;
import org.caleydo.core.data.filter.event.RemoveFilterEvent;
import org.caleydo.core.data.filter.event.RemoveFilterListener;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;
import org.caleydo.core.data.virtualarray.events.VADeltaEvent;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.event.data.RemoveDataDomainEvent;
import org.caleydo.core.manager.GeneralManager;

/**
 * <p>
 * Managing class for {@link Filter}s. A Filter manages changes in virtual arrays, based on virtual array deltas. The
 * FilterManager handles the succession of Filters and thereby allows to remove previous filters, allowing undo
 * functionality.
 * </p>
 * <p>
 * The FilterManager is the base for a statically typed FilterManager sub-class, such as {@link RecordFilter} .
 * </p>
 *
 * @author Alexander Lex
 */
public class FilterManager implements IListenerOwner {

	// private final IFilterFactory<FilterType> factory;
	private ArrayList<Filter> filterPipe;
	protected Perspective perspective;
	protected IDataDomain dataDomain;

	EventPublisher eventPublisher = GeneralManager.get().getEventPublisher();

	private RemoveFilterListener removeDimensionFilterListener;
	private MoveFilterListener moveDimensionFilterListener;
	private NewFilterListener newDimensionFilterListener;
	private ReEvaluateFilterListListener reEvaluateDimensionFilterListListener;

	private UnRegisterListenersOnEvent unregisterListener = null;

	/**
	 * Pass the dataDomain, the initial VA, and a factory to create filters of the specified type.
	 *
	 * @param dataDomain
	 * @param virtualArray
	 * @param factory
	 */
	public FilterManager(IDataDomain dataDomain, Perspective perspective) {
		this.dataDomain = dataDomain;

		this.perspective = perspective;
		// this.factory = factory;
		filterPipe = new ArrayList<Filter>();
		eventPublisher = GeneralManager.get().getEventPublisher();
		registerEventListeners();
	}

	/**
	 * Adds a filter and triggers a {@link VADeltaEvent} with the {@link VirtualArrayDelta} in the filter.
	 *
	 * @param filter
	 * @return
	 */
	public void addFilter(Filter filter) {
		filterPipe.add(filter);
		runFilter(filter);

		triggerFilterUpdatedEvent();

	}

	private void runFilter(Filter filter) {

		if (!(filter instanceof MetaFilter)) {
			triggerVADeltaEvent(filter.getVADelta());
		} else {
			for (Filter subFilter : ((MetaFilter) filter).getFilterList()) {
				triggerVADeltaEvent(subFilter.getVADelta());
			}
		}
	}

	@Override
	public void registerEventListeners() {

		unregisterListener = new UnRegisterListenersOnEvent(this, dataDomain);
		eventPublisher.addListener(RemoveDataDomainEvent.class, unregisterListener);

		removeDimensionFilterListener = new RemoveFilterListener();
		removeDimensionFilterListener.setHandler(this);
		removeDimensionFilterListener.setExclusiveEventSpace(dataDomain.getDataDomainID());
		eventPublisher.addListener(RemoveFilterEvent.class, removeDimensionFilterListener);

		moveDimensionFilterListener = new MoveFilterListener();
		moveDimensionFilterListener.setHandler(this);
		moveDimensionFilterListener.setExclusiveEventSpace(dataDomain.getDataDomainID());
		eventPublisher.addListener(MoveFilterEvent.class, moveDimensionFilterListener);

		newDimensionFilterListener = new NewFilterListener();
		newDimensionFilterListener.setHandler(this);
		newDimensionFilterListener.setExclusiveEventSpace(dataDomain.getDataDomainID());
		eventPublisher.addListener(NewFilterEvent.class, newDimensionFilterListener);

		reEvaluateDimensionFilterListListener = new ReEvaluateFilterListListener();
		reEvaluateDimensionFilterListListener.setHandler(this);
		reEvaluateDimensionFilterListListener.setExclusiveEventSpace(dataDomain.getDataDomainID());
		eventPublisher.addListener(ReEvaluateFilterListEvent.class, reEvaluateDimensionFilterListListener);

	}

	@Override
	public void unregisterEventListeners() {
		if (unregisterListener != null)
			eventPublisher.removeListener(RemoveDataDomainEvent.class, unregisterListener);
		unregisterListener = null;
	}

	/**
	 * To also support legacy VA update events, this listener creates a filter for every incoming vaDelta event.
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
	public ArrayList<Filter> getFilterPipe() {
		return filterPipe;
	}

	public void handleRemoveFilter(Filter filter) {

		Iterator<Filter> filterIterator = filterPipe.iterator();
		while (filterIterator.hasNext()) {
			if (filterIterator.next() == filter) {

				filterIterator.remove();
			}
		}

		reEvaluateFilters();
		triggerFilterUpdatedEvent();
	}

	public void handleMoveFilter(Filter filter, int offset) {
		int index = filterPipe.indexOf(filter);

		if (index < 0)
			throw new RuntimeException("handleMoveFilter: filter not found.");

		// move filters before/after
		if (offset < 0) {
			for (int i = index; i > index + offset; --i)
				filterPipe.set(i, filterPipe.get(i - 1));
		} else {
			for (int i = index; i < index + offset; ++i)
				filterPipe.set(i, filterPipe.get(i + 1));
		}

		// place filter on new position
		filterPipe.set(index + offset, filter);

		reEvaluateFilters();
		triggerFilterUpdatedEvent();
	}

	public void handleCombineFilter(Filter filter, Collection<Filter> combineFilters) {
		int index = filterPipe.indexOf(filter);

		if (index < 0)
			throw new RuntimeException("handleCombineFilter: filter not found.");

		RecordMetaOrFilter metaFilter = null;

		if (filter instanceof RecordMetaOrFilter) {
			metaFilter = (RecordMetaOrFilter) filter;
		} else {
			metaFilter = new RecordMetaOrFilter(perspective.getPerspectiveID());
			metaFilter.setDataDomain(filter.getDataDomain());
			metaFilter.getFilterList().add(filter);
		}

		metaFilter.getFilterList().addAll(combineFilters);
		metaFilter.updateDelta(perspective.getPerspectiveID());

		filterPipe.set(index, metaFilter);
		filterPipe.removeAll(combineFilters);

		reEvaluateFilters();
		triggerFilterUpdatedEvent();
	}

	private void triggerFilterUpdatedEvent() {
		FilterUpdatedEvent event = new FilterUpdatedEvent();
		event.setEventSpace(dataDomain.getDataDomainID());
		eventPublisher.triggerEvent(event);
	}

	public void reEvaluateFilters() {

		for (Filter filter : filterPipe) {
			runFilter(filter);
		}
	}


	/**
	 * Triggers event signaling a virtual array update. Has to be implemented in sub-classes, because only there the
	 * type is known.
	 *
	 * @param delta
	 */

	protected void triggerVADeltaEvent(VirtualArrayDelta delta) {
		VADeltaEvent event = new VADeltaEvent();
		event.setSender(this);
		event.setEventSpace(dataDomain.getDataDomainID());
		event.setVirtualArrayDelta(delta);
		eventPublisher.triggerEvent(event);
	}

}
