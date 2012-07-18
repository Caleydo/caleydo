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
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.gui.SWTGUIManager;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.MappingType;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.Status;

/**
 * <p>
 * Loads ID mappings from a file to and {@link IDMappingManager}. The
 * {@link IDMappingManager} is specified trough the {@link IDCategory}.
 * </p>
 * <p>
 * Mappings can be loaded in two different ways:
 * <ol>
 * <li>From explicit mapping files, which contain something similar to
 * <code>fromID;toID</code> where fromID is of the type fromIDType in and toID
 * of type toIDType in {@link MappingType}.</li>
 * <li>From an ID specified in a file to a dynamically generated ID, where the
 * dynamic ID corresponds to the line number (where line number 0 is considered
 * the line number of the first ID, i.e., skipped lines of the file are
 * ignored).</li>
 * </ol>
 * </p>
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public class IDMappingParser extends ATextParser {
	protected MappingType mappingType;

	protected final IDMappingManager idMappingManager;

	/** Defines the token separator. TAB is default. */
	protected String tokenSeparator = TAB;

	protected SWTGUIManager swtGuiManager;

	/**
	 * Constructor.
	 */
	public IDMappingParser(IDCategory idCategory, String fileName, MappingType mappingType) {
		super(fileName);

		this.mappingType = mappingType;

		swtGuiManager = GeneralManager.get().getSWTGUIManager();
		this.idMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(
				idCategory);
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
			 * Start parsing if current line lineInFile is larger than
			 * parsingStartLine ..
			 */
			if (lineCounter <= parsingStartLine) {
				lineCounter++;
				continue;
			}

			String[] textTokens = line.split(tokenSeparator);

			try {

				String fromID = convertID(textTokens[0], mappingType.getFromIDType()
						.getIdTypeParsingRules());
				String toID = convertID(textTokens[1], mappingType.getToIDType()
						.getIdTypeParsingRules());
				if (mappingType.getFromIDType().getColumnType() == EDataType.INT) {
					if (mappingType.getToIDType().getColumnType() == EDataType.INT) {
						idMappingManager.getMap(mappingType).put(Integer.valueOf(fromID),
								Integer.valueOf(toID));
					} else if (mappingType.getToIDType().getColumnType() == EDataType.STRING) {
						idMappingManager.getMap(mappingType).put(Integer.valueOf(fromID),
								toID);
					} else
						throw new IllegalStateException("Unsupported data type!");
				} else if (mappingType.getFromIDType().getColumnType() == EDataType.STRING) {
					if (mappingType.getToIDType().getColumnType() == EDataType.INT) {
						idMappingManager.getMap(mappingType).put(fromID,
								Integer.valueOf(toID));
					} else if (mappingType.getToIDType().getColumnType() == EDataType.STRING) {
						idMappingManager.getMap(mappingType).put(fromID, toID);
					} else
						throw new IllegalStateException("Unsupported data type!");
				} else
					throw new IllegalStateException("Unsupported data type!");
			} catch (NumberFormatException nfe) {
				Logger.log(new Status(Status.ERROR, this.toString(),
						"Caught NFE: could not parse: " + mappingType, nfe));

			} catch (ArrayIndexOutOfBoundsException boundEx) {
				Logger.log(new Status(
						Status.ERROR,
						this.toString(),
						"Caught Out of bounds exception: could not parse: " + mappingType,
						boundEx));
			}

			// Update progress bar only on each 100th line
			if (lineCounter % 100 == 0) {
				swtGuiManager
						.setProgressBarPercentage((int) (progressBarFactor * lineCounter));
			}
			lineCounter++;
		}
	}
}
