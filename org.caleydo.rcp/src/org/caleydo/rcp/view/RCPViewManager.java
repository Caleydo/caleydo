package org.caleydo.rcp.view;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.view.grouper.CompareGroupsEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.rcp.view.listener.ActivateViewListener;
import org.eclipse.ui.PlatformUI;

/**
 * The view manager knows about all RCP views. It can react on certain events and take appropriate action. For
 * example that the Bucket RCP view is shown when a new pathway is loaded.
 * 
 * @author Marc Streit
 */
public class RCPViewManager
	implements IListenerOwner {

	private static RCPViewManager rcpViewManager = null;

	private ActivateViewListener activateViewListener;

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

	/**
	 * Registers the listeners for this view to the event system. To release the allocated resources
	 * unregisterEventListeners() has to be called.
	 */
	private void registerEventListeners() {
		activateViewListener = new ActivateViewListener();
		activateViewListener.setHandler(this);
		GeneralManager.get().getEventPublisher().addListener(LoadPathwayEvent.class,
			activateViewListener);
		GeneralManager.get().getEventPublisher().addListener(LoadPathwaysByGeneEvent.class,
			activateViewListener);
		GeneralManager.get().getEventPublisher().addListener(CompareGroupsEvent.class,
			activateViewListener);		
	}

	/**
	 * Unregisters the listeners for this view from the event system. To release the allocated resources
	 * unregisterEventListenrs() has to be called.
	 */
	private void unregisterEventListeners() {
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
}