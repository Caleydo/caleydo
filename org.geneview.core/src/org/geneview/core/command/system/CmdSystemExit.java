/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.geneview.core.command.system;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.ICommand;
import org.geneview.core.command.window.CmdWindowPopupInfo;
import org.geneview.core.data.AUniqueManagedObject;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.type.ManagerObjectType;
import org.geneview.core.parser.parameter.IParameterHandler;
import org.geneview.core.util.exception.GeneViewRuntimeException;

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
	 * @see org.geneview.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws GeneViewRuntimeException {
		System.out.println("CmdSystemExit: shut down application...");
		
		CmdWindowPopupInfo exitWarning = new CmdWindowPopupInfo(refGeneralManager,"");
		exitWarning.setText("WARNING","Close application. Current state is stored..");
		exitWarning.doCommand();
		
		System.out.println("CmdSystemExit: ...cleanup.. ");
		System.out.println("CmdSystemExit: shut down done!");
		
		
		System.exit( 0 );
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.command.ICommand#undoCommand()
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
