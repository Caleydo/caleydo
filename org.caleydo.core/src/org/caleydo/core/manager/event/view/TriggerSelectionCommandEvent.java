package org.caleydo.core.manager.event.view;

import java.util.List;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.manager.event.AEvent;

/**
 * TODO javadoc  
 * @author Werner Puff
 */
public class TriggerSelectionCommandEvent
	extends AEvent {

	/** selected genome data-type */
	EIDType type;
	
	/** list of selection commands to handle by the receiver */
	List<SelectionCommand> selectionCommands = null;

	public List<SelectionCommand> getSelectionCommands() {
		return selectionCommands;
	}

	public void setSelectionCommands(List<SelectionCommand> selectionCommands) {
		this.selectionCommands = selectionCommands;
	}

	public EIDType getType() {
		return type;
	}

	public void setType(EIDType type) {
		this.type = type;
	}
	
}
