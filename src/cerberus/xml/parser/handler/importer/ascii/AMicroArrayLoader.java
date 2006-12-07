/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */

package cerberus.xml.parser.handler.importer.ascii;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.ListIterator;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Vector;
import java.util.NoSuchElementException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import cerberus.manager.IGeneralManager;

//import java.util.*;

//import prometheus.data.DataStorageInterface;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.StorageType;
import cerberus.data.collection.ISet;
import cerberus.data.collection.IVirtualArray;
import cerberus.data.collection.virtualarray.VirtualArrayThreadSingleBlock;
import cerberus.data.xml.IMementoXML;
import cerberus.data.collection.parser.CollectionSelectionSaxParserHandler;
//import cerberus.data.collection.parser.ParserTokenType;
import cerberus.data.collection.parser.ParserTokenHandler;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.xml.parser.IParserObject;
import cerberus.xml.parser.ISaxParserHandler;


/**
 * Loader for MircoArray data sets in *.gpr format.
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class AMicroArrayLoader 
implements IMementoXML, IParserObject {
	
	

	
	/**
	 *  file name of *.gpr file
	 */  
	protected String sFileName;
	

	//protected IDataStorage refDataStorage;
	
	/**
	 * Imports data from file to this set.
	 * uses first storage and overwrites first selection.
	 */
	protected ISet refImportDataToSet;

	/**
	 * Define numbers of lines to skip as assumed to be the header of a file.
	 * Defines how many lines are part of the header file.
	 * By default these lines are skipped during parsing.
	 * Default is 32, because gpr files have a header of that size!
	 */
	protected int iStartParsingAtLine = 32;
	
	/**
	 * Define numbers of lines to skip as assumed to be the header of a file.
	 * Default is -1 which means until the end of file.
	 */
	protected int iStopParsingAtLine = Integer.MAX_VALUE;
	
	/**
	 * ref to singelton
	 */
	protected IGeneralManager refGeneralManager;
	
	/**
	 * Define the speperator
	 */
	protected String sTokenSeperator;
	
	/**
	 * Define, if exact file size need to be computed prior to loading the file.
	 * Default is fales.
	 * 
	 * @see AMicroArrayLoader#loadData_TestLinesToBeRead(BufferedReader)
	 */
	protected boolean bRequiredSizeOfReadableLines = false;
	
//	private LinkedList<Integer> LLInteger = null;
//	
//	private LinkedList<Float> LLFloat = null;
//	
//	private LinkedList<String> LLString = null;
	
	/** 
	 * Define the pattern for with values shall be paresed.
	 */
	protected ArrayList alTokenPattern;
	
	protected int iLineInFile = 1;
	protected int iLineInFile_CurrentDataIndex = 0;
	
	/**
	 * Defines where(in which MultiDataStorage) the parsed data shall be stored.
	 */
	protected ArrayList<ParserTokenHandler> alTokenTargetToParserTokenType;
	
	
	/**
	 * Default size for the token-ArrayList's
	 */
	private final int iInitialParseTokenSize = 8;
	
//	/**
//	 * Inistial size for the allocation of Gene-data sets
//	 */
//	private int iInitialArraylistSize = 39000;
	
	/*
	 * Defines index 
	 */
	protected int iIndexPerArray[];
	
	
	public AMicroArrayLoader(IGeneralManager setGeneralManager) {
				
		refGeneralManager = setGeneralManager;
		
		assert refGeneralManager!= null :"null-pointer in constructor";		
		
		this.sFileName = "";
		
		alTokenPattern =
			new ArrayList(iInitialParseTokenSize);
		
		alTokenTargetToParserTokenType = 
			new ArrayList<ParserTokenHandler> (iInitialParseTokenSize);
		
		iIndexPerArray = new int[iInitialParseTokenSize];
		
		/// TAB is the default token.
		sTokenSeperator = "\t";	
		
		//setTokenPattern("skip;skip;skip;string;string;skip;int;int;abort");
		
		init();
	}
	
	
	public AMicroArrayLoader(IGeneralManager setGeneralManager, String setFileName) {

		this(setGeneralManager);
		
		this.sFileName = setFileName;
		
		init();
	}
	
	
	public final void setFileName(String setFileName) {
		this.sFileName = setFileName;
	}
	
	public final String getFileName( ) {
		return this.sFileName;
	}
	

//	/**
//	 * 
//	 * @deprecated use setTargetSet(ISet) instead
//	 * 
//	 * @param setDataStorage reference to IStorage
//	 */
//	public final void setFileDataStorage(IStorage setDataStorage) {
//		this.refDataStorage = setDataStorage;
//	}
	
	/**
	 * Assign a ISet to write the data to.
	 * 
	 * @param refUseSet target set.
	 */
	public abstract void setTargetSet(ISet refUseSet);
	
	public final void setStartParsingStopParsingAtLine( final int iStartParsingAtLine,
			final int iStopParsingAtLine ) 
	{	
		this.iStartParsingAtLine = iStartParsingAtLine;
		
		if ( iStopParsingAtLine < 0 )
		{
			this.iStopParsingAtLine = Integer.MAX_VALUE;
			return;
		}
		
		if ( iStartParsingAtLine > iStopParsingAtLine )
		{
			this.iStopParsingAtLine = Integer.MAX_VALUE;
			refGeneralManager.getSingelton().getLoggerManager().logMsg(
					"AMicroArrayLoader.setStartParsingStopParsingAtLine() stop index is smaller than start index. set stop index to end of file!",
					LoggerType.MINOR_ERROR );			
			return;
		}		
		this.iStopParsingAtLine = iStopParsingAtLine;
	}
	
	public final int getStartParsingAtLine()
	{	
		return this.iStartParsingAtLine;
	}
	
	public final int getStopParsingAtLine()
	{	
		return this.iStopParsingAtLine;
	}
	
//	public void setFileDataStorage(MultiDataInterface setDataStorage) {
//		this.refDataStorage = setDataStorage;
//	}
//	
//	public MultiDataInterface getFileDataStorage( ) {
//		return this.refDataStorage;
//	}
	
	/**
	 * @param brFile input stream
	 * @param iNumberOfLinesInFile optional, number of lines in file, only valid if bRequiredSizeOfReadableLines==true
	 */
	protected abstract boolean loadDataParseFile( BufferedReader brFile,
			final int iNumberOfLinesInFile )
		throws IOException; 
	
	protected abstract boolean copyDataToInternalDataStructures();
	
	/**
	 * Reads the fiel and count the numbers of lines to be read.
	 * 
	 * @param brFile file handler
	 * @return number of liens in file to be read.
	 * @throws IOException
	 */
	private int loadData_TestLinesToBeRead(BufferedReader brFile) 
		throws IOException  
	{
		
		int iCountLinesToBeRead = 0;
		int iCountLines = 1;
		
		 while ( (( brFile.readLine()) != null)&&
		    		( iCountLines <= iStopParsingAtLine) )  
		    {
				if( iCountLines > this.iStartParsingAtLine ){
					iCountLinesToBeRead++;
					
					
				} // end of: if( iLineInFile > this.iHeaderLinesSize) {			
				
				iCountLines++;
		    }
		 
		 return iCountLinesToBeRead;
	}
	
	public boolean loadData() {			
		
		
		allocateStorageBufferForTokenPattern();
		
		int iNumberOfLinesInFile = -1;
		
		if ( bRequiredSizeOfReadableLines ) 
		{
			try {
			    BufferedReader brFile = 
				new BufferedReader( new FileReader( this.sFileName ) );
			   
			    iNumberOfLinesInFile = 
			    	loadData_TestLinesToBeRead( brFile );
			    
			    brFile.close();
			}
			catch (IOException ioe) {
				refGeneralManager.getSingelton().logMsg(
						"MicroArrayLoader: IO-error line=[" + iLineInFile +
						"] while testing file: " + ioe.toString(),
						LoggerType.MINOR_ERROR );
			    
			    return false;
			    //System.exit(1);
			}
			catch (Exception ex) {
				refGeneralManager.getSingelton().logMsg(
						"MicroArrayLoader: ERROR line=[" + iLineInFile +
						"] while testing file: " + ex.toString(),
						LoggerType.ERROR_ONLY );
				
				ex.printStackTrace();
			    return false;
			}		
		}
		
		
		try {
		    BufferedReader brFile = 
			new BufferedReader( new FileReader( this.sFileName ) );
		   
		    		
		    // sample line: 1110 Kybernetik
		    refGeneralManager.getSingelton().logMsg(
		    		"Read file \""+ 
				       this.sFileName + "\" ...",
				       LoggerType.VERBOSE );

		    this.loadDataParseFile( brFile, iNumberOfLinesInFile );
		    
//		    Vector <String> vecBufferText = new Vector<String>(10);
//		    StringBuffer strLineBuffer = new StringBuffer();
//		    
//			String sLine;
//			
//			
//		    while ( ((sLine = brFile.readLine()) != null)&&
//		    		( iLineInFile <= iStopParsingAtLine) )  
//		    {
//		    	
//				//index = sLine.indexOf(" ");
//				// 				h_oestat.put( line.substring(0, index), 
//				// 					      line.substring( index+1, 
//				// 							      line.indexOf("(", index+1) != -1 ? 
//				// 							      line.indexOf("(", index+1) : line.length());
//		    	
//		    	//TODO: remove next lines...
//				/*			
//		    	if ( sLine.length() > 0 ) 
//				    {
//						System.out.println( iLineInFile + 
//								": " + sLine);
//				    }
//				*/
//				
//				if( iLineInFile > this.iStartParsingAtLine ){
//					
//					boolean bMaintainLoop = true;
//					StringTokenizer strTokenText = new StringTokenizer(sLine,"\"");
//					
//					int iStringIndex = 0;
//					
//					strLineBuffer.setLength(0);
//					vecBufferText.clear();
//					
//					int iCountTokens = strTokenText.countTokens();
//					
//					if ( (iCountTokens % 2) == 0 ) {						
//						strTokenText = new StringTokenizer (sLine.replace( "\"\"", "\" \"") , "\"");
//						//System.out.println("Substitute [\"\"] ==> [\" \"] in line " + iLineInFile );
//					}
//					
//					/**
//					 * are there any tokens containing " 
//					 */
//					if ( iCountTokens > 1) {
//						strLineBuffer.append( strTokenText.nextToken() );
//						
//						boolean bToggle_Buffer = true;
//						
//						while ( strTokenText.hasMoreTokens() ) {
//							String sBuffer = strTokenText.nextToken().trim();
//							
//							if ( bToggle_Buffer ) {
//								vecBufferText.addElement( sBuffer );
//								bToggle_Buffer = false;
//							}
//							else {
//								strLineBuffer.append( sBuffer );
//								bToggle_Buffer = true;
//							}
//						}
//					} 
//					else {
//						strLineBuffer.append( sLine );
//					}
//					
//					
//					StringTokenizer strToken = new StringTokenizer( new String(strLineBuffer) );
//					ListIterator <ParserTokenHandler> iterPerLine = 
//						alTokenTargetToParserTokenType.listIterator();
//					
//					while (( strToken.hasMoreTokens() )&&(bMaintainLoop)) {
//						String sTokenObject = strToken.nextToken();
//
//						try {
//							ParserTokenHandler bufferIter = iterPerLine.next();
//						
//							//switch ( iterPerLine.next().getType() ) {
//							switch ( bufferIter.getType() ) {
//								//case SKIP: do nothing, only consume current token.
//								case ABORT:
//									bMaintainLoop = false;
//									break;
//								case INT:
//									LLInteger.add( new Integer(sTokenObject) );
//									break;
//								case FLOAT:
//									LLFloat.add( new Float(sTokenObject) );
//									break;
//								case STRING:	
//									LLString.add( vecBufferText.get(iStringIndex) );	
//									iStringIndex++;
//									break;
//								case SKIP:
//									break;
//								default:
//									System.err.println("Unknown label");
//									
//							} // end switch
//						
//						} catch ( NoSuchElementException  nsee) {
//							/* no ABORT was set. 
//							 * since no more tokens are in ParserTokenHandler skip rest of line..*/
//							bMaintainLoop = false;
//						}
//					
//					} // end of: while (( strToken.hasMoreTokens() )&&(bMaintainLoop)) {
//					
//					
//					iLineInFile_CurrentDataIndex++;
//					
//					
//				} // end of: if( iLineInFile > this.iHeaderLinesSize) {			
////				else {
////					System.out.println( 
////							" (" + Integer.toString( iLineInFile ) + "/ 0 /" +
////							Integer.toString( iHeaderLinesSize ) + "): " + sLine );					
////				}
//				
//				iLineInFile++;
//				
//			
//		    } // end: while ((sLine = brFile.readLine()) != null) { 
		    
		    if ( brFile != null ) {
		    	brFile.close();
		    }
		    
		   
		    
		    // sample line: E016|Zentrale Medienstelle|Media Centre|00
		    
		    refGeneralManager.getSingelton().logMsg(
		    		" read file \""+ 
				       this.sFileName + "\"  ....  [DONE]",
				     LoggerType.STATUS );

		    copyDataToInternalDataStructures();
		    
		 
		    
		    refGeneralManager.getSingelton().logMsg(
		    		"  Read file \""+ 
				       this.sFileName + "\" .... copy to storage ...[DONE]",
				       LoggerType.VERBOSE );
		    
		}
		catch (IOException ioe) {
			refGeneralManager.getSingelton().logMsg(
					"MicroArrayLoader: IO-error line=[" + iLineInFile +
					"] while parsing: " + ioe.toString(),
					LoggerType.MINOR_ERROR );
		    
		    return false;
		    //System.exit(1);
		}
		catch (Exception ex) {
			refGeneralManager.getSingelton().logMsg(
					"MicroArrayLoader: ERROR line=[" + iLineInFile +
					"] while parsing: " + ex.toString(),
					LoggerType.ERROR_ONLY );
			
			ex.printStackTrace();
		    return false;
		}		
		
		return true;
	}
	
	/**
	 * Define how many lines are part of the header
	 *  
	 * @param iSetHeaderLineSize
	 */
	public final void setHeaderLineSize( int iSetHeaderLineSize ) {
		
	}
	
	/** 
	 * Defines a pattern for parsing 
	 */
	public final boolean setTokenPattern( String tokenPattern ) {
		
		boolean bAllTokensProper = true;
		
		StringTokenizer tokenizer = new StringTokenizer(tokenPattern);

		final String sTokenPatternParserSeperator = ";";
		
		// wipe former binding...
		alTokenTargetToParserTokenType.clear();
		
		while (tokenizer.hasMoreTokens()) {
			String sBuffer =
				tokenizer.nextToken(sTokenPatternParserSeperator);

			
			if ( sBuffer.equalsIgnoreCase("abort")) {
				//MultiDataEnumType addType = MultiDataEnumType.ABORT;				
				
				alTokenTargetToParserTokenType.add( 
						new ParserTokenHandler( StorageType.ABORT) );
				
				return bAllTokensProper;				
			} 
			else if (sBuffer.equalsIgnoreCase("skip")) {								
				/// insert "mdnone" token to indicate skipping...
				//MultiDataEnumType addType = MultiDataEnumType.SKIP;
				
				alTokenTargetToParserTokenType.add( 
						new ParserTokenHandler( StorageType.SKIP ));							
			} 
			else if (sBuffer.equalsIgnoreCase("int")) {
				
				int iIndexFromType = StorageType.INT.ordinal();
				
				ParserTokenHandler addType = 
					new ParserTokenHandler(StorageType.INT,
							iIndexPerArray[iIndexFromType] );
				
				alTokenTargetToParserTokenType.add( addType );
			
				/// increment index...
				iIndexPerArray[iIndexFromType]++;
								          
			} 
			else if (sBuffer.equalsIgnoreCase("float")) {
				
				int iIndexFromType = StorageType.FLOAT.ordinal();
				
				ParserTokenHandler addType = 
					new ParserTokenHandler(StorageType.FLOAT,
							iIndexPerArray[iIndexFromType] );	
				
				alTokenTargetToParserTokenType.add( addType );
			
				/// increment index...
				iIndexPerArray[iIndexFromType]++;
								          
			}
			else if (sBuffer.equalsIgnoreCase("string")) {				
				
				int iIndexFromType = StorageType.STRING.ordinal();
				
				ParserTokenHandler addType = 
					new ParserTokenHandler(StorageType.STRING,								
							iIndexPerArray[iIndexFromType] );	
				
				alTokenTargetToParserTokenType.add( addType );
			
				/// increment index...
				iIndexPerArray[iIndexFromType]++;
								          
			} 
			else {
				// now common token!
				bAllTokensProper = false;
				
				System.err.println("MicroArrayLoader::setTokenPattern() WARNING: Unknown token [" +
						sBuffer + 
						"]");
			}
			
		} // end of while
		
		return bAllTokensProper;
	}
	
	
	protected abstract void allocateStorageBufferForTokenPattern( );

	
	public abstract boolean setMementoXML_usingHandler( 
			final ISaxParserHandler refSaxHandler );
	
	/**
	 * Removes all data structures.
	 * 
	 * @see cerberus.xml.parser.IParserObject#destroy()
	 */
	public abstract void destroy();


}
