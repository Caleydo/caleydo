package org.caleydo.core.command.system;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.ICommand;
import org.caleydo.core.data.AUniqueManagedObject;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Command, shuts down application.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdSystemExit 
extends AUniqueManagedObject
implements ICommand {

	/**
	 * Constrcutor.
	 * 
	 */
	public CmdSystemExit(final IGeneralManager refGeneralManager) {
		super (-1,refGeneralManager);
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException {
		System.out.println("CmdSystemExit: shut down application...");
		System.out.println("CmdSystemExit: ...cleanup.. ");
		System.out.println("CmdSystemExit: shut down done!");
		
		System.exit( 0 );
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException {
		// no undo of system shutdown!
	}

	public boolean isEqualType(ICommand compareToObject) {
		return false;
	}

	public void setParameterHandler(IParameterHandler refParameterHandler) {
		assert false : "Must not be called for this classs.";
	}

	public CommandQueueSaxType getCommandType() {
		return CommandQueueSaxType.SYSTEM_SHUT_DOWN;
	}
	
	public String getInfoText() {
		assert false : "Must not be called for this calss.";
	
		return null;
	}

	public final ManagerObjectType getBaseType() {
		return ManagerObjectType.COMMAND;
	}

}
