/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.event;

import org.caleydo.core.view.opengl.canvas.AGLView;

/**
 * Interface for all instances that have listeners. Used for thread-safe event queuing.
 * 
 * @author Alexander Lex
 */
public interface IListenerOwner {

	/**
	 * Submit an event which is executed by the specified listener once the IListenerOwner thinks it's safe to
	 * do so. This method needs to be implemented using the synchronized keyword.
	 * 
	 * @param listener
	 *            The listener used by the IListenerOwner to listen to the event
	 * @param event
	 *            The event which is to be executed
	 */
	public void queueEvent(final AEventListener<? extends IListenerOwner> listener, final AEvent event);

	/**
	 * <p>
	 * Registers the listeners for this view to the event system. To release the allocated resources
	 * unregisterEventListeners() has to be called. This method is intended to be overridden, but it's super()
	 * should be called to be registered to the listeners defined by other classes in the hierarchy.
	 * </p>
	 * <p>
	 * In GL Views this is called in {@link AGLView#initialize()}, therefore there is no need to call it
	 * yourself. You must call the initialize of the View though.
	 * </p>
	 * <p>
	 * If part of {@link AEventHandler}, it is called on thread creation
	 * </p>
	 */
	public void registerEventListeners();

	/**
	 * Unregisters the listeners for this view from the event system. To release the allocated resources
	 * unregisterEventListenrs() has to be called. This method is intended to be overridden, but it's super()
	 * should be called to unregistered the listeners defined by other classes in the hierarchy.
	 */
	public void unregisterEventListeners();
}
