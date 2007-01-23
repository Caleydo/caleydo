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
import cerberus.base.map.MultiHashArrayStringMap;
import cerberus.manager.IGeneralManager;
import cerberus.manager.command.factory.CommandFactory;
import cerberus.xml.parser.handler.importer.ascii.LookupTableLoaderProxy;


/**
 * @author Michael Kalkusch
 *
 */
public class LookupTableStringStringMultiMapLoader 
extends ALookupTableLoader
implements ILookupTableLoader {

	protected boolean bOneLineConaintsMultipleStrings = false;
	
	protected MultiHashArrayStringMap refMultiHashMap_StringString;
	
	/**
	 * @param setGeneralManager
	 * @param setFileName
	 */
	public LookupTableStringStringMultiMapLoader(final IGeneralManager setGeneralManager,
			final String setFileName,
			final GenomeMappingType genometype,
			final LookupTableLoaderProxy setLookupTableLoaderProxy) {

		super(setGeneralManager, setFileName, genometype, setLookupTableLoaderProxy);
		
		refLookupTableLoaderProxy.setTokenSeperator( 
				CommandFactory.sDelimiter_Parser_DataType);
	}

	/* (non-Javadoc)
	 * @see cerberus.xml.parser.handler.importer.ascii.lookuptable.ILookupTableLoader#loadDataParseFileLUT(java.io.BufferedReader, int)
	 */
	public boolean loadDataParseFileLUT(BufferedReader brFile,
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
	
	
	protected boolean loadDataParseFileLUT_oneStringPerLine(BufferedReader brFile,
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
				
			} // end of: if( iLineInFile > this.iHeaderLinesSize) {			
			
			iLineInFile++;
			
	    } // end: while ((sLine = brFile.readLine()) != null) { 
	 
		return true;
	}
	
	
	protected boolean loadDataParseFileLUT_multipleStringPerLine(BufferedReader brFile,
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
				
			} // end of: if( iLineInFile > this.iHeaderLinesSize) {			
			
			iLineInFile++;
		
	    } // end: while ((sLine = brFile.readLine()) != null) { 
	 
		return true;
	}

	
//	public void setHashMap_MultiStringString( HashMap  <String,String> setHashMap ) {
//		Class buffer = setHashMap.getClass();
//		
//		//this.refMultiHashMap_StringString = setHashMap;
//	}

}
