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
package org.caleydo.core.event.data;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.id.IDCategory;

/**
 * A SelectionCommandEvent holds a {@link SelectionCommand} which is used to
 * signal one of the actions defined in {@link ESelectionCommandType} to a
 * {@link VABasedSelectionManager}. Which particular selection manager the
 * command should be applied to is specified via the additional {@link EIDType},
 * which can be null if it should be applied to all types.
 * 
 * @author Werner Puff
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class SelectionCommandEvent extends AEvent {

	/**
	 * The ID Category this selection command event should be used for, or null
	 * if it should be used for all categories
	 */
	IDCategory idCategory = null;

	/** list of selection commands to handle by the receiver */
	SelectionCommand selectionCommand = null;

	public SelectionCommand getSelectionCommand() {
		return selectionCommand;
	}

	public void setSelectionCommand(SelectionCommand selectionCommand) {
		this.selectionCommand = selectionCommand;
	}

	public IDCategory getIdCategory() {
		return idCategory;
	}

	public void tableIDCategory(IDCategory idCategory) {
		this.idCategory = idCategory;
	}

	@Override
	public boolean checkIntegrity() {
		if (selectionCommand == null)
			throw new NullPointerException("selectionCommands was null");
		return true;
	}
}
