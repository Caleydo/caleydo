/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */

package cerberus.data.loader;

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

import cerberus.manager.GeneralManager;

//import java.util.*;

//import prometheus.data.DataStorageInterface;
import cerberus.data.collection.Storage;
import cerberus.data.collection.Set;
import cerberus.data.collection.Selection;
import cerberus.data.collection.selection.SelectionThreadSingleBlock;
import cerberus.data.xml.MementoXML;
import cerberus.data.collection.parser.CollectionSelectionParseSaxHandler;
import cerberus.data.collection.parser.ParserTokenType;
import cerberus.xml.parser.DParseSaxHandler;
import cerberus.data.collection.parser.ParserTokenHandler;


/**
 * Loader for MircoArray data sets in *.gpr format.
 * 
 * @author Michael Kalkusch
 *
 */
public class MicroArrayLoader 
implements MementoXML {

	/**
	 *  file name of *.gpr file
	 */  
	protected String sFileName;
	
	/**
	 * Reference to the current DataStorage.
	 */
	private Storage refDataStorage;
	//protected DataStorageInterface refDataStorage;
	
	/**
	 * Imports data from file to this set.
	 * uses first storage and overwrites first selection.
	 */
	protected Set refImportDataToSet;
	
	/**
	 * 
	 */
	private Selection refImportDataOverrideSelection;
	
	/**
	 * ref to singelton
	 */
	protected GeneralManager refGeneralManager;
	
	/**
	 * Define the speperator
	 */
	protected String sTokenSeperator;
	
	private LinkedList<Integer> LLInteger = null;
	
	private LinkedList<Float> LLFloat = null;
	
	private LinkedList<String> LLString = null;
	
	/** 
	 * Define the pattern for with values shall be paresed.
	 */
	protected ArrayList alTokenPattern;
	
	
	/**
	 * Defines where(in which MultiDataStorage) the parsed data shall be stored.
	 */
	protected ArrayList<ParserTokenHandler> alTokenTargetToParserTokenType;
	
	
	/**
	 * Defines how many lines are part of the header file.
	 * By default these liens are skipped during parsing.
	 */
	protected int iHeaderLinesSize;
	
	
	/**
	 * Default size for the token-ArrayList's
	 */
	private final int iInitialParseTokenSize = 8;
	
	/**
	 * Inistial size for the allocation of Gene-data sets
	 */
	private int iInitialArraylistSize = 39000;
	
	/*
	 * Defines index 
	 */
	protected int iIndexPerArray[];
	
	public MicroArrayLoader(GeneralManager setGeneralManager) {
		super();
				
		refGeneralManager = setGeneralManager;
		
		assert refGeneralManager!= null :"null-pointer in constructor";		
		
		this.sFileName = "";
		this.iHeaderLinesSize = 33;
		
		alTokenPattern =
			new ArrayList(iInitialParseTokenSize);
		
		alTokenTargetToParserTokenType = 
			new ArrayList<ParserTokenHandler> (iInitialParseTokenSize);
		
		iIndexPerArray = new int[iInitialParseTokenSize];
		
		/// TAB is the default token.
		sTokenSeperator = "\t";	
		
		//setTokenPattern("skip;skip;skip;string;string;skip;int;int;abort");
	}
	
	
	public MicroArrayLoader(GeneralManager setGeneralManager, String setFileName) {
		super();
		
		assert refGeneralManager!= null :"null-pointer in constructor";
		refGeneralManager = setGeneralManager;
		
		this.sFileName = setFileName;
	}
	
	
	public final void setFileName(String setFileName) {
		this.sFileName = setFileName;
	}
	
	public final String getFileName( ) {
		return this.sFileName;
	}
	

	/**
	 * 
	 * @deprecated use setTargetSet(Set) instead
	 * 
	 * @param setDataStorage reference to Storage
	 */
	public void setFileDataStorage(Storage setDataStorage) {
		this.refDataStorage = setDataStorage;
	}
	
	/**
	 * Assign a Set to write the data to.
	 * 
	 * @param refUseSet target set.
	 */
	public void setTargetSet(Set refUseSet) {
		this.refImportDataToSet = refUseSet;
	}
	
//	public void setFileDataStorage(MultiDataInterface setDataStorage) {
//		this.refDataStorage = setDataStorage;
//	}
//	
//	public MultiDataInterface getFileDataStorage( ) {
//		return this.refDataStorage;
//	}
	
	public boolean loadData() {			
		
		if ( refImportDataToSet == null ) {
			if ( refDataStorage == null ) {
				assert false: "No reference to Storage was set!";
			
				return false;
			}
			assert false : "deprecated call! need to assign a Set!";
		}
		else {
			/* refImportDataToSet != null */
			refDataStorage = 
				refImportDataToSet.getStorageByDimAndIndex(0,0);
			refImportDataOverrideSelection = 
				refImportDataToSet.getSelectionByDimAndIndex(0,0);
		}
		
		allocateStorageBufferForTokenPattern();
		
		/// open file....
		
		String sLine;
		int iLineInFile = 1;
		int iLineInFile_CurrentDataIndex = 0;

		/*
		/// get the references to the ArrayList's before opening the file.
		ArrayList<ArrayList> refLists =
			new ArrayList <ArrayList>(this.alTokenTargetToParserTokenType.size());
			
		ListIterator <ParserTokenHandler> iter = alTokenTargetToParserTokenType.listIterator();
		
		while( iter.hasNext() ) {
			ParserTokenHandler currentType = iter.next();
			
			if ( !  currentType.isEmpty() ) {
				refLists.add( refDataStorage.getArrayListByIndex( currentType ));
			}
		}
		*/
		
		try {
		    BufferedReader brFile = 
			new BufferedReader( new FileReader( this.sFileName ) );


		    // sample line: 1110 Kybernetik
		    System.out.println("Read file \""+ 
				       this.sFileName + "\" ..." );

		    Vector <String> vecBufferText = new Vector<String>(10);
		    StringBuffer strLineBuffer = new StringBuffer();
		    
		    while ((sLine = brFile.readLine()) != null) {
		    	
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
				
				if( iLineInFile > this.iHeaderLinesSize ){
					
					boolean bMaintainLoop = true;
					StringTokenizer strTokenText = new StringTokenizer(sLine,"\"");
					
					int iStringIndex = 0;
					
					strLineBuffer.setLength(0);
					vecBufferText.clear();
					
					int iCountTokens = strTokenText.countTokens();
					
					if ( (iCountTokens % 2) == 0 ) {						
						strTokenText = new StringTokenizer (sLine.replace( "\"\"", "\" \"") , "\"");
						//System.out.println("Substitute [\"\"] ==> [\" \"] in line " + iLineInFile );
					}
					
					/**
					 * are there any tokens containing " 
					 */
					if ( iCountTokens > 1) {
						strLineBuffer.append( strTokenText.nextToken() );
						
						boolean bToggle_Buffer = true;
						
						while ( strTokenText.hasMoreTokens() ) {
							String sBuffer = strTokenText.nextToken().trim();
							
							if ( bToggle_Buffer ) {
								vecBufferText.addElement( sBuffer );
								bToggle_Buffer = false;
							}
							else {
								strLineBuffer.append( sBuffer );
								bToggle_Buffer = true;
							}
						}
					} 
					else {
						strLineBuffer.append( sLine );
					}
					
					
					StringTokenizer strToken = new StringTokenizer( new String(strLineBuffer) );
					ListIterator <ParserTokenHandler> iterPerLine = 
						alTokenTargetToParserTokenType.listIterator();
					
					while (( strToken.hasMoreTokens() )&&(bMaintainLoop)) {
						String sTokenObject = strToken.nextToken();

						try {
							ParserTokenHandler bufferIter = iterPerLine.next();
						
							//switch ( iterPerLine.next().getType() ) {
							switch ( bufferIter.getType() ) {
								//case SKIP: do nothing, only consume current token.
								case ABORT:
									bMaintainLoop = false;
									break;
								case INT:
									LLInteger.add( new Integer(sTokenObject) );
									break;
								case FLOAT:
									LLFloat.add( new Float(sTokenObject) );
									break;
								case STRING:	
									LLString.add( vecBufferText.get(iStringIndex) );	
									iStringIndex++;
									break;
								case SKIP:
									break;
								default:
									System.err.println("Unknown label");
									
							} // end switch
						
						} catch ( NoSuchElementException  nsee) {
							/* no ABORT was set. 
							 * since no more tokens are in ParserTokenHandler skip rest of line..*/
							bMaintainLoop = false;
						}
					
					} // end of: while (( strToken.hasMoreTokens() )&&(bMaintainLoop)) {
					
//					System.out.println( 
//							" " + Integer.toString( iLineInFile ) + "/" +
//							Integer.toString( iLineInFile_CurrentDataIndex ) + "/" +
//							Integer.toString( iHeaderLinesSize ));
					
					iLineInFile_CurrentDataIndex++;
					
				} // end of: if( iLineInFile > this.iHeaderLinesSize) {			
//				else {
//					System.out.println( 
//							" (" + Integer.toString( iLineInFile ) + "/ 0 /" +
//							Integer.toString( iHeaderLinesSize ) + "): " + sLine );					
//				}
				
				iLineInFile++;
				
			
		    } // end: while ((sLine = brFile.readLine()) != null) { 
		    
		    brFile.close();
		    
		    // sample line: E016|Zentrale Medienstelle|Media Centre|00
		    System.out.println("Read file \""+ 
				       this.sFileName + "\"  ....  [DONE]" );

		    /**
		     * Copy valued to refStorage...
		     */
		    		   
		    refImportDataToSet.setLabel("microarray loader set");
		    refDataStorage.setLabel( "microarray loader storage");
		    
		    /*
		     * notify storage cacheId of changed data...
		     */
		    refDataStorage.setCacheId( refDataStorage.getCacheId() + 1);
		    
		    if ( LLInteger.size() > 1) {
			    Iterator<Integer> iter_I = LLInteger.iterator();		    
			    int[] intBuffer = new int[LLInteger.size()];		    
			    for ( int i=0; iter_I.hasNext() ;i++ ) {
			    	intBuffer[i]=iter_I.next().intValue();
			    }
			    refDataStorage.setArrayInt( intBuffer );
			    
			    refImportDataOverrideSelection.setLabel("import INTEGER");
			    refImportDataOverrideSelection.setOffset( 0 );
			    refImportDataOverrideSelection.setLength( LLInteger.size() );			    
			    
			    /*
			     * notify selection cacheId of changed data...
			     */
			    refImportDataOverrideSelection.setCacheId(
			    		refImportDataOverrideSelection.getCacheId() + 1 );
			    
			    refImportDataToSet.setStorageByDimAndIndex(
			    		refDataStorage,0,0);
			    refImportDataToSet.setSelectionByDimAndIndex(
			    		refImportDataOverrideSelection,0,0);
		    }
		    
		    if ( LLFloat.size() > 1) {
			    Iterator<Float> iter_F = LLFloat.iterator();		    
			    float[] floatBuffer = new float[LLFloat.size()];		    
			    for ( int i=0; iter_F.hasNext() ;i++ ) {
			    	floatBuffer[i]=iter_F.next().floatValue();
			    }
			    refDataStorage.setArrayFloat( floatBuffer );
			    
			    Selection selFloat = 
			    	new SelectionThreadSingleBlock(1,null,null);
			    selFloat.setLabel("import FLOAT");
			    selFloat.setLength( LLFloat.size() );
			    
			    refImportDataToSet.setStorageByDimAndIndex(
			    		refDataStorage,0,1);
			    refImportDataToSet.setSelectionByDimAndIndex(
			    		selFloat,0,1);
		    }
		    
		    if ( LLString.size() > 1) {
			    Iterator<String> iter_S = LLString.iterator();		    
			    String[] stringBuffer = new String[LLString.size()];		    
			    for ( int i=0; iter_S.hasNext() ;i++ ) {
			    	stringBuffer[i]=iter_S.next();
			    }
			    refDataStorage.setArrayString( stringBuffer );
			    
			    Selection selFloat = 
			    	new SelectionThreadSingleBlock(1,null,null);
			    selFloat.setLabel("import STRING");
			    selFloat.setLength( LLString.size() );
			    
			    refImportDataToSet.setStorageByDimAndIndex(
			    		refDataStorage,0,2);
			    refImportDataToSet.setSelectionByDimAndIndex(
			    		selFloat,0,2);
		    }
		    
		    //TODO: test if cacheId concept works fine...
		    
		    /*
		     * update cacheId of set by calling getCacheId() ...
		     */
		    refImportDataToSet.getCacheId();
		    
		    System.out.println("Read file \""+ 
				       this.sFileName + "\"  ....  [DONE]  ....  copy to storage  [DONE]" );
		    
		}
		catch (IOException ioe) {
		    System.err.println( "MicroArrayLaoder: ERROR line=[" + iLineInFile + "] while parsing: " + ioe.getMessage() );
		    return false;
		    //System.exit(1);
		}
		catch (Exception ex) {
		    System.err.println( "MicroArrayLaoder: ERROR line=[" + iLineInFile + "] while parsing: " + ex.getMessage() );
		    return false;
		}		
		
		return true;
	}
	
	/**
	 * Define how many lines are part of the header
	 *  
	 * @param iSetHeaderLineSize
	 */
	public void setHeaderLineSize( int iSetHeaderLineSize ) {
		
	}
	
	/** 
	 * Defines a pattern for parsing 
	 */
	public boolean setTokenPattern( String tokenPattern ) {
		
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
						new ParserTokenHandler( ParserTokenType.ABORT) );
				
				return bAllTokensProper;				
			} 
			else if (sBuffer.equalsIgnoreCase("skip")) {								
				/// insert "mdnone" token to indicate skipping...
				//MultiDataEnumType addType = MultiDataEnumType.SKIP;
				
				alTokenTargetToParserTokenType.add( 
						new ParserTokenHandler( ParserTokenType.SKIP ));							
			} 
			else if (sBuffer.equalsIgnoreCase("int")) {
				
				int iIndexFromType = ParserTokenType.INT.ordinal();
				
				ParserTokenHandler addType = 
					new ParserTokenHandler(ParserTokenType.INT,
							iIndexPerArray[iIndexFromType] );
				
				alTokenTargetToParserTokenType.add( addType );
			
				/// increment index...
				iIndexPerArray[iIndexFromType]++;
								          
			} 
			else if (sBuffer.equalsIgnoreCase("float")) {
				
				int iIndexFromType = ParserTokenType.FLOAT.ordinal();
				
				ParserTokenHandler addType = 
					new ParserTokenHandler(ParserTokenType.FLOAT,
							iIndexPerArray[iIndexFromType] );	
				
				alTokenTargetToParserTokenType.add( addType );
			
				/// increment index...
				iIndexPerArray[iIndexFromType]++;
								          
			}
			else if (sBuffer.equalsIgnoreCase("string")) {				
				
				int iIndexFromType = ParserTokenType.STRING.ordinal();
				
				ParserTokenHandler addType = 
					new ParserTokenHandler(ParserTokenType.STRING,								
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
	
	
	protected void allocateStorageBufferForTokenPattern( ) {
		
		if ( LLInteger == null ) {
			LLInteger = new LinkedList<Integer>(); 
		}
		if ( LLFloat == null ) {
			LLFloat = new LinkedList<Float>(); 
		}
		if ( LLString == null ) {
			LLString = new LinkedList<String>(); 
		}
		
	}
	
	public boolean setMementoXML_usingHandler( 
			final DParseSaxHandler refSaxHandler ) {
		
		try {
			CollectionSelectionParseSaxHandler handler = 
				(CollectionSelectionParseSaxHandler) refSaxHandler;
			
			setFileName( handler.getXML_MicroArray_FileName() );
			
			int [] iLinkToIdList = 
				handler.getXML_RLE_Random_LookupTable();

			if ( iLinkToIdList.length < 1 ) {
				throw new RuntimeException("MicroArrayLoader.setMementoXML_usingHandler() failed. need <DataComponentItemDetails type=RandomLookup> tag.");
			}
			try {
				refDataStorage= (Storage) refGeneralManager.getItem( iLinkToIdList[0] );
				
				setTokenPattern( handler.getXML_MicroArray_TokenPattern().trim() );
				//setTokenPattern( "SKIP;SKIP;SKIP;STRING;STRING;INT;INT;ABORT" );
								
				//loadData();
			}
			catch (NullPointerException npe) {
				refDataStorage = null;
			}
			
			return true;
			
		} catch (NullPointerException npe) {
			
			return false;
		}
	}

}
