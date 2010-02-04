package org.caleydo.view.radial;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;

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
	protected IEventPublisher eventPublisher;

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
	public void registerEventListeners() {

		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		eventPublisher.addListener(SelectionUpdateEvent.class,
				selectionUpdateListener);

		registerDataSpecificEventListeners();
	}

	/**
	 * Unregister all event listeners used by the DataEventHandler.
	 */
	public void unregisterEventListeners() {

		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(selectionUpdateListener);
			selectionUpdateListener = null;
		}

		unregisterDataSpecificEventListeners();
	}

	@Override
	public void queueEvent(AEventListener<? extends IListenerOwner> listener,
			AEvent event) {
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
	public abstract void triggerDataSelectionEvents(
			SelectionType selectionType, PartialDisc pdSelected);

	/**
	 * Registers all data type specific event listeners.
	 */
	protected abstract void registerDataSpecificEventListeners();

	/**
	 * Unregisters all data type specific event listeners.
	 */
	protected abstract void unregisterDataSpecificEventListeners();
}
