/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.event.data;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.id.IDCategory;

/**
 * A SelectionCommandEvent holds a {@link SelectionCommand} which is used to signal one of the actions defined in
 * {@link ESelectionCommandType} to a {@link SelectionManager}. Which particular selection manager the command should be
 * applied to is specified via the additional {@link IDCategory}, which can be null if it should be applied to all
 * types.
 *
 * @author Werner Puff
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class SelectionCommandEvent extends AEvent {

	/**
	 * The ID Category this selection command event should be used for, or null if it should be used for all categories
	 */
	private IDCategory idCategory = null;

	/** list of selection commands to handle by the receiver */
	private SelectionCommand selectionCommand = null;

	/**
	 *
	 */
	public SelectionCommandEvent() {
	}

	/** Constructor taking a selection command */
	public SelectionCommandEvent(SelectionCommand selectionCommand) {
		this.selectionCommand = selectionCommand;
	}

	public SelectionCommand getSelectionCommand() {
		return selectionCommand;
	}

	public void setSelectionCommand(SelectionCommand selectionCommand) {
		this.selectionCommand = selectionCommand;
	}

	public IDCategory getIdCategory() {
		return idCategory;
	}

	public void setIDCategory(IDCategory idCategory) {
		this.idCategory = idCategory;
	}

	@Override
	public boolean checkIntegrity() {
		if (selectionCommand == null)
			throw new NullPointerException("selectionCommands was null");
		return true;
	}
}
