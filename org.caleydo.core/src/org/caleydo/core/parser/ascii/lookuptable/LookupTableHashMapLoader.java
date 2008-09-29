package org.caleydo.core.parser.ascii.lookuptable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.manager.specialized.genome.IGenomeIdMap;

/**
 * Loads a lookup table mapping an ID to another.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class LookupTableHashMapLoader
	extends ALookupTableLoader
	implements ILookupTableLoader
{

	protected IGenomeIdMap genomeIdMap;

	/**
	 * Constructor.
	 * 
	 */
	public LookupTableHashMapLoader(final String sFileName, final EMappingType genomeIdType,
			final LookupTableLoaderProxy lookupTableLoaderProxy)
	{
		super(sFileName, genomeIdType, lookupTableLoaderProxy);
	}

	@Override
	public int loadDataParseFileLUT(BufferedReader brFile, int iNumberOfLinesInFile)
			throws IOException
	{

		String sLine;

		int iLineInFile = 1;
		int iStartParsingAtLine = lookupTableLoaderProxy.getStartParsingAtLine();
		int iStopParsingAtLine = lookupTableLoaderProxy.getStopParsingAtLine();
		String sOuterTokenSeperator = lookupTableLoaderProxy.getTokenSeperator();

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
				StringTokenizer strTokenText = new StringTokenizer(sLine, sOuterTokenSeperator);

				// Expect two Integer values in one row!
				try
				{
					// Check if line consists of just one entity
					if (sLine.length() != 0 && strTokenText.countTokens() == 1)
					{
						// Special case for creating indexing of storages
						if (currentGenomeIdType
								.equals(EMappingType.DAVID_2_EXPRESSION_INDEX))
							genomeIdMap.put(sLine, Integer.toString(iLineInFile
									- iStartParsingAtLine));
						else
							genomeIdMap.put(sLine, strTokenText.nextToken());
					}
					else
					{
						// Read all tokens
						while (strTokenText.hasMoreTokens() && bMaintainLoop)
						{
							String buffer = strTokenText.nextToken();

							// Special case for creating indexing of storages
							if (currentGenomeIdType
									.equals(EMappingType.DAVID_2_EXPRESSION_INDEX))
								genomeIdMap.put(buffer, Integer.toString(iLineInFile
										- iStartParsingAtLine));
							else
								genomeIdMap.put(buffer, strTokenText.nextToken());

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
				catch (NullPointerException npe)
				{
					bMaintainLoop = false;

					// reset return value to indicate error
					iStopParsingAtLine = 1;

					System.out.println("LookupTableHashMapLoader NullPointerException! "
							+ npe.toString());
					npe.printStackTrace();

				}
			}

			iLineInFile++;

			// Update progress bar only on each 100th line
			if (iLineInFile % 1000 == 0)
			{
				swtGuiManager
						.setProgressBarPercentage((int) (fProgressBarFactor * iLineInFile));
			}
		}

		return iLineInFile - iStartParsingAtLine;
	}

	@Override
	public final void setHashMap(final IGenomeIdMap setHashMap, final EMappingType type)
	{

		assert type == currentGenomeIdType : "must use same type as in constructor!";

		if (type.isMultiMap())
		{
			assert false : "setHashMap() must not call MultiMap via setHashMap()";
			return;
		}

		genomeIdMap = setHashMap;
	}

	/**
	 * Write back data to IGenomeIdManager
	 * 
	 * @see org.caleydo.core.parser.ascii.lookuptable.ILookupTableLoader#wirteBackMapToGenomeManager()
	 * @see org.caleydo.core.manager.specialized.genome.IGenomeIdManager
	 */
	public void wirteBackMapToGenomeIdManager()
	{

		genomeIdManager.setMapByType(currentGenomeIdType, genomeIdMap);
	}
}
