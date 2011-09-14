package org.caleydo.core.parser.ascii;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.caleydo.core.data.collection.EColumnType;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.mapping.IDMappingManagerRegistry;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.data.mapping.MappingType;
import org.caleydo.core.gui.SWTGUIManager;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.Status;

/**
 * <p>
 * Loads ID mappings from a file to and {@link IDMappingManager}. The {@link IDMappingManager} is specified
 * trough the {@link IDCategory}.
 * </p>
 * <p>
 * Mappings can be loaded in two different ways:
 * <ol>
 * <li>From explicit mapping files, which contain something similar to <code>fromID;toID</code> where fromID
 * is of the type fromIDType in and toID of type toIDType in {@link MappingType}.</li>
 * <li>From an ID specified in a file to a dynamically generated ID, where the dynamic ID corresponds to the
 * line number (where line number 0 is considered the line number of the first ID, i.e., skipped lines of the
 * file are ignored).</li>
 * </ol>
 * </p>
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public class IDMappingParser
	extends ATextParser {
	protected MappingType mappingType;

	protected final IDMappingManager idMappingManager;

	/**
	 * Factor with that the line index must be multiplied to get a normalized (0-100) progress percentage
	 * value.
	 */
	protected float progressBarFactor = 0;

	protected SWTGUIManager swtGuiManager;

	private AStringConverter stringConverter = null;

	/**
	 * Constructor.
	 */
	public IDMappingParser(IDCategory idCategory, String fileName, MappingType mappingType) {
		super(fileName);

		this.mappingType = mappingType;

		swtGuiManager = GeneralManager.get().getSWTGUIManager();
		this.idMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(idCategory);

		setTokenSeperator(SEMICOLON);

		// FIXME that should be set from somewhere else
		if (mappingType.getFromIDType().getTypeName().contains("REFSEQ"))
			stringConverter = new RefSeqStringConverter();
	}

	/**
	 * @param stringConverter
	 *            setter, see {@link #stringConverter}
	 */
	public void setStringConverter(AStringConverter stringConverter) {
		this.stringConverter = stringConverter;
	}

	@Override
	protected void loadDataParseFile(BufferedReader reader, int numberOfLinesInFile) throws IOException {

		String line;

		int currentLine = 0;

		progressBarFactor = 100f / stopParsingAtLine;

		while ((line = reader.readLine()) != null && currentLine <= stopParsingAtLine) {
			/**
			 * Start parsing if current line lineInFile is larger than parsingStartLine ..
			 */
			if (currentLine >= parsingStartLine) {

				boolean maintainLoop = true;
				StringTokenizer textTokens = new StringTokenizer(line, tokenSeperator);

				// Expect two Integer values in one row!
				try {

					// Read all tokens
					while (textTokens.hasMoreTokens() && maintainLoop) {
						String token = textTokens.nextToken();
						// Special case for creating dynamic IDs for rows
						if (mappingType.getToIDType().isInternalType()) {
							if (stringConverter != null) {
								token = stringConverter.convert(token);
							}

							// Check for integer values that must be ignored
							// - in that case no RefSeq is available or the
							// cell is empty
							try {
								Float.valueOf(token);
								if (mappingType.getFromIDType().getColumnType() == EColumnType.INT) {
									idMappingManager.getMap(mappingType).put(Integer.valueOf(token),
										currentLine - parsingStartLine);
								}
								else if (mappingType.getFromIDType().getTypeName().contains("UNSPECIFIED")) {
									idMappingManager.getMap(mappingType).put(token,
										currentLine - parsingStartLine);
								}
							}
							catch (NumberFormatException e) {
								// System.out.println(buffer + " " +
								// (lineInFile - parsingStartLine));
								idMappingManager.getMap(mappingType).put(token,
									currentLine - parsingStartLine);
							}

							break;
						}
						else {
							try {
								if (mappingType.getFromIDType().getColumnType() == EColumnType.INT) {
									if (mappingType.getToIDType().getColumnType() == EColumnType.INT) {
										idMappingManager.getMap(mappingType).put(Integer.valueOf(token),
											Integer.valueOf(textTokens.nextToken()));
									}
									else if (mappingType.getToIDType().getColumnType() == EColumnType.STRING) {
										idMappingManager.getMap(mappingType).put(Integer.valueOf(token),
											textTokens.nextToken());
									}
									else
										throw new IllegalStateException("Unsupported data type!");
								}
								else if (mappingType.getFromIDType().getColumnType() == EColumnType.STRING) {
									if (mappingType.getToIDType().getColumnType() == EColumnType.INT) {
										idMappingManager.getMap(mappingType).put(token,
											Integer.valueOf(textTokens.nextToken()));
									}
									else if (mappingType.getToIDType().getColumnType() == EColumnType.STRING) {
										idMappingManager.getMap(mappingType).put(token,
											textTokens.nextToken());
									}
									else
										throw new IllegalStateException("Unsupported data type!");
								}
								else
									throw new IllegalStateException("Unsupported data type!");
							}
							catch (NumberFormatException nfe) {
								Logger.log(new Status(Status.ERROR, this.toString(),
									"Caught NFE: could not parse: " + mappingType, nfe));
							}
						}
						break;
					}
				}
				catch (NoSuchElementException nsee) {
					// no ABORT was table. since no more tokens are in ParserTokenHandler skip rest of line..
					maintainLoop = false;

					// reset return value to indicate error
					stopParsingAtLine = -1;
				}
			}

			currentLine++;

			// Update progress bar only on each 100th line
			if (currentLine % 1000 == 0) {
				swtGuiManager.setProgressBarPercentage((int) (progressBarFactor * currentLine));
			}
		}
	}
}
