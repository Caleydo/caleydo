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

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.AGLView;

/**
 * Interface for {@link IListenerOwner}s that need to perform polling. All subclasses of {@link AGLView} do
 * so.
 * 
 * @author Alexander Lex
 */
public interface IPollingListenerOwner
	extends IListenerOwner {

	/**
	 * Submit an event which is executed by the specified listener once the IListenerOwner thinks it's safe to
	 * do so. This method needs to be implemented using the synchronized keyword.
	 * 
	 * @return The return value is a pair containing the listener used by the IListenerOwner to listen to the
	 *         event, and the event which is to be executed.
	 */
	public Pair<AEventListener<? extends IListenerOwner>, AEvent> getEvent();
}
