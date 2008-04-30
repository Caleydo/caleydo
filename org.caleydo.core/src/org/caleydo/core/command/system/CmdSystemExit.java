package org.caleydo.core.command.system;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.ICommand;
import org.caleydo.core.data.AUniqueManagedObject;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Command shuts down application.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdSystemExit 
extends AUniqueManagedObject
implements ICommand {

	/**
	 * Constructor.
	 * 
	 */
	public CmdSystemExit(final IGeneralManager generalManager) {
	
		super (-1, generalManager);
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException {
		
//		generalManager.logMsg("CmdSystemExit.doCommand(): shut down application.", LoggerType.FULL);	
		System.exit( 0 );
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException {
		// no undo of system shutdown!
	}

	public void setParameterHandler(IParameterHandler refParameterHandler) {
		assert false : "Must not be called for this classs.";
	}

	public CommandQueueSaxType getCommandType() {
		return CommandQueueSaxType.SYSTEM_SHUT_DOWN;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#getInfoText()
	 */
	public String getInfoText() {
		assert false : "Must not be called for this calss.";
	
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.IUniqueManagedObject#getBaseType()
	 */
	public final ManagerObjectType getBaseType() {
		return ManagerObjectType.COMMAND;
	}

}
