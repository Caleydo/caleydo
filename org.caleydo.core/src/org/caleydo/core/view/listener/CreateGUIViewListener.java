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
package org.caleydo.core.view.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.CreateGUIViewEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.net.NetworkManager;
import org.caleydo.core.view.ViewManager;

/**
 * Handles {@link CreateGUIViewEvent}s by invoking the method on the related ViewManager.
 * {@link CreateGUIViewEvent}s are only handled if they do not have a target application name specified or the
 * specified target application name is equals to this network name the executing caleydo application.
 * 
 * @author Werner Puff
 */
public class CreateGUIViewListener
	extends AEventListener<ViewManager> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof CreateGUIViewEvent) {
			CreateGUIViewEvent createSWTViewEvent = (CreateGUIViewEvent) event;
			System.out.println("create swt view event serialized-view="
				+ createSWTViewEvent.getSerializedView());
			String target = createSWTViewEvent.getTargetApplicationID();
			NetworkManager networkManager = GeneralManager.get().getGroupwareManager().getNetworkManager();
			if (target == null || target.equals(networkManager.getNetworkName())) {
				handler.createSWTView(createSWTViewEvent.getSerializedView());
			}
		}
	}

}
