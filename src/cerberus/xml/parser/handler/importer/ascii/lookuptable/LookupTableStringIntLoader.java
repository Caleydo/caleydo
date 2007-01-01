/**
 * 
 */
package cerberus.xml.parser.handler.importer.ascii.lookuptable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import cerberus.data.mapping.GenomeMappingType;
import cerberus.manager.IGeneralManager;
import cerberus.xml.parser.handler.importer.ascii.LookupTableLoaderProxy;

/**
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class LookupTableStringIntLoader 
extends ALookupTableLoader
implements ILookupTableLoader {

	protected HashMap <String, Integer> refHashMap;
	
	protected HashMap <Integer, String> refHashMap_reverse;
	
	/**
	 * Constructor.
	 * 
	 * @param refGeneralManager
	 * @param sFileName
	 * @param genomeType
	 * @param refLookupTableLoaderProxy
	 */
	public LookupTableStringIntLoader(final IGeneralManager refGeneralManager,
			final String sFileName,
			final GenomeMappingType genomeType,
			final LookupTableLoaderProxy refLookupTableLoaderProxy ) {

		super(refGeneralManager, sFileName, genomeType, refLookupTableLoaderProxy);	
	}

	/*
	 * (non-Javadoc)
	 * @see cerberus.xml.parser.handler.importer.ascii.lookuptable.ALookupTableLoader#setHashMap_StringInteger(java.util.HashMap, boolean)
	 */
	public void setHashMap_StringInteger(HashMap refHashMap,
			final boolean bIsReverse) {
		
		if (!bIsReverse)
		{
			this.refHashMap = (HashMap<String, Integer>) refHashMap;
		}
		else 
		{
			this.refHashMap_reverse = (HashMap<Integer, String>) refHashMap;
		}
	}
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.handler.importer.ascii.lookuptable.ILookupTableLoader#loadDataParseFileLUT(java.io.BufferedReader, int)
	 */
	public boolean loadDataParseFileLUT(BufferedReader brFile,
			final int iNumberOfLinesInFile) throws IOException {

		String sLine;
		int iLineInFile = 1;
		int iStartParsingAtLine = refLookupTableLoaderProxy.getStartParsingAtLine();
		int iStopParsingAtLine  = refLookupTableLoaderProxy.getStopParsingAtLine();
		
	    while ( ((sLine = brFile.readLine()) != null) &&
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
							int iSecond = new Integer(strTokenText.nextToken());
							
							refHashMap.put(sFirst, iSecond);
							refHashMap_reverse.put(iSecond, sFirst);
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
				
			} // end of: if( iLineInFile > this.iHeaderLinesSize) {			
			
			iLineInFile++;
			
		
	    } // end: while ((sLine = brFile.readLine()) != null) { 
	 
		return true;
	}
}
