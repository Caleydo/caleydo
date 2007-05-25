/**
 * 
 */
package cerberus.xml.parser.handler.importer.ascii;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import cerberus.data.collection.ISet;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.IVirtualArray;
import cerberus.data.collection.StorageType;
import cerberus.data.collection.parser.CollectionSelectionSaxParserHandler;
import cerberus.data.collection.parser.ParserTokenHandler;
import cerberus.data.collection.virtualarray.VirtualArrayThreadSingleBlock;
//import cerberus.data.collection.set.SetMultiDim;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.type.ManagerObjectType;
import cerberus.xml.parser.ISaxParserHandler;
import cerberus.xml.parser.handler.importer.ascii.AMicroArrayLoader;


/**
 * @author Michael Kalkusch
 *
 */
public class MicroArrayLoaderValues2MultipleStorages 
extends AMicroArrayLoader {

//	private LinkedList<Integer> LLInteger = null;
//	
//	private LinkedList<Float> LLFloat = null;
//	
//	private LinkedList<String> LLString = null;
	
	private IStorage[] refArrayDataStorage = null;
	
//	private int iArrayDataStorageLength = -1;
	
	/**
	 * Reference to the current DataStorage.
	 */
	private IStorage refDataStorage;
	
	/**
	 * 
	 */
	private IVirtualArray refImportDataOverrideSelection;
	
		
	/**
	 * 
	 */
	public MicroArrayLoaderValues2MultipleStorages(final IGeneralManager setGeneralManager,
			final String setFileName,
			final boolean enableMultipeThreads) {

		super(setGeneralManager, 
				setFileName,
				enableMultipeThreads); 
	
		super.bRequiredSizeOfReadableLines = true;
	}
	
	
	
	protected void allocateStorageBufferForTokenPattern( ) {
		
		allocateStorageBufferForTokenPatternAbstractClass();
	}
	
	
	
	/**
	 * Assign a ISet to write the data to.
	 * 
	 * @param refUseSet target set.
	 */
	public final void setTargetSet(ISet refUseSet) {
		this.refImportDataToSet = refUseSet;
		
		if ( refImportDataToSet.getBaseType() != ManagerObjectType.SET_MULTI_DIM ) {
			refGeneralManager.getSingelton().logMsg("setTargetSet() ERROR! need a MultiSet!",
					LoggerType.MINOR_ERROR );
		}
		
		int iSizeStoragesInSet =  refImportDataToSet.getDimensions();
		
		refArrayDataStorage = new IStorage[iSizeStoragesInSet];
		
		for ( int i=0; i < iSizeStoragesInSet; i++ ) {
			/**
			 * Copy needed Storages from Set to local array...
			 */			
			refArrayDataStorage[i] = 
				refImportDataToSet.getStorageByDimAndIndex(i,0);
		}
	}
	
	/**
	 * Removes all data structures.
	 * 
	 * @see cerberus.xml.parser.IParserObject#destroy()
	 */
	public final void destroy() {
		
		super.destroy();
		
	}

	
	
	@Override
	protected int loadDataParseFile(BufferedReader brFile,
			final int iNumberOfLinesInFile )
		throws IOException 
	{

		allocateStorageBufferForTokenPattern();
		
		/**
		 * Consistency check: is a Set defined?
		 */
		
		if ( refImportDataToSet == null ) {
			if ( refDataStorage == null ) {
				assert false: "No reference to IStorage was set!";
			
				return -1;
			}
			assert false : "deprecated call! need to assign a ISet!";
		}
		else {
			/* refImportDataToSet != null */
			refDataStorage = 
				refImportDataToSet.getStorageByDimAndIndex(0,0);
			refImportDataOverrideSelection = 
				refImportDataToSet.getVirtualArrayByDimAndIndex(0,0);
		}
		
		/**
		 * Allocate storage inside Set...
		 */
		
		ListIterator <ParserTokenHandler> iterForAllocationInit = 
			alTokenTargetToParserTokenType.listIterator();
		
		ParserTokenHandler bufferAllocationTokenInit;
		
		Vector <StorageType> vecBuffersTorage = new Vector <StorageType>();
		
		/**
		 * progress bar init
		 */
		progressBarSetStoreInitTitle("load " + this.getFileName(),
				0,  // reset progress bar to 0
				refArrayDataStorage.length );
		
		
		for ( int i=0; i < refArrayDataStorage.length; i++ ) 
		{ 			
			boolean bStayInLoop = true;
			
			while ((iterForAllocationInit.hasNext())&&
					( bStayInLoop ))
			{
				bufferAllocationTokenInit = iterForAllocationInit.next();
				
				switch ( bufferAllocationTokenInit.getType() ) {
				// case SKIP: do nothing, only consume current token.
				case INT:
					refArrayDataStorage[i].setSize(StorageType.INT,iNumberOfLinesInFile);
					vecBuffersTorage.addElement( StorageType.INT );
					bStayInLoop = false;
					break;
				case FLOAT:
					refArrayDataStorage[i].setSize(StorageType.FLOAT,iNumberOfLinesInFile);
					vecBuffersTorage.addElement( StorageType.FLOAT );
					bStayInLoop = false;
					break;
				case STRING:	
					refArrayDataStorage[i].setSize(StorageType.STRING,iNumberOfLinesInFile);
					vecBuffersTorage.addElement( StorageType.STRING );
					bStayInLoop = false;
					break;
					
				default:
					//System.out.println("Unknown label or ignore label [" + bufferAllocationTokenInit.getType().toString() + "]");
					
				} // switch ( bufferAllocationToken.getType() ) {
				
			
			} // while ((iterForAllocation.hasNext())&& ...
			
			
		} // for ( int i=0; i < refArrayDataStorage.length; i++ )
			
		/**
		 * Array for StorageType...
		 */
		// StorageType[] arryStorageType = new
		// StorageType[vecBuffersTorage.size()];
		// arryStorageType = vecBuffersTorage.toArray( arryStorageType );

		/**
		 * End Allocate Storage
		 */

		Vector<String> vecBufferText = new Vector<String>(10);
		StringBuffer strLineBuffer = new StringBuffer();

		String sLine;

		int lineInFile = 0;
		int lineInFile_CurrentDataIndex = 0;

		while (((sLine = brFile.readLine()) != null)
				&& (lineInFile <= iStopParsingAtLine))
		{

			if (lineInFile > this.iStartParsingAtLine)
			{

				boolean bMaintainLoop = true;
				StringTokenizer strTokenText = new StringTokenizer(sLine, "\"");

				//int iStringIndex = 0;

				strLineBuffer.setLength(0);
				vecBufferText.clear();

				int iCountTokens = strTokenText.countTokens();

				if ((iCountTokens % 2) == 0)
				{
					strTokenText = new StringTokenizer(sLine.replace("\"\"",
							"\" \""), "\"");
					// System.out.println("Substitute [\"\"] ==> [\" \"] in
					// line " + iLineInFile );
				}

				/**
				 * are there any tokens containing "
				 */
				if (iCountTokens > 1)
				{
					strLineBuffer.append(strTokenText.nextToken());

					boolean bToggle_Buffer = true;

					while (strTokenText.hasMoreTokens())
					{
						String sBuffer = strTokenText.nextToken().trim();

						if (bToggle_Buffer)
						{
							vecBufferText.addElement(sBuffer);
							strLineBuffer.append( sBuffer );
							strLineBuffer.append( " " );
							bToggle_Buffer = false;
						} else
						{
							strLineBuffer.append(sBuffer);
							bToggle_Buffer = true;
						}
					}
				} else
				{
					strLineBuffer.append(sLine);
				}

				
//				String intermediateLine = strLineBuffer.toString();
//				
//				System.out.println(" I:" + sLine );
//				System.out.println(" X:" + intermediateLine );
				
				StringTokenizer strToken = new StringTokenizer(new String(
						strLineBuffer));
				ListIterator<ParserTokenHandler> iterPerLine = alTokenTargetToParserTokenType
						.listIterator();

				int iDataArrayIndexPerLine = 0;
				ParserTokenHandler bufferIter = null;

				while ((strToken.hasMoreTokens()) && (bMaintainLoop))
				{
					String sTokenObject = strToken.nextToken();

					try
					{
						bufferIter = iterPerLine.next();

						// switch ( iterPerLine.next().getType() ) {
						switch (bufferIter.getType())
						{
						// case SKIP: do nothing, only consume current
						// token.
						case ABORT:
							bMaintainLoop = false;
							break;
						case INT:

							int[] bufferIntArray = 
								refArrayDataStorage[iDataArrayIndexPerLine]
									.getArrayInt();
							bufferIntArray[lineInFile_CurrentDataIndex] = 
								new Integer(sTokenObject);
							
							iDataArrayIndexPerLine++;
							break;
						case FLOAT:

							float[] bufferFloatArray = refArrayDataStorage[iDataArrayIndexPerLine]
									.getArrayFloat();							
							bufferFloatArray[lineInFile_CurrentDataIndex] = new Float(
									sTokenObject);

							iDataArrayIndexPerLine++;
							// LLFloat.add( new Float(sTokenObject) );
							break;
						case STRING:
							String[] bufferStringArray = refArrayDataStorage[iDataArrayIndexPerLine]
									.getArrayString();
							bufferStringArray[lineInFile_CurrentDataIndex] =
								sTokenObject;

							iDataArrayIndexPerLine++;
							// LLString.add(
							// vecBufferText.get(iStringIndex) );
							// iStringIndex++;
							break;
						case SKIP:
							break;
						default:
							System.err.println("Unknown label");

						} // end switch

					} 
					catch (NoSuchElementException nsee)
					{
						/*
						 * no ABORT was set. since no more tokens are in
						 * ParserTokenHandler skip rest of line..
						 */
						bMaintainLoop = false;
					}
					catch (NumberFormatException nfe) {
						
						if ( bufferIter == null ) 
						{
							refGeneralManager.getSingelton().logMsg(
								"Can not parse element, skip value: " + 
								nfe.getMessage(),
								LoggerType.ERROR_ONLY );
						}
						else
						{
							refGeneralManager.getSingelton().logMsg(
									"Can not parse element, skip value: Assumed type=[" + 
									bufferIter.getType() + "] => " + 
									nfe.getMessage(),
									LoggerType.ERROR_ONLY );							
						}
					}

				} // end of: while (( strToken.hasMoreTokens()
				// )&&(bMaintainLoop)) {

				// iLineInFile_CurrentDataIndex++;
				lineInFile_CurrentDataIndex++;

			} // end of: if( iLineInFile > this.iHeaderLinesSize) {

			// iLineInFile++;
			lineInFile++;
			
			super.progressBarStoredIncrement();

		} // end: while ((sLine = brFile.readLine()) != null) {

		iLineInFile = lineInFile;
		iLineInFile_CurrentDataIndex = lineInFile_CurrentDataIndex;
		
		refGeneralManager.getSingelton().logMsg(
				"  parsed " + this.iLineInFile_CurrentDataIndex + 
				" lines, stoped at line " + 
				(this.iLineInFile - 1) + "  [" +
				this.iStartParsingAtLine + " -> " +
				this.iStopParsingAtLine + "]",
				LoggerType.VERBOSE);

		super.progressBarResetTitle();
		super.progressBarIncrement( 10 );
		
		return lineInFile - this.iStartParsingAtLine;
	}

	@Override
	protected boolean copyDataToInternalDataStructures() {

		   /**
			 * Copy valued to refStorage...
			 */
	    		   
	    refImportDataToSet.setLabel("microarray loader set " + this.getFileName() );
	    refDataStorage.setLabel( "microarray loader storage " + this.getFileName() );
	    
	    /*
	     * notify storage cacheId of changed data...
	     */
	    refDataStorage.setCacheId( refDataStorage.getCacheId() + 1);
	    
	    refDataStorage.setSize(StorageType.INT,1);
	    refDataStorage.setSize(StorageType.FLOAT,1);
	    refDataStorage.setSize(StorageType.STRING,1);
	    
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
		    refImportDataToSet.setVirtualArrayByDimAndIndex(
		    		refImportDataOverrideSelection,0,0);
	    }
	    
	    if ( LLFloat.size() > 1) {
		    Iterator<Float> iter_F = LLFloat.iterator();		    
		    float[] floatBuffer = new float[LLFloat.size()];		    
		    for ( int i=0; iter_F.hasNext() ;i++ ) {
		    	floatBuffer[i]=iter_F.next().floatValue();
		    }
		    refDataStorage.setArrayFloat( floatBuffer );
		    
		    IVirtualArray selFloat = 
		    	new VirtualArrayThreadSingleBlock(1,null,null);
		    selFloat.setLabel("import FLOAT");
		    selFloat.setLength( LLFloat.size() );
		    
		    refImportDataToSet.setStorageByDimAndIndex(
		    		refDataStorage,0,1);
		    refImportDataToSet.setVirtualArrayByDimAndIndex(
		    		selFloat,0,1);
	    }
	    
	    if ( LLString.size() > 1) {
		    Iterator<String> iter_S = LLString.iterator();		    
		    String[] stringBuffer = new String[LLString.size()];		    
		    for ( int i=0; iter_S.hasNext() ;i++ ) {
		    	stringBuffer[i]=iter_S.next();
		    }
		    refDataStorage.setArrayString( stringBuffer );
		    
		    IVirtualArray selFloat = 
		    	new VirtualArrayThreadSingleBlock(1, refGeneralManager, null);
		    selFloat.setLabel("import STRING");
		    selFloat.setLength( LLString.size() );
		    
		    refImportDataToSet.setStorageByDimAndIndex(
		    		refDataStorage,0,2);
		    refImportDataToSet.setVirtualArrayByDimAndIndex(
		    		selFloat,0,2);
	    }
	    
	    //TODO: test if cacheId concept works fine...
	    
	    /*
	     * update cacheId of set by calling getCacheId() ...
	     */
	    refImportDataToSet.getCacheId();
	    
		return true;
	}
	
	
	public final boolean setMementoXML_usingHandler( 
			final ISaxParserHandler refSaxHandler ) {
		
		try {
			CollectionSelectionSaxParserHandler handler = 
				(CollectionSelectionSaxParserHandler) refSaxHandler;
			
			setFileName( handler.getXML_MicroArray_FileName() );
			
			int [] iLinkToIdList = 
				handler.getXML_RLE_Random_LookupTable();

			if ( iLinkToIdList.length < 1 ) {
				throw new RuntimeException("MicroArrayLoader.setMementoXML_usingHandler() failed. need <DataComponentItemDetails type=RandomLookup> tag.");
			}
			try {
				refDataStorage= (IStorage) refGeneralManager.getItem( iLinkToIdList[0] );
				
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

	/**
	 * Init data structues. Use this to reset the stat also!
	 * 
	 * @see cerberus.xml.parser.IParserObject#init()
	 */
	public void init() {
		iLineInFile = 1;
		iLineInFile_CurrentDataIndex = 0;
		
		bRequiredSizeOfReadableLines = true;
	}

}
