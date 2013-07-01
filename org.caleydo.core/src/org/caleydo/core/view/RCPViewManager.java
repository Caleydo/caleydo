/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
