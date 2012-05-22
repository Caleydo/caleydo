/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.io.parser.ascii;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;

import org.caleydo.core.gui.SWTGUIManager;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Base class for text parsers.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public abstract class ATextParser {

	public static final String SPACE = " ";
	public static final String SEMICOLON = ";";
	public static final String TAB = "\t";

	/** The path of the file to parse */
	protected String fileName = "";

	/**
	 * Contains the number of lines of the number of lines in the file to be
	 * parsed, after {@link #calculateNumberOfLinesInFile()} was called.
	 */
	int numberOfLinesInFile = -1;

	/**
	 * Defines numbers of lines to skip in the read file. This is, e.g., useful
	 * to ignore headers.
	 */
	protected int parsingStartLine = 0;

	/** Defines at which line to stop parsing */
	protected int stopParsingAtLine = Integer.MAX_VALUE;

	// protected int lineInFile = 0;

	protected SWTGUIManager swtGuiManager;

	/**
	 * Constructor.
	 */
	public ATextParser(final String fileName) {
		this.fileName = fileName;
		this.swtGuiManager = GeneralManager.get().getSWTGUIManager();
	}

	/**
	 * Set the current file name.
	 * 
	 * @param fileName
	 *            set current file name
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public final void setStartParsingStopParsingAtLine(int startParsingAtLine,
			int stopParsingAtLine) {

		this.parsingStartLine = startParsingAtLine;

		if (stopParsingAtLine < 0) {
			this.stopParsingAtLine = Integer.MAX_VALUE;
			return;
		}

		if (startParsingAtLine > stopParsingAtLine) {
			this.stopParsingAtLine = Integer.MAX_VALUE;
			return;
		}
		this.stopParsingAtLine = stopParsingAtLine;

		numberOfLinesInFile = stopParsingAtLine - parsingStartLine + 1;

	}

	/**
	 * Reads the file and counts the numbers of lines to be read.
	 */
	protected final int calculateNumberOfLinesInFile() {
		try {
			LineNumberReader lnr = new LineNumberReader(GeneralManager.get().getResourceLoader().getResource(fileName));
			lnr.skip(Long.MAX_VALUE);
			numberOfLinesInFile = lnr.getLineNumber();
			lnr.close();

		} catch (IOException ioe) {
			throw new IllegalStateException("Could not read from file: " + fileName);
		}

		return numberOfLinesInFile;
	}

	public boolean loadData() {

		try {

			Logger.log(new Status(IStatus.INFO, GeneralManager.PLUGIN_ID,
					"Start loading file " + fileName + "..."));

			BufferedReader reader = GeneralManager.get().getResourceLoader().getResource(fileName);

			this.parseFile(reader);

			if (reader != null) {
				reader.close();
			}
		} catch (Exception e) {
			Logger.log(new Status(IStatus.ERROR, this.toString(),
					"Could not read data file.", e));
			throw new IllegalStateException(
					"Could not read data file '" + fileName + "'", e);
		}

		Logger.log(new Status(IStatus.INFO, toString(), "File " + fileName
				+ " successfully loaded."));

		return true;
	}

	protected void parseFile(BufferedReader reader) throws IOException
	{}
	
	public static String convertID(String sourceID, IDSpecification idSpecification )
	{
		if(idSpecification == null)
			return sourceID;
		if (idSpecification.getReplacingExpression() != null) {
			sourceID = sourceID.replaceAll(
					idSpecification.getReplacingExpression(),
					idSpecification.getReplacementString());
		}
		if (idSpecification.getSubStringExpression() != null) {
			String[] splitID = sourceID.split(idSpecification
					.getSubStringExpression());
			for (String result : splitID) {
				if (!result.isEmpty())
				{
					sourceID = result;
					break;
				}
			}
		}
		return sourceID;
	}
}
