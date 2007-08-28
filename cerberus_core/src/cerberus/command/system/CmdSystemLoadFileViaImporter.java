/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.system;

//import java.util.LinkedList;
//import java.util.Iterator;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.ICommand;
import cerberus.command.base.ACommand;
import cerberus.command.window.CmdWindowPopupInfo;
//import cerberus.command.window.CmdWindowPopupInfo;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.parser.ascii.microarray.MicroArrayLoader1Storage;
import cerberus.parser.parameter.IParameterHandler;
import cerberus.util.exception.GeneViewRuntimeException;
import cerberus.util.system.StringConversionTool;

import cerberus.data.collection.ISet;


/**
 * Command, load data from file using a token pattern and a target ISet.
 * Use MicroArrayLoader1Storage to laod dataset.
 * 
 * @author Michael Kalkusch
 *
 * @see cerberus.data.collection.ISet
 * @see cerberus.parser.ascii.microarray.MicroArrayLoader1Storage
 */
public class CmdSystemLoadFileViaImporter 
extends ACommand
implements ICommand {
	
	protected String sFileName;
	
	protected String sTokenPattern;
	
	/**
	 * Default is 32, because gpr files have a header of that size!
	 * 
	 * @see cerberus.parser.ascii.microarray.MicroArrayLoader1Storage#iStartParsingAtLine
	 * @see cerberus.parser.ascii.microarray.MicroArrayLoader1Storage#getStartParsingAtLine()
	 * @see cerberus.parser.ascii.microarray.MicroArrayLoader1Storage#setStartParsingStopParsingAtLine(int, int)
	 */
	protected int iStartPareseFileAtLine = 32;
	
	/**
	 * Default is -1 indicateing read till end of file.
	 * 
	 * @see cerberus.parser.ascii.microarray.MicroArrayLoader1Storage#iStopParsingAtLine
	 * @see cerberus.parser.ascii.microarray.MicroArrayLoader1Storage#getStopParsingAtLine()
	 * @see cerberus.parser.ascii.microarray.MicroArrayLoader1Storage#setStartParsingStopParsingAtLine(int, int)
	 */
	protected int iStopPareseFileAtLine = -1;
	
	protected int iTargetSetId;
	
	
	/**
	 * Constructor.
	 * 
	 * @param refGeneralManager
	 * @param refCommandManager
	 * @param refCommandQueueSaxType
	 */
	public CmdSystemLoadFileViaImporter( 
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		super(-1,
				refGeneralManager,
				refCommandManager,
				refCommandQueueSaxType);
		
		setCommandQueueSaxType(CommandQueueSaxType.LOAD_DATA_FILE_BY_IMPORTER);
	}
	
	public void setParameterHandler( IParameterHandler refParameterHandler) {
		super.setParameterHandler(refParameterHandler);
		
		this.setId( refParameterHandler.getValueInt( 
				CommandQueueSaxType.TAG_CMD_ID.getXmlKey()) );
	
		this.sFileName = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_DETAIL.getXmlKey() );
		this.sTokenPattern =  refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() );
		this.iTargetSetId =	StringConversionTool.convertStringToInt(
				refParameterHandler.getValueString( 
						CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey()),
				-1 );
		
		int[] iArrayStartStop = StringConversionTool.convertStringToIntArrayVariableLength(
				refParameterHandler.getValueString( 
						CommandQueueSaxType.TAG_ATTRIBUTE3.getXmlKey() ),
				" " );
		
		if ( iArrayStartStop.length > 0 ) 
		{
			iStartPareseFileAtLine = iArrayStartStop[0];
			
			if ( iArrayStartStop.length > 1 ) 
			{
				if (( iArrayStartStop[0] > iArrayStartStop[1] )&&
				   ( iArrayStartStop[1] != -1 )){
					refGeneralManager.getSingelton().logMsg(
							"CmdSystemLoadFileViaImporter ignore stop index=(" + 
							iArrayStartStop[1]  + 
							"), because it is smaller than start index (" + 
							iArrayStartStop[0] + ") !",
							LoggerType.STATUS );
					return;
				}
				iStopPareseFileAtLine = iArrayStartStop[1];
			} // if ( iArrayStartStop.length > 0 ) 
		} // if ( iArrayStartStop.length > 0 ) 
	}
	
//	/**
//	 * Use 
//	 * 
//	 * @see cerberus.parser.handler.importer.ascii.MicroArrayLoader1Storage
//	 */
//	public CmdSystemLoadFileViaImporter( IGeneralManager refGeneralManager,
//			String fileName, 
//			String tokenPattern,
//			final int iTargetSet ) {
//		
//		this.refGeneralManager = refGeneralManager;		
//		this.sFileName = fileName;		
//		this.sTokenPattern =tokenPattern;
//		this.iTargetSetId = iTargetSet;
//	}

	/**
	 * Load data from file using a token pattern.
	 * 
	 * @see cerberus.parser.ascii.microarray.MicroArrayLoader1Storage#loadData()
	 * 
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws GeneViewRuntimeException {
		
		refGeneralManager.getSingelton().logMsg(
	    		"load file via importer... ([" +
				sFileName + "] tokens:[" +
				sTokenPattern + "]  targetSet(s)=[" +
				iTargetSetId + "])",
				LoggerType.STATUS );
		
		ISet useSet = refGeneralManager.getSingelton().getSetManager(
				).getItemSet( iTargetSetId );
		
		if ( useSet == null ) {
			String errorMsg = "Could not load data via MicroArrayLoader1Storage, target Set is not valid! file=["+
			sFileName + "] tokens:[" +
			sTokenPattern + "]  targetSet(s)=[" +
			iTargetSetId + "]) CmdSystemLoadfileViaImporter";
			
			refGeneralManager.getSingelton().logMsg(
					errorMsg,
					LoggerType.ERROR_ONLY );
			
			CmdWindowPopupInfo exitWarning = new CmdWindowPopupInfo(refGeneralManager,"");
			exitWarning.setText("ERROR",errorMsg);
			exitWarning.doCommand();
			return;
		}
		
		MicroArrayLoader1Storage loader = null;
		
		try 
		{
			loader = new MicroArrayLoader1Storage( refGeneralManager, 
					sFileName,
					IGeneralManager.bEnableMultipelThreads );
			
			//loader.setFileName( sFileName );
			loader.setTokenPattern( sTokenPattern );
			loader.setTargetSet( useSet );
			loader.setStartParsingStopParsingAtLine( iStartPareseFileAtLine,
					iStopPareseFileAtLine );
			
			loader.loadData();
			
			
		} //try
		catch ( Exception e ) 
		{
			String errorMsg = "Could not load data via MicroArrayLoader1Storage, error during loading! file=["+
				sFileName + "] tokens:[" +
				sTokenPattern + "]  targetSet(s)=[" +
				iTargetSetId + "])";
			
			refGeneralManager.getSingelton().logMsg(
					errorMsg,
					LoggerType.ERROR_ONLY );
			
			CmdWindowPopupInfo exitWarning = new CmdWindowPopupInfo(refGeneralManager,"");
			exitWarning.setText("ERROR",errorMsg);
			exitWarning.doCommand();
		} // catch
		finally 
		{
			if ( loader != null ) 
			{
				loader.destroy();
				loader = null;
			}
		} // finally
		
		refCommandManager.runDoCommand(this);
	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws GeneViewRuntimeException {
		refCommandManager.runUndoCommand(this);
	}
}
