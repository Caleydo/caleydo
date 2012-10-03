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
package org.caleydo.view.bucket.toolbar;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.event.view.ResetViewEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.canvas.listener.IRemoteRenderingHandler;
import org.caleydo.view.bucket.listener.DisableConnectionLinesEvent;
import org.caleydo.view.bucket.listener.DisableConnectionLinesListener;
import org.caleydo.view.bucket.listener.EnableConnectionLinesEvent;
import org.caleydo.view.bucket.listener.EnableConnectionLinesListener;
import org.caleydo.view.bucket.listener.ToggleNavigationModeEvent;
import org.caleydo.view.bucket.listener.ToggleZoomEvent;
import org.eclipse.swt.widgets.Display;

/**
 * Mediator for remote-rendering (bucket) related toolbar items
 * 
 * @author Werner Puff
 */
public class RemoteRenderingToolBarMediator implements IRemoteRenderingHandler {

	private EventPublisher eventPublisher;

	/**
	 * related toolBarContent that contains the gui-control items for
	 * mediatation
	 */
	private RemoteRenderingToolBarContent toolBarContent;

	protected EnableConnectionLinesListener enableConnectionLinesListener;
	protected DisableConnectionLinesListener disableConnectionLinesListener;

	public RemoteRenderingToolBarMediator() {
		eventPublisher = GeneralManager.get().getEventPublisher();
		registerEventListeners();
	}

	@Override
	public void registerEventListeners() {
		enableConnectionLinesListener = new EnableConnectionLinesListener();
		enableConnectionLinesListener.setHandler(this);
		eventPublisher.addListener(EnableConnectionLinesEvent.class,
				enableConnectionLinesListener);

		disableConnectionLinesListener = new DisableConnectionLinesListener();
		disableConnectionLinesListener.setHandler(this);
		eventPublisher.addListener(DisableConnectionLinesEvent.class,
				disableConnectionLinesListener);
	}

	@Override
	public void unregisterEventListeners() {

		if (enableConnectionLinesListener != null) {
			eventPublisher.removeListener(enableConnectionLinesListener);
			enableConnectionLinesListener = null;
		}
		if (disableConnectionLinesListener != null) {
			eventPublisher.removeListener(disableConnectionLinesListener);
			disableConnectionLinesListener = null;
		}

	}

	public void enableConnectionLines() {
		EnableConnectionLinesEvent event = new EnableConnectionLinesEvent();
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}

	public void disableConnectionLines() {
		DisableConnectionLinesEvent event = new DisableConnectionLinesEvent();
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}

	@Override
	public void toggleNavigationMode() {
		ToggleNavigationModeEvent event = new ToggleNavigationModeEvent();
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}

	public void closeOrResetViews() {
		ResetViewEvent event = new ResetViewEvent();
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}

	@Override
	public void toggleZoom() {
		ToggleZoomEvent event = new ToggleZoomEvent();
		event.setSender(this);
		eventPublisher.triggerEvent(event);
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
	 * Gets the related {@link RemoteRenderingToolBarContent} of this mediator
	 * 
	 * @return {@link RemoteRenderingToolBarContent} that is mediated
	 */
	public RemoteRenderingToolBarContent getToolBarContent() {
		return toolBarContent;
	}

	/**
	 * Sets the related {@link RemoteRenderingToolBarContent} for this mediator
	 * 
	 * @param toolBarContent
	 *            {@link RemoteRenderingToolBarContent} to mediate
	 */
	public void setToolBarContent(RemoteRenderingToolBarContent toolBarContent) {
		this.toolBarContent = toolBarContent;
	}

	@Override
	public void setConnectionLinesEnabled(boolean enabled) {
		toolBarContent.toggleConnectionLinesAction.setConnectionLinesEnabled(enabled);
	}

	@Override
	public void addPathwayView(final int iPathwayID, String dataDomainID) {

	}

	@Override
	public void setGeneMappingEnabled(boolean geneMappingEnabled) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNeighborhoodEnabled(boolean neighborhoodEnabled) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPathwayTexturesEnabled(boolean pathwayTexturesEnabled) {
		// TODO Auto-generated method stub

	}

}
