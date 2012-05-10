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
package org.caleydo.core.view.swt.collab;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.net.GroupwareUtils;
import org.caleydo.core.net.IGroupwareManager;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * Listener for the "start server" button that triggers the creation of the deskotheque related groupware
 * management.
 * 
 * @author Werner Puff
 */
public class StartDeskothequeServerListener
	implements Listener {

	@Override
	public void handleEvent(Event event) {
		IGroupwareManager groupwareManager = GroupwareUtils.createDeskothequeManager();
		GeneralManager.get().setGroupwareManager(groupwareManager);
		groupwareManager.startServer();
		GeneralManager.get().getViewManager().getDisplayLoopExecution().executeMultiple(groupwareManager);
	}

}
