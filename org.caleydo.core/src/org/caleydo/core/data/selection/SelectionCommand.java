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
	private ESelectionCommandType eSelectionCommandType;
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
	 * @param eSelectionCommandType
	 */
	public SelectionCommand(ESelectionCommandType eSelectionCommandType) {
		this.eSelectionCommandType = eSelectionCommandType;
	}

	public SelectionCommand(ESelectionCommandType eSelectionCommandType, SelectionType selectionType) {

		this.eSelectionCommandType = eSelectionCommandType;
		this.selectionType = selectionType;
	}

	public ESelectionCommandType getSelectionCommandType() {
		return eSelectionCommandType;
	}

	public SelectionType getSelectionType() {
		return selectionType;
	}

	public ESelectionCommandType getESelectionCommandType() {
		return eSelectionCommandType;
	}

	public void setESelectionCommandType(ESelectionCommandType selectionCommandType) {
		eSelectionCommandType = selectionCommandType;
	}

	public void setSelectionType(SelectionType selectionType) {
		this.selectionType = selectionType;
	}

}
