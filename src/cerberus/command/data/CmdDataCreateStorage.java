/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.data;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.StringTokenizer;


import cerberus.data.collection.IStorage;
import cerberus.data.collection.StorageType;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabel;
//import cerberus.command.window.CmdWindowPopupInfo;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.command.factory.CommandFactory;
import cerberus.manager.data.IStorageManager;
import cerberus.util.exception.CerberusRuntimeException;

import cerberus.xml.parser.command.CommandQueueSaxType;
import cerberus.xml.parser.parameter.IParameterHandler;

import cerberus.manager.type.ManagerObjectType;

/**
 * Command, creates a new storage.
 * 
 * @author Michael Kalkusch
 *
 * @see cerberus.data.collection.IStorage
 */
public class CmdDataCreateStorage 
extends ACmdCreate_IdTargetLabel
implements ICommand {

	/**
	 * This list contains the data types for cerberus.data.collection.StorageType as String.
	 * 
	 * Note: llDataRaw.size() == llDataTypes.size() must be equal!
	 *
	 * @see cerberus.data.collection.StorageType
	 * @see cerberus.command.data.CmdDataCreateStorage#llDataRaw
	 * @see cerberus.command.data.CmdDataCreateStorage#bDisposeDataAfterDoCommand
	 */
	protected LinkedList<String> llDataTypes;
	
	/**
	 * This list contains the raw data as String.
	 * The data type for each "String" is defined in the linked list llDataTypes.
	 * 
	 * Note: llDataRaw.size() == llDataTypes.size() must be equal!
	 * 
	 * @see cerberus.command.data.CmdDataCreateStorage#llDataTypes
	 * @see cerberus.command.data.CmdDataCreateStorage#bDisposeDataAfterDoCommand	 
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
	 * 
	 * List of expected Strings inside LinkedList <String>: <br>
	 * sData_CmdId <br>
	 * sData_TargetId <br>
	 * sData_Cmd_label <br>
	 * sData_Cmd_process <br> 
	 * sData_Cmd_MementoId <br> 
	 * sData_Cmd_detail <br>
	 * sData_Cmd_attribute1 <br>
	 * sData_Cmd_attribute2 <br>
	 * 
	 * 
	 */
	public CmdDataCreateStorage( final IGeneralManager refGeneralManager,
			final IParameterHandler refParameterHandler,
			final boolean bDisposeDataAfterDoCommand ) {
		
		super( refGeneralManager, refParameterHandler );
			
		this.bDisposeDataAfterDoCommand = bDisposeDataAfterDoCommand;
		
		llDataTypes = new LinkedList<String> ();
		llDataRaw = new LinkedList<String> ();
	}

	

	/**
	 * Load data from file using a token pattern.
	 * 
	 * @see cerberus.xml.parser.handler.importer.ascii.MicroArrayLoader1Storage#loadData()
	 * 
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws CerberusRuntimeException {
		
		assert llDataTypes != null : "Probably this doCommand() was already executed once!";
		
		IStorageManager refStorageManager = 
			refGeneralManager.getSingelton().getStorageManager();
		
		IStorage newObject = (IStorage) refStorageManager.createStorage(
				ManagerObjectType.STORAGE_FLAT );
		
		newObject.setId( iUniqueTargetId );
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
						refGeneralManager.getSingelton().logMsg(
								"Can not convert (String) to (int) at index=[" +
								iTokenIndex + "]  => skip raw data:",
								LoggerType.ERROR_ONLY );
						refGeneralManager.getSingelton().logMsg(
								"  SKIP: " + strToParse,
								LoggerType.ERROR_ONLY );
						
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
						refGeneralManager.getSingelton().logMsg(
								"Can not convert (String) to (int) at index=[" +
								iTokenIndex + "]  => skip raw data:",
								LoggerType.ERROR_ONLY );
						refGeneralManager.getSingelton().logMsg(
								"  SKIP: " + strToParse,
								LoggerType.ERROR_ONLY );
						
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
					throw new CerberusRuntimeException(
							"CmdDataCreateStorage.doCommand() failed due to unsupported type=["+
							sCurrentDataType +"]");
				
			} //end: switch ( StorageType.valueOf( iter_DataTypes.next() )) {
			
		} //end: while ( iter_DataRaw.hasNext() ) {
						
		
		refStorageManager.registerItem( newObject, 
				newObject.getId(), 
				newObject.getBaseType() );

		refGeneralManager.getSingelton().logMsg( 
				"DO new STO: " + 
				newObject.toString(),
				LoggerType.VERBOSE );
		
		if ( bDisposeDataAfterDoCommand ) {
			llDataTypes = null;
			llDataRaw = null;
		}
	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CerberusRuntimeException {
		refGeneralManager.getSingelton().getVirtualArrayManager().unregisterItem( 
				iUniqueTargetId,
				ManagerObjectType.VIRTUAL_ARRAY_MULTI_BLOCK );
		
		refGeneralManager.getSingelton().logMsg( 
				"UNDO new SEL: " + 
				iUniqueTargetId,
				LoggerType.VERBOSE );		
	}
	

	/**
	 * ISet new target ISet.
	 * 
	 * excpected format: sUniqueId sLabel [sStorageType_TokenPattern [{datacontainer}*]]
	 *
	 * 
	 * @param sUniqueId uniqueId of new target ISet
	 * @return TRUE on successful conversion of Strgin to interger
	 */
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

}
