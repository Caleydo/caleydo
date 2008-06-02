package org.caleydo.core.command.data.parser;

import java.util.StringTokenizer;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACommand;
import org.caleydo.core.data.mapping.EGenomeMappingDataType;
import org.caleydo.core.data.mapping.EGenomeMappingType;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.data.IGenomeIdManager;
import org.caleydo.core.parser.ascii.lookuptable.LookupTableLoaderProxy;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.system.StringConversionTool;


/**
 * Command, load lookup table from file using one delimiter and a target Collection.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 * @see org.caleydo.core.data.collection.ISet
 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage
 */
public class CmdLoadFileLookupTable 
extends ACommand {

	public static final String sCommaSeperatedFileExtension = ".csv";
	
	protected String sFileName;
	
	protected String sLookupTableType;
	
	/**
	 * Special cases for creating reverse map and
	 * using internal LUTs.
	 * Valid values are: LUT|LUT_2
	 *                   REVERSE
	 */
	protected String sLookupTableOptions;
	
	/**
	 * Define type of lookup table to be created.
	 * 
	 * @see org.caleydo.core.data.mapping.EGenomeIdType
	 */
	protected String sLUT_Target;
	
	/**
	 * Default is 32, because gpr files have a header of that size!
	 * 
	 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage#iStartParsingAtLine
	 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage#getStartParsingAtLine()
	 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage#setStartParsingStopParsingAtLine(int, int)
	 */
	protected int iStartPareseFileAtLine = 0;
	
	/**
	 * Default is -1 indicateing read till end of file.
	 * 
	 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage#iStopParsingAtLine
	 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage#getStopParsingAtLine()
	 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage#setStartParsingStopParsingAtLine(int, int)
	 */
	protected int iStopPareseFileAtLine = -1;
	
	protected int iTargetSetId;
	
	protected boolean bCreateReverseMap = false;
	
	/**
	 * Boolean indicates if one column of the mapping needs
	 * to be resolved. Resolving means replacing codes by internal IDs. 
	 */	
	protected boolean bResolveCodeMappingUsingCodeToId_LUT_1 = false;
	protected boolean bResolveCodeMappingUsingCodeToId_LUT_2 = false;
	
	/**
	 * Boolean indicates if both columns of the mapping needs
	 * to be resolved. Resolving means replacing codes by internal IDs. 
	 */
	protected boolean bResolveCodeMappingUsingCodeToId_LUT_BOTH = false;
	
	/**
	 * Variable contains the lookup table types 
	 * that are needed to resolve mapping tables that 
	 * contain codes instead of internal IDs.
	 */
	protected String sCodeResolvingLUTTypes;
	
	protected String sCodeResolvingLUTMappingType_1;
	
	protected String sCodeResolvingLUTMappingType_2;
	
	
	/**
	 * Constructor.
	 * 
	 * @param generalManager
	 * @param refCommandManager
	 * @param refCommandQueueSaxType
	 */
	public CmdLoadFileLookupTable( 
			final IGeneralManager generalManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		super(-1,
				generalManager,
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
		
		while (tokenizer.hasMoreTokens())
		{
			sLookupTableOptions = tokenizer.nextToken();
		
			if (sLookupTableOptions.equals("REVERSE"))
			{
				bCreateReverseMap = true;
			}
			else if (sLookupTableOptions.equals("LUT_1"))
			{
				sCodeResolvingLUTTypes = refParameterHandler.getValueString( 
						CommandQueueSaxType.TAG_ATTRIBUTE4.getXmlKey());
				
				tokenizer = new StringTokenizer(sCodeResolvingLUTTypes,
						IGeneralManager.sDelimiter_Parser_DataItems);
				
				sCodeResolvingLUTMappingType_1 = tokenizer.nextToken();
				
				bResolveCodeMappingUsingCodeToId_LUT_1 = true;
			}
			else if (sLookupTableOptions.equals("LUT_2"))
			{
				sCodeResolvingLUTTypes = refParameterHandler.getValueString( 
						CommandQueueSaxType.TAG_ATTRIBUTE4.getXmlKey());
				
				tokenizer = new StringTokenizer(sCodeResolvingLUTTypes,
						IGeneralManager.sDelimiter_Parser_DataItems);
				
				sCodeResolvingLUTMappingType_2 = tokenizer.nextToken();
				
				bResolveCodeMappingUsingCodeToId_LUT_2 = true;
			}
			else if (sLookupTableOptions.equals("LUT_BOTH"))
			{
				sCodeResolvingLUTTypes = refParameterHandler.getValueString( 
						CommandQueueSaxType.TAG_ATTRIBUTE4.getXmlKey());
				
				tokenizer = new StringTokenizer(sCodeResolvingLUTTypes,
						IGeneralManager.sDelimiter_Parser_DataItems);
				
				sCodeResolvingLUTMappingType_1 = tokenizer.nextToken();
				sCodeResolvingLUTMappingType_2 = tokenizer.nextToken();
				
				bResolveCodeMappingUsingCodeToId_LUT_BOTH = true;
			}
		}
		
		this.sLUT_Target =	refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey());
		
		this.iTargetSetId =	StringConversionTool.convertStringToInt(
				refParameterHandler.getValueString(CommandQueueSaxType.TAG_UNIQUE_ID.getXmlKey()),
				-1 );
		
		int[] iArrayStartStop = StringConversionTool.convertStringToIntArrayVariableLength(
				refParameterHandler.getValueString(CommandQueueSaxType.TAG_ATTRIBUTE3.getXmlKey() ),
				" " );
		
		if ( iArrayStartStop.length > 0 ) 
		{
			iStartPareseFileAtLine = iArrayStartStop[0];
			
			if ( iArrayStartStop.length > 1 ) 
			{
				if (( iArrayStartStop[0] > iArrayStartStop[1] )&&
						( iArrayStartStop[1] != -1 )) {
//					generalManager.logMsg(
//							"CmdSystemLoadFileLookupTable ignore stop index=(" + 
//							iArrayStartStop[1]  + 
//							"), because it is smaller than start index (" + 
//							iArrayStartStop[0] + ") !",
//							LoggerType.STATUS );
					return;
				}
				iStopPareseFileAtLine = iArrayStartStop[1];
			} // if ( iArrayStartStop.length > 0 ) 
		} // if ( iArrayStartStop.length > 0 ) 
	}
	

	/**
	 * Load data from file using a token pattern.
	 * 
	 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage#loadData()
	 * 
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException {
		
//		generalManager.logMsg(
//	    		"load file via importer... ([" +
//				sFileName + "]",
//				LoggerType.STATUS );
		
//		generalManager.logMsg(
//	    		"load file via importer: [LUT-tpye:[" +
//				sLookupTableType + "]  cast=[" + 
//				iTargetSetId + "])",
//				LoggerType.VERBOSE );
		
		LookupTableLoaderProxy loader = null;
		
		IGenomeIdManager refGenomeIdManager = 
			generalManager.getGenomeIdManager();
		
		try 
		{
			EGenomeMappingType lut_genome_type = 
				EGenomeMappingType.valueOf( sLookupTableType );
			
			EGenomeMappingDataType genomeDataType;
			
			genomeDataType = lut_genome_type.getDataMapppingType();

			
			// FIXME: find solution for lut resolve process
			if (bResolveCodeMappingUsingCodeToId_LUT_BOTH)
			{
				if (genomeDataType == EGenomeMappingDataType.INT2INT)
				{
					genomeDataType = EGenomeMappingDataType.STRING2STRING;
				}
				else if (genomeDataType == EGenomeMappingDataType.MULTI_INT2INT)
				{
					genomeDataType = EGenomeMappingDataType.MULTI_STRING2STRING;
				}
			}
			else if (bResolveCodeMappingUsingCodeToId_LUT_1)
			{	
				if (genomeDataType == EGenomeMappingDataType.INT2STRING)
				{
					genomeDataType = EGenomeMappingDataType.STRING2STRING;
				}
				else if (genomeDataType == EGenomeMappingDataType.INT2INT)
				{
					genomeDataType = EGenomeMappingDataType.STRING2INT;
				}
			}
			else if (bResolveCodeMappingUsingCodeToId_LUT_2)
			{	
				if (genomeDataType == EGenomeMappingDataType.STRING2INT)
				{
					genomeDataType = EGenomeMappingDataType.STRING2STRING;
				}
				else if (genomeDataType == EGenomeMappingDataType.INT2INT)
				{
					genomeDataType = EGenomeMappingDataType.INT2STRING;
				}
			}
			loader = new LookupTableLoaderProxy( 
					generalManager, 
					sFileName,
					lut_genome_type,
					genomeDataType,
					IGeneralManager.bEnableMultipelThreads );	
			
			loader.setTokenSeperator(sLUT_Target);
			
//			if ( sFileName.endsWith( sCommaSeperatedFileExtension )) {
//				loader.setTokenSeperator( IGeneralManager.sDelimiter_Parser_DataType );
//			}
			
			loader.setStartParsingStopParsingAtLine( iStartPareseFileAtLine, iStopPareseFileAtLine );
			
			refGenomeIdManager.buildLUT_startEditing( lut_genome_type );
			loader.loadData();
			refGenomeIdManager.buildLUT_stopEditing( lut_genome_type );
			
			
			/* --- Map codes in LUT to IDs --- */
			if (bResolveCodeMappingUsingCodeToId_LUT_1 || 
					bResolveCodeMappingUsingCodeToId_LUT_2 || bResolveCodeMappingUsingCodeToId_LUT_BOTH)
			{
				EGenomeMappingType genomeMappingLUT_1 = null;
				EGenomeMappingType genomeMappingLUT_2 = null;
				
				if(bResolveCodeMappingUsingCodeToId_LUT_1 || bResolveCodeMappingUsingCodeToId_LUT_BOTH)
				{
					genomeMappingLUT_1 = EGenomeMappingType.valueOf( sCodeResolvingLUTMappingType_1 );
				}
				
				if(bResolveCodeMappingUsingCodeToId_LUT_2 || bResolveCodeMappingUsingCodeToId_LUT_BOTH)
				{
					genomeMappingLUT_2 = EGenomeMappingType.valueOf( sCodeResolvingLUTMappingType_2 );
				}
				
				EGenomeMappingDataType targetMappingDataType = genomeDataType;
				
				// Reset genomeDataType to real type
				genomeDataType = lut_genome_type.getDataMapppingType();
				
				if (genomeDataType == EGenomeMappingDataType.MULTI_INT2INT)
				{
					loader.createCodeResolvedMultiMapFromMultiMapString(
							generalManager, 
							lut_genome_type, 
							genomeMappingLUT_1, 
							genomeMappingLUT_2);
				}
				else 
				{
					loader.createCodeResolvedMapFromMap(
							generalManager, 
							lut_genome_type, 
							genomeMappingLUT_1, 
							genomeMappingLUT_2,
							targetMappingDataType);					
				}				
			}
			
			/* ---  create reverse Map ... --- */
			if (bCreateReverseMap)
			{
				// Concatenate genome id type target and origin type in swapped 
				// order to determine reverse genome mapping type.
				EGenomeMappingType lut_genome_reverse_type = EGenomeMappingType.valueOf(
					lut_genome_type.getTypeTarget().toString()
					+ "_2_"
					+ lut_genome_type.getTypeOrigin().toString());
				
				if (lut_genome_reverse_type.equals(EGenomeMappingType.NON_MAPPING))
				{
					assert false : "Reverse mapping: type=" + 
					lut_genome_reverse_type.toString() + " has no valid reverse type.";
					
					throw new RuntimeException("Reverse mapping: type=" +
							lut_genome_type.toString() +
							" has no valid reverse type.");
				} //if (lut_genome_reverse_type.equals(EGenomeMappingType.NON_MAPPING))
				
				if ( lut_genome_reverse_type.isMultiMap() ) 
				{
					switch (lut_genome_reverse_type.getTypeOrigin().getStorageType()) 
					{
					case INT:
						loader.createReverseMultiMapFromMultiMapInt(
								generalManager, 
								lut_genome_type, 
								lut_genome_reverse_type);
						break;
						
					case STRING:
						loader.createReverseMultiMapFromMultiMapString(
								generalManager, 
								lut_genome_type, 
								lut_genome_reverse_type);
						break;
						
					default:
						assert false : "Reverse mapping not suported yet for this type=" + 
							lut_genome_reverse_type.toString();
					
						throw new RuntimeException("Reverse mapping not suported yet for this type=" +
								lut_genome_reverse_type.toString());
					
					} //switch (lut_genome_reverse_type.getTypeOrigin().getStorageType()) 
					
				} //if ( lut_genome_reverse_type.isMultiMap() ) 
				else
				{			
					loader.createReverseMapFromMap(generalManager, 
							lut_genome_type, 
							lut_genome_reverse_type);				
					
				} //if ( lut_genome_reverse_type.isMultiMap() ) {...} else {
				
			} //if (bCreateReverseMap)
			
			commandManager.runDoCommand(this);
			
		} //try
		catch ( Exception e ) 
		{
			String errorMsg = "Could not load data via LookupTableLoaderProxy, error during loading! file=["+
				sFileName + "] LUT-type:[" +
				sLookupTableType + "]  targetSet(s)=[" +
				iTargetSetId + "]) CmdSystemLoadFileLookupTable \n   error-message=" + e.getMessage();
			
			e.printStackTrace();
			
//			generalManager.logMsg(
//					errorMsg,
//					LoggerType.ERROR );
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
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException {
		commandManager.runUndoCommand(this);
	}
}
