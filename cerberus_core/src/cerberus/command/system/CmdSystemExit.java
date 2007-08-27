/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.system;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.ICommand;
import cerberus.command.window.CmdWindowPopupInfo;
import cerberus.data.AUniqueManagedObject;
import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.GeneViewRuntimeException;
import cerberus.xml.parser.parameter.IParameterHandler;

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
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws GeneViewRuntimeException {
		System.out.println("shut down application...");
		
		CmdWindowPopupInfo exitWarning = new CmdWindowPopupInfo(refGeneralManager,"");
		exitWarning.setText("WARNING","Close application. Current state is stored..");
		exitWarning.doCommand();
		
		System.out.println(" ...cleanup.. ");
		System.out.println("shut down done!");
		
		
		System.exit( 0 );
	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws GeneViewRuntimeException {
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
