/**
 * 
 */
package cerberus.xml.parser.handler.importer.ascii.lookuptable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import cerberus.base.map.MultiHashArrayMap;
import cerberus.data.mapping.GenomeMappingType;
import cerberus.manager.IGeneralManager;
import cerberus.xml.parser.handler.importer.ascii.LookupTableLoaderProxy;

/**
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class LookupTableIntIntMultiMapLoader 
extends ALookupTableLoader
implements ILookupTableLoader {

	protected MultiHashArrayMap refMultiHashMap;
	
	protected MultiHashArrayMap refMultiHashMap_reverse;
	
	/**
	 * Constructor.
	 * 
	 * @param refGeneralManager
	 * @param sFileName
	 * @param genomeType
	 * @param refLookupTableLoaderProxy
	 */
	public LookupTableIntIntMultiMapLoader(final IGeneralManager refGeneralManager,
			final String sFileName,
			final GenomeMappingType genomeType,
			final LookupTableLoaderProxy refLookupTableLoaderProxy ) {

		super(refGeneralManager, sFileName, genomeType, refLookupTableLoaderProxy);	
	}

	/*
	 * (non-Javadoc)
	 * @see cerberus.xml.parser.handler.importer.ascii.lookuptable.ALookupTableLoader#setMultiHashMap(cerberus.base.map.MultiHashArrayMap, boolean)
	 */
	public void setMultiHashMap(MultiHashArrayMap refMultiHashMap ,
			final boolean bIsReverse) {
		
		if (!bIsReverse)
		{
			this.refMultiHashMap = refMultiHashMap;
		}
		else 
		{
			this.refMultiHashMap_reverse = refMultiHashMap;
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
						int iFirst = new Integer( strTokenText.nextToken() );
						
						if  ( strTokenText.hasMoreTokens() ) 
						{
							int iSecond = new Integer(strTokenText.nextToken());
							
							refMultiHashMap.put(iFirst,iSecond);
							refMultiHashMap_reverse.put(iSecond, iFirst);
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
