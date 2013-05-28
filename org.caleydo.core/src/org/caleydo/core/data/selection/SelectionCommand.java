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
package org.caleydo.core.data.selection;

import javax.xml.bind.annotation.XmlType;

/**
 * SelectionCommand stores a command and a data type on which it should be applied. It is used to pass
 * commands between two instances to remotely control the {@link VABasedSelectionManager} of the receiving
 * instance.
 *
 * @author Alexander Lex
 */
@XmlType(name = "SelectionCommand")
public class SelectionCommand {

	private ESelectionCommandType selectionCommandType;
	private SelectionType selectionType;

	/**
	 * Default Constructor
	 */
	public SelectionCommand() {

	}

	/**
	 * Constructor that can be used for selection commands that don't depend on a particular
	 * {@link SelectionType} such as {@link ESelectionCommandType#CLEAR_ALL} or
	 * {@link ESelectionCommandType#RESET}.
	 *
	 * @param selectionCommandType
	 */
	public SelectionCommand(ESelectionCommandType selectionCommandType) {
		this.selectionCommandType = selectionCommandType;
	}

	public SelectionCommand(ESelectionCommandType selectionCommandType, SelectionType selectionType) {
		this.selectionCommandType = selectionCommandType;
		this.selectionType = selectionType;
	}

	public ESelectionCommandType getSelectionCommandType() {
		return selectionCommandType;
	}

	public SelectionType getSelectionType() {
		return selectionType;
	}

	public ESelectionCommandType getESelectionCommandType() {
		return selectionCommandType;
	}

	public void setSelectionCommandType(ESelectionCommandType selectionCommandType) {
		this.selectionCommandType = selectionCommandType;
	}

	public void setSelectionType(SelectionType selectionType) {
		this.selectionType = selectionType;
	}

}
