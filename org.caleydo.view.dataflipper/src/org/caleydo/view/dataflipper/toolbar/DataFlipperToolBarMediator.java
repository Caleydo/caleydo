package org.caleydo.view.dataflipper.toolbar;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
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
