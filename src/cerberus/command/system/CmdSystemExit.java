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
import cerberus.command.window.CmdWindowPopupInfo;
import cerberus.data.AUniqueItem;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * Command, shuts down application.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdSystemExit 
extends AUniqueItem
implements ICommand {

	/**
	 * 
	 */
	public CmdSystemExit() {
		super (-1);
	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#doCommand()
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
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CerberusRuntimeException {
		// no undo of system shutdown!
	}
	
	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#getCommandType()
	 */
	public CommandType getCommandType() throws CerberusRuntimeException {
		return CommandType.SYSTEM_EXIT; 
	}

	public boolean isEqualType(ICommand compareToObject) {
		return false;
	}

	public void setParameterHandler(IParameterHandler refParameterHandler) {
		assert false : "Must not be called for this classs.";
	}

}
