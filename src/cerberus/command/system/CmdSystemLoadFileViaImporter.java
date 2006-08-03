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
import cerberus.manager.GeneralManager;
import cerberus.util.exception.CerberusRuntimeException;

import cerberus.data.loader.MicroArrayLoader;


/**
 * Command, shuts down application.
 * 
 * @author Michael Kalkusch
 *
 */
public class CmdSystemLoadFileViaImporter 
extends AbstractCommand
implements CommandInterface {

	private final GeneralManager refGeneralManager;
	
	protected String sFileName;
	
	protected String sTokenPattern;
	
	protected String sTargetSet;
	
	
	/**
	 * 
	 */
	public CmdSystemLoadFileViaImporter( GeneralManager refGeneralManager,
			String fileName, 
			String tokenPattern,
			String targetSet ) {
		
		this.refGeneralManager = refGeneralManager;		
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
		
		int iTargetSetId = -1;
		
		try {
			iTargetSetId = Integer.valueOf(sTargetSet);
		}
		catch ( NumberFormatException nfe ) 
		{
			refGeneralManager.getSingelton().getLoggerManager().logMsg("CmdSystemLoadFileViaImporter::doCommand() can not convert ["+
					sTargetSet + "] to integer. Can not load [" +
					sFileName + "] with tokenPattern=[" +
					sTokenPattern + "]");
			
			return;
		}
		
		MicroArrayLoader loader = new MicroArrayLoader( refGeneralManager );
		
		loader.setFileName( sFileName );
		loader.setTokenPattern( sTokenPattern );
		loader.setTargetSet( refGeneralManager.getSingelton().getSetManager().getItemSet( iTargetSetId ));
		
		//boolean bSuccessOnLoad = 
		loader.loadData();
		
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
