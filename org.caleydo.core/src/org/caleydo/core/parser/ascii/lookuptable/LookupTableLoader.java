package org.caleydo.core.parser.ascii.lookuptable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import org.caleydo.core.data.collection.EStorageType;
import org.caleydo.core.data.mapping.EMappingDataType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.ISWTGUIManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.parser.ascii.AbstractLoader;

/**
 * Abstract lookup table loader.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class LookupTableLoader
	extends AbstractLoader
{
	protected EMappingType mappingType;

	protected final IIDMappingManager genomeIdManager;

	/**
	 * Factor with that the line index must be multiplied to get a normalized
	 * (0-100) progress percentage value.
	 */
	protected float fProgressBarFactor = 0;

	protected ISWTGUIManager swtGuiManager;

	/**
	 * Constructor.
	 */
	public LookupTableLoader(final String sFileName, final EMappingType mappingType,
			EMappingDataType dataType)
	{
		super(sFileName);

		this.mappingType = mappingType;

		swtGuiManager = GeneralManager.get().getSWTGUIManager();
		genomeIdManager = GeneralManager.get().getIDMappingManager();

		setTokenSeperator(IGeneralManager.sDelimiter_Parser_DataType);

		IIDMappingManager genomeIdManager = GeneralManager.get().getIDMappingManager();

		genomeIdManager.createMap(mappingType, dataType);
	}

	@Override
	protected void loadDataParseFile(BufferedReader brFile, int numberOfLinesInFile)
			throws IOException
	{
		String sLine;

		int iLineInFile = 1;

		fProgressBarFactor = 100f / iStopParsingAtLine;

		while (((sLine = brFile.readLine()) != null) && (iLineInFile <= iStopParsingAtLine))
		{
			/**
			 * Start parsing if current line iLineInFile is larger than
			 * iStartParsingAtLine ..
			 */
			if (iLineInFile > iStartParsingAtLine)
			{

				boolean bMaintainLoop = true;
				StringTokenizer strTokenText = new StringTokenizer(sLine, sTokenSeperator);

				// Expect two Integer values in one row!
				try
				{
					// Check if line consists of just one entity
					if (sLine.length() != 0 && strTokenText.countTokens() == 1)
					{
						// Special case for creating indexing of storages
						// TODO review sLine should be integer?
						if (mappingType.equals(EMappingType.REFSEQ_MRNA_2_EXPRESSION_INDEX)
								|| mappingType.equals(EMappingType.OLIGO_2_EXPRESSION_INDEX)
								|| mappingType
										.equals(EMappingType.EXPERIMENT_2_EXPERIMENT_INDEX))
						{
							// Remove multiple RefSeqs because all point to the
							// same gene DAVID ID
							if (sLine.contains(";"))
								sLine = sLine.substring(0, sLine.indexOf(";"));

							// Remove version in RefSeq (NM_*.* -> NM_*)
							if (sLine.contains("."))
								sLine = sLine.substring(0, sLine.indexOf("."));

							genomeIdManager.getMapping(mappingType).put(sLine,
									iLineInFile - iStartParsingAtLine);
						}
						else
						{
							genomeIdManager.getMapping(mappingType).put(sLine,
									strTokenText.nextToken());
						}
					}
					else
					{
						// Read all tokens
						while (strTokenText.hasMoreTokens() && bMaintainLoop)
						{
							String buffer = strTokenText.nextToken();

							// Special case for creating indexing of storages
							if (mappingType
									.equals(EMappingType.REFSEQ_MRNA_2_EXPRESSION_INDEX)
									|| mappingType
											.equals(EMappingType.OLIGO_2_EXPRESSION_INDEX)
									|| mappingType
											.equals(EMappingType.EXPERIMENT_2_EXPERIMENT_INDEX))
							{
								// Remove multiple RefSeqs because all point to
								// the same gene DAVID ID
								if (buffer.contains(";"))
									buffer = sLine.substring(0, sLine.indexOf(";"));

								// Remove version in RefSeq (NM_*.* -> NM_*)
								if (buffer.contains("."))
									buffer = buffer.substring(0, buffer.indexOf("."));

								// Check for integer values that must be ignored
								// - in that case no RefSeq is available or the
								// cell is empty
								try
								{
									Float.valueOf(buffer);
								}
								catch (NumberFormatException e)
								{
									// System.out.println(buffer + " " +
									// (iLineInFile - iStartParsingAtLine));
									genomeIdManager.getMapping(mappingType).put(buffer,
											iLineInFile - iStartParsingAtLine);
								}

								break;
							}
							else
							{
								if (mappingType.getTypeOrigin().getStorageType() == EStorageType.INT)
								{
									if (mappingType.getTypeTarget().getStorageType() == EStorageType.INT)
									{
										genomeIdManager.getMapping(mappingType).put(
												Integer.valueOf(buffer),
												Integer.valueOf(strTokenText.nextToken()));
									}
									else if (mappingType.getTypeTarget().getStorageType() == EStorageType.STRING)
									{
										genomeIdManager.getMapping(mappingType).put(
												Integer.valueOf(buffer),
												strTokenText.nextToken());
									}
									else
										throw new IllegalStateException(
												"Unsupported data type!");
								}
								else if (mappingType.getTypeOrigin().getStorageType() == EStorageType.STRING)
								{
									if (mappingType.getTypeTarget().getStorageType() == EStorageType.INT)
									{
										genomeIdManager.getMapping(mappingType).put(buffer,
												Integer.valueOf(strTokenText.nextToken()));
									}
									else if (mappingType.getTypeTarget().getStorageType() == EStorageType.STRING)
									{
										genomeIdManager.getMapping(mappingType).put(buffer,
												strTokenText.nextToken());
									}
									else
										throw new IllegalStateException(
												"Unsupported data type!");
								}
								else
									throw new IllegalStateException("Unsupported data type!");
							}

							break;
						} // end of: while (( strToken.hasMoreTokens()
						// )&&(bMaintainLoop)) {
					}
				}
				catch (NoSuchElementException nsee)
				{
					/*
					 * no ABORT was set. since no more tokens are in
					 * ParserTokenHandler skip rest of line..
					 */
					bMaintainLoop = false;

					// reset return value to indicate error
					iStopParsingAtLine = -1;

				}
				// catch (NullPointerException npe)
				// {
				// bMaintainLoop = false;
				//
				// // reset return value to indicate error
				// iStopParsingAtLine = 1;
				//
				// System.out.println("LookupTableHashMapLoader NullPointerException! "
				// + npe.toString());
				// npe.printStackTrace();
				//
				// }
			}

			iLineInFile++;

			// Update progress bar only on each 100th line
			if (iLineInFile % 1000 == 0)
			{
				swtGuiManager
						.setProgressBarPercentage((int) (fProgressBarFactor * iLineInFile));
			}
		}
	}

	@Override
	protected void setArraysToStorages()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void init()
	{
		// TODO Auto-generated method stub

	}
}
