/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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

	public void setSelectionCommandType(ESelectionCommandType selectionCommandType) {
		this.selectionCommandType = selectionCommandType;
	}

	public void setSelectionType(SelectionType selectionType) {
		this.selectionType = selectionType;
	}

}
