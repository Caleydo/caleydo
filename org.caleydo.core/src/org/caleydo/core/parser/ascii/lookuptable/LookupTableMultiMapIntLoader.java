/**
 * 
 */
package org.caleydo.core.parser.ascii.lookuptable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.caleydo.core.data.map.MultiHashArrayIntegerMap;
import org.caleydo.core.data.mapping.EGenomeMappingType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.ascii.lookuptable.ALookupTableLoader;
import org.caleydo.core.parser.ascii.lookuptable.ILookupTableLoader;


/**
 * @author Michael Kalkusch
 *
 */
public class LookupTableMultiMapIntLoader extends ALookupTableLoader
		implements ILookupTableLoader {

	protected MultiHashArrayIntegerMap multiHashMapInteger;
	
	/**
	 * @param setGeneralManager
	 * @param setFileName
	 */
	public LookupTableMultiMapIntLoader(final IGeneralManager setGeneralManager,
			final String setFileName,
			final EGenomeMappingType genomeIdType,
			final LookupTableLoaderProxy setLookupTableLoaderProxy) {

		super(setGeneralManager, setFileName, genomeIdType, setLookupTableLoaderProxy);
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.parser.ascii.lookuptable.ILookupTableLoader#loadDataParseFileLUT(java.io.BufferedReader, int)
	 */
	public int loadDataParseFileLUT(BufferedReader brFile,
			final int iNumberOfLinesInFile) throws IOException {

		String sLine;
		int iLineInFile = 1;
		int iStartParsingAtLine = lookupTableLoaderProxy.getStartParsingAtLine();
		int iStopParsingAtLine  = lookupTableLoaderProxy.getStopParsingAtLine();
		
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
							lookupTableLoaderProxy.getTokenSeperator());
				
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
							
							multiHashMapInteger.put(iFirst,iSecond);
						}
						
					
					} catch ( NoSuchElementException  nsee) {
						/* no ABORT was set. 
						 * since no more tokens are in ParserTokenHandler skip rest of line..*/
						bMaintainLoop = false;
					} catch ( NullPointerException npe ) {
						bMaintainLoop = false;
						System.out.println( "LookupTableMultiMapIntLoader NullPointerException! " + npe.toString() );
						npe.printStackTrace();
					}
				
				} // end of: while (( strToken.hasMoreTokens() )&&(bMaintainLoop)) {
				
		
				 if ( ! bMaintainLoop ) {
					lookupTableLoaderProxy.progressBarStoredIncrement();
				    return -1;
				 }
				 
				lookupTableLoaderProxy.progressBarStoredIncrement();
				
			} // end of: if( iLineInFile > this.iHeaderLinesSize) {			
			
			iLineInFile++;
			
		
	    } // end: while ((sLine = brFile.readLine()) != null) { 
	 
	   
		return iLineInFile;
	}
	
	public void setMultiMapInteger( MultiHashArrayIntegerMap setMultiHashMap,
			EGenomeMappingType type ) {
		
		//genomeIdManager.
		this.multiHashMapInteger = setMultiHashMap;
	}	
	
	/**
	 * Write back data to IGenomeIdManager
	 * @see org.caleydo.core.parser.ascii.lookuptable.ILookupTableLoader#wirteBackMapToGenomeManager()
	 * @see org.caleydo.core.manager.specialized.genome.IGenomeIdManager
	 */
	public void wirteBackMapToGenomeIdManager() {

		genomeIdManager.setMapByType(currentGenomeIdType, multiHashMapInteger);
	}

}
