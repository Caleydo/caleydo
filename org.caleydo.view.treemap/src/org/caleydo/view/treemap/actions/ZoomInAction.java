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
package org.caleydo.view.treemap.actions;

import org.caleydo.core.gui.SimpleAction;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.view.treemap.listener.ZoomInEvent;

/**
 * Action for zoom in function.
 *
 * @author Michael Lafer
 *
 */

public class ZoomInAction extends SimpleAction {

	private static final String LABEL = "Zoom";
	private static final String ICON = "resources/icons/general/search.png";

	public ZoomInAction() {
		super(LABEL, ICON);
		setChecked(false);
	}

	@Override
	public void run() {
		super.run();

		GeneralManager.get().getEventPublisher().triggerEvent(new ZoomInEvent());
		setChecked(false);
	}
}
