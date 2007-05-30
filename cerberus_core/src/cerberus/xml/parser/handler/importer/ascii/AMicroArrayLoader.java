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
import java.util.LinkedList;
import java.util.StringTokenizer;
//import java.util.ListIterator;
//import java.util.LinkedList;
//import java.util.Iterator;
//import java.util.Vector;
//import java.util.NoSuchElementException;

import java.io.BufferedReader;
//import java.io.FileReader;s
import java.io.IOException;

import cerberus.manager.IGeneralManager;

//import java.util.*;

//import prometheus.data.DataStorageInterface;
//import cerberus.data.collection.IStorage;
import cerberus.data.collection.StorageType;
import cerberus.data.collection.ISet;
//import cerberus.data.collection.IVirtualArray;
//import cerberus.data.collection.virtualarray.VirtualArrayThreadSingleBlock;
import cerberus.data.xml.IMementoXML;
//import cerberus.data.collection.parser.CollectionSelectionSaxParserHandler;
//import cerberus.data.collection.parser.ParserTokenType;
import cerberus.data.collection.parser.ParserTokenHandler;
//import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.xml.parser.IParserObject;
import cerberus.xml.parser.ISaxParserHandler;
import cerberus.xml.parser.handler.importer.ascii.AbstractLoader;


/**
 * Loader for MircoArray data sets in *.gpr format.
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class AMicroArrayLoader 
extends AbstractLoader
implements IMementoXML, IParserObject {

	protected LinkedList<Integer> LLInteger = null;
	
	protected LinkedList<Float> LLFloat = null;
	
	protected LinkedList<String> LLString = null;
	
	
	//protected IDataStorage refDataStorage;
	
	/**
	 * Imports data from file to this set.
	 * uses first storage and overwrites first selection.
	 */
	protected ISet refImportDataToSet;

	
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
	
	
	protected AMicroArrayLoader(final IGeneralManager setGeneralManager, 
			final String setFileName,
			final boolean enableMultipeThreads) {

		super(setGeneralManager,
				setFileName, 
				enableMultipeThreads);
		
		alTokenTargetToParserTokenType = 
			new ArrayList<ParserTokenHandler> (iInitialParseTokenSize);
		
		iIndexPerArray = new int[iInitialParseTokenSize];
		
		init();
	}
	
	
	/**
	 * Assign a ISet to write the data to.
	 * 
	 * @param refUseSet target set.
	 */
	public abstract void setTargetSet(ISet refUseSet);
	
	
	
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
	protected abstract int loadDataParseFile( BufferedReader brFile,
			final int iNumberOfLinesInFile )
		throws IOException; 
	
	//protected abstract boolean copyDataToInternalDataStructures();

	
//	public boolean loadData() {			
//		
//		
//		allocateStorageBufferForTokenPattern();
//		
//		int iNumberOfLinesInFile = -1;
//		
//		if ( bRequiredSizeOfReadableLines ) 
//		{
//			iNumberOfLinesInFile = 
//				this.loadData_TestLinesToBeRead( this.getFileName() );
//		}
//		
//		
//		try {
//		    BufferedReader brFile = 
//			new BufferedReader( new FileReader( this.getFileName() ) );
//		   
//		    		
//		    // sample line: 1110 Kybernetik
//		    refGeneralManager.getSingelton().logMsg(
//		    		"Read file \""+ 
//				       this.getFileName() + "\" ...",
//				       LoggerType.VERBOSE );
//
//		    this.loadDataParseFile( brFile, iNumberOfLinesInFile );
//		    
//		    if ( brFile != null ) {
//		    	brFile.close();
//		    }
//		    
//		   
//		    
//		    // sample line: E016|Zentrale Medienstelle|Media Centre|00
//		    
//		    refGeneralManager.getSingelton().logMsg(
//		    		" read file \""+ 
//				       this.getFileName() + "\"  ....  [DONE]",
//				     LoggerType.STATUS );
//
//		    copyDataToInternalDataStructures();
//		    
//		    refGeneralManager.getSingelton().logMsg(
//		    		"  Read file \""+ 
//				       this.getFileName() + "\" .... copy to storage ...[DONE]",
//				       LoggerType.VERBOSE );
//		    
//		}
//		catch (IOException ioe) {
//			refGeneralManager.getSingelton().logMsg(
//					"MicroArrayLoader: IO-error line=[" + iLineInFile +
//					"] while parsing: " + ioe.toString(),
//					LoggerType.MINOR_ERROR );
//		    
//		    return false;
//		    //System.exit(1);
//		}
//		catch (Exception ex) {
//			refGeneralManager.getSingelton().logMsg(
//					"MicroArrayLoader: ERROR line=[" + iLineInFile +
//					"] while parsing: " + ex.toString(),
//					LoggerType.ERROR_ONLY );
//			
//			ex.printStackTrace();
//		    return false;
//		}		
//		
//		return true;
//	}

	
	/** 
	 * Defines a pattern for parsing 
	 */
	public final boolean setTokenPattern( String tokenPattern ) {
		
		boolean bAllTokensProper = true;
		
		StringTokenizer tokenizer = new StringTokenizer(tokenPattern);

		final String sTokenPatternParserSeperator = 
			IGeneralManager.sDelimiter_Parser_DataType;
		
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


	protected final void allocateStorageBufferForTokenPatternAbstractClass( ) {
		
		if ( LLInteger == null ) 
		{
			LLInteger = new LinkedList<Integer>(); 
		}
		if ( LLFloat == null ) 
		{
			LLFloat = new LinkedList<Float>(); 
		}
		if ( LLString == null ) 
		{
			LLString = new LinkedList<String>(); 
		}
		
	}

	/**
	 * Removes all data structures.
	 * 
	 * @see cerberus.xml.parser.IParserObject#destroy()
	 * @see cerberus.xml.parser.handler.importer.ascii.AMicroArrayLoader#destroy()
	 */
	public void destroy() {
		
		if ( LLInteger != null ) 
		{
			LLInteger.clear();	
		}
		if ( LLFloat!= null ) 
		{
		LLFloat.clear();
		}
		if ( LLString != null ) 
		{
			LLString.clear();
		}
		
		if ( alTokenTargetToParserTokenType != null ) {
			alTokenTargetToParserTokenType.clear();
		}
		
		LLInteger = null;		
		LLFloat = null;		
		LLString = null;
		
		alTokenTargetToParserTokenType = null;
		
		iIndexPerArray = null;
	}
	
	
	public abstract boolean setMementoXML_usingHandler( 
			final ISaxParserHandler refSaxHandler );
	
}
