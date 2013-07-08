/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.radial;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.events.ISelectionHandler;
import org.caleydo.core.data.selection.events.SelectionUpdateListener;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.event.data.SelectionUpdateEvent;
import org.caleydo.core.manager.GeneralManager;

/**
 * ADataEventManager is the abstract base class for all DataEventManagers, which
 * are responsible to handle and trigger events which are specific for the data
 * type of hierarchical data that is displayed in the radial hierarchy view.
 * 
 * @author Christian Partl
 */
public abstract class ADataEventManager implements ISelectionHandler {
	protected SelectionUpdateListener selectionUpdateListener;
	protected GLRadialHierarchy radialHierarchy;
	protected EventPublisher eventPublisher;

	/**
	 * Constructor.
	 * 
	 * @param radialHierarchy
	 *            Radial hierarchy view this DataEventManager shall be used for.
	 */
	public ADataEventManager(GLRadialHierarchy radialHierarchy) {
		this.radialHierarchy = radialHierarchy;
		eventPublisher = GeneralManager.get().getEventPublisher();
	}

	/**
	 * Register all event listeners used by the DataEventHandler.
	 */
	@Override
	public void registerEventListeners() {

		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		registerDataSpecificEventListeners();
	}

	/**
	 * Unregister all event listeners used by the DataEventHandler.
	 */
	@Override
	public void unregisterEventListeners() {

		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(selectionUpdateListener);
			selectionUpdateListener = null;
		}

		unregisterDataSpecificEventListeners();
	}

	@Override
	public void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event) {
		radialHierarchy.queueEvent(listener, event);
	}

	/**
	 * This method is called when a partial disc of the radial hierarchy view is
	 * selected. It is responsible for triggering all events specific for the
	 * data object the selected partial disc represents.
	 * 
	 * @param selectionType
	 *            Type of selection.
	 * @param pdSelected
	 *            Partial disc that has been selected.
	 */
	public abstract void triggerDataSelectionEvents(SelectionType selectionType,
			PartialDisc pdSelected);

	/**
	 * Registers all data type specific event listeners.
	 */
	protected abstract void registerDataSpecificEventListeners();

	/**
	 * Unregisters all data type specific event listeners.
	 */
	protected abstract void unregisterDataSpecificEventListeners();
}
