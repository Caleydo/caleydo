package org.caleydo.core.data.selection;

/**
 * SelectionCommand stores a command and a data type on which it should be applied. It is used to pass
 * commands between two instances to remotely control the {@link GenericSelectionManager} of the receiving
 * instance.
 * 
 * @author Alexander Lex
 */
public class SelectionCommand {
	private ESelectionCommandType eSelectionCommandType;
	private ESelectionType eSelectionType;

	/**
	 * Constructor that can be used for selection commands that don't depend on a particular
	 * {@link ESelectionType} such as {@link ESelectionCommandType#CLEAR_ALL} or
	 * {@link ESelectionCommandType#RESET}.
	 * 
	 * @param eSelectionCommandType
	 */
	public SelectionCommand(ESelectionCommandType eSelectionCommandType) {
		this.eSelectionCommandType = eSelectionCommandType;
	}

	public SelectionCommand(ESelectionCommandType eSelectionCommandType, ESelectionType eSelectionType) {

		this.eSelectionCommandType = eSelectionCommandType;
		this.eSelectionType = eSelectionType;
	}

	public ESelectionCommandType getSelectionCommandType() {
		return eSelectionCommandType;
	}

	public ESelectionType getSelectionType() {
		return eSelectionType;
	}

}
