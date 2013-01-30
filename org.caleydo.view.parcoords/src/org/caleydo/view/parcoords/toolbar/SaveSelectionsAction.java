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
package org.caleydo.view.parcoords.toolbar;

import org.caleydo.core.gui.SimpleEventAction;
import org.caleydo.view.parcoords.Activator;
import org.caleydo.view.parcoords.listener.ApplyCurrentSelectionToVirtualArrayEvent;

public class SaveSelectionsAction extends SimpleEventAction {

	private static final String LABEL = "Save Selections";
	private static final String ICON = "resources/icons/save_selections_16.png";

	public SaveSelectionsAction() {
		super(LABEL, ICON, Activator.getResourceLoader(), new ApplyCurrentSelectionToVirtualArrayEvent());
	}
}
