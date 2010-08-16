package org.caleydo.core.parser.ascii;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.caleydo.core.data.collection.EStorageType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.ISWTGUIManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.mapping.MappingType;

/**
 * Abstract lookup table loader.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class LookupTableLoader
	extends AbstractLoader {
	protected MappingType mappingType;

	protected final IIDMappingManager genomeIdManager;

	/**
	 * Factor with that the line index must be multiplied to get a normalized (0-100) progress percentage
	 * value.
	 */
	protected float fProgressBarFactor = 0;

	protected ISWTGUIManager swtGuiManager;

	/**
	 * Constructor.
	 */
	public LookupTableLoader(String sFileName, MappingType mappingType) {
		super(sFileName);

		this.mappingType = mappingType;

		swtGuiManager = GeneralManager.get().getSWTGUIManager();
		genomeIdManager = GeneralManager.get().getIDMappingManager();

		setTokenSeperator(IGeneralManager.sDelimiter_Parser_DataType);
	}

	@Override
	protected void loadDataParseFile(BufferedReader brFile, int numberOfLinesInFile) throws IOException {
		String sLine;

		int iLineInFile = 0;

		fProgressBarFactor = 100f / iStopParsingAtLine;

		while ((sLine = brFile.readLine()) != null && iLineInFile <= iStopParsingAtLine) {
			/**
			 * Start parsing if current line iLineInFile is larger than iStartParsingAtLine ..
			 */
			if (iLineInFile >= iStartParsingAtLine) {

				boolean bMaintainLoop = true;
				StringTokenizer strTokenText = new StringTokenizer(sLine, sTokenSeperator);

				// Expect two Integer values in one row!
				try {
					// Check if line consists of just one entity
					if (sLine.length() != 0 && strTokenText.countTokens() == 1) {
						
						// Special case for creating indexing of storages
						if (mappingType.getToIDType().getTypeName().equals("content_")) {

							// Remove multiple RefSeqs because all point to the
							// same gene DAVID ID
							if (sLine.contains(";")) {
								sLine = sLine.substring(0, sLine.indexOf(";"));
							}

							// Remove version in RefSeq (NM_*.* -> NM_*)
							if (sLine.contains(".")) {
								sLine = sLine.substring(0, sLine.indexOf("."));
							}

							if (mappingType.getFromIDType().getStorageType() == EStorageType.INT) {
								try {
									Integer id = Integer.parseInt(sLine);
									genomeIdManager.getMap(mappingType).put(id,
										iLineInFile - iStartParsingAtLine);
								}
								catch (NumberFormatException e) {
								}
							}
							else if (mappingType.getFromIDType().getStorageType() == EStorageType.STRING) {
								genomeIdManager.getMap(mappingType).put(sLine,
									iLineInFile - iStartParsingAtLine);
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

							// Special case for creating indexing of storages
							if (mappingType.getToIDType().getTypeName().contains("content_")) {

								if (mappingType.getFromIDType().getTypeName().equals("REF_SEQ")) {
//									 Remove multiple RefSeqs because all point to
//									 the same gene DAVID ID
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
									if (mappingType.getFromIDType().getStorageType() == EStorageType.INT) {
										genomeIdManager.getMap(mappingType).put(Integer.valueOf(buffer),
											iLineInFile - iStartParsingAtLine);
									}
									else if (mappingType.getFromIDType().getTypeName().equals("UNSPECIFIED")) {
										genomeIdManager.getMap(mappingType).put(buffer,
											iLineInFile - iStartParsingAtLine);
									}
								}
								catch (NumberFormatException e) {
									// System.out.println(buffer + " " +
									// (iLineInFile - iStartParsingAtLine));
									genomeIdManager.getMap(mappingType).put(buffer,
										iLineInFile - iStartParsingAtLine);
								}

								break;
							}
							else {
								if (mappingType.getFromIDType().getStorageType() == EStorageType.INT) {
									if (mappingType.getToIDType().getStorageType() == EStorageType.INT) {
										genomeIdManager.getMap(mappingType).put(Integer.valueOf(buffer),
											Integer.valueOf(strTokenText.nextToken()));
									}
									else if (mappingType.getToIDType().getStorageType() == EStorageType.STRING) {
										genomeIdManager.getMap(mappingType).put(Integer.valueOf(buffer),
											strTokenText.nextToken());
									}
									else
										throw new IllegalStateException("Unsupported data type!");
								}
								else if (mappingType.getFromIDType().getStorageType() == EStorageType.STRING) {
									if (mappingType.getToIDType().getStorageType() == EStorageType.INT) {
										genomeIdManager.getMap(mappingType).put(buffer,
											Integer.valueOf(strTokenText.nextToken()));
									}
									else if (mappingType.getToIDType().getStorageType() == EStorageType.STRING) {
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
					 * no ABORT was set. since no more tokens are in ParserTokenHandler skip rest of line..
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
	protected void setArraysToStorages() {
		// TODO Auto-generated method stub

	}
}
