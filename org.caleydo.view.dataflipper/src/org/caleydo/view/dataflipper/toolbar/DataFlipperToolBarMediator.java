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
package org.caleydo.view.dataflipper.toolbar;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.IListenerOwner;
import org.eclipse.swt.widgets.Display;

/**
 * Mediator for remote-rendering (data flipper) related toolbar items
 * 
 * @author Werner Puff
 * @author Marc Streit
 */
public class DataFlipperToolBarMediator implements IListenerOwner {

	// private EventPublisher eventPublisher;

	/**
	 * related toolBarContent that contains the gui-control items for
	 * mediatation
	 */
	private DataFlipperToolBarContent toolBarContent;

	public DataFlipperToolBarMediator() {
		// eventPublisher = GeneralManager.get().getEventPublisher();
		registerEventListeners();
	}

	@Override
	public void registerEventListeners() {
	}

	@Override
	public void unregisterEventListeners() {

	}

	@Override
	public void queueEvent(final AEventListener<? extends IListenerOwner> listener,
			final AEvent event) {
		System.out.println("queue: listener.handleEvent(event);");
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				listener.handleEvent(event);
				System.out.println("listener.handleEvent(event);");
			}
		});
	}

	/**
	 * Releases all obtained resources (e.g. event-listeners.
	 */
	public void dispose() {
		unregisterEventListeners();
	}

	/**
	 * Gets the related {@link DataFlipperToolBarContent} of this mediator
	 * 
	 * @return {@link DataFlipperToolBarContent} that is mediated
	 */
	public DataFlipperToolBarContent getToolBarContent() {
		return toolBarContent;
	}

	/**
	 * Sets the related {@link DataFlipperToolBarContent} for this mediator
	 * 
	 * @param toolBarContent
	 *            {@link DataFlipperToolBarContent} to mediate
	 */
	public void setToolBarContent(DataFlipperToolBarContent toolBarContent) {
		this.toolBarContent = toolBarContent;
	}
}
