package org.caleydo.core.data.selection;

import java.util.ArrayList;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.manager.event.AEventContainer;
import org.caleydo.core.manager.event.EEventType;

/**
 * Event Container for SelectionCommands
 * 
 * @author Alexander Lex
 */
public class SelectionCommandEventContainer
	extends AEventContainer {
	ArrayList<SelectionCommand> alSelectionCommands;
	EIDType iDType;

	/**
	 * Constructor
	 * 
	 * @param iDType
	 *            used to identify the selection manager associated with the id type
	 */
	public SelectionCommandEventContainer(EIDType iDType) {
		super(EEventType.TRIGGER_SELECTION_COMMAND);
		alSelectionCommands = new ArrayList<SelectionCommand>();
		this.iDType = iDType;

	}

	/**
	 * Constructor
	 * 
	 * @param iDType
	 *            used to identify the selection manager associated with the id type
	 * @param selectionCommand
	 *            shortcut when only used with one command
	 */
	public SelectionCommandEventContainer(EIDType iDType, SelectionCommand selectionCommand) {
		this(iDType);
		addSelectionCommand(selectionCommand);
	}

	/**
	 * Adds a selection command
	 * 
	 * @param selectionCommand
	 *            the command to add
	 */
	public void addSelectionCommand(SelectionCommand selectionCommand) {
		alSelectionCommands.add(selectionCommand);
	}

	/**
	 * Get the id type
	 * 
	 * @return the id type
	 */
	public EIDType getIDType() {
		return iDType;
	}

	/**
	 * Get the list of selection commands
	 * 
	 * @return
	 */
	public ArrayList<SelectionCommand> getSelectionCommands() {
		return alSelectionCommands;
	}

}
