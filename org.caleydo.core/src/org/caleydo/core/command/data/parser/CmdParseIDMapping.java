package org.caleydo.core.command.data.parser;

import java.util.Map;
import java.util.StringTokenizer;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.data.mapping.IDMappingManagerRegistry;
import org.caleydo.core.data.mapping.MappingType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.parser.ascii.AStringConverter;
import org.caleydo.core.parser.ascii.ATextParser;
import org.caleydo.core.parser.ascii.IDMappingParser;
import org.caleydo.core.parser.parameter.ParameterHandler;
import org.caleydo.core.specialized.Organism;
import org.caleydo.core.util.conversion.ConversionTools;

/**
 * Command loads lookup table from file using one delimiter and a target Collection.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdParseIDMapping
	extends ACmdExternalAttributes {

	protected String fileName;

	private String sLookupTableInfo;

	private IDCategory idCategory;

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
	private String delimiter;

	private int startParsingAtLine = 0;

	/**
	 * Default is -1 indicating read till end of file.
	 */
	private int stopParsingAtLine = -1;

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

	private AStringConverter stringConverter;


	public CmdParseIDMapping() {
		super(CommandType.PARSE_ID_MAPPING);
	}

	@Override
	public void setParameterHandler(final ParameterHandler parameterHandler) {

		super.setParameterHandler(parameterHandler);

		fileName = detail;
		sLookupTableInfo = attrib1;
		delimiter = attrib2;

		if (attrib3 != null) {
			int[] iArrayStartStop = ConversionTools.convertStringToIntArray(attrib3, " ");

			if (iArrayStartStop.length == 2) {
				startParsingAtLine = iArrayStartStop[0];
				stopParsingAtLine = iArrayStartStop[1];
			}
		}

		sCodeResolvingLUTTypes = attrib4;

		isMultiMap = Boolean.parseBoolean(attrib5);

		idCategory = IDCategory.getIDCategory(attrib6);

		extractParameters();
	}

	public void setAttributes(final String fileName, final int startParsingInLine,
		final int stopParsingInLine, final String sLookupTableInfo, final String delimiter,
		final String sCodeResolvingLUTTypes, final IDCategory idCategory) {

		this.startParsingAtLine = startParsingInLine;
		this.stopParsingAtLine = stopParsingInLine;
		this.sLookupTableInfo = sLookupTableInfo;
		this.delimiter = delimiter;
		this.sCodeResolvingLUTTypes = sCodeResolvingLUTTypes;
		this.fileName = fileName;
		this.idCategory = idCategory;
		extractParameters();
	}

	/**
	 * @param stringConverter
	 *            setter, see {@link #stringConverter}
	 */
	public void setStringConverter(AStringConverter stringConverter) {
		this.stringConverter = stringConverter;
	}

	private void extractParameters() {
		StringTokenizer tokenizer = new StringTokenizer(sLookupTableInfo, ATextParser.SPACE);

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
				tokenizer = new StringTokenizer(sCodeResolvingLUTTypes, ATextParser.SPACE);

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
		IDMappingParser idMappingParser = null;

		if (fileName.contains("ORGANISM")) {
			Organism eOrganism = GeneralManager.get().getBasicInfo().getOrganism();
			this.fileName = fileName.replace("ORGANISM", eOrganism.toString());
		}

		// FIXME: Currently we do not have the ensembl mapping table for home sapiens
		if (fileName.contains("HOMO_SAPIENS") && fileName.contains("ENSEMBL"))
			return;

		if (idCategory == null)
			throw new IllegalStateException("ID Category was null");
		IDMappingManager idMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(idCategory);
		MappingType mappingType = idMappingManager.createMap(fromIDType, toIDType, isMultiMap);

		if (bResolveCodeMappingUsingCodeToId_LUT) {

			IDType codeResolvedFromIDType =
				IDType.getIDType(sCodeResolvingLUTMappingType.substring(0,
					sCodeResolvingLUTMappingType.indexOf("_2_")));
			IDType codeResolvedToIDType =
				IDType.getIDType(sCodeResolvingLUTMappingType.substring(
					sCodeResolvingLUTMappingType.indexOf("_2_") + 3, sCodeResolvingLUTMappingType.length()));

			idMappingManager.createCodeResolvedMap(mappingType, codeResolvedFromIDType, codeResolvedToIDType);
		}

		int index = 0;
		if (fileName.equals("generate")) {

			Map<String, Integer> hashTmp = idMappingManager.getMap(mappingType);
			for (Object refSeqIDObject : idMappingManager.getMap(
				idMappingManager.getMappingType("DAVID_2_REFSEQ_MRNA")).values()) {

				hashTmp.put((String) refSeqIDObject, index++);
			}
		}
		else if (!fileName.equals("already_loaded")) {
			idMappingParser = new IDMappingParser(idCategory, fileName, mappingType);
			idMappingParser.setStringConverter(stringConverter);
			idMappingParser.setTokenSeperator(delimiter);
			idMappingParser.setStartParsingStopParsingAtLine(startParsingAtLine, stopParsingAtLine);
			idMappingParser.loadData();

		}

		if (bCreateReverseMap) {
			idMappingManager.createReverseMap(mappingType);
		}
	}

	@Override
	public void undoCommand() {
	}
}
