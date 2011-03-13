package org.caleydo.core.command.data.parser;

import java.util.Map;
import java.util.StringTokenizer;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.mapping.IDMappingManager;
import org.caleydo.core.manager.mapping.MappingType;
import org.caleydo.core.manager.specialized.Organism;
import org.caleydo.core.parser.ascii.LookupTableLoader;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.system.StringConversionTool;

/**
 * Command loads lookup table from file using one delimiter and a target Collection.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdLoadFileLookupTable
	extends ACmdExternalAttributes {

	protected String fileName;

	private String sLookupTableInfo;

	private IDType fromIDType;

	private IDType toIDType;

	/**
	 * Special cases for creating reverse map and using internal LUTs. Valid values are: LUT|LUT_2 REVERSE
	 */
	private String sLookupTableOptions;

	/**
	 * Define type of lookup table to be created.
	 * 
	 * @see org.caleydo.core.data.mapping.EIDType
	 */
	private String sLookupTableDelimiter;

	private int iStartPareseFileAtLine = 0;

	/**
	 * Default is -1 indicating read till end of file.
	 * 
	 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage#iStopParsingAtLine
	 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage#getStopParsingAtLine()
	 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage#setStartParsingStopParsingAtLine(int,
	 *      int)
	 */
	private int iStopParseFileAtLine = -1;

	private boolean bCreateReverseMap = false;

	/**
	 * Boolean indicates if one column of the mapping needs to be resolved. Resolving means replacing codes by
	 * internal IDs.
	 */
	private boolean bResolveCodeMappingUsingCodeToId_LUT = false;

	/**
	 * Variable contains the lookup table types that are needed to resolve mapping tables that contain codes
	 * instead of internal IDs.
	 */
	private String sCodeResolvingLUTTypes;

	private String sCodeResolvingLUTMappingType;

	private boolean isMultiMap;

	/**
	 * Constructor.
	 * 
	 * @param cmdType
	 */
	public CmdLoadFileLookupTable(final ECommandType cmdType) {
		super(cmdType);
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler) {

		super.setParameterHandler(parameterHandler);

		fileName = detail;
		sLookupTableInfo = attrib1;
		sLookupTableDelimiter = attrib2;

		if (attrib3 != null) {
			int[] iArrayStartStop = StringConversionTool.convertStringToIntArray(attrib3, " ");

			if (iArrayStartStop.length == 2) {
				iStartPareseFileAtLine = iArrayStartStop[0];
				iStopParseFileAtLine = iArrayStartStop[1];
			}
		}

		sCodeResolvingLUTTypes = attrib4;

		isMultiMap = Boolean.parseBoolean(attrib5);

		extractParameters();
	}

	public void setAttributes(final String sFileName, final int iStartParseFileAtLine,
		final int iStopParseFileAtLine, final String sLookupTableInfo, final String sLookupTableDelimiter,
		final String sCodeResolvingLUTTypes) {

		this.iStartPareseFileAtLine = iStartParseFileAtLine;
		this.iStopParseFileAtLine = iStopParseFileAtLine;
		this.sLookupTableInfo = sLookupTableInfo;
		this.sLookupTableDelimiter = sLookupTableDelimiter;
		this.sCodeResolvingLUTTypes = sCodeResolvingLUTTypes;
		this.fileName = sFileName;

		extractParameters();
	}

	private void extractParameters() {

		StringTokenizer tokenizer =
			new StringTokenizer(sLookupTableInfo, GeneralManager.sDelimiter_Parser_DataItems);

		String mappingTypeString = tokenizer.nextToken();
		fromIDType = IDType.getIDType(mappingTypeString.substring(0, mappingTypeString.indexOf("_2_")));
		toIDType =
			IDType.getIDType(mappingTypeString.substring(mappingTypeString.indexOf("_2_") + 3,
				mappingTypeString.length()));

		while (tokenizer.hasMoreTokens()) {
			sLookupTableOptions = tokenizer.nextToken();

			if (sLookupTableOptions.equals("REVERSE")) {
				bCreateReverseMap = true;
			}
			else if (sLookupTableOptions.equals("LUT")) {
				tokenizer =
					new StringTokenizer(sCodeResolvingLUTTypes, GeneralManager.sDelimiter_Parser_DataItems);

				sCodeResolvingLUTMappingType = tokenizer.nextToken();

				bResolveCodeMappingUsingCodeToId_LUT = true;
			}
		}
	}

	/**
	 * Load data from file using a token pattern.
	 * 
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	@Override
	public void doCommand() {
		LookupTableLoader loader = null;

		if (fileName.contains("ORGANISM")) {
			Organism eOrganism = GeneralManager.get().getBasicInfo().getOrganism();
			this.fileName = fileName.replace("ORGANISM", eOrganism.toString());
		}

		// FIXME: Currently we do not have the ensembl mapping table for home sapiens
		if (fileName.contains("HOMO_SAPIENS") && fileName.contains("ENSEMBL"))
			return;
		
		IDMappingManager genomeIdManager = generalManager.getIDMappingManager();

		// Remove old lookuptable if it already exists
		// genomeIdManager.removeMapByType(EMappingType.valueOf(sLookupTableType));
	
		MappingType mappingType = genomeIdManager.createMap(fromIDType, toIDType, isMultiMap);

		if (bResolveCodeMappingUsingCodeToId_LUT) {

			IDType codeResolvedFromIDType =
				IDType.getIDType(sCodeResolvingLUTMappingType.substring(0,
					sCodeResolvingLUTMappingType.indexOf("_2_")));
			IDType codeResolvedToIDType =
				IDType.getIDType(sCodeResolvingLUTMappingType.substring(
					sCodeResolvingLUTMappingType.indexOf("_2_") + 3, sCodeResolvingLUTMappingType.length()));

			genomeIdManager.createCodeResolvedMap(mappingType, codeResolvedFromIDType, codeResolvedToIDType);
		}

		int iIndex = 0;
		if (fileName.equals("generate")) {

			Map<String, Integer> hashTmp = genomeIdManager.getMap(mappingType);
			for (Object refSeqIDObject : genomeIdManager.getMap(
				genomeIdManager.getMappingType("DAVID_2_REFSEQ_MRNA")).values()) {

				hashTmp.put((String) refSeqIDObject, iIndex++);
			}
		}
		else if (!fileName.equals("already_loaded")) {
			loader = new LookupTableLoader(fileName, mappingType);
			loader.setTokenSeperator(sLookupTableDelimiter);
			loader.setStartParsingStopParsingAtLine(iStartPareseFileAtLine, iStopParseFileAtLine);
			loader.loadData();
		}

		/* --- create reverse Map ... --- */
		if (bCreateReverseMap) {
			genomeIdManager.createReverseMap(mappingType);
		}
	}

	@Override
	public void undoCommand() {
	}
}
