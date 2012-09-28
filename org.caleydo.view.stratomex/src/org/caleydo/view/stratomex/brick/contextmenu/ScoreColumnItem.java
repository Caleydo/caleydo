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

import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.stratomex.column.BrickColumn;
import org.caleydo.view.stratomex.event.ScoreTablePerspectiveEvent;
import org.caleydo.view.stratomex.vendingmachine.EScoreReferenceMode;
import org.caleydo.view.stratomex.vendingmachine.VendingMachine;

/**
 * @author Marc Streit
 * 
 */
public class ScoreColumnItem
	extends AContextMenuItem {

	public ScoreColumnItem(VendingMachine vendingMachine, BrickColumn referenceBrickColumn) {

		setLabel("Score column");

		ScoreTablePerspectiveEvent event = new ScoreTablePerspectiveEvent(
				EScoreReferenceMode.COLUMN, referenceBrickColumn.getTablePerspective(),
				referenceBrickColumn);
		event.setSender(this);
		registerEvent(event);
	}
}
