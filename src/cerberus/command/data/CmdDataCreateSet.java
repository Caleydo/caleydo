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
import cerberus.data.collection.SetType;
import cerberus.data.collection.IStorage;

import cerberus.command.ICommand;
import cerberus.command.CommandType;
import cerberus.command.base.ACommand;
//import cerberus.command.window.CmdWindowPopupInfo;
import cerberus.manager.GeneralManager;
import cerberus.manager.SelectionManager;
import cerberus.manager.SetManager;
import cerberus.manager.StorageManager;
import cerberus.manager.command.factory.CommandFactory;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.util.system.StringConversionTool;

import cerberus.xml.parser.CerberusDefaultSaxHandler;

import cerberus.manager.type.ManagerObjectType;

/**
 * Command, creates a new storage.
 * 
 * @author Michael Kalkusch
 *
 * @see cerberus.data.collection.IStorage
 */
public class CmdDataCreateSet 
extends ACommand
implements ICommand {

	private final GeneralManager refGeneralManager;
	
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
	 * Unique Id of the IStorage, that will be created.
	 */
	protected int iUniqueId;
	
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
	 * sData_Cmd_label <br>
	 * sData_Cmd_process <br> 
	 * sData_Cmd_MementoId <br> 
	 * sData_Cmd_detail <br>
	 * sData_Cmd_attribute1 <br>
	 * sData_Cmd_attribute2 <br>
	 * 
	 * @see cerberus.data.loader.MicroArrayLoader
	 */
	public CmdDataCreateSet( GeneralManager refGeneralManager,
			final LinkedList <String> listAttributes,
			final boolean bDisposeDataAfterDoCommand ) {
		
		this.refGeneralManager = refGeneralManager;		
		this.bDisposeDataAfterDoCommand = bDisposeDataAfterDoCommand;
		
		llRefStorage = new LinkedList<String> ();
		llRefSelection = new LinkedList<String> ();
		
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
		
		assert llRefStorage != null : "Probably this doCommand() was already executed once!";
		
		StorageManager refStorageManager = 
			refGeneralManager.getSingelton().getStorageManager();		
		SelectionManager refSelectionManager = 
			refGeneralManager.getSingelton().getSelectionManager();		
		SetManager refSetManager = 
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
			iUniqueId = StringConversionTool.convertStringToInt( iter.next(), -1 );			
			sLabel = StringConversionTool.convertStringToString( iter.next(), 
					Integer.toString(iUniqueId) );			
			
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
			 * Fill selection Id ...
			 */
			StringTokenizer strToken_SelectionId = 
				new StringTokenizer( iter.next(),
						CommandFactory.sDelimiter_CreateSelection_DataItemBlock); 
	
			while ( strToken_SelectionId.hasMoreTokens() ) {
				llRefSelection.add( strToken_SelectionId.nextToken() );
			}
	
			/** 
			 * Fill storage Id ...
			 */
			StringTokenizer strToken_StorageId = new StringTokenizer( 
					iter.next(), 
					CommandFactory.sDelimiter_CreateStorage_DataItemBlock );
			
			while ( strToken_StorageId.hasMoreTokens() ) 
			{
				llRefStorage.add( strToken_StorageId.nextToken() );
			}	
			
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
