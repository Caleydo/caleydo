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
package org.caleydo.core.data.selection;

import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.events.ClearSelectionsListener;
import org.caleydo.core.data.selection.events.ISelectionCommandHandler;
import org.caleydo.core.data.selection.events.ISelectionUpdateHandler;
import org.caleydo.core.data.selection.events.SelectionCommandListener;
import org.caleydo.core.data.selection.events.SelectionUpdateListener;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
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
		ISelectionUpdateHandler, ISelectionCommandHandler {

	protected SelectionUpdateListener selectionUpdateListener;
	protected SelectionCommandListener selectionCommandListener;
	protected ClearSelectionsListener clearSelectionsListener;

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

	@Override
	public void registerEventListeners() {
		EventPublisher eventPublisher = GeneralManager.get().getEventPublisher();
		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		selectionCommandListener = new SelectionCommandListener();
		selectionCommandListener.setHandler(this);
		// selectionCommandListener.setDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(SelectionCommandEvent.class, selectionCommandListener);

		// clearSelectionsListener = new ClearSelectionsListener();
		// clearSelectionsListener.setHandler(this);
		// eventPublisher.addListener(ClearSelectionsEvent.class,
		// clearSelectionsListener);
	}

	@Override
	public void unregisterEventListeners() {
		EventPublisher eventPublisher = GeneralManager.get().getEventPublisher();

		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(selectionUpdateListener);
			selectionUpdateListener = null;
		}
		if (selectionCommandListener != null) {
			eventPublisher.removeListener(selectionCommandListener);
			selectionCommandListener = null;
		}

		if (clearSelectionsListener != null) {
			eventPublisher.removeListener(clearSelectionsListener);
			clearSelectionsListener = null;
		}

	}

	@Override
	public void handleSelectionUpdate(SelectionDelta selectionDelta) {
		if (selectionDelta.getIDType().getIDCategory().equals(idType.getIDCategory())) {
			setDelta(selectionDelta);
			parent.notifyOfChange(this);
		}
	}

	@Override
	public void handleSelectionCommand(IDCategory idCategory,
			SelectionCommand selectionCommand) {
		if (idCategory == null || idCategory.equals(idType.getIDCategory())) {
			super.executeSelectionCommand(selectionCommand);
			parent.notifyOfChange(this);
		}
	}

	public void triggerSelectionUpdateEvent() {
		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setSelectionDelta(getDelta());
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}

}
