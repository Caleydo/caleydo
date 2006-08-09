/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.system;

import cerberus.command.ICommand;
import cerberus.command.CommandType;
import cerberus.command.base.ACommand;
import cerberus.command.window.CmdWindowPopupInfo;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * No Opoeration command does nothing.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdSystemNop
extends ACommand
implements ICommand {

	/**
	 * 
	 */
	public CmdSystemNop() {
		
	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws CerberusRuntimeException {
		CmdWindowPopupInfo exitWarning = new CmdWindowPopupInfo("");
		exitWarning.setText("DEBUG-INFO","do nop ...");
		exitWarning.doCommand();
	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CerberusRuntimeException {
		
	}


	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#getCommandType()
	 */
	public CommandType getCommandType() throws CerberusRuntimeException {
		return CommandType.SYSTEM_NOP;
	}

}
