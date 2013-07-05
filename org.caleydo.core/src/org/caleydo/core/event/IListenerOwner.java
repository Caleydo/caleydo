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
