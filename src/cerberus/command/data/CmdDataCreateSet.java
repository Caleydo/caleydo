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


import cerberus.data.collection.ISelection;
import cerberus.data.collection.ISet;
import cerberus.data.collection.IStorage;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabel;
//import cerberus.command.window.CmdWindowPopupInfo;
import cerberus.manager.IGeneralManager;
import cerberus.manager.command.factory.CommandFactory;
import cerberus.manager.data.ISelectionManager;
import cerberus.manager.data.ISetManager;
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
public class CmdDataCreateSet 
extends ACmdCreate_IdTargetLabel
implements ICommand {
	
	/**
	 * This list contains the data types for cerberus.data.collection.StorageType as String.
	 * 
	 * Note: llDataRaw.size() == llDataTypes.size() must be equal!
	 *
	 * @see cerberus.data.collection.StorageType
	 * @see cerberus.command.data.CmdDataCreateSet#llRefSelection
	 * @see cerberus.command.data.CmdDataCreateSet#bDisposeDataAfterDoCommand
	 */
	protected LinkedList<String> llRefStorage;
	
	/**
	 * This list contains the raw data as String.
	 * The data type for each "String" is defined in the linked list llDataTypes.
	 * 
	 * Note: llDataRaw.size() == llDataTypes.size() must be equal!
	 * 
	 * @see cerberus.command.data.CmdDataCreateSet#llRefStorage
	 * @see cerberus.command.data.CmdDataCreateSet#bDisposeDataAfterDoCommand	 
	 */
	protected LinkedList<String> llRefSelection;
	

		
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
	 * sData_Cmd_label <br>
	 * sData_Cmd_process <br> 
	 * sData_Cmd_MementoId <br> 
	 * sData_Cmd_detail <br>
	 * sData_Cmd_attribute1 <br>
	 * sData_Cmd_attribute2 <br>
	 * 
	 * @see cerberus.data.loader.MicroArrayLoader
	 */
	public CmdDataCreateSet( final IGeneralManager refGeneralManager,
			final IParameterHandler refParameterHandler,
			final boolean bDisposeDataAfterDoCommand ) {
		
		super( refGeneralManager, refParameterHandler );
		
		this.bDisposeDataAfterDoCommand = bDisposeDataAfterDoCommand;
		
		llRefStorage = new LinkedList<String> ();
		llRefSelection = new LinkedList<String> ();
		
		setAttributes( refParameterHandler );
	}
	

	/**
	 * Load data from file using a token pattern.
	 * 
	 * @see cerberus.data.loader.MicroArrayLoader#loadData()
	 * 
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws CerberusRuntimeException {
		
		assert llRefStorage != null : "Probably this doCommand() was already executed once!";
		
		IStorageManager refStorageManager = 
			refGeneralManager.getSingelton().getStorageManager();		
		ISelectionManager refSelectionManager = 
			refGeneralManager.getSingelton().getSelectionManager();		
		ISetManager refSetManager = 
			refGeneralManager.getSingelton().getSetManager();
		
		
		ISet newObject = (ISet) refSetManager.createSet(
				ManagerObjectType.SET_LINEAR );
		
		newObject.setId( iUniqueId );
		newObject.setLabel( sLabel );
		
		
		Iterator <String> iter_Storage 		= llRefStorage.iterator();
		Iterator <String> iter_Selection 	= llRefSelection.iterator();
		
		int iIndexDimensionStorage = 0;
		int iIndexDimensionSelection = 0;		
		
		try {
					
			while ( iter_Selection.hasNext() ) {
					
				StringTokenizer tokenizer_Selection = 
					new StringTokenizer( iter_Selection.next(), 
							CommandFactory.sDelimiter_CreateSelection_DataItems );
				
				int iDim_Sel = tokenizer_Selection.countTokens();
				int iIndexSelection = 0;
				ISelection[] arraySelection = new ISelection[iDim_Sel];
						
				while ( tokenizer_Selection.hasMoreTokens() ) 
				{		
					iUniqueId = 
						Integer.valueOf( tokenizer_Selection.nextToken() );
					
					arraySelection[iIndexSelection] = 
						refSelectionManager.getItemSelection( iUniqueId );
					
					iIndexSelection++;
					
					
				} // end while ( tokenizer_Storage() )
				
				newObject.setSelectionByDim( arraySelection,
						iIndexDimensionSelection );
				
				iIndexDimensionSelection++;
				
			} //end: while ( iter_Selection.hasNext() ) {
			
			
			while ( iter_Storage.hasNext() ) {
				
				StringTokenizer tokenizer_Storage = 
					new StringTokenizer( iter_Storage.next(), 
							CommandFactory.sDelimiter_CreateStorage_DataItems );
				
				int iDim_Storage = tokenizer_Storage.countTokens();
				int iIndexStorage = 0;
				IStorage[] arrayStorage = new IStorage[iDim_Storage];
				
				while ( tokenizer_Storage.hasMoreTokens() ) 
				{		
					iUniqueId = 
						Integer.valueOf( tokenizer_Storage.nextToken() );
					
					arrayStorage[iIndexStorage] = 
						refStorageManager.getItemStorage( iUniqueId );
					
					iIndexStorage++;
				} // end while ( tokenizer_Storage.hasMoreTokens() ) 
				
				newObject.setStorageByDim( arrayStorage,
						iIndexDimensionStorage );
				
				iIndexDimensionStorage++;
				
			} //end: while ( iter_Storage.hasNext() ) {
					
			refSetManager.registerItem( newObject, 
					newObject.getId(),
					newObject.getBaseType() );
			
			System.out.println("SET: done! " + newObject.toString() );
			
		} catch (NumberFormatException nfe) {
			refGeneralManager.getSingelton().getLoggerManager().logMsg(
					"error while creation of ISet!");
		}
	}

	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CerberusRuntimeException {
		refGeneralManager.getSingelton().getSelectionManager().unregisterItem( 
				iUniqueId,
				ManagerObjectType.SELECTION_MULTI_BLOCK );
		
		refGeneralManager.getSingelton().getLoggerManager().logMsg( 
				"UNDO new SEL: " + 
				iUniqueId );		
	}
	

//	/* (non-Javadoc)
//	 * @see cerberus.command.ICommand#getCommandType()
//	 */
//	public CommandType getCommandType() throws CerberusRuntimeException {
//		assert false : "add propper type!";
//	
//		return CommandType.SELECT_NEW; 
//	}
	
	
	/**
	 * ISet new target ISet.
	 * 
	 * excpected format: sUniqueId sLabel [sStorageType_TokenPattern [{datacontainer}*]]
	 *
	 * List of expected Strings inside LinkedList <String>: <br>
	 * sData_CmdId <br>
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
	protected boolean setAttributes( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "can not handle null object!";		
		
		setAttributesBase( refParameterHandler );
		
		/**
		 * Fill data type pattern...
		 */		
			
		/**
		 * Fill selection Id ...
		 */
		StringTokenizer strToken_SelectionId = 
			new StringTokenizer( 
					refParameterHandler.getValueString( 
							CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() ),	
					CommandFactory.sDelimiter_CreateSelection_DataItemBlock); 

		while ( strToken_SelectionId.hasMoreTokens() ) {
			llRefSelection.add( strToken_SelectionId.nextToken() );
		}

		/** 
		 * Fill storage Id ...
		 */
		StringTokenizer strToken_StorageId = new StringTokenizer( 
				refParameterHandler.getValueString( 
						CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() ),	
				CommandFactory.sDelimiter_CreateStorage_DataItemBlock );
		
		while ( strToken_StorageId.hasMoreTokens() ) 
		{
			llRefStorage.add( strToken_StorageId.nextToken() );
		}	
		
		return true;
		
		
	}

}
