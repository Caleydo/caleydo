/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.system;

//import java.util.HashMap;
//import java.util.Iterator;
import java.util.StringTokenizer;


import cerberus.command.CommandQueueSaxType;
import cerberus.command.ICommand;
import cerberus.command.base.ACommand;
import cerberus.command.window.CmdWindowPopupInfo;
//import cerberus.command.window.CmdWindowPopupInfo;
import cerberus.data.mapping.GenomeMappingType;
import cerberus.data.mapping.GenomeMappingDataType;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.data.IGenomeIdManager;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.util.system.StringConversionTool;
import cerberus.xml.parser.handler.importer.ascii.LookupTableLoaderProxy;
import cerberus.xml.parser.parameter.IParameterHandler;

//import cerberus.data.collection.ISet;
//import cerberus.base.map.MultiHashArrayMap;


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

	public static final String sCommaSeperatedFileExtension = ".csv";
	
	protected String sFileName;
	
	protected String sLookupTableType;
	
	protected String sLookupTableDataType;
	
	protected String sLookupTableTypeOptionalTarget;
	
	protected String sLookupTableTargetType;
	
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
	
	protected boolean bCreateReverseMap = false;
	
	
	
	/**
	 * Constructor.
	 * 
	 * @param refGeneralManager
	 * @param refCommandManager
	 * @param refCommandQueueSaxType
	 */
	public CmdSystemLoadFileLookupTable( 
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		super(-1,
				refGeneralManager,
				refCommandManager,
				refCommandQueueSaxType);
		
		setCommandQueueSaxType(CommandQueueSaxType.LOAD_LOOKUP_TABLE_FILE);
	}
	
	
	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		super.setParameterHandler(refParameterHandler);
		
		this.setId( refParameterHandler.getValueInt( 
				CommandQueueSaxType.TAG_CMD_ID.getXmlKey()) );
	
		this.sFileName = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_DETAIL.getXmlKey() );
		
		String sLUT_info =  refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() );

		StringTokenizer tokenizer = new StringTokenizer( sLUT_info,
				IGeneralManager.sDelimiter_Parser_DataItems);
		
		sLookupTableType = tokenizer.nextToken();
		sLookupTableDataType = tokenizer.nextToken();
		
		if  (tokenizer.hasMoreTokens()) 
		{
			sLookupTableTargetType = tokenizer.nextToken();
			bCreateReverseMap = true;
		}
		
		if ( tokenizer.hasMoreTokens() )
		{
			sLookupTableTypeOptionalTarget = tokenizer.nextToken();
		}
		
		this.sLUT_Target =	refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey());
		
		this.iTargetSetId =	StringConversionTool.convertStringToInt(
				refParameterHandler.getValueString( 
						CommandQueueSaxType.TAG_TARGET_ID.getXmlKey()),
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
						( iArrayStartStop[1] != -1 )) {
					refGeneralManager.getSingelton().logMsg(
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
				sFileName + "]",
				LoggerType.STATUS );
		
		refGeneralManager.getSingelton().logMsg(
	    		"load file via importer: [LUT-tpye:[" +
				sLookupTableType + "]  cast=[" + 
				sLookupTableDataType + "] targetSet(s)=[" +
				iTargetSetId + "])",
				LoggerType.VERBOSE );
		

//			if ( ?? ) {
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
			GenomeMappingType lut_genome_type = 
				GenomeMappingType.valueOf( sLookupTableType );
			
			GenomeMappingDataType genomeDataType = 
				GenomeMappingDataType.valueOf( sLookupTableDataType );
			
			GenomeMappingType lut_genome_type_OptionalTarget = null;
			
			if ((sLookupTableTypeOptionalTarget != null)&&( sLookupTableTypeOptionalTarget.length() > 0 ))
			{
				lut_genome_type_OptionalTarget = GenomeMappingType.valueOf( sLookupTableTypeOptionalTarget );
			}
			
//			refGenomeIdManager.createMapByType( lut_genome_type,
//					genomeDataType,
//					1000 );
			
			loader = new LookupTableLoaderProxy( 
					refGeneralManager, 
					sFileName,
					lut_genome_type,
					genomeDataType,
					lut_genome_type_OptionalTarget,
					IGeneralManager.bEnableMultipelThreads );	
			
			loader.setTokenSeperator(sLUT_Target);
			
			if ( sFileName.endsWith( sCommaSeperatedFileExtension )) {
				loader.setTokenSeperator( IGeneralManager.sDelimiter_Parser_DataType );
			}
			
//			loader.setHashMap(
//					refGenomeIdManager.getMapByType(lut_genome_type),
//					lut_genome_type );
			
			loader.setStartParsingStopParsingAtLine( iStartPareseFileAtLine,
					iStopPareseFileAtLine );
			
			refGenomeIdManager.buildLUT_startEditing( lut_genome_type );
			loader.loadData();
			refGenomeIdManager.buildLUT_stopEditing( lut_genome_type );
			
			/* ---  create reverse Map ... --- */
			if (bCreateReverseMap)
			{
				GenomeMappingType lut_genome_reverse_type = 
					GenomeMappingType.valueOf(sLookupTableTargetType);
				
				if ( lut_genome_reverse_type.isMultiMap() ) 
				{
					switch (lut_genome_reverse_type.getTypeOrigin().getStorageType()) 
					{
					case INT:
						LookupTableLoaderProxy.createReverseMultiMapFromMultiMapInt(
								refGeneralManager, 
								lut_genome_type, 
								lut_genome_reverse_type);
						break;
						
					case STRING:
						LookupTableLoaderProxy.createReverseMultiMapFromMultiMapInt(
								refGeneralManager, 
								lut_genome_type, 
								lut_genome_reverse_type);
						break;
						
					default:
						assert false : "unsupported type! " + 
							lut_genome_reverse_type.toString() + " " + 
							lut_genome_reverse_type.getTypeOrigin().getStorageType().toString();
					
					} //switch (lut_genome_reverse_type.getTypeOrigin().getStorageType()) 
					
				} //if ( lut_genome_reverse_type.isMultiMap() ) 
				else
				{
					assert false : "lut_genome_reverse_type= " + 
					lut_genome_reverse_type.toString() + " is not a Multimap! ";
				
				} //if ( lut_genome_reverse_type.isMultiMap() ) {...} else {
				
			} //if (bCreateReverseMap)
			
			refCommandManager.runDoCommand(this);
			
		} //try
		catch ( Exception e ) 
		{
			String errorMsg = "Could not load data via LookupTableLoaderProxy, error during loading! file=["+
				sFileName + "] LUT-type:[" +
				sLookupTableType + "]  targetSet(s)=[" +
				iTargetSetId + "]) CmdSystemLoadFileLookupTable \n   error-message=" + e.getMessage();
			
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
		
		
	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CerberusRuntimeException {
		refCommandManager.runUndoCommand(this);
	}
}
