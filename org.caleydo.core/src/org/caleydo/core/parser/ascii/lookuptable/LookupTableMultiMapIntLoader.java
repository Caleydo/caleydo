package org.caleydo.core.parser.ascii.lookuptable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import org.caleydo.core.data.map.MultiHashArrayIntegerMap;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.manager.general.GeneralManager;

/**
 * Multi hash map lookup table loader.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class LookupTableMultiMapIntLoader
	extends ALookupTableLoader
	implements ILookupTableLoader
{

	protected MultiHashArrayIntegerMap multiHashMapInteger;

	/**
	 * Constructor.
	 */
	public LookupTableMultiMapIntLoader(final String sFileName, final EMappingType genomeIdType,
			final LookupTableLoaderProxy lookupTableLoaderProxy)
	{
		super(sFileName, genomeIdType, lookupTableLoaderProxy);
	}

	@Override
	public int loadDataParseFileLUT(BufferedReader brFile, final int iNumberOfLinesInFile)
			throws IOException
	{

		String sLine;
		int iLineInFile = 1;
		int iStartParsingAtLine = lookupTableLoaderProxy.getStartParsingAtLine();
		int iStopParsingAtLine = lookupTableLoaderProxy.getStopParsingAtLine();

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
				StringTokenizer strTokenText = new StringTokenizer(sLine,
						lookupTableLoaderProxy.getTokenSeperator());

				/**
				 * Read all tokens
				 */
				while ((strTokenText.hasMoreTokens()) && (bMaintainLoop))
				{
					// Expect two Integer values in one row!
					try
					{
						int iFirst = new Integer(strTokenText.nextToken());

						if (strTokenText.hasMoreTokens())
						{
							int iSecond = new Integer(strTokenText.nextToken());

							multiHashMapInteger.put(iFirst, iSecond);
						}

					}
					catch (NoSuchElementException nsee)
					{
						/*
						 * no ABORT was set. since no more tokens are in
						 * ParserTokenHandler skip rest of line..
						 */
						bMaintainLoop = false;
					}
					catch (NullPointerException npe)
					{
						bMaintainLoop = false;
						System.out
								.println("LookupTableMultiMapIntLoader NullPointerException! "
										+ npe.toString());
						npe.printStackTrace();
					}
				}
				
				if (!bMaintainLoop)
				{	
					return -1;
				}
			}
			
			iLineInFile++;
			
			// Update progress bar only on each 100th line
			if (iLineInFile % 1000 == 0)
			{
				swtGuiManager.setProgressBarPercentage(
						(int)(fProgressBarFactor * iLineInFile));
			}
		}

		return iLineInFile;
	}

	public void setMultiMapInteger(MultiHashArrayIntegerMap setMultiHashMap,
			EMappingType type)
	{

		// genomeIdManager.
		this.multiHashMapInteger = setMultiHashMap;
	}

	/**
	 * Write back data to IGenomeIdManager
	 * 
	 * @see org.caleydo.core.parser.ascii.lookuptable.ILookupTableLoader#wirteBackMapToGenomeManager()
	 * @see org.caleydo.core.manager.specialized.genome.IGenomeIdManager
	 */
	public void wirteBackMapToGenomeIdManager()
	{

		genomeIdManager.setMapByType(currentGenomeIdType, multiHashMapInteger);
	}

}
