package org.caleydo.core.parser.ascii.microarray;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.StringTokenizer;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.StorageType;
import org.caleydo.core.data.collection.parser.ParserTokenHandler;
import org.caleydo.core.data.xml.IMementoXML;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.ascii.AbstractLoader;
import org.caleydo.core.parser.ascii.IParserObject;
import org.caleydo.core.parser.xml.sax.ISaxParserHandler;


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
	
	protected LinkedList<Double> LLDouble = null;
	
	protected LinkedList<String> LLString = null;
	
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
	
	/**
	 * Defines index 
	 */
	protected int iIndexPerArray[];
	
	/**
	 * Constructor.
	 * 
	 * @param setGeneralManager
	 * @param setFileName
	 * @param enableMultipeThreads
	 */
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

	
	/**
	 * @param brFile input stream
	 * @param iNumberOfLinesInFile optional, number of lines in file, only valid if bRequiredSizeOfReadableLines==true
	 */
	protected abstract int loadDataParseFile( BufferedReader brFile,
			final int iNumberOfLinesInFile )
		throws IOException; 

	
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
			else if (sBuffer.equalsIgnoreCase("double")) {
				
				int iIndexFromType = StorageType.DOUBLE.ordinal();
				
				ParserTokenHandler addType = 
					new ParserTokenHandler(StorageType.DOUBLE,
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
		if ( LLDouble == null ) 
		{
			LLDouble = new LinkedList<Double>(); 
		}		
		if ( LLString == null ) 
		{
			LLString = new LinkedList<String>(); 
		}
		
	}

	/**
	 * Removes all data structures.
	 * 
	 * @see org.caleydo.core.parser.ascii.IParserObject#destroy()
	 * @see org.caleydo.core.parser.ascii.microarray.AMicroArrayLoader#destroy()
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
		if ( LLDouble!= null ) 
		{
			LLDouble.clear();
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
		LLDouble = null;
		LLString = null;
		
		alTokenTargetToParserTokenType = null;
		
		iIndexPerArray = null;
	}
	
	
	public abstract boolean setMementoXML_usingHandler( 
			final ISaxParserHandler refSaxHandler );
	
}
