/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io.parser.ascii;

import java.io.BufferedReader;
import java.io.IOException;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.MappingType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * <p>
 * Loads ID mappings from a dedicated mapping file to and {@link IDMappingManager}. The {@link IDMappingManager} is
 * specified trough the {@link IDCategory}.
 * </p>
 * <p>
 * Mappings are loaded from explicit, text-based mapping files, where two IDs are specified in each row in a format
 * similar to <code>fromID;toID</code> where fromID is of the type fromIDType in and toID of type toIDType in
 * {@link MappingType}. The IDs have to be separated by a token, which can be specified using
 * {@link #setTokenSeperator(String)}
 * </p>
 *
 * @author Marc Streit
 * @author Alexander Lex
 */
public class IDMappingParser extends ATextParser {
	protected MappingType mappingType;

	protected final IDMappingManager idMappingManager;

	/** Defines the token separator. TAB is default. */
	protected String tokenSeparator = TAB;

	/**
	 * Creates an ID mapping for the id types specified by parsing the supplied file.
	 *
	 * @param filePath
	 *            the path to the file containing the mapping file
	 * @param startParsingAtLine
	 *            the line at which to start the parsing
	 * @param stopParsingAtLine
	 *            the line at which to stop the parsing
	 * @param codeResolvingLUTMappingType
	 * @param delimiter
	 * @param idCategory
	 * @param isMultiMap
	 * @param createReverseMap
	 * @param resolveCodeMappingUsingCodeToId_LUT
	 *            Boolean indicates if one column of the mapping needs to be resolved. Resolving means replacing codes
	 *            by internal IDs.
	 */
	public static void loadMapping(String filePath, int startParsingAtLine, int stopParsingAtLine, IDType fromIDType,
			IDType toIDType, String delimiter, IDCategory idCategory, boolean isMultiMap, boolean createReverseMap,
			boolean resolveCodeMappingUsingCodeToId_LUT, IDType codeResolvedFromIDType, IDType codeResolvedToIDType) {

		IDMappingParser idMappingParser = null;

		if (idCategory == null)
			throw new IllegalStateException("ID Category was null");

		IDMappingManager idMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(idCategory);

		MappingType mappingType = idMappingManager.createMap(fromIDType, toIDType, isMultiMap, createReverseMap);

		{
			idMappingParser = new IDMappingParser(idCategory, filePath, mappingType);
			idMappingParser.setTokenSeperator(delimiter);
			idMappingParser.setStartParsingAtLine(startParsingAtLine);
			idMappingParser.setStopParsingAtLine(stopParsingAtLine);
			idMappingParser.loadData();
		}

		if (resolveCodeMappingUsingCodeToId_LUT) {

			idMappingManager.createCodeResolvedMap(mappingType, codeResolvedFromIDType, codeResolvedToIDType);
		}
	}

	// public static class MappingLoader {
	// private final MappingType mappingType;
	//
	// public MappingLoader(IDType fromIDType, IDType toIDType, boolean isMultiMap) {
	//
	// }
	// }

	/**
	 * Constructor.
	 */
	private IDMappingParser(IDCategory idCategory, String fileName, MappingType mappingType) {
		super(fileName);

		this.mappingType = mappingType;

		swtGuiManager = GeneralManager.get().getSWTGUIManager();
		this.idMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(idCategory);
	}

	/**
	 * Set the current token separator.
	 *
	 * @param tokenSeparator
	 */
	public final void setTokenSeperator(final String tokenSeparator) {
		this.tokenSeparator = tokenSeparator;
	}

	@Override
	protected void parseFile(BufferedReader reader) throws IOException {

		swtGuiManager.setProgressBarText("Loading ID mapping for " + mappingType);
		String line;

		int lineCounter = 0;
		calculateNumberOfLinesInFile();

		float progressBarFactor = 100f / numberOfLinesInFile;

		while ((line = reader.readLine()) != null && lineCounter <= stopParsingAtLine) {
			/**
			 * Start parsing if current line lineInFile is larger than parsingStartLine ..
			 */
			if (lineCounter <= startParsingAtLine) {
				lineCounter++;
				continue;
			}

			String[] textTokens = line.split(tokenSeparator);

			try {

				String fromID = convertID(textTokens[0], mappingType.getFromIDType().getIdTypeParsingRules());
				String toID = convertID(textTokens[1], mappingType.getToIDType().getIdTypeParsingRules());
				if (mappingType.getFromIDType().getDataType() == EDataType.INTEGER) {
					if (mappingType.getToIDType().getDataType() == EDataType.INTEGER) {
						idMappingManager.addMapping(mappingType, Integer.valueOf(fromID), Integer.valueOf(toID));
					} else if (mappingType.getToIDType().getDataType() == EDataType.STRING) {
						idMappingManager.addMapping(mappingType, Integer.valueOf(fromID), toID.intern());
					} else
						throw new IllegalStateException("Unsupported data type!");
				} else if (mappingType.getFromIDType().getDataType() == EDataType.STRING) {
					if (mappingType.getToIDType().getDataType() == EDataType.INTEGER) {
						idMappingManager.addMapping(mappingType, fromID.intern(), Integer.valueOf(toID));
					} else if (mappingType.getToIDType().getDataType() == EDataType.STRING) {
						idMappingManager.addMapping(mappingType, fromID.intern(), toID.intern());
					} else
						throw new IllegalStateException("Unsupported data type!");
				} else
					throw new IllegalStateException("Unsupported data type!");
			} catch (NumberFormatException nfe) {
				Logger.log(new Status(IStatus.ERROR, this.toString(), "Caught NFE: could not parse: " + mappingType,
						nfe));

			} catch (ArrayIndexOutOfBoundsException boundEx) {
				Logger.log(new Status(IStatus.ERROR, this.toString(),
						"Caught Out of bounds exception: could not parse: " + mappingType, boundEx));
			}

			// Update progress bar only on each 100th line
			if (lineCounter % 100 == 0) {
				swtGuiManager.setProgressBarPercentage((int) (progressBarFactor * lineCounter));
			}
			lineCounter++;
		}
	}
}
