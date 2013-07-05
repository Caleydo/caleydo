/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io.parser.ascii;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;

import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Base class for text parsers.
 *
 * @author Alexander Lex
 * @author Marc Streit
 */
public abstract class ATextParser {

	public static final String SPACE = " ";
	public static final String SEMICOLON = ";";
	public static final String TAB = "\t";

	/** The path of the file to parse */
	protected final String filePath;

	/**
	 * the loader to use for locating the file
	 */
	protected final ResourceLoader loader;

	/**
	 * Contains the number of lines of the number of lines in the file to be parsed, after
	 * {@link #calculateNumberOfLinesInFile()} was called.
	 */
	protected int numberOfLinesInFile = -1;

	/**
	 * Defines at which line to start the parsing. This is, e.g., useful to ignore headers. Must be positive. Defaults
	 * to 0, the first line.
	 */
	protected int startParsingAtLine = 0;

	/**
	 * Defines at which line to stop parsing. Is set to parse all lines by default.
	 */
	protected int stopParsingAtLine = Integer.MAX_VALUE;

	/**
	 * Constructor.
	 */
	public ATextParser(final String fileName) {
		this(fileName, GeneralManager.get().getResourceLoader());
	}

	public ATextParser(final String fileName, ResourceLoader loader) {
		this.filePath = fileName;
		this.loader = loader;
	}

	/**
	 * @param startParsingAtLine
	 *            setter, see {@link #startParsingAtLine}
	 */
	public void setStartParsingAtLine(int startParsingAtLine) {
		if (startParsingAtLine < 0)
			throw new IllegalArgumentException("Can not start parsing at a negative line: " + startParsingAtLine);
		this.startParsingAtLine = startParsingAtLine;
	}

	/**
	 * Setter for the line at which to stop parsing. If attribute is <0, all lines in the file are parsed.
	 *
	 * @param stopParsingAtLine
	 *            setter, see {@link #stopParsingAtLine}
	 *
	 */
	public void setStopParsingAtLine(int stopParsingAtLine) {
		if (stopParsingAtLine < 0)
			this.stopParsingAtLine = Integer.MAX_VALUE;
		else
			this.stopParsingAtLine = stopParsingAtLine;
	}

	/**
	 * Reads the file and counts the numbers of lines to be read.
	 */
	protected final int calculateNumberOfLinesInFile() {
		try {
			LineNumberReader lnr = new LineNumberReader(loader.getResource(filePath));
			lnr.skip(Long.MAX_VALUE);
			numberOfLinesInFile = lnr.getLineNumber();
			lnr.close();

		} catch (IOException ioe) {
			throw new IllegalStateException("Could not read from file: " + filePath);
		}

		return numberOfLinesInFile;
	}

	/**
	 * Triggers the actual loading and parsing of the data specified.
	 *
	 * @return
	 */
	public boolean loadData() {
		try {

			Logger.log(new Status(IStatus.INFO, GeneralManager.PLUGIN_ID, "Start loading file " + filePath + "..."));

			BufferedReader reader = loader.getResource(filePath);

			this.parseFile(reader);

			if (reader != null) {
				reader.close();
			}
		} catch (Exception e) {
			Logger.log(new Status(IStatus.ERROR, this.toString(), "Could not read data file.", e));
			throw new IllegalStateException("Could not read data file '" + filePath + "'", e);
		}

		Logger.log(new Status(IStatus.INFO, toString(), "File " + filePath + " successfully loaded."));

		return true;
	}

	protected void parseFile(BufferedReader reader) throws IOException {
	}

	/**
	 * Converts a sourceID based on the {@link IDTypeParsingRules} specified and returns a new string with the converted
	 * ID
	 *
	 * @param sourceID
	 * @param idTypeParsingRules
	 * @return a new String with the converted ID
	 */
	public static String convertID(String sourceID, IDTypeParsingRules idTypeParsingRules) {
		if (idTypeParsingRules == null)
			return sourceID;
		if (idTypeParsingRules.isToLowerCase())
			sourceID = sourceID.toLowerCase();
		else if (idTypeParsingRules.isToUpperCase())
			sourceID = sourceID.toUpperCase();
		if (idTypeParsingRules.getReplacingExpressions() != null) {
			for (String replacingExpression : idTypeParsingRules.getReplacingExpressions()) {
				sourceID = sourceID.replaceAll(replacingExpression, idTypeParsingRules.getReplacementString());
			}
		}
		if (idTypeParsingRules.getSubStringExpression() != null) {
			String[] splitID = sourceID.split(idTypeParsingRules.getSubStringExpression());
			// first one found used
			for (String result : splitID) {
				if (!result.isEmpty()) {
					sourceID = result;
					break;
				}
			}
		}
		return sourceID;
	}

	@Override
	public String toString() {
		return "Parser for " + filePath;
	}
}
