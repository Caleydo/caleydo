/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.selection;

import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.events.ISelectionHandler;
import org.caleydo.core.data.selection.events.SelectionCommandListener;
import org.caleydo.core.data.selection.events.SelectionUpdateListener;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.event.data.SelectionCommandEvent;
import org.caleydo.core.event.data.SelectionUpdateEvent;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;

/**
 * Wrapper for {@link SelectionManager} that integrates all the event handling
 * related to selections.
 *
 * @author Alexander Lex
 *
 */
public class EventBasedSelectionManager extends SelectionManager implements
 ISelectionHandler {

	private EventListenerManager eventListeners;

	protected IEventBasedSelectionManagerUser parent;

	/**
	 * Creates a new <code> EventBasedSelectionManager</code> for the provided
	 * {@link IDType}.
	 *
	 * @param parent
	 *            the referencing class
	 * @param idType
	 *            the idType for this manager
	 */
	public EventBasedSelectionManager(IEventBasedSelectionManagerUser parent,
			IDType idType) {
		super(idType);
		this.parent = parent;
		this.idType = idType;
	}

	@Override
	public synchronized void queueEvent(
			AEventListener<? extends IListenerOwner> listener, AEvent event) {
		synchronized (this) {
			listener.handleEvent(event);
		}
	}

	/**
	 * !!!! already called during constructor
	 */
	@Override
	public synchronized void registerEventListeners() {
		if (eventListeners != null) // already called
			return;
		eventListeners = EventListenerManagers.wrap(this);
		SelectionUpdateListener selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		eventListeners.register(SelectionUpdateEvent.class, selectionUpdateListener);

		SelectionCommandListener selectionCommandListener = new SelectionCommandListener();
		selectionCommandListener.setHandler(this);
		// selectionCommandListener.setDataDomainID(dataDomain.getDataDomainID());
		eventListeners.register(SelectionCommandEvent.class, selectionCommandListener);

		super.registerEventListeners();


	}

	@Override
	public synchronized void unregisterEventListeners() {
		eventListeners.unregisterAll();
		super.unregisterEventListeners();
	}

	@Override
	public void handleSelectionUpdate(SelectionDelta selectionDelta) {
		if (selectionDelta.getIDType().getIDCategory().equals(idType.getIDCategory())) {
			setDelta(selectionDelta);
			parent.notifyOfSelectionChange(this);
		}
	}

	@Override
	public void handleSelectionCommand(IDCategory idCategory,
			SelectionCommand selectionCommand) {
		if (idCategory == null || idCategory.equals(idType.getIDCategory())) {
			super.executeSelectionCommand(selectionCommand);
			parent.notifyOfSelectionChange(this);
		}
	}

	public void triggerSelectionUpdateEvent() {
		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setSelectionDelta(getDelta());
		event.setSender(this);
		
		EventPublisher.INSTANCE.triggerEvent(event);
	}

}
