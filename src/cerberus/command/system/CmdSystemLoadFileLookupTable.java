/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.system;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.StringTokenizer;


import cerberus.command.ICommand;
import cerberus.command.CommandType;
import cerberus.command.base.ACommand;
import cerberus.command.window.CmdWindowPopupInfo;
//import cerberus.command.window.CmdWindowPopupInfo;
import cerberus.data.mapping.GenomeMappingType;
import cerberus.data.mapping.GenomeMappingDataType;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.command.factory.CommandFactory;
import cerberus.manager.data.IGenomeIdManager;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.util.system.StringConversionTool;
import cerberus.xml.parser.command.CommandQueueSaxType;
import cerberus.xml.parser.handler.importer.ascii.LookupTableLoaderProxy;
import cerberus.xml.parser.parameter.IParameterHandler;

import cerberus.data.collection.ISet;
import cerberus.base.map.MultiHashArrayMap;


/**
 * Command, load lookup table from file using one delimiter and a target Collection.
 * 
 * @author Michael Kalkusch
 *
 * @see cerberus.data.collection.ISet
 * @see cerberus.xml.parser.handler.importer.ascii.MicroArrayLoader1Storage
 */
public class CmdSystemLoadFileLookupTable 
extends ACommand
implements ICommand {

	private final IGeneralManager refGeneralManager;
	
	protected String sFileName;
	
	protected String sLookupTableType;
	
	protected String sLookupTableDataType;
	
	/**
	 * Define type of lookup table to be created.
	 * 
	 * @see cerberus.data.mapping.GenomeIdType
	 */
	protected String sLUT_Target;
	
	/**
	 * Default is 32, because gpr files have a header of that size!
	 * 
	 * @see cerberus.xml.parser.handler.importer.ascii.MicroArrayLoader1Storage#iStartParsingAtLine
	 * @see cerberus.xml.parser.handler.importer.ascii.MicroArrayLoader1Storage#getStartParsingAtLine()
	 * @see cerberus.xml.parser.handler.importer.ascii.MicroArrayLoader1Storage#setStartParsingStopParsingAtLine(int, int)
	 */
	protected int iStartPareseFileAtLine = 0;
	
	/**
	 * Default is -1 indicateing read till end of file.
	 * 
	 * @see cerberus.xml.parser.handler.importer.ascii.MicroArrayLoader1Storage#iStopParsingAtLine
	 * @see cerberus.xml.parser.handler.importer.ascii.MicroArrayLoader1Storage#getStopParsingAtLine()
	 * @see cerberus.xml.parser.handler.importer.ascii.MicroArrayLoader1Storage#setStartParsingStopParsingAtLine(int, int)
	 */
	protected int iStopPareseFileAtLine = -1;
	
	protected int iTargetSetId;
	
	
	public CmdSystemLoadFileLookupTable( IGeneralManager refGeneralManager,
			final IParameterHandler phAttributes ) {
		
		super();
		
		this.refGeneralManager = refGeneralManager;		
		
		this.setId( phAttributes.getValueInt( 
				CommandQueueSaxType.TAG_CMD_ID.getXmlKey()) );
	
		this.sFileName = phAttributes.getValueString( 
				CommandQueueSaxType.TAG_DETAIL.getXmlKey() );
		
		String sLUT_info =  phAttributes.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() );

		StringTokenizer tokenizer = new StringTokenizer( sLUT_info,
				CommandFactory.sDelimiter_Parser_DataItems);
		
		sLookupTableType = tokenizer.nextToken();
		sLookupTableDataType = tokenizer.nextToken();
		
		this.sLUT_Target =	phAttributes.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey());
		
		this.iTargetSetId =	StringConversionTool.convertStringToInt(
				phAttributes.getValueString( 
						CommandQueueSaxType.TAG_TARGET_ID.getXmlKey()),
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
				if ( iArrayStartStop[0] > iArrayStartStop[1] ) {
					refGeneralManager.getSingelton().getLoggerManager().logMsg(
							"CmdSystemLoadFileLookupTable ignore stop index=(" + 
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
	

	/**
	 * Load data from file using a token pattern.
	 * 
	 * @see cerberus.xml.parser.handler.importer.ascii.MicroArrayLoader1Storage#loadData()
	 * 
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws CerberusRuntimeException {
		
		refGeneralManager.getSingelton().logMsg(
	    		"load file via importer... ([" +
				sFileName + "] LUT-tpye:[" +
				sLookupTableType + "]  targetSet(s)=[" +
				iTargetSetId + "])",
				LoggerType.STATUS );
		
//		ISet useSet = refGeneralManager.getSingelton().getSetManager(
//				).getItemSet( iTargetSetId );
//		
//		if ( useSet == null ) {
//			String errorMsg = "Could not load data via MicroArrayLoader1Storage, target Set is not valid! file=["+
//			sFileName + "] LUT-tpye:[" +
//			sLookupTableType + "]  targetSet(s)=[" +
//			iTargetSetId + "])";
//			
//			refGeneralManager.getSingelton().logMsg(
//					errorMsg,
//					LoggerType.ERROR_ONLY );
//			
//			CmdWindowPopupInfo exitWarning = new CmdWindowPopupInfo("");
//			exitWarning.setText("ERROR",errorMsg);
//			exitWarning.doCommand();
//			return;
//		}
		
		LookupTableLoaderProxy loader = null;
		
		IGenomeIdManager refGenomeIdManager = 
			refGeneralManager.getSingelton().getGenomeIdManager();
		
		
		try 
		{
			GenomeMappingType lut_genome_tpye = 
				GenomeMappingType.valueOf( sLookupTableType );
			
			GenomeMappingDataType genomeDataType = 
				GenomeMappingDataType.valueOf( sLookupTableDataType );
			
			loader = new LookupTableLoaderProxy( 
					refGeneralManager, 
					sFileName,
					lut_genome_tpye,
					genomeDataType );					
			
			//loader.setFileName( sFileName );
			//loader.setTargetSet( useSet );
			loader.setStartParsingStopParsingAtLine( iStartPareseFileAtLine,
					iStopPareseFileAtLine );
			//loader.setTokenSeperator( ";" );
			
			loader.loadData();
			
			
		} //try
		catch ( Exception e ) 
		{
			String errorMsg = "Could not load data via MicroArrayLoader1Storage, error during loading! file=["+
				sFileName + "] LUT-tpye:[" +
				sLookupTableType + "]  targetSet(s)=[" +
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
