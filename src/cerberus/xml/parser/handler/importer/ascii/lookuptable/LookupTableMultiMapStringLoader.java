/**
 * 
 */
package cerberus.xml.parser.handler.importer.ascii.lookuptable;

import java.io.BufferedReader;
import java.io.IOException;
//import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;

import cerberus.data.mapping.GenomeMappingType;
import cerberus.base.map.MultiHashArrayIntegerMap;
import cerberus.base.map.MultiHashArrayMap;
import cerberus.base.map.MultiHashArrayStringMap;
import cerberus.manager.IGeneralManager;
import cerberus.manager.command.factory.CommandFactory;
import cerberus.xml.parser.handler.importer.ascii.LookupTableLoaderProxy;


/**
 * @author Michael Kalkusch
 *
 */
public class LookupTableMultiMapStringLoader 
extends ALookupTableLoader
implements ILookupTableLoader {

	protected boolean bOneLineConaintsMultipleStrings = true;
	
	protected MultiHashArrayStringMap refMultiHashMap_StringString;
	
	protected MultiHashArrayIntegerMap refMultiHashArrayIntegerMap;
	
	/**
	 * @param setGeneralManager
	 * @param setFileName
	 */
	public LookupTableMultiMapStringLoader(final IGeneralManager setGeneralManager,
			final String setFileName,
			final GenomeMappingType genometype,
			final LookupTableLoaderProxy setLookupTableLoaderProxy) {

		super(setGeneralManager, setFileName, genometype, setLookupTableLoaderProxy);
		
		refLookupTableLoaderProxy.setTokenSeperator( 
				CommandFactory.sDelimiter_Parser_DataType);
	}

	
	public final void initLUT() {
		
		if ( refMultiHashMap_StringString == null )
		{
			refMultiHashMap_StringString = 
				new MultiHashArrayStringMap( iInitialSizeMultiHashMap );
		}
	}
	
	public void destroyLUT() {
		if ( refMultiHashArrayIntegerMap != null ) {
			/**
			 * Convert MultiMap<String> to Multimap<Integer>
			 */
			
			Set <String> refKeySet = 
				refMultiHashMap_StringString.keySet();
			
			if ( refKeySet == null ) {
				assert false : "WARNING: empty key-set!";
				return;
			}
			
			Iterator <String> iter = refKeySet.iterator();
			
			while ( iter.hasNext() ) 
			{
				String sKey = iter.next();
				
				ArrayList <String> alStringValue = 
					refMultiHashMap_StringString.get( sKey );							
				Iterator <String> iterValue = 
					alStringValue.iterator();
				
				ArrayList <Integer> alIntValue = 
					new ArrayList <Integer> ();
				
				while ( iterValue.hasNext() ) 
				{
					alIntValue.add(	
							Integer.valueOf( iterValue.next() ) );
					
				} // while ( iterValue.hasNext() )
				
				refMultiHashArrayIntegerMap.put( 
						Integer.valueOf(sKey),
						alIntValue );
				
			} // while ( iter.hasNext() ) 
			
			/* clean up MultiMap */
			refMultiHashMap_StringString.clear();
			
		} // if ( refMultiHashArrayIntegerMap != null ) {
	}
	
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.handler.importer.ascii.lookuptable.ILookupTableLoader#loadDataParseFileLUT(java.io.BufferedReader, int)
	 */
	public int loadDataParseFileLUT(BufferedReader brFile,
			final int iNumberOfLinesInFile) throws IOException {

		
		int iStartParsingAtLine = refLookupTableLoaderProxy.getStartParsingAtLine();
		int iStopParsingAtLine  = refLookupTableLoaderProxy.getStopParsingAtLine();
		
		if ( bOneLineConaintsMultipleStrings ) {
			return loadDataParseFileLUT_multipleStringPerLine(brFile,
					iNumberOfLinesInFile,
					iStartParsingAtLine,
					iStopParsingAtLine);
		}
		
		return loadDataParseFileLUT_oneStringPerLine(brFile,
				iNumberOfLinesInFile,
				iStartParsingAtLine,
				iStopParsingAtLine);
	}
	

	public void setMultiMap( final MultiHashArrayIntegerMap setHashMap,
			final GenomeMappingType type) {
	
		refMultiHashArrayIntegerMap = setHashMap;
	}
	
	protected int loadDataParseFileLUT_oneStringPerLine(BufferedReader brFile,
			final int iNumberOfLinesInFile,
			final int iStartParsingAtLine,
			final int iStopParsingAtLine ) throws IOException {

		String sLine;
		int iLineInFile = 1;
		
	    while ( ((sLine = brFile.readLine()) != null)&&
	    		( iLineInFile <= iStopParsingAtLine) )  
	    {
			
	    	/**
	    	 * Start parsing if current line 
	    	 * iLineInFile is larger than iStartParsingAtLine ..
	    	 */
			if( iLineInFile > iStartParsingAtLine ){
				
				boolean bMaintainLoop = true;
				StringTokenizer strTokenText = 
					new StringTokenizer(sLine, 
							refLookupTableLoaderProxy.getTokenSeperator());
				
				/**
				 * Read all tokens
				 */
				while (( strTokenText.hasMoreTokens() )&&(bMaintainLoop)) {
					
					/**
					 * Excpect two Integer values in one row!
					 */
					
					try {
						String sFirst = strTokenText.nextToken();
						
						if  ( strTokenText.hasMoreTokens() ) 
						{
							String sSecond = strTokenText.nextToken();
							
							refMultiHashMap_StringString.put( sFirst, sSecond);
						}
						
					
					} catch ( NoSuchElementException  nsee) {
						/* no ABORT was set. 
						 * since no more tokens are in ParserTokenHandler skip rest of line..*/
						bMaintainLoop = false;
					} catch ( NullPointerException npe ) {
						bMaintainLoop = false;
						System.out.println( "NullPointerException! " + npe.toString() );
						npe.printStackTrace();
					}
				
				} // end of: while (( strToken.hasMoreTokens() )&&(bMaintainLoop)) {
				
				
				
				refLookupTableLoaderProxy.progressBarStoredIncrement();
				
				if ( ! bMaintainLoop )
				{
					return -1;
				}
				
			} // end of: if( iLineInFile > this.iHeaderLinesSize) {			
			
			iLineInFile++;
			
	    } // end: while ((sLine = brFile.readLine()) != null) { 
	 
		return iLineInFile;
	}
	
	
	protected int loadDataParseFileLUT_multipleStringPerLine(BufferedReader brFile,
			final int iNumberOfLinesInFile,
			final int iStartParsingAtLine,
			final int iStopParsingAtLine ) throws IOException {
		
		final String sTokenDelimiterOuterLoop = refLookupTableLoaderProxy.getTokenSeperator();
		final String sTokenDelimiterInnerLoop = refLookupTableLoaderProxy.getTokenSeperatorInnerLoop();
		String sLine;
			
		int iLineInFile = 1;
		
	    while ( ((sLine = brFile.readLine()) != null)&&
	    		( iLineInFile <= iStopParsingAtLine) )  
	    {
			
	    	/**
	    	 * Start parsing if current line 
	    	 * iLineInFile is larger than iStartParsingAtLine ..
	    	 */
			if( iLineInFile > iStartParsingAtLine ){
				
				boolean bMaintainLoop = true;
				StringTokenizer strTokenText = 
					new StringTokenizer(sLine, sTokenDelimiterOuterLoop);
				
				/**
				 * Read all tokens
				 */
				while (( strTokenText.hasMoreTokens() )&&(bMaintainLoop)) {
					
					/**
					 * Excpect two Integer values in one row!
					 */
					
					try {
						String sFirst = strTokenText.nextToken();
						
						if  ( strTokenText.hasMoreTokens() ) 
						{
							StringTokenizer tokenizerInnerLoop = 
								new StringTokenizer( strTokenText.nextToken(),
										sTokenDelimiterInnerLoop );
							
							while ( tokenizerInnerLoop.hasMoreTokens() ) 
							{
								refMultiHashMap_StringString.put( sFirst, 
										tokenizerInnerLoop.nextToken() );
								
							} // while ( tokenizerInnerLoop.hasMoreTokens() ) 
							
						}
						
					
					} catch ( NoSuchElementException  nsee) {
						/* no ABORT was set. 
						 * since no more tokens are in ParserTokenHandler skip rest of line..*/
						bMaintainLoop = false;
					} catch ( NullPointerException npe ) {
						bMaintainLoop = false;
						System.out.println( "NullPointerException! " + npe.toString() );
						npe.printStackTrace();
					}
				
				} // end of: while (( strToken.hasMoreTokens() )&&(bMaintainLoop)) {
				
				refLookupTableLoaderProxy.progressBarStoredIncrement();
				
				if ( ! bMaintainLoop ) 
				{
					return -1;
				}
				
			} // end of: if( iLineInFile > this.iHeaderLinesSize) {			
			
			iLineInFile++;
		
	    } // end: while ((sLine = brFile.readLine()) != null) { 
	 
		return iLineInFile;
	}

	public void setHashMap_MultiStringString( MultiHashArrayStringMap  setHashMap ) {
		
		refMultiHashMap_StringString = (MultiHashArrayStringMap) setHashMap;
	}
	
	public MultiHashArrayStringMap getHashMap_MultiStringString( ) {
		
		return refMultiHashMap_StringString;
	}

	/**
	 * TRUE if only multple values my be in one line assigned to one key.
	 * 
	 * @see cerberus.xml.parser.handler.importer.ascii.lookuptable.LookupTableMultiMapStringLoader#setOneLineHasMultipleStrings(boolean)
	 * @return
	 */
	public boolean hasOneLineMultipleStrings() {
		
		return bOneLineConaintsMultipleStrings;
	}
	
	/**
	 * Switch parser between two modes parsing one STRING per line (FALSE) or parsing multiple STRINGS in one line (TRUE)
	 * TRUE: [key, value1 value2 ... value_n] <br>
	 * FALSE: [key,value1]<br>
	 *        [key1, value2] <br>
	 *         .. <br>
	 *        [key1, value_n] <br>
	 *        
	 * @see cerberus.xml.parser.handler.importer.ascii.lookuptable.LookupTableMultiMapStringLoader#hasOneLineMultipleStrings()
	 * 
	 * @param bset TRUE for parsing multiple Strings per line
	 */
	public void setOneLineHasMultipleStrings( final boolean bset ) {	
		bOneLineConaintsMultipleStrings = bset;
	}
	
}
