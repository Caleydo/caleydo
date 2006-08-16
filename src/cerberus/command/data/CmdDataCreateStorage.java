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
import cerberus.command.CommandType;
import cerberus.command.base.ACommand;
import cerberus.command.base.AManagedCommand;
//import cerberus.command.window.CmdWindowPopupInfo;
import cerberus.manager.GeneralManager;
import cerberus.manager.StorageManager;
import cerberus.manager.command.factory.CommandFactory;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.util.system.StringConversionTool;

import cerberus.xml.parser.ACerberusDefaultSaxHandler;

import cerberus.manager.type.ManagerObjectType;

/**
 * Command, creates a new storage.
 * 
 * @author Michael Kalkusch
 *
 * @see cerberus.data.collection.IStorage
 */
public class CmdDataCreateStorage 
extends AManagedCommand
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
	 * Unique Id of the IStorage, that will be created.
	 */
	protected int iUniqueTargetId;
	
	/**
	 * Label of the new IStorage, that will be created.
	 */
	protected String sLabel;
		
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
	 * @see cerberus.data.loader.MicroArrayLoader
	 */
	public CmdDataCreateStorage( GeneralManager refGeneralManager,
			final LinkedList <String> listAttributes,
			final boolean bDisposeDataAfterDoCommand ) {
		
		super( -1, refGeneralManager );
			
		this.bDisposeDataAfterDoCommand = bDisposeDataAfterDoCommand;
		
		llDataTypes = new LinkedList<String> ();
		llDataRaw = new LinkedList<String> ();
		
		setAttributes( listAttributes );
	}
	

	/**
	 * Load data from file using a token pattern.
	 * 
	 * @see cerberus.data.loader.MicroArrayLoader#loadData()
	 * 
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws CerberusRuntimeException {
		
		assert llDataTypes != null : "Probably this doCommand() was already executed once!";
		
		StorageManager refSelectionManager = 
			refGeneralManager.getSingelton().getStorageManager();
		
		IStorage newObject = (IStorage) refSelectionManager.createStorage(
				ManagerObjectType.STORAGE );
		
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
						CommandFactory.sDelimiter_CreateStorage_DataItems );
				
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
						refGeneralManager.getSingelton().getLoggerManager().logMsg(
								"Can not convert (String) to (int) at index=[" +
								iTokenIndex + "]  => skip raw data:");
						refGeneralManager.getSingelton().getLoggerManager().logMsg(
								"  SKIP: " + strToParse );
						
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
						refGeneralManager.getSingelton().getLoggerManager().logMsg(
								"Can not convert (String) to (int) at index=[" +
								iTokenIndex + "]  => skip raw data:");
						refGeneralManager.getSingelton().getLoggerManager().logMsg(
								"  SKIP: " + strToParse );
						
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
						
		
		refSelectionManager.registerItem( newObject, 
				iUniqueTargetId, 
				ManagerObjectType.SELECTION_MULTI_BLOCK );

		refGeneralManager.getSingelton().getLoggerManager().logMsg( 
				"DO new STO: " + 
				newObject.toString() );
		
		if ( bDisposeDataAfterDoCommand ) {
			llDataTypes = null;
			llDataRaw = null;
		}
	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CerberusRuntimeException {
		refGeneralManager.getSingelton().getSelectionManager().unregisterItem( 
				iUniqueTargetId,
				ManagerObjectType.SELECTION_MULTI_BLOCK );
		
		refGeneralManager.getSingelton().getLoggerManager().logMsg( 
				"UNDO new SEL: " + 
				iUniqueTargetId );		
	}
	

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#getCommandType()
	 */
	public CommandType getCommandType() throws CerberusRuntimeException {
		assert false : "add propper type!";
	
		return CommandType.SELECT_NEW; 
	}
	
	
	/**
	 * ISet new target ISet.
	 * 
	 * excpected format: sUniqueId sLabel [sStorageType_TokenPattern [{datacontainer}*]]
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
	 * @param sUniqueId uniqueId of new target ISet
	 * @return TRUE on successful conversion of Strgin to interger
	 */
	protected boolean setAttributes( final LinkedList <String> listAttrib ) {
		
		assert listAttrib != null: "can not handle null object!";		
				
		Iterator <String> iter = listAttrib.iterator();		
		final int iSizeList= listAttrib.size();
		
		assert iSizeList > 1 : "can not handle empty argument list!";					
		

		
		try {			
			this.setId( StringConversionTool.convertStringToInt( iter.next(), -1 ) );
			
			iUniqueTargetId = StringConversionTool.convertStringToInt( iter.next(), -1 );			
			sLabel = StringConversionTool.convertStringToString( iter.next(), 
					Integer.toString(iUniqueTargetId) );			
			
			/**
			 * SKIP:
			 *  sData_Cmd_process 
			 *  sData_Cmd_MementoId  
			 *  sData_Cmd_detail
			 */
			iter.next();
			iter.next();
			iter.next();
			
			/**
			 * Fill data type pattern...
			 */
			StringTokenizer strToken_DataTypes = 
				new StringTokenizer( iter.next(),
						CommandFactory.sDelimiter_CreateStorage_DataType); 
	
			while ( strToken_DataTypes.hasMoreTokens() ) {
				llDataTypes.add( strToken_DataTypes.nextToken() );
			}
	
			/** 
			 * Fill raw data...
			 */
			StringTokenizer strTokenLine = new StringTokenizer( 
					iter.next(), 
					CommandFactory.sDelimiter_CreateStorage_DataItemBlock );
			
			while ( strTokenLine.hasMoreTokens() ) 
			{
				llDataRaw.add( strTokenLine.nextToken() );
			}
			
//			/**
//			 * Copy remainig Strings to internal data structure.
//			 */
//			while ( iter.hasNext() ) {
//				llDataRaw.add( iter.next() );
//			}		
			
			return true;
		}
		catch ( NumberFormatException nfe ) 
		{
			refGeneralManager.getSingelton().getLoggerManager().logMsg(
					"CmdDataCreateSelection::doCommand() error on attributes!");			
			return false;
		}
		
	}

}
