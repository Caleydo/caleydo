package org.caleydo.core.parser.ascii.lookuptable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import org.caleydo.core.data.map.MultiHashArrayIntegerMap;
import org.caleydo.core.data.map.MultiHashArrayStringMap;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.manager.IGeneralManager;

/**
 * Multi hash map lookup table loader.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class LookupTableMultiMapStringLoader
	extends ALookupTableLoader
	implements ILookupTableLoader
{

	/**
	 * Switch between loadDataParseFileLUT_multipleStringPerLine() == TRUE and
	 * loadDataParseFileLUT_oneStringPerLine()and == FALSE
	 * 
	 * @see org.caleydo.core.parser.ascii.lookuptable.LookupTableMultiMapStringLoader#loadDataParseFileLUT_oneStringPerLine(BufferedReader,
	 *      int, int, int)
	 * @see org.caleydo.core.parser.ascii.lookuptable.LookupTableMultiMapStringLoader#loadDataParseFileLUT_multipleStringPerLine(BufferedReader,
	 *      int, int, int)
	 */
	protected boolean bOneLineConaintsMultipleStrings = true;

	protected MultiHashArrayStringMap multiHashMapString;

	protected MultiHashArrayIntegerMap multiHashMapInteger;

	/**
	 * Constructor.
	 */
	public LookupTableMultiMapStringLoader(final IGeneralManager setGeneralManager,
			final String setFileName, final EMappingType genomeIdType,
			final LookupTableLoaderProxy setLookupTableLoaderProxy)
	{

		super(setGeneralManager, setFileName, genomeIdType, setLookupTableLoaderProxy);
	}

	protected int loadDataParseFileLUT_oneStringPerLine(BufferedReader brFile,
			final int iNumberOfLinesInFile, final int iStartParsingAtLine,
			final int iStopParsingAtLine) throws IOException
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
				StringTokenizer strTokenText = new StringTokenizer(sLine,
						lookupTableLoaderProxy.getTokenSeperator());

				/**
				 * Read all tokens
				 */
				while ((strTokenText.hasMoreTokens()) && (bMaintainLoop))
				{

					/**
					 * Expect two Integer values in one row!
					 */

					try
					{
						String sFirst = strTokenText.nextToken();

						if (strTokenText.hasMoreTokens())
						{
							String sSecond = strTokenText.nextToken();

							multiHashMapString.put(sFirst, sSecond);
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
								.println("LookupTableMultiMapStringLoader NullPointerException! "
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
				generalManager.getSWTGUIManager().setProgressBarPercentage(
						(int)(fProgressBarFactor * iLineInFile));
			}
		}
		return iLineInFile;
	}

	protected int loadDataParseFileLUT_multipleStringPerLine(BufferedReader brFile,
			final int iNumberOfLinesInFile, final int iStartParsingAtLine,
			final int iStopParsingAtLine) throws IOException
	{

		final String sTokenDelimiterOuterLoop = lookupTableLoaderProxy.getTokenSeperator();
		final String sTokenDelimiterInnerLoop = lookupTableLoaderProxy.getTokenSeperator();// getTokenSeperatorInnerLoop
		// (
		// )
		// ;
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
				StringTokenizer strTokenText = new StringTokenizer(sLine,
						sTokenDelimiterOuterLoop);

				/**
				 * Read all tokens
				 */
				while ((strTokenText.hasMoreTokens()) && (bMaintainLoop))
				{

					/**
					 * Expect two Integer values in one row!
					 */

					try
					{
						String sFirst = strTokenText.nextToken();

						if (strTokenText.hasMoreTokens())
						{
							StringTokenizer tokenizerInnerLoop = new StringTokenizer(
									strTokenText.nextToken(), sTokenDelimiterInnerLoop);

							while (tokenizerInnerLoop.hasMoreTokens())
							{
								multiHashMapString.put(sFirst, tokenizerInnerLoop.nextToken());

							} // while ( tokenizerInnerLoop.hasMoreTokens() )

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
								.println("LookupTableMultiMapStringLoader NullPointerException! "
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
				generalManager.getSWTGUIManager().setProgressBarPercentage(
						(int)(fProgressBarFactor * iLineInFile));
			}
		}

		return iLineInFile;
	}

	public final void initLUT()
	{

		if (multiHashMapString == null)
		{
			multiHashMapString = new MultiHashArrayStringMap(iInitialSizeMultiHashMap);
		}
	}

	public final void destroyLUT()
	{

		// TODO implement

		// if ( multiHashMapInteger != null ) {
		// /**
		// * Convert MultiMap<String> to Multimap<Integer>
		// */
		//			
		// IGenomeIdManager gidmng = generalManager.getGenomeIdManager();
		//			
		// EIDType originType = this.currentGenomeIdType.getTypeOrigin();
		// EIDType targetType = this.currentGenomeIdType.getTypeTarget();
		//			
		// IGenomeIdMap originMap = gidmng.getMapByType(
		// originType.getBasicConversion() );
		// IGenomeIdMap targetMap = gidmng.getMapByType(
		// targetType.getBasicConversion() );
		//			
		//			
		// Set <String> keySet =
		// multiHashMapString.keySet();
		//			
		// if ( keySet == null ) {
		// assert false : "WARNING: empty key-set!";
		// return;
		// }
		//			
		// Iterator <String> iter = keySet.iterator();
		//			
		// while ( iter.hasNext() )
		// {
		// String sKey = iter.next();
		//				
		// ArrayList <String> alStringValue =
		// multiHashMapString.get( sKey );
		// Iterator <String> iterValue =
		// alStringValue.iterator();
		//				
		// ArrayList <Integer> alIntValue =
		// new ArrayList <Integer> ();
		//				
		// while ( iterValue.hasNext() )
		// {
		// alIntValue.add(
		// targetMap.getIntByString(iterValue.next()) );
		//					
		// } // while ( iterValue.hasNext() )
		//				
		// multiHashMapInteger.put(
		// originMap.getIntByString(sKey),
		// alIntValue );
		//				
		// } // while ( iter.hasNext() )
		//			
		// /* clean up MultiMap */
		// multiHashMapString.clear();
		//			
		// } // if ( multiHashMapInteger != null ) {
	}

	/**
	 * TRUE if only multple values my be in one line assigned to one key.
	 * 
	 * @see org.caleydo.core.parser.ascii.lookuptable.LookupTableMultiMapStringLoader#setOneLineHasMultipleStrings(boolean)
	 * @return
	 */
	public final boolean hasOneLineMultipleStrings()
	{

		return bOneLineConaintsMultipleStrings;
	}

	/**
	 * Switch parser between two modes parsing one STRING per line (FALSE) or
	 * parsing multiple STRINGS in one line (TRUE) TRUE: [key, value1 value2 ...
	 * value_n] <br>
	 * FALSE: [key,value1]<br>
	 * [key1, value2] <br>
	 * .. <br>
	 * [key1, value_n] <br>
	 * 
	 * @see org.caleydo.core.parser.ascii.lookuptable.LookupTableMultiMapStringLoader#hasOneLineMultipleStrings()
	 * @param bset TRUE for parsing multiple Strings per line
	 */
	public final void setOneLineHasMultipleStrings(final boolean bset)
	{

		bOneLineConaintsMultipleStrings = bset;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.caleydo.core.parser.ascii.lookuptable.ILookupTableLoader#
	 * loadDataParseFileLUT(java.io.BufferedReader, int)
	 */
	public int loadDataParseFileLUT(BufferedReader brFile, final int iNumberOfLinesInFile)
			throws IOException
	{

		int iStartParsingAtLine = lookupTableLoaderProxy.getStartParsingAtLine();
		int iStopParsingAtLine = lookupTableLoaderProxy.getStopParsingAtLine();

		if (bOneLineConaintsMultipleStrings)
		{
			return loadDataParseFileLUT_multipleStringPerLine(brFile, iNumberOfLinesInFile,
					iStartParsingAtLine, iStopParsingAtLine);
		}

		return loadDataParseFileLUT_oneStringPerLine(brFile, iNumberOfLinesInFile,
				iStartParsingAtLine, iStopParsingAtLine);
	}

	// MARC: Changed method from Integer to String because we are
	// in the MultiMapString class.
	public void setMultiMapString(final MultiHashArrayStringMap setHashMap,
			final EMappingType type)
	{

		multiHashMapString = setHashMap;
	}

	/**
	 * Write back data to IGenomeIdManager
	 * 
	 * @see org.caleydo.core.parser.ascii.lookuptable.ILookupTableLoader#wirteBackMapToGenomeManager()
	 * @see org.caleydo.core.manager.specialized.genome.IGenomeIdManager
	 */
	public void wirteBackMapToGenomeIdManager()
	{

		genomeIdManager.setMapByType(currentGenomeIdType, multiHashMapString);
	}

}
