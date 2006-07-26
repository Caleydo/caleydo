/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.system;

import cerberus.command.CommandInterface;
import cerberus.command.CommandType;
import cerberus.command.base.AbstractCommand;
import cerberus.command.window.CmdWindowPopupInfo;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * No Opoeration command does nothing.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdSystemNop
extends AbstractCommand
implements CommandInterface {

	/**
	 * 
	 */
	public CmdSystemNop() {
		
	}

	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#doCommand()
	 */
	public void doCommand() throws CerberusRuntimeException {
		CmdWindowPopupInfo exitWarning = new CmdWindowPopupInfo("");
		exitWarning.setText("DEBUG-INFO","do nop ...");
		exitWarning.doCommand();
	}

	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#undoCommand()
	 */
	public void undoCommand() throws CerberusRuntimeException {
		
	}


	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#getCommandType()
	 */
	public CommandType getCommandType() throws CerberusRuntimeException {
		return CommandType.SYSTEM_NOP;
	}

}
