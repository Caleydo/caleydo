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

import cerberus.command.ICommand;
import cerberus.command.CommandType;
import cerberus.command.base.ACommand;
import cerberus.command.window.CmdWindowPopupInfo;
//import cerberus.command.window.CmdWindowPopupInfo;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.util.system.StringConversionTool;
import cerberus.xml.parser.command.CommandQueueSaxType;
import cerberus.xml.parser.handler.importer.ascii.MicroArrayLoaderValues2MultipleStorages;
import cerberus.xml.parser.parameter.IParameterHandler;

import cerberus.data.collection.ISet;


/**
 * Command, load data from file using a token pattern and a target ISet.
 * Use AMicroArrayLoader to load dataset.
 * 
 * @author Michael Kalkusch
 *
 * @see cerberus.data.collection.ISet
 * @see cerberus.xml.parser.handler.importer.ascii.AMicroArrayLoader
 */
public class CmdSystemLoadFileNStorages 
extends ACommand
implements ICommand {

	private final IGeneralManager refGeneralManager;
	
	protected String sFileName;
	
	protected String sTokenPattern;
	
	/**
	 * Default is 32, because gpr files have a header of that size!
	 * 
	 * @see cerberus.xml.parser.handler.importer.ascii.AMicroArrayLoader#iStartParsingAtLine
	 * @see cerberus.xml.parser.handler.importer.ascii.AMicroArrayLoader#getStartParsingAtLine()
	 * @see cerberus.xml.parser.handler.importer.ascii.AMicroArrayLoader#setStartParsingStopParsingAtLine(int, int)
	 */
	protected int iStartPareseFileAtLine = 32;
	
	/**
	 * Default is -1 indicateing read till end of file.
	 * 
	 * @see cerberus.xml.parser.handler.importer.ascii.AMicroArrayLoader#iStopParsingAtLine
	 * @see cerberus.xml.parser.handler.importer.ascii.AMicroArrayLoader#getStopParsingAtLine()
	 * @see cerberus.xml.parser.handler.importer.ascii.AMicroArrayLoader#setStartParsingStopParsingAtLine(int, int)
	 */
	protected int iStopPareseFileAtLine = -1;
	
	protected int iTargetSetId;
	
	
//	/**
//	 * 
//	 * List of expected Strings inside LinkedList <String>: <br>
//	 * sData_CmdId <br>
//	 * sData_Cmd_label <br>
//	 * sData_Cmd_process <br> 
//	 * sData_Cmd_MementoId <br> 
//	 * sData_Cmd_detail <br>
//	 * sData_Cmd_attribute1 <br>
//	 * sData_Cmd_attribute2 <br>
//	 * 
//	 * @see cerberus.xml.parser.handler.importer.ascii.AMicroArrayLoader
//	 */
//	public CmdSystemLoadFileViaImporter( IGeneralManager refGeneralManager,
//			final LinkedList <String> llAttributes ) {
//		
//		Iterator <String> iter = llAttributes.iterator();
//		
//		this.setId( StringConversionTool.convertStringToInt(
//				iter.next(), 
//				-1 ) );
//		
//		/**
//		 * skip unneeded Strings...
//		 */
//		iter.next();
//		iter.next();
//		iter.next();
//		iter.next();
//		
//		this.refGeneralManager = refGeneralManager;		
//		this.sFileName = iter.next();		
//		this.sTokenPattern = iter.next();
//		this.iTargetSetId = StringConversionTool.convertStringToInt(
//				iter.next(), 
//				-1 );	
//	}
	
	public CmdSystemLoadFileNStorages( IGeneralManager refGeneralManager,
			final IParameterHandler phAttributes ) {
		
		super();
		
		this.refGeneralManager = refGeneralManager;		
		
		this.setId( phAttributes.getValueInt( 
				CommandQueueSaxType.TAG_CMD_ID.getXmlKey()) );
	
		this.sFileName = phAttributes.getValueString( 
				CommandQueueSaxType.TAG_DETAIL.getXmlKey() );
		this.sTokenPattern =  phAttributes.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() );
		this.iTargetSetId =	StringConversionTool.convertStringToInt(
				phAttributes.getValueString( 
						CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey()),
				-1 );
		
		int[] iArrayStartStop = StringConversionTool.convertStringToIntArrayVariableLength(
				phAttributes.getValueString( 
						CommandQueueSaxType.TAG_ATTRIBUTE3.getXmlKey() ),
				" " );
		
		if ( iArrayStartStop.length > 0 ) 
		{
			iStartPareseFileAtLine = iArrayStartStop[0];
			
			if ( iArrayStartStop.length > 1 ) 
			{
				
				if (( iArrayStartStop[0] > iArrayStartStop[1] )&&
						(iArrayStartStop[1] != -1 )) {
					refGeneralManager.getSingelton().logMsg(
							"CmdSystemLoadFileNStorages ignore stop index=(" + 
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
//	 * @see cerberus.xml.parser.handler.importer.ascii.AMicroArrayLoader
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
	 * @see cerberus.xml.parser.handler.importer.ascii.AMicroArrayLoader#loadData()
	 * 
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws CerberusRuntimeException {
		
		refGeneralManager.getSingelton().logMsg(
	    		"load file via importer... ([" +
				sFileName + "] tokens:[" +
				sTokenPattern + "]  targetSet(s)=[" +
				iTargetSetId + "])",
				LoggerType.STATUS );
		
		ISet useSet = refGeneralManager.getSingelton().getSetManager(
				).getItemSet( iTargetSetId );
		
		if ( useSet == null ) {
			String errorMsg = "Could not load data via MicroArrayLoaderValues2MultipleStorages, target Set is not valid! file=["+
			sFileName + "] tokens:[" +
			sTokenPattern + "]  targetSet(s)=[" +
			iTargetSetId + "])";
			
			refGeneralManager.getSingelton().logMsg(
					errorMsg,
					LoggerType.ERROR_ONLY );
			
			CmdWindowPopupInfo exitWarning = new CmdWindowPopupInfo("");
			exitWarning.setText("ERROR",errorMsg);
			exitWarning.doCommand();
			return;
		}
		
		MicroArrayLoaderValues2MultipleStorages loader = null;
		
		try 
		{
			loader = new MicroArrayLoaderValues2MultipleStorages( refGeneralManager );
			
			loader.setFileName( sFileName );
			loader.setTokenPattern( sTokenPattern );
			loader.setTargetSet( useSet );
			loader.setStartParsingStopParsingAtLine( iStartPareseFileAtLine,
					iStopPareseFileAtLine );
			
			loader.loadData();
			
			
		} //try
		catch ( Exception e ) 
		{
			String errorMsg = "Could not load data via MicroArrayLoaderValues2MultipleStorages, error during loading! file=["+
				sFileName + "] tokens:[" +
				sTokenPattern + "]  targetSet(s)=[" +
				iTargetSetId + "])";
			
			refGeneralManager.getSingelton().logMsg(
					errorMsg,
					LoggerType.ERROR_ONLY );
			
			CmdWindowPopupInfo exitWarning = new CmdWindowPopupInfo("");
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
		return CommandType.DATASET_LOAD; 
	}
	

}
