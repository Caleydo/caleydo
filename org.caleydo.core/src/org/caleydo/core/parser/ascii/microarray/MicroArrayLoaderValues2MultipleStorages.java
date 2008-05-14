package org.caleydo.core.parser.ascii.microarray;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;

import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.IVirtualArray;
import org.caleydo.core.data.collection.StorageType;
import org.caleydo.core.data.collection.parser.CollectionSelectionSaxParserHandler;
import org.caleydo.core.data.collection.parser.ParserTokenHandler;
import org.caleydo.core.data.collection.virtualarray.VirtualArrayThreadSingleBlock;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.xml.sax.ISaxParserHandler;


/**
 * Load data file to multiple storages.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class MicroArrayLoaderValues2MultipleStorages 
extends AMicroArrayLoader {
		
	/**
	 * Constructor.
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
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.parser.ascii.microarray.AMicroArrayLoader#setTargetSotrage(java.util.ArrayList)
	 */
	public void setTargetStorages(final ArrayList<Integer> iAlTargetStorageId)
	{
		for (int iStorageId : iAlTargetStorageId)
			alTargetStorages.add((IStorage) generalManager.getStorageManager().getItem(iStorageId));
	}
	
	/**
	 * Removes all data structures.
	 * 
	 * @see org.caleydo.core.parser.ascii.IParserObject#destroy()
	 */
	public final void destroy() {
		
		super.destroy();
		
	}

	@Override
	protected int loadDataParseFile(BufferedReader brFile, final int iNumberOfLinesInFile )
		throws IOException 
	{
		allocateStorageBufferForTokenPattern();

		/**
		 * Allocate storage inside Set...
		 */
		
		ListIterator <ParserTokenHandler> iterForAllocationInit = 
			alTokenTargetToParserTokenType.listIterator();
		
		ParserTokenHandler bufferAllocationTokenInit;
		
		Vector <StorageType> vecBuffersTorage = new Vector <StorageType>();
		
		// Init progress bar
		progressBarSetStoreInitTitle("Load data file " + this.getFileName(),
				0,  // reset progress bar to 0
				alTargetStorages.size());
		
		
		for (IStorage tmpStorage : alTargetStorages) 
		{ 			
			boolean bStayInLoop = true;
			
			while ((iterForAllocationInit.hasNext())&&
					( bStayInLoop ))
			{
				bufferAllocationTokenInit = iterForAllocationInit.next();
				
				switch ( bufferAllocationTokenInit.getType() ) {
				// case SKIP: do nothing, only consume current token.
				case INT:
					tmpStorage.setSize(StorageType.INT,iNumberOfLinesInFile);
					vecBuffersTorage.addElement( StorageType.INT );
					bStayInLoop = false;
					break;
				case FLOAT:
					tmpStorage.setSize(StorageType.FLOAT,iNumberOfLinesInFile);
					vecBuffersTorage.addElement( StorageType.FLOAT );
					bStayInLoop = false;
					break;
				case STRING:	
					tmpStorage.setSize(StorageType.STRING,iNumberOfLinesInFile);
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
			if (lineInFile >= this.iStartParsingAtLine)
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
				
				StringTokenizer strToken = new StringTokenizer(new String(strLineBuffer));
				ListIterator<ParserTokenHandler> iterPerLine = alTokenTargetToParserTokenType
						.listIterator();

				int iDataArrayIndexPerLine = 0;
				ParserTokenHandler bufferIter = null;

				while ((strToken.hasMoreTokens()) && (bMaintainLoop))
				{
					String sTokenObject = strToken.nextToken();
					String[] bufferStringArray = null;

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
								alTargetStorages.get(lineInFile_CurrentDataIndex).getArrayInt();
							
							if ( bufferIntArray == null ) 
							{
								generalManager.getLogger().log(Level.SEVERE, "Index out of bounds!");
								break;
							}
							
							bufferIntArray[lineInFile_CurrentDataIndex] = 
								new Integer(sTokenObject);
							
							iDataArrayIndexPerLine++;
							break;
						case FLOAT:

							float[] bufferFloatArray = alTargetStorages.get(iDataArrayIndexPerLine)
									.getArrayFloat();							
							bufferFloatArray[lineInFile_CurrentDataIndex] = new Float(sTokenObject);

							iDataArrayIndexPerLine++;
							// LLFloat.add( new Float(sTokenObject) );
							break;
						case STRING:

							bufferStringArray = alTargetStorages.get(lineInFile_CurrentDataIndex)
								.getArrayString();
						
							if ( bufferStringArray == null ) 
							{
								generalManager.getLogger().log(Level.SEVERE, "Index out of bounds!");
								break;
							}
							
							bufferStringArray[lineInFile_CurrentDataIndex] =
								sTokenObject;							
	
							if ( bufferStringArray.length > iDataArrayIndexPerLine )
							{
								iDataArrayIndexPerLine++;
							}
							else
							{
								System.err.println("index out of bounce; skip index [" + 
										lineInFile_CurrentDataIndex +
										" index2=" +
										iDataArrayIndexPerLine +
										" text=" +
										bufferStringArray[lineInFile_CurrentDataIndex] + 
										"] empty array[] ");;
							}
//						
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
//							generalManager.logMsg(
//								"Can not parse element, skip value: " + 
//								nfe.getMessage(),
//								LoggerType.ERROR );
						}
						else
						{
//							generalManager.logMsg(
//									"Can not parse element, skip value: Assumed type=[" + 
//									bufferIter.getType() + "] => " + 
//									nfe.getMessage(),
//									LoggerType.ERROR );							
						}
					}
					catch (ArrayIndexOutOfBoundsException aie)
					{
						String info = "index out of bounds; skip index [" + 
						lineInFile_CurrentDataIndex + 
						"] empty array[]= {";
						
						if ( bufferStringArray != null )
						{
							info += bufferStringArray.toString();
						}						
						else
						{
							info += "null";
						}
						
						info +=  "} " + aie.toString();
						
//						generalManager.logMsg(
//								info,
//								LoggerType.ERROR );	
						
						System.out.println("index out of bounds; skip index [" + 
								iDataArrayIndexPerLine + 
								"] empty array[]= " + aie.toString()  );
						
						iDataArrayIndexPerLine++;
					}

				} // end of: while (( strToken.hasMoreTokens()
				// )&&(bMaintainLoop)) {

				lineInFile_CurrentDataIndex++;

			} // end of: if( iLineInFile > this.iHeaderLinesSize) {

			// iLineInFile++;
			lineInFile++;
			
			super.progressBarStoredIncrement();

		} // end: while ((sLine = brFile.readLine()) != null) {

		iLineInFile = lineInFile;
		iLineInFile_CurrentDataIndex = lineInFile_CurrentDataIndex;

		super.progressBarResetTitle();
		super.progressBarIncrement( 10 );
		
		return lineInFile - this.iStartParsingAtLine;
	}

	@Override
	protected boolean copyDataToInternalDataStructures() {

	   /**
		 * Copy valued to refStorage...
		 */   		   
	    currentDataStorage.setLabel( "microarray loader storage " + this.getFileName() );
	    
	    /*
	     * notify storage cacheId of changed data...
	     */
	    currentDataStorage.setCacheId( currentDataStorage.getCacheId() + 1);
	    
	    currentDataStorage.setSize(StorageType.INT,1);
	    currentDataStorage.setSize(StorageType.FLOAT,1);
	    currentDataStorage.setSize(StorageType.STRING,1);
	    
	    if ( LLInteger.size() > 1) {
		    Iterator<Integer> iter_I = LLInteger.iterator();		    
		    int[] intBuffer = new int[LLInteger.size()];		    
		    for ( int i=0; iter_I.hasNext() ;i++ ) {
		    	intBuffer[i]=iter_I.next().intValue();
		    }
		    currentDataStorage.setArrayInt( intBuffer );
	    }
	    
	    if ( LLFloat.size() > 1) {
		    Iterator<Float> iter_F = LLFloat.iterator();		    
		    float[] floatBuffer = new float[LLFloat.size()];		    
		    for ( int i=0; iter_F.hasNext() ;i++ ) {
		    	floatBuffer[i]=iter_F.next().floatValue();
		    }
		    currentDataStorage.setArrayFloat( floatBuffer );
		    
		    IVirtualArray selFloat = 
		    	new VirtualArrayThreadSingleBlock(1,null,null);
		    selFloat.setLabel("import FLOAT");
		    selFloat.setLength( LLFloat.size() );
	    }
	    
	    if ( LLString.size() > 1) {
		    Iterator<String> iter_S = LLString.iterator();		    
		    String[] stringBuffer = new String[LLString.size()];		    
		    for ( int i=0; iter_S.hasNext() ;i++ ) {
		    	stringBuffer[i]=iter_S.next();
		    }
		    currentDataStorage.setArrayString( stringBuffer );
		    
		    IVirtualArray selFloat = 
		    	new VirtualArrayThreadSingleBlock(1, generalManager, null);
		    selFloat.setLabel("import STRING");
		    selFloat.setLength( LLString.size() );
	    }
	    
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
				currentDataStorage= (IStorage) generalManager.getStorageManager().getItem( iLinkToIdList[0] );
				
				setTokenPattern( handler.getXML_MicroArray_TokenPattern().trim() );
				//setTokenPattern( "SKIP;SKIP;SKIP;STRING;STRING;INT;INT;ABORT" );
								
				//loadData();
			}
			catch (NullPointerException npe) {
				currentDataStorage = null;
			}
			
			return true;
			
		} catch (NullPointerException npe) {
			
			return false;
		}
	}

	/**
	 * Init data structures. Use this to reset the state also!
	 * 
	 * @see org.caleydo.core.parser.ascii.IParserObject#init()
	 */
	public void init() 
	{
		iLineInFile = 1;
		iLineInFile_CurrentDataIndex = 0;
		
		bRequiredSizeOfReadableLines = true;
	}

}
