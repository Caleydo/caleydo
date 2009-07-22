package org.caleydo.core.manager.event.view;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.manager.event.AEvent;

/**
 * A SelectionCommandEvent holds a {@link SelectionCommand} which is used to signal one of the actions defined
 * in {@link ESelectionCommandType} to a {@link SelectionManager}. Which particular selection manager
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
	EIDType type;

	/** list of selection commands to handle by the receiver */
	SelectionCommand selectionCommand = null;

	public SelectionCommand getSelectionCommand() {
		return selectionCommand;
	}

	public void setSelectionCommand(SelectionCommand selectionCommand) {
		this.selectionCommand = selectionCommand;
	}

	public EIDType getType() {
		return type;
	}

	public void setType(EIDType type) {
		this.type = type;
	}

	@Override
	public boolean checkIntegrity() {
		if (type == null)
			throw new NullPointerException("type was null");
		if (selectionCommand == null)
			throw new NullPointerException("selectionCommands was null");
		return true;
	}
}
