/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.io.parser.ascii;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;

import org.caleydo.core.gui.SWTGUIManager;
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
	 * GUI manager used to update the progress bar.
	 */
	protected SWTGUIManager swtGuiManager;

	/**
	 * Constructor.
	 */
	public ATextParser(final String fileName) {
		this(fileName, GeneralManager.get().getResourceLoader());
	}

	public ATextParser(final String fileName, ResourceLoader loader) {
		this.filePath = fileName;
		this.loader = loader;
		this.swtGuiManager = GeneralManager.get().getSWTGUIManager();
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
			for (String result : splitID) {
				if (!result.isEmpty()) {
					sourceID = result;
					break;
				}
			}
		}
		return sourceID;
	}
}
