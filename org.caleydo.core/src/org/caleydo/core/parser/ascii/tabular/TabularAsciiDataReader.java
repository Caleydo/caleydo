package org.caleydo.core.parser.ascii.tabular;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.caleydo.core.data.collection.EStorageType;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.ascii.AbstractLoader;
import org.caleydo.core.parser.ascii.IParserObject;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;
import org.caleydo.core.util.system.StringConversionTool;

/**
 * Loader for micro array data sets.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class TabularAsciiDataReader 
extends AbstractLoader
implements IParserObject {
	
	/**
	 * Imports data from file to this set.
	 * uses first storage and overwrites first selection.
	 */
	protected ArrayList<IStorage> alTargetStorages;
	
	protected ArrayList<EStorageType> alColumnDataTypes;
	
	/**
	 * Reference to the current DataStorage.
	 */
	protected IStorage currentDataStorage;
	
	private ArrayList<int[]> alIntBuffers;
	
	private ArrayList<float[]> alFloatBuffers;
	
	private ArrayList<ArrayList<String>> alStringBuffers;
	
	/**
	 * Constructor.
	 * 
	 * @param setGeneralManager
	 * @param setFileName
	 * @param enableMultipeThreads
	 */
	public TabularAsciiDataReader(final IGeneralManager setGeneralManager, 
			final String setFileName) {

		super(setGeneralManager,
				setFileName);
		
		alTargetStorages = new ArrayList<IStorage>();
		alColumnDataTypes = new ArrayList<EStorageType>();
		
		alIntBuffers = new ArrayList<int[]>();
		alFloatBuffers = new ArrayList<float[]>();
		alStringBuffers = new ArrayList<ArrayList<String>>();
		
		init();
	}

	/** 
	 * Defines a pattern for parsing 
	 */
	public final boolean setTokenPattern(final String tokenPattern ) {
		
		boolean bAllTokensProper = true;
		
		StringTokenizer tokenizer = new StringTokenizer(tokenPattern);

		final String sTokenPatternParserSeperator = 
			IGeneralManager.sDelimiter_Parser_DataType;
		
		while (tokenizer.hasMoreTokens()) 
		{
			String sBuffer = tokenizer.nextToken(sTokenPatternParserSeperator);

			if ( sBuffer.equalsIgnoreCase("abort")) 
			{
				alColumnDataTypes.add(EStorageType.ABORT);
				
				return bAllTokensProper;				
			} 
			else if (sBuffer.equalsIgnoreCase("skip")) 
			{								
				alColumnDataTypes.add(EStorageType.SKIP);							
			} 
			else if (sBuffer.equalsIgnoreCase("int")) 
			{
				alColumnDataTypes.add(EStorageType.INT);
								          
			} 
			else if (sBuffer.equalsIgnoreCase("float")) 
			{	
				alColumnDataTypes.add(EStorageType.FLOAT);
								          
			}
			else if (sBuffer.equalsIgnoreCase("string")) 
			{				
				alColumnDataTypes.add(EStorageType.STRING);					          
			} 
			else {
				bAllTokensProper = false;
				
				generalManager.getLogger().log(Level.WARNING, "Unknown column data type: "+tokenPattern);
			}
			
		} // end of while
		
		return bAllTokensProper;
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
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.parser.ascii.IParserObject#destroy()
	 */
	public final void destroy() {
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.parser.ascii.microarray.AMicroArrayLoader#loadDataParseFile(java.io.BufferedReader, int)
	 */
	protected void loadDataParseFile(BufferedReader brFile, final int iNumberOfLinesInFile )
		throws IOException 
	{
		allocateStorageBufferForTokenPattern();

		// Init progress bar
		progressBarSetStoreInitTitle("Load data file " + this.getFileName(),
				0,  // reset progress bar to 0
				alTargetStorages.size());

		String sLine;

		int iColumnIndex = 0;
		int iParsedLineIndex = 0;
		
		while (((sLine = brFile.readLine()) != null)
				&& (iLineInFile < iStopParsingAtLine))
		{
			// Check if line should be ignored
			if (iLineInFile < this.iStartParsingAtLine)
			{
				iLineInFile++;
				continue;
			}
				
//				StringTokenizer strTokenText = new StringTokenizer(sLine, "\"");
//				strLineBuffer.setLength(0);
//				int iCountTokens = strTokenText.countTokens();
//				if ((iCountTokens % 2) == 0)
//				{
//					strTokenText = new StringTokenizer(sLine.replace("\"\"",
//							"\" \""), "\"");
//				}
			
			System.out.println("Line: " +iLineInFile);
			System.out.println(" I:" + sLine );
			
			StringTokenizer strTokenLine = new StringTokenizer(sLine, sTokenSeperator);
			
			iColumnIndex = 0;
			
			while(strTokenLine.hasMoreTokens())
			{					
				for(EStorageType columnDataType : alColumnDataTypes)
				{
					switch(columnDataType) 
					{
					case INT:
						alIntBuffers.get(iColumnIndex)[iParsedLineIndex] = 
							StringConversionTool.convertStringToInt(strTokenLine.nextToken(), -1);
						break;
					case FLOAT:
						alFloatBuffers.get(iColumnIndex)[iParsedLineIndex] = 
							StringConversionTool.convertStringToFloat(strTokenLine.nextToken(), -1);
						break;
					case STRING:
						alStringBuffers.get(iColumnIndex).set(iParsedLineIndex, strTokenLine.nextToken());
						break;
					case SKIP: // do nothing
						break;
					case ABORT:
						iColumnIndex = alColumnDataTypes.size();
						break;
					default:
						throw new CaleydoRuntimeException("Unknown token pattern detected: "
								+columnDataType.toString(), CaleydoRuntimeExceptionType.DATAHANDLING);
					}
				
					// Check if the line is finished or early aborted
					if (iColumnIndex == alColumnDataTypes.size())
						continue;
					
					iColumnIndex++;
				}
			}

			iLineInFile++;
			iParsedLineIndex++;
	
			super.progressBarStoredIncrement();		
		}

		super.progressBarResetTitle();
		super.progressBarIncrement( 10 );
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.parser.ascii.AbstractLoader#setArraysToStorages()
	 */
	protected void setArraysToStorages() {

//		int iStorageIndex = 0;
//		
//		for (EStorageType storageType : alColumnDataTypes) 
//		{ 			
//			switch(storageType) 
//			{
//			case INT:
//				
//				
//				break;
//			case FLOAT:
//				alFloatBuffers.add(new float[iStopParsingAtLine - iStartParsingAtLine + 1]);
//				break;
//			case STRING:
//				alStringBuffers.add(new ArrayList<String>(iStopParsingAtLine - iStartParsingAtLine + 1));
//				break;
//			case SKIP:
//				break;
//			case ABORT:
//				return;
//			default:
//				throw new CaleydoRuntimeException("Unknown token pattern detected: "
//						+storageType.toString(), CaleydoRuntimeExceptionType.DATAHANDLING);
//			}
//
//			iStorageIndex++;
//		}	
//
//		
//		for (IStorage tmpStorage : alTargetStorages) 
//		{ 
//		}
	}

	/**
	 * Init data structures. Use this to reset the state also!
	 * 
	 * @see org.caleydo.core.parser.ascii.IParserObject#init()
	 */
	public void init() 
	{
		iLineInFile = 0;
	}
	
	protected void allocateStorageBufferForTokenPattern() {
		
		for (EStorageType storageType : alColumnDataTypes) 
		{ 			
			switch(storageType) 
			{
			case INT:
				alIntBuffers.add(new int[iStopParsingAtLine - iStartParsingAtLine + 1]);
				break;
			case FLOAT:
				alFloatBuffers.add(new float[iStopParsingAtLine - iStartParsingAtLine + 1]);
				break;
			case STRING:
				alStringBuffers.add(new ArrayList<String>(iStopParsingAtLine - iStartParsingAtLine + 1));
				break;
			case SKIP:
				break;
			case ABORT:
				return;
			default:
				throw new CaleydoRuntimeException("Unknown token pattern detected: "
						+storageType.toString(), CaleydoRuntimeExceptionType.DATAHANDLING);
			}
		}	
	}
}
