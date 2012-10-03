/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.radial;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.events.ISelectionUpdateHandler;
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
public abstract class ADataEventManager implements ISelectionUpdateHandler {
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
