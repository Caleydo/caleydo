package org.caleydo.core.command.data;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.StringTokenizer;


import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.StorageType;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabel;
//import org.caleydo.core.command.window.CmdWindowPopupInfo;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ILoggerManager.LoggerType;
import org.caleydo.core.manager.data.IStorageManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;


import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;

/**
 * Command, creates a new storage.
 * 
 * @author Michael Kalkusch
 *
 * @see org.caleydo.core.data.collection.IStorage
 */
public class CmdDataCreateStorage 
extends ACmdCreate_IdTargetLabel {

	/**
	 * This list contains the data types for org.caleydo.core.data.collection.StorageType as String.
	 * 
	 * Note: llDataRaw.size() == llDataTypes.size() must be equal!
	 *
	 * @see org.caleydo.core.data.collection.StorageType
	 * @see org.caleydo.core.command.data.CmdDataCreateStorage#llDataRaw
	 * @see org.caleydo.core.command.data.CmdDataCreateStorage#bDisposeDataAfterDoCommand
	 */
	protected LinkedList<String> llDataTypes;
	
	/**
	 * This list contains the raw data as String.
	 * The data type for each "String" is defined in the linked list llDataTypes.
	 * 
	 * Note: llDataRaw.size() == llDataTypes.size() must be equal!
	 * 
	 * @see org.caleydo.core.command.data.CmdDataCreateStorage#llDataTypes
	 * @see org.caleydo.core.command.data.CmdDataCreateStorage#bDisposeDataAfterDoCommand	 
	 */
	protected LinkedList<String> llDataRaw;
	
	
		
	/**
	 * Define if data from llDataRaw and llDataTypes shall be removed after
	 * executing this command.
	 * 
	 * Default TRUE
	 */
	private final boolean bDisposeDataAfterDoCommand;
	
	/**
	 * Constructor.
	 */
	public CmdDataCreateStorage( 
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType,
			final boolean bDisposeDataAfterDoCommand ) {
		
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
			
		this.bDisposeDataAfterDoCommand = bDisposeDataAfterDoCommand;
		
		llDataTypes = new LinkedList<String> ();
		llDataRaw = new LinkedList<String> ();
	}

	

	/**
	 * Load data from file using a token pattern.
	 * 
	 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage#loadData()
	 * 
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException {
		
		assert llDataTypes != null : "Probably this doCommand() was already executed once!";
		
		IStorageManager refStorageManager = 
			generalManager.getStorageManager();
		
		IStorage newObject = (IStorage) refStorageManager.createStorage(
				ManagerObjectType.STORAGE_FLAT );
		
		newObject.setId( iUniqueId );
		newObject.setLabel( sLabel );
		
		newObject.allocate();
		
		Iterator <String> iter_DataTypes = llDataTypes.iterator();
		Iterator <String> iter_DataRaw   = llDataRaw.iterator();
		
		
		while ( iter_DataRaw.hasNext() ) {
			
			String sCurrentDataType = iter_DataTypes.next();
			
			/**
			 * Prepare variabels...
			 */
			String strToParse = iter_DataRaw.next();
			
			StringTokenizer tokenizer = 
				new StringTokenizer( strToParse, 
						IGeneralManager.sDelimiter_Parser_DataItems );
				
			int iSizeArray = tokenizer.countTokens();
			int iTokenIndex = 0;
			boolean bParse = true;
			
			/**
			 * Select data type...
			 */
			switch ( StorageType.valueOf( sCurrentDataType )) {
			
			case INT:
			{				
				/**
				 * initialize ...
				 */
				int [] bufferedArray = new int [iSizeArray];				
				bParse = true;
				iTokenIndex = 0;
				
				while (( tokenizer.hasMoreTokens() )&&(bParse) ) 
				{				
					try 
					{
						bufferedArray[iTokenIndex] = 
							Integer.valueOf( tokenizer.nextToken() );
						
						iTokenIndex++;
					}
					catch (NumberFormatException nfe) {
//						generalManager.logMsg(
//								"Can not convert (String) to (int) at index=[" +
//								iTokenIndex + "]  => skip raw data:",
//								LoggerType.ERROR );
//						generalManager.logMsg(
//								"  SKIP: " + strToParse,
//								LoggerType.ERROR );
						
						/** 
						 * terminate while loop...
						 */
						bParse = false;
					}
					
				} // end while ( tokenizer.hasMoreTokens() ) 
					
				/**
				 * copy result to IStorage...
				 */
				newObject.setArrayInt( bufferedArray );
				
				bufferedArray = null;
				
				break;
			} // end case INT
				
			case FLOAT:
			{
					
				/**
				 * initialize ...
				 */
				float [] bufferedArray = new float [iSizeArray];				
				bParse = true;
				iTokenIndex = 0;
				
				while (( tokenizer.hasMoreTokens() )&&(bParse) ) 
				{				
					try 
					{
						bufferedArray[iTokenIndex] = 
							Float.valueOf( tokenizer.nextToken() );
						
						iTokenIndex++;
					}
					catch (NumberFormatException nfe) {
//						generalManager.logMsg(
//								"Can not convert (String) to (float) at index=[" +
//								iTokenIndex + "]  => skip raw data:",
//								LoggerType.ERROR );
//						generalManager.logMsg(
//								"  SKIP: " + strToParse,
//								LoggerType.ERROR );
						
						/** 
						 * terminate while loop...
						 */
						bParse = false;
					}
					
				} // end while ( tokenizer.hasMoreTokens() ) 
					
				/**
				 * copy result to IStorage...
				 */
				newObject.setArrayFloat( bufferedArray );
				
				bufferedArray = null;
				
				break;
			} // end case FLOAT

			case DOUBLE:
			{
					
				/**
				 * initialize ...
				 */
				double [] bufferedArray = new double [iSizeArray];				
				bParse = true;
				iTokenIndex = 0;
				
				while (( tokenizer.hasMoreTokens() )&&(bParse) ) 
				{				
					try 
					{
						bufferedArray[iTokenIndex] = 
							Double.valueOf( tokenizer.nextToken() );
						
						iTokenIndex++;
					}
					catch (NumberFormatException nfe) {
//						generalManager.logMsg(
//								"Can not convert (String) to (double) at index=[" +
//								iTokenIndex + "]  => skip raw data:",
//								LoggerType.ERROR );
//						generalManager.logMsg(
//								"  SKIP: " + strToParse,
//								LoggerType.ERROR );
						
						/** 
						 * terminate while loop...
						 */
						bParse = false;
					}
					
				} // end while ( tokenizer.hasMoreTokens() ) 
					
				/**
				 * copy result to IStorage...
				 */
				newObject.setArrayDouble ( bufferedArray );
				
				bufferedArray = null;
				
				break;
			} // end case Double
			
			case STRING:
			{
				/**
				 * initialize ...
				 */
				String [] bufferedArray = new String [iSizeArray];				
				bParse = true;
				iTokenIndex = 0;
				
				while ( tokenizer.hasMoreTokens() ) 
				{			
					bufferedArray[iTokenIndex] = tokenizer.nextToken();					
					iTokenIndex++;
					
				} // end while ( tokenizer.hasMoreTokens() ) 
					
				/**
				 * copy result to IStorage...
				 */
				newObject.setArrayString( bufferedArray );
				
				bufferedArray = null;
				
				break;
			} // end case STRING	
			
				
				default:
					throw new CaleydoRuntimeException(
							"CmdDataCreateStorage.doCommand() failed due to unsupported type=["+
							sCurrentDataType +"]");
				
			} //end: switch ( StorageType.valueOf( iter_DataTypes.next() )) {
			
		} //end: while ( iter_DataRaw.hasNext() ) {
						
		
		refStorageManager.registerItem( newObject, 
				newObject.getId(), 
				newObject.getBaseType() );

//		generalManager.logMsg( 
//				"DO new STO: " + 
//				newObject.toString(),
//				LoggerType.VERBOSE );
		
		if ( bDisposeDataAfterDoCommand ) {
			llDataTypes = null;
			llDataRaw = null;
		}
		
		refCommandManager.runDoCommand(this);
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException {
		generalManager.getVirtualArrayManager().unregisterItem( 
				iUniqueId,
				ManagerObjectType.VIRTUAL_ARRAY_MULTI_BLOCK );
		
//		generalManager.logMsg( 
//				"UNDO new SEL: " + 
//				iUniqueId,
//				LoggerType.VERBOSE );	
		
		refCommandManager.runUndoCommand(this);
	}
	

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
				
		assert refParameterHandler != null: "can not handle null object!";		
		
		super.setParameterHandler(refParameterHandler);
		
		/**
		 * Fill data type pattern...
		 */
		StringTokenizer strToken_DataTypes = 
			new StringTokenizer( 
					refParameterHandler.getValueString( 
							CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() ),
							
						IGeneralManager.sDelimiter_Parser_DataType); 

		while ( strToken_DataTypes.hasMoreTokens() ) {
			llDataTypes.add( strToken_DataTypes.nextToken() );
		}

		/* Free memory.. */
		strToken_DataTypes = null;
		
		/** 
		 * Fill raw data...
		 */
		StringTokenizer strTokenLine = new StringTokenizer( 
				refParameterHandler.getValueString( 
						CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey() ),
					IGeneralManager.sDelimiter_Paser_DataItemBlock );
		
		while ( strTokenLine.hasMoreTokens() ) 
		{
			llDataRaw.add( strTokenLine.nextToken() );
		}	
	}

	public void setAttributes(int iStorageID, 
			String sDataTypes, String sRawData) {

		iUniqueId = iStorageID;
		
		/**
		 * Fill data type pattern...
		 */
		StringTokenizer strToken_DataTypes = 
			new StringTokenizer(sDataTypes,
				IGeneralManager.sDelimiter_Parser_DataType); 

		while ( strToken_DataTypes.hasMoreTokens() ) {
			llDataTypes.add( strToken_DataTypes.nextToken() );
		}

		/* Free memory.. */
		strToken_DataTypes = null;
		
		/** 
		 * Fill raw data...
		 */
		StringTokenizer strTokenLine = 
			new StringTokenizer(sRawData,
				IGeneralManager.sDelimiter_Paser_DataItemBlock );
		
		while ( strTokenLine.hasMoreTokens() ) 
		{
			llDataRaw.add( strTokenLine.nextToken() );
		}	
	}
}
