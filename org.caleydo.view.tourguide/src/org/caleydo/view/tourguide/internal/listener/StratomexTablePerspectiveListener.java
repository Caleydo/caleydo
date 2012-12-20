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
package org.caleydo.view.tourguide.internal.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.data.ReplaceTablePerspectiveEvent;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.listener.RemoveTablePerspectiveEvent;
import org.caleydo.view.tourguide.internal.view.VendingMachine;

/**
 * listens for remove events of the stratomex brick columns
 *
 * @author Samuel Gratzl
 *
 */
public class StratomexTablePerspectiveListener extends AEventListener<VendingMachine> {

	public StratomexTablePerspectiveListener(VendingMachine vendingMachine) {
		setHandler(vendingMachine);
	}

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof RemoveTablePerspectiveEvent) {
			RemoveTablePerspectiveEvent e = (RemoveTablePerspectiveEvent) event;
			handler.onStratomexRemoveBrick(e.getReceiver(), e.getTablePerspectiveID());
		} else if (event instanceof AddTablePerspectivesEvent) {
			AddTablePerspectivesEvent e = (AddTablePerspectivesEvent) event;
			handler.onStratomexAddBricks(e.getReceiver(), e.getTablePerspectives());
		} else if (event instanceof ReplaceTablePerspectiveEvent) {
			ReplaceTablePerspectiveEvent e = (ReplaceTablePerspectiveEvent) event;
			handler.onStratomexReplaceBricks(e.getViewID(), e.getOldPerspective(), e.getNewPerspective());
		}
	}

}

