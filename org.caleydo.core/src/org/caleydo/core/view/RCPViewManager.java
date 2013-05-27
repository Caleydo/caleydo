/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.view;

import java.util.HashMap;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.event.data.BookmarkEvent;
import org.caleydo.core.event.view.OpenViewEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.listener.ActivateViewListener;
import org.eclipse.ui.PlatformUI;

/**
 * The view manager knows about all RCP views. It can react on certain events and take appropriate action. For example
 * that the Bucket RCP view is shown when a new pathway is loaded.
 *
 * @author Marc Streit
 */
public class RCPViewManager implements IListenerOwner {

	private static RCPViewManager rcpViewManager = null;

	private ActivateViewListener activateViewListener;

	private HashMap<String, RCPViewInitializationData> rcpSecondaryID2ViewInitializationData = new HashMap<String, RCPViewInitializationData>();

	/**
	 * Constructor, only called internally
	 */
	private RCPViewManager() {
		registerEventListeners();
	}

	/**
	 * Get the instance of the colorMappingManager
	 *
	 * @return the manager
	 */
	public static RCPViewManager get() {
		if (rcpViewManager == null) {
			rcpViewManager = new RCPViewManager();
		}
		return rcpViewManager;
	}

	@Override
	public void registerEventListeners() {
		activateViewListener = new ActivateViewListener();
		activateViewListener.setHandler(this);
		GeneralManager.get().getEventPublisher().addListener(OpenViewEvent.class, activateViewListener);
		GeneralManager.get().getEventPublisher().addListener(BookmarkEvent.class, activateViewListener);
	}

	@Override
	public void unregisterEventListeners() {
		if (activateViewListener != null) {
			GeneralManager.get().getEventPublisher().removeListener(activateViewListener);
			activateViewListener = null;
		}
	}

	public void dispose() {
		unregisterEventListeners();
	}

	@Override
	public void queueEvent(AEventListener<? extends IListenerOwner> listener, final AEvent event) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				activateViewListener.handleEvent(event);
			}
		});
	}

	public void addRCPView(String secondaryID, RCPViewInitializationData rcpViewInitData) {
		rcpSecondaryID2ViewInitializationData.put(secondaryID, rcpViewInitData);
	}

	public void removeRCPView(String secondaryID) {
		rcpSecondaryID2ViewInitializationData.remove(secondaryID);
	}

	public RCPViewInitializationData getRCPViewInitializationData(String secondaryID) {
		return rcpSecondaryID2ViewInitializationData.get(secondaryID);
	}
}