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
package org.caleydo.view.stratomex.brick.contextmenu;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.stratomex.event.OpenCreatePathwayGroupDialogEvent;

/**
 * Context menu item for opening a dialog used to create a pathway dimension
 * group.
 * 
 * @author Christian Partl
 * 
 */
public class CreatePathwayGroupFromDataItem
	extends AContextMenuItem {

	public CreatePathwayGroupFromDataItem(ATableBasedDataDomain dataDomain,
			VirtualArray recordVA, Perspective dimensionPerspective) {

		setLabel("Create Pathway Group From Data");

		OpenCreatePathwayGroupDialogEvent event = new OpenCreatePathwayGroupDialogEvent(
				dataDomain, recordVA);
		event.setSender(this);
		registerEvent(event);
	}

}
