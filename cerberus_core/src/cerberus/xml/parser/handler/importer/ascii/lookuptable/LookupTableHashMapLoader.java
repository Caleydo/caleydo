/**
 * 
 */
package cerberus.xml.parser.handler.importer.ascii.lookuptable;

import java.io.BufferedReader;
import java.io.IOException;
//import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import cerberus.data.mapping.GenomeMappingType;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.data.genome.IGenomeIdMap;
import cerberus.xml.parser.handler.importer.ascii.LookupTableLoaderProxy;


/**
 * @author Michael Kalkusch
 *
 */
public class LookupTableHashMapLoader 
extends ALookupTableLoader
implements ILookupTableLoader {
	
	protected IGenomeIdMap refGenomeIdMap;
	
	/**
	 * @param setGeneralManager
	 * @param setFileName
	 */
	public LookupTableHashMapLoader(final IGeneralManager setGeneralManager,
			final String setFileName,
			final GenomeMappingType genomeIdType,
			final LookupTableLoaderProxy setLookupTableLoaderProxy ) {

		super(setGeneralManager, setFileName, genomeIdType, setLookupTableLoaderProxy);
	}


	/* (non-Javadoc)
	 * @see cerberus.xml.parser.handler.importer.ascii.lookuptable.ILookupTableLoader#loadDataParseFileLUT(java.io.BufferedReader, int)
	 */
	public int loadDataParseFileLUT(BufferedReader brFile,
			int iNumberOfLinesInFile ) throws IOException {

		String sLine;
		
		int iLineInFile = 1;
		int iStartParsingAtLine = refLookupTableLoaderProxy.getStartParsingAtLine();
		int iStopParsingAtLine  = refLookupTableLoaderProxy.getStopParsingAtLine();		
		String sOuterTokenSeperator = refLookupTableLoaderProxy.getTokenSeperator();
		
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
					new StringTokenizer(sLine, sOuterTokenSeperator );
				
				/**
				 * Read all tokens
				 */
				while (( strTokenText.hasMoreTokens() )&&(bMaintainLoop)) {
					
					/**
					 * Excpect two Integer values in one row!
					 */
					
					try {
						String buffer = strTokenText.nextToken();
						
						if  ( strTokenText.hasMoreTokens() ) 
						{
							refGenomeIdMap.put(buffer,strTokenText.nextToken());
						}
						else
						{
							refGeneralManager.getSingelton().logMsg(
									"(Key,Value) [" +
									buffer + ", ?? ] value is missing (ignore key-value pair) in line " +
									iLineInFile,
									LoggerType.FULL);
						}
						
					
					} catch ( NoSuchElementException  nsee) {
						/* no ABORT was set. 
						 * since no more tokens are in ParserTokenHandler skip rest of line..*/
						bMaintainLoop = false;
						
						//reset return value to indicate error
						iStopParsingAtLine = -1;
						
					} catch ( NullPointerException npe ) {
						bMaintainLoop = false;
						
						//reset return value to indicate error
						iStopParsingAtLine = 1;
						
						System.out.println( "NullPointerException! " + npe.toString() );
						npe.printStackTrace();
						
					}
				
				} // end of: while (( strToken.hasMoreTokens() )&&(bMaintainLoop)) {
				
				
				refLookupTableLoaderProxy.progressBarStoredIncrement();
				
			} // end of: if( iLineInFile > this.iHeaderLinesSize) {			
			
			iLineInFile++;
			
	    } // end: while ((sLine = brFile.readLine()) != null) { 
	 
		return iLineInFile - iStartParsingAtLine;
	}
	
	public final void setHashMap( final IGenomeIdMap setHashMap,
			final GenomeMappingType type) {
		
		assert type == currentGenomeIdType : "must use same type as in constructor!";
		
		if ( type.isMultiMap() )
		{
			assert false : "setHashMap() must not call MultiMap via setHashMap()";
			return;
		}
		
		refGenomeIdMap = setHashMap;		
	}


	/**
	 * Write back data to IGenomeIdManager
	 * @see cerberus.xml.parser.handler.importer.ascii.lookuptable.ILookupTableLoader#wirteBackMapToGenomeManager()
	 * @see cerberus.manager.data.IGenomeIdManager
	 */
	public void wirteBackMapToGenomeIdManager() {
		
		refGenomeIdManager.setMapByType(currentGenomeIdType, refGenomeIdMap);
	}
}
