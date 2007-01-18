/**
 * 
 */
package cerberus.xml.parser.handler.importer.ascii;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import cerberus.base.map.MultiHashArrayMap;
import cerberus.data.collection.parser.ParserTokenHandler;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.xml.parser.ISaxParserHandler;
import cerberus.xml.parser.handler.importer.ascii.AbstractLoader;


/**
 * @author kalkusch
 *
 * @deprecated use LookupTableLoaderProxy instead!
 *
 */
public class LookupTableLoader extends AbstractLoader {

	protected MultiHashArrayMap refHashMap;
	
	/**
	 * @param setGeneralManager
	 * @param setFileName
	 */
	public LookupTableLoader(IGeneralManager setGeneralManager,
			String setFileName) {

		super(setGeneralManager, setFileName);
		
		super.bRequiredSizeOfReadableLines = true;		
	}

	public void setHashMap( MultiHashArrayMap refHashMap ) {
		this.refHashMap = refHashMap;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.xml.parser.handler.importer.ascii.AbstractLoader#loadDataParseFile(java.io.BufferedReader, int)
	 */
	@Override
	protected boolean loadDataParseFile(BufferedReader brFile,
			final int iNumberOfLinesInFile) throws IOException {

	
			/**
			 * progress bar init
			 */
			progressBarSetStoreInitTitle("load " + this.getFileName(),
					0,  // reset progress bar to 0
					getLinesInCurrentFileToBeRead());
			
			String sLine;
			
		    while ( ((sLine = brFile.readLine()) != null)&&
		    		( iLineInFile <= iStopParsingAtLine) )  
		    {
		    	
				//index = sLine.indexOf(" ");
				// 				h_oestat.put( line.substring(0, index), 
				// 					      line.substring( index+1, 
				// 							      line.indexOf("(", index+1) != -1 ? 
				// 							      line.indexOf("(", index+1) : line.length());
		    	
		    	//TODO: remove next lines...
				/*			
		    	if ( sLine.length() > 0 ) 
				    {
						System.out.println( iLineInFile + 
								": " + sLine);
				    }
				*/
				
				if( iLineInFile > this.iStartParsingAtLine ){
					
					boolean bMaintainLoop = true;
					StringTokenizer strTokenText = 
						new StringTokenizer(sLine, sTokenSeperator);
					
					while (( strTokenText.hasMoreTokens() )&&(bMaintainLoop)) {
						
						/**
						 * Excpect two Integer values in one row!
						 */
						
						try {
							int iFirst = new Integer( strTokenText.nextToken() );
							
							if  ( strTokenText.hasMoreTokens() ) 
							{
								int iSecond = new Integer(strTokenText.nextToken());
								
								refHashMap.put(iFirst,iSecond);
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
					
					
					iLineInFile_CurrentDataIndex++;
					
					progressBarStoredIncrement();
					
				} // end of: if( iLineInFile > this.iHeaderLinesSize) {			
	//			else {
	//				System.out.println( 
	//						" (" + Integer.toString( iLineInFile ) + "/ 0 /" +
	//						Integer.toString( iHeaderLinesSize ) + "): " + sLine );					
	//			}
				
				iLineInFile++;
				
			
		    } // end: while ((sLine = brFile.readLine()) != null) { 
		    
		refGeneralManager.getSingelton().logMsg("  parsed #" + 
				this.iLineInFile_CurrentDataIndex + "  [" + 			
				this.iStartParsingAtLine + " -> " +
				this.iStopParsingAtLine +  "] stoped at line #" +
				(this.iLineInFile-1),
				LoggerType.VERBOSE );				
		
		/**
		 * reset progressbar...
		 */
		progressBarResetTitle();		
		progressBarIncrement(5);
		
		return true;
	}

	/* (non-Javadoc)
	 * @see cerberus.xml.parser.handler.importer.ascii.AbstractLoader#copyDataToInternalDataStructures()
	 */
	@Override
	protected boolean copyDataToInternalDataStructures() {

		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.xml.IMementoXML#setMementoXML_usingHandler(cerberus.xml.parser.ISaxParserHandler)
	 */
	public boolean setMementoXML_usingHandler(ISaxParserHandler refSaxHandler) {

		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.xml.parser.IParserObject#init()
	 */
	public void init() {

		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.xml.parser.IParserObject#destroy()
	 */
	public void destroy() {

		// TODO Auto-generated method stub

	}

}
