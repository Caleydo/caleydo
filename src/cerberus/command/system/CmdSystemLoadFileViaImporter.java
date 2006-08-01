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
public class CmdSystemLoadFileViaImporter 
extends AbstractCommand
implements CommandInterface {

	protected String sFileName;
	
	protected String sTokenPattern;
	
	protected String sTargetSet;
	
	
	/**
	 * 
	 */
	public CmdSystemLoadFileViaImporter( String fileName, 
			String tokenPattern,
			String targetSet ) {
		
		this.sFileName = fileName;
		
		this.sTokenPattern =tokenPattern;
		
		this.sTargetSet = targetSet;
	}

	/* (non-Javadoc)
	 * @see cerberus.command.CommandInterface#doCommand()
	 */
	public void doCommand() throws CerberusRuntimeException {
		System.out.println("load file via importer... ([" +
				sFileName + "] tokens:[" +
				sTokenPattern + "]  targetSet(s)=[" +
				sTargetSet + "])");
		
//		CmdWindowPopupInfo exitWarning = new CmdWindowPopupInfo("");
//		exitWarning.setText("WARNING","Load data from file..");
//		exitWarning.doCommand();
		
		
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
		return CommandType.DATASET_LOAD; 
	}

}
