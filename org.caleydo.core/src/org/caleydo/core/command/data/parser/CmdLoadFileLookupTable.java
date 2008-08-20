package org.caleydo.core.command.data.parser;

import java.util.StringTokenizer;
import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACommand;
import org.caleydo.core.data.mapping.EMappingDataType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.specialized.genome.IGenomeIdManager;
import org.caleydo.core.parser.ascii.lookuptable.LookupTableLoaderProxy;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.system.StringConversionTool;

/**
 * Command loads lookup table from file using one delimiter and a target
 * Collection.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @see org.caleydo.core.data.collection.ISet
 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage
 */
public class CmdLoadFileLookupTable
	extends ACommand
{

	protected String sFileName;

	private String sLookupTableInfo;

	protected String sLookupTableType;

	/**
	 * Special cases for creating reverse map and using internal LUTs. Valid
	 * values are: LUT|LUT_2 REVERSE
	 */
	protected String sLookupTableOptions;

	/**
	 * Define type of lookup table to be created.
	 * 
	 * @see org.caleydo.core.data.mapping.EIDType
	 */
	protected String sLookupTableDelimiter;

	protected int iStartPareseFileAtLine = 0;

	/**
	 * Default is -1 indicating read till end of file.
	 * 
	 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage#iStopParsingAtLine
	 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage#getStopParsingAtLine()
	 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage#setStartParsingStopParsingAtLine(int,
	 *      int)
	 */
	protected int iStopParseFileAtLine = -1;

	protected boolean bCreateReverseMap = false;

	/**
	 * Boolean indicates if one column of the mapping needs to be resolved.
	 * Resolving means replacing codes by internal IDs.
	 */
	protected boolean bResolveCodeMappingUsingCodeToId_LUT_1 = false;

	protected boolean bResolveCodeMappingUsingCodeToId_LUT_2 = false;

	/**
	 * Boolean indicates if both columns of the mapping needs to be resolved.
	 * Resolving means replacing codes by internal IDs.
	 */
	protected boolean bResolveCodeMappingUsingCodeToId_LUT_BOTH = false;

	/**
	 * Variable contains the lookup table types that are needed to resolve
	 * mapping tables that contain codes instead of internal IDs.
	 */
	protected String sCodeResolvingLUTTypes;

	protected String sCodeResolvingLUTMappingType_1;

	protected String sCodeResolvingLUTMappingType_2;

	/**
	 * Constructor.
	 * 
	 * @param cmdType
	 */
	public CmdLoadFileLookupTable(final ECommandType cmdType)
	{
		super(cmdType);
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		sFileName = parameterHandler
				.getValueString(ECommandType.TAG_DETAIL.getXmlKey());

		sLookupTableInfo = parameterHandler.getValueString(ECommandType.TAG_ATTRIBUTE1
				.getXmlKey());

		sLookupTableDelimiter = parameterHandler
				.getValueString(ECommandType.TAG_ATTRIBUTE2.getXmlKey());

		int[] iArrayStartStop = StringConversionTool.convertStringToIntArrayVariableLength(
				parameterHandler
						.getValueString(ECommandType.TAG_ATTRIBUTE3.getXmlKey()), " ");

		iStartPareseFileAtLine = iArrayStartStop[0];
		iStopParseFileAtLine = iArrayStartStop[1];

		sCodeResolvingLUTTypes = parameterHandler
				.getValueString(ECommandType.TAG_ATTRIBUTE4.getXmlKey());

		extractParameters();
	}

	public void setAttributes(final String sFileName, final int iStartParseFileAtLine,
			final int iStopParseFileAtLine, final String sLookupTableInfo,
			final String sLookupTableDelimiter, final String sCodeResolvingLUTTypes)
	{

		this.sFileName = sFileName;
		this.iStartPareseFileAtLine = iStartParseFileAtLine;
		this.iStopParseFileAtLine = iStopParseFileAtLine;
		this.sLookupTableInfo = sLookupTableInfo;
		this.sLookupTableDelimiter = sLookupTableDelimiter;
		this.sCodeResolvingLUTTypes = sCodeResolvingLUTTypes;

		extractParameters();
	}

	private void extractParameters()
	{

		StringTokenizer tokenizer = new StringTokenizer(sLookupTableInfo,
				IGeneralManager.sDelimiter_Parser_DataItems);

		sLookupTableType = tokenizer.nextToken();

		while (tokenizer.hasMoreTokens())
		{
			sLookupTableOptions = tokenizer.nextToken();

			if (sLookupTableOptions.equals("REVERSE"))
			{
				bCreateReverseMap = true;
			}
			else if (sLookupTableOptions.equals("LUT_1")
					|| sLookupTableOptions.equals("LUT_2")
					|| sLookupTableOptions.equals("LUT_BOTH"))
			{
				tokenizer = new StringTokenizer(sCodeResolvingLUTTypes,
						IGeneralManager.sDelimiter_Parser_DataItems);

				if (sLookupTableOptions.equals("LUT_1"))
				{
					sCodeResolvingLUTMappingType_1 = tokenizer.nextToken();
					bResolveCodeMappingUsingCodeToId_LUT_1 = true;
				}
				else if (sLookupTableOptions.equals("LUT_2"))
				{
					sCodeResolvingLUTMappingType_2 = tokenizer.nextToken();
					bResolveCodeMappingUsingCodeToId_LUT_2 = true;
				}
				else if (sLookupTableOptions.equals("LUT_BOTH"))
				{
					sCodeResolvingLUTMappingType_1 = tokenizer.nextToken();
					sCodeResolvingLUTMappingType_2 = tokenizer.nextToken();

					bResolveCodeMappingUsingCodeToId_LUT_BOTH = true;
				}
			}
		}
	}

	/**
	 * Load data from file using a token pattern.
	 * 
	 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage#loadData()
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException
	{

		// generalManager.logMsg(
		// "load file via importer... ([" +
		// sFileName + "]",
		// LoggerType.STATUS );

		// generalManager.logMsg(
		// "load file via importer: [LUT-tpye:[" +
		// sLookupTableType + "]  cast=[" +
		// iTargetSetId + "])",
		// LoggerType.VERBOSE );

		LookupTableLoaderProxy loader = null;

		IGenomeIdManager genomeIdManager = generalManager.getGenomeIdManager();

		try
		{
			EMappingType lut_genome_type = EMappingType.valueOf(sLookupTableType);

			EMappingDataType genomeDataType;

			genomeDataType = lut_genome_type.getDataMapppingType();

			// FIXME: find solution for lut resolve process
			if (bResolveCodeMappingUsingCodeToId_LUT_BOTH)
			{
				if (genomeDataType == EMappingDataType.INT2INT)
				{
					genomeDataType = EMappingDataType.STRING2STRING;
				}
				else if (genomeDataType == EMappingDataType.MULTI_INT2INT)
				{
					genomeDataType = EMappingDataType.MULTI_STRING2STRING;
				}
			}
			else if (bResolveCodeMappingUsingCodeToId_LUT_1)
			{
				if (genomeDataType == EMappingDataType.INT2STRING)
				{
					genomeDataType = EMappingDataType.STRING2STRING;
				}
				else if (genomeDataType == EMappingDataType.INT2INT)
				{
					genomeDataType = EMappingDataType.STRING2INT;
				}
			}
			else if (bResolveCodeMappingUsingCodeToId_LUT_2)
			{
				if (genomeDataType == EMappingDataType.STRING2INT)
				{
					genomeDataType = EMappingDataType.STRING2STRING;
				}
				else if (genomeDataType == EMappingDataType.INT2INT)
				{
					genomeDataType = EMappingDataType.INT2STRING;
				}
			}
			loader = new LookupTableLoaderProxy(generalManager, sFileName, lut_genome_type,
					genomeDataType);

			loader.setTokenSeperator(sLookupTableDelimiter);

			// if ( sFileName.endsWith( sCommaSeperatedFileExtension )) {
			// loader.setTokenSeperator(
			// IGeneralManager.sDelimiter_Parser_DataType );
			// }

			loader.setStartParsingStopParsingAtLine(iStartPareseFileAtLine,
					iStopParseFileAtLine);

			genomeIdManager.buildLUT_startEditing(lut_genome_type);
			loader.loadData();
			genomeIdManager.buildLUT_stopEditing(lut_genome_type);

			/* --- Map codes in LUT to IDs --- */
			if (bResolveCodeMappingUsingCodeToId_LUT_1
					|| bResolveCodeMappingUsingCodeToId_LUT_2
					|| bResolveCodeMappingUsingCodeToId_LUT_BOTH)
			{
				EMappingType genomeMappingLUT_1 = null;
				EMappingType genomeMappingLUT_2 = null;

				if (bResolveCodeMappingUsingCodeToId_LUT_1
						|| bResolveCodeMappingUsingCodeToId_LUT_BOTH)
				{
					genomeMappingLUT_1 = EMappingType
							.valueOf(sCodeResolvingLUTMappingType_1);
				}

				if (bResolveCodeMappingUsingCodeToId_LUT_2
						|| bResolveCodeMappingUsingCodeToId_LUT_BOTH)
				{
					genomeMappingLUT_2 = EMappingType
							.valueOf(sCodeResolvingLUTMappingType_2);
				}

				EMappingDataType targetMappingDataType = genomeDataType;

				// Reset genomeDataType to real type
				genomeDataType = lut_genome_type.getDataMapppingType();

				if (genomeDataType == EMappingDataType.MULTI_INT2INT)
				{
					loader.createCodeResolvedMultiMapFromMultiMapString(generalManager,
							lut_genome_type, genomeMappingLUT_1, genomeMappingLUT_2);
				}
				else
				{
					loader.createCodeResolvedMapFromMap(generalManager, lut_genome_type,
							genomeMappingLUT_1, genomeMappingLUT_2, targetMappingDataType);
				}
			}

			/* --- create reverse Map ... --- */
			if (bCreateReverseMap)
			{
				// Concatenate genome id type target and origin type in swapped
				// order to determine reverse genome mapping type.
				EMappingType lut_genome_reverse_type = EMappingType
						.valueOf(lut_genome_type.getTypeTarget().toString() + "_2_"
								+ lut_genome_type.getTypeOrigin().toString());

				// if
				//(lut_genome_reverse_type.equals(EMappingType.NON_MAPPING
				// ))
				// {
				// assert false : "Reverse mapping: type=" +
				// lut_genome_reverse_type.toString() +
				// " has no valid reverse type.";
				//					
				// throw new RuntimeException("Reverse mapping: type=" +
				// lut_genome_type.toString() +
				// " has no valid reverse type.");
				// } //if
				//(lut_genome_reverse_type.equals(EMappingType.NON_MAPPING
				// ))

				if (lut_genome_reverse_type.isMultiMap())
				{
					switch (lut_genome_reverse_type.getTypeOrigin().getStorageType())
					{
						case INT:
							loader.createReverseMultiMapFromMultiMapInt(generalManager,
									lut_genome_type, lut_genome_reverse_type);
							break;

						case STRING:
							loader.createReverseMultiMapFromMultiMapString(generalManager,
									lut_genome_type, lut_genome_reverse_type);
							break;

						default:
							assert false : "Reverse mapping not suported yet for this type="
									+ lut_genome_reverse_type.toString();

							throw new RuntimeException(
									"Reverse mapping not suported yet for this type="
											+ lut_genome_reverse_type.toString());

					} // switch
					// (lut_genome_reverse_type.getTypeOrigin().getStorageType
					// ())

				} // if ( lut_genome_reverse_type.isMultiMap() )
				else
				{
					loader.createReverseMapFromMap(generalManager, lut_genome_type,
							lut_genome_reverse_type);

				} // if ( lut_genome_reverse_type.isMultiMap() ) {...} else {

			} // if (bCreateReverseMap)

			commandManager.runDoCommand(this);

		} // try
		catch (Exception e)
		{
			e.printStackTrace();

		} // catch
		finally
		{
			if (loader != null)
			{
				loader.destroy();
				loader = null;
			}
		} // finally
	}

	@Override
	public void undoCommand() throws CaleydoRuntimeException
	{

		commandManager.runUndoCommand(this);
	}
}
