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
package org.caleydo.view.info.listener;

import org.caleydo.core.data.virtualarray.events.VADeltaEvent;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.infoarea.InfoAreaUpdateEvent;
import org.caleydo.core.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.view.info.InfoArea;

/**
 * Listener for virtual array update events. This listener gets the payload from
 * a {@link VADeltaEvent} and calls a related
 * {@link IVirtaualArrayUpdateHandler}.
 * 
 * @author Werner Puff
 */
public class InfoAreaUpdateListener extends AEventListener<InfoArea> {

	/**
	 * Handles {@link VirtualArrayUdpateEvent}s by extracting the events payload
	 * and calling the related handler
	 * 
	 * @param event
	 *            {@link SelectionUpdateEvent} to handle, other events will be
	 *            ignored
	 */
	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof InfoAreaUpdateEvent) {
			InfoAreaUpdateEvent infoAreaUpdateEvent = (InfoAreaUpdateEvent) event;
			String info = infoAreaUpdateEvent.getInfo();
			handler.handleInfoAreaUpdate(info);
		}
	}

}
