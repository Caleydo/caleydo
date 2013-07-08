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
package org.caleydo.view.browser.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.browser.ChangeURLEvent;
import org.caleydo.view.browser.HTMLBrowser;

/**
 * Handles change-url events by setting the related browser-view's URL
 * 
 * @author Marc Streit
 */
public class ChangeURLListener extends AEventListener<HTMLBrowser> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ChangeURLEvent) {
			handler.setUrl(((ChangeURLEvent) event).getUrl());
		}
	}

}
