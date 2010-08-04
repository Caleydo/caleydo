package org.caleydo.core.manager.event.view;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.VABasedSelectionManager;
import org.caleydo.core.manager.event.AEvent;

/**
 * A SelectionCommandEvent holds a {@link SelectionCommand} which is used to signal one of the actions defined
 * in {@link ESelectionCommandType} to a {@link VABasedSelectionManager}. Which particular selection manager
 * the command should be applied to is specified via the additional {@link EIDType}.
 * 
 * @author Werner Puff
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class SelectionCommandEvent
	extends AEvent {

	/** selected genome data-type */
	EIDCategory category;

	/** list of selection commands to handle by the receiver */
	SelectionCommand selectionCommand = null;

	public SelectionCommand getSelectionCommand() {
		return selectionCommand;
	}

	public void setSelectionCommand(SelectionCommand selectionCommand) {
		this.selectionCommand = selectionCommand;
	}

	@Override
	public boolean checkIntegrity() {
		if (category == null)
			throw new NullPointerException("category was null");
		if (selectionCommand == null)
			throw new NullPointerException("selectionCommands was null");
		return true;
	}
}
