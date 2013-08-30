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
package org.caleydo.view.entourage.event;

import org.caleydo.core.event.AEvent;

/**
 * Event to trigger the display of portals.
 *
 * @author Christian Partl
 *
 */
public class ShowPortalsEvent extends AEvent {

	private boolean showPortals;

	/**
	 *
	 */
	public ShowPortalsEvent(boolean showPortalLinks) {
		this.showPortals = showPortalLinks;
	}

	@Override
	public boolean checkIntegrity() {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * @return the showPortalLinks, see {@link #showPortals}
	 */
	public boolean isShowPortals() {
		return showPortals;
	}

	/**
	 * @param showPortalLinks
	 *            setter, see {@link showPortalLinks}
	 */
	public void setShowPortalLinks(boolean showPortalLinks) {
		this.showPortals = showPortalLinks;
	}

}
