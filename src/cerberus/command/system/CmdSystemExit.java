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
 * Command, shuts down application.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdSystemExit 
extends AbstractCommand
implements CommandInterface {

	/**
	 * 
	 */
	public CmdSystemExit() {
		
	}

	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#doCommand()
	 */
	public void doCommand() throws CerberusRuntimeException {
		System.out.println("shut down application...");
		
		CmdWindowPopupInfo exitWarning = new CmdWindowPopupInfo("");
		exitWarning.setText("WARNING","Close application. Current state is stored..");
		exitWarning.doCommand();
		
		System.out.println(" ...cleanup.. ");
		System.out.println("shut down done!");
		
		
		System.exit( 0 );
	}

	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#undoCommand()
	 */
	public void undoCommand() throws CerberusRuntimeException {
		// no undo of system shutdown!
	}
	

	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#getCommandType()
	 */
	public CommandType getCommandType() throws CerberusRuntimeException {
		return CommandType.SYSTEM_EXIT; 
	}

}
