package org.caleydo.core.parser.ascii;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.caleydo.core.data.collection.DimensionType;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.data.mapping.MappingType;
import org.caleydo.core.gui.SWTGUIManager;
import org.caleydo.core.manager.GeneralManager;

/**
 * Abstract lookup table loader.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class LookupTableLoader
	extends AbstractLoader {
	protected MappingType mappingType;

	protected final IDMappingManager genomeIdManager;

	/**
	 * Factor with that the line index must be multiplied to get a normalized (0-100) progress percentage
	 * value.
	 */
	protected float fProgressBarFactor = 0;

	protected SWTGUIManager swtGuiManager;

	/**
	 * Constructor.
	 */
	public LookupTableLoader(String sFileName, MappingType mappingType) {
		super(sFileName);

		this.mappingType = mappingType;

		swtGuiManager = GeneralManager.get().getSWTGUIManager();
		genomeIdManager = GeneralManager.get().getIDMappingManager();

		setTokenSeperator(GeneralManager.sDelimiter_Parser_DataType);
	}

	@Override
	protected void loadDataParseFile(BufferedReader brFile, int numberOfLinesInFile) throws IOException {
		String sLine;

		int iLineInFile = 0;

		fProgressBarFactor = 100f / iStopParsingAtLine;

		while ((sLine = brFile.readLine()) != null && iLineInFile <= iStopParsingAtLine) {
			/**
			 * Start parsing if current line lineInFile is larger than parsingStartLine ..
			 */
			if (iLineInFile >= parsingStartLine) {

				boolean bMaintainLoop = true;
				StringTokenizer strTokenText = new StringTokenizer(sLine, tokenSeperator);

				// Expect two Integer values in one row!
				try {
					// Check if line consists of just one entity
					if (sLine.length() != 0 && strTokenText.countTokens() == 1) {

						// Special case for creating indexing of dimensions
						if (mappingType.getToIDType().getTypeName().equals("record_")) {

							// Remove multiple RefSeqs because all point to the
							// same gene DAVID ID
							if (sLine.contains(";")) {
								sLine = sLine.substring(0, sLine.indexOf(";"));
							}

							// Remove version in RefSeq (NM_*.* -> NM_*)
							if (sLine.contains(".")) {
								sLine = sLine.substring(0, sLine.indexOf("."));
							}

							if (mappingType.getFromIDType().getDimensionType() == DimensionType.INT) {
								try {
									Integer id = Integer.parseInt(sLine);
									genomeIdManager.getMap(mappingType).put(id,
										iLineInFile - parsingStartLine);
								}
								catch (NumberFormatException e) {
								}
							}
							else if (mappingType.getFromIDType().getDimensionType() == DimensionType.STRING) {
								genomeIdManager.getMap(mappingType).put(sLine,
									iLineInFile - parsingStartLine);
							}
							else
								throw new IllegalStateException("Unsupported data type!");
						}
						else {
							genomeIdManager.getMap(mappingType).put(sLine, strTokenText.nextToken());
						}
					}
					else {
						// Read all tokens
						while (strTokenText.hasMoreTokens() && bMaintainLoop) {
							String buffer = strTokenText.nextToken();

							// Special case for creating indexing of dimensions
							if (mappingType.getToIDType().getTypeName().contains("record_")) {

								if (mappingType.getFromIDType().getTypeName().contains("REFSEQ")) {
									// Remove multiple RefSeqs because all point to
									// the same gene DAVID ID
									if (buffer.contains(";")) {
										buffer = sLine.substring(0, sLine.indexOf(";"));
									}

									// Remove version in RefSeq (NM_*.* -> NM_*)
									if (buffer.contains(".")) {
										buffer = buffer.substring(0, buffer.indexOf("."));
									}
								}

								// Check for integer values that must be ignored
								// - in that case no RefSeq is available or the
								// cell is empty
								try {
									Float.valueOf(buffer);
									if (mappingType.getFromIDType().getDimensionType() == DimensionType.INT) {
										genomeIdManager.getMap(mappingType).put(Integer.valueOf(buffer),
											iLineInFile - parsingStartLine);
									}
									else if (mappingType.getFromIDType().getTypeName().equals("UNSPECIFIED")) {
										genomeIdManager.getMap(mappingType).put(buffer,
											iLineInFile - parsingStartLine);
									}
								}
								catch (NumberFormatException e) {
									// System.out.println(buffer + " " +
									// (lineInFile - parsingStartLine));
									genomeIdManager.getMap(mappingType).put(buffer,
										iLineInFile - parsingStartLine);
								}

								break;
							}
							else {
								if (mappingType.getFromIDType().getDimensionType() == DimensionType.INT) {
									if (mappingType.getToIDType().getDimensionType() == DimensionType.INT) {
										genomeIdManager.getMap(mappingType).put(Integer.valueOf(buffer),
											Integer.valueOf(strTokenText.nextToken()));
									}
									else if (mappingType.getToIDType().getDimensionType() == DimensionType.STRING) {
										genomeIdManager.getMap(mappingType).put(Integer.valueOf(buffer),
											strTokenText.nextToken());
									}
									else
										throw new IllegalStateException("Unsupported data type!");
								}
								else if (mappingType.getFromIDType().getDimensionType() == DimensionType.STRING) {
									if (mappingType.getToIDType().getDimensionType() == DimensionType.INT) {
										genomeIdManager.getMap(mappingType).put(buffer,
											Integer.valueOf(strTokenText.nextToken()));
									}
									else if (mappingType.getToIDType().getDimensionType() == DimensionType.STRING) {
										genomeIdManager.getMap(mappingType).put(buffer,
											strTokenText.nextToken());
									}
									else
										throw new IllegalStateException("Unsupported data type!");
								}
								else
									throw new IllegalStateException("Unsupported data type!");
							}

							break;
						} // end of: while (( strToken.hasMoreTokens()
							// )&&(bMaintainLoop)) {
					}
				}
				catch (NoSuchElementException nsee) {
					/*
					 * no ABORT was dataTable. since no more tokens are in ParserTokenHandler skip rest of line..
					 */
					bMaintainLoop = false;

					// reset return value to indicate error
					iStopParsingAtLine = -1;

				}

			}

			iLineInFile++;

			// Update progress bar only on each 100th line
			if (iLineInFile % 1000 == 0) {
				swtGuiManager.setProgressBarPercentage((int) (fProgressBarFactor * iLineInFile));
			}
		}
	}

	@Override
	protected void setArraysToDimensions() {
		// TODO Auto-generated method stub

	}
}
