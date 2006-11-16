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
import java.util.Vector;


import cerberus.data.collection.IVirtualArray;
import cerberus.data.collection.ISet;
import cerberus.data.collection.IStorage;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabelAttr;
//import cerberus.command.window.CmdWindowPopupInfo;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
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
extends ACmdCreate_IdTargetLabelAttr {
	
	private CommandQueueSaxType set_type;

	private ISet newObject = null;
	
	/**
	 * This list contains the data types for cerberus.data.collection.StorageType as String.
	 * 
	 * Note: llDataRaw.size() == llDataTypes.size() must be equal!
	 *
	 * @see cerberus.data.collection.StorageType
	 * @see cerberus.command.data.CmdDataCreateSet#llRefSelection
	 * @see cerberus.command.data.CmdDataCreateSet#bDisposeDataAfterDoCommand
	 */
	protected LinkedList< LinkedList<String> > llRefStorage_nDim;
	
	/**
	 * This list contains the raw data as String.
	 * The data type for each "String" is defined in the linked list llDataTypes.
	 * 
	 * Note: llDataRaw.size() == llDataTypes.size() must be equal!
	 * 
	 * @see cerberus.command.data.CmdDataCreateSet#llRefStorage
	 * @see cerberus.command.data.CmdDataCreateSet#bDisposeDataAfterDoCommand	 
	 */
	protected LinkedList< LinkedList<String> > llRefSelection_nDim;
	
		
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
	 * @see cerberus.xml.parser.handler.importer.ascii.MicroArrayLoader
	 */
	public CmdDataCreateSet( final IGeneralManager refGeneralManager,
			final IParameterHandler refParameterHandler,
			final boolean bDisposeDataAfterDoCommand ) {
		
		super( refGeneralManager, refParameterHandler );
		
		this.bDisposeDataAfterDoCommand = bDisposeDataAfterDoCommand;
		
		llRefStorage_nDim 	= new LinkedList< LinkedList<String> > ();
		llRefSelection_nDim 	= new LinkedList< LinkedList<String> > ();
		
		set_type = CommandQueueSaxType.CREATE_SET;
		
		setAttributes( refParameterHandler );
	}
	

	/**
	 * Load data from file using a token pattern.
	 * 
	 * @see cerberus.xml.parser.handler.importer.ascii.MicroArrayLoader#loadData()
	 * 
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws CerberusRuntimeException {
		
		assert llRefStorage_nDim != null : "Probably this doCommand() was already executed once!";
		
		ISetManager refSetManager = 
			refGeneralManager.getSingelton().getSetManager();
		
		newObject = null;
		
		switch ( set_type ) 
		{
		case CREATE_SET:
			newObject = (ISet) refSetManager.createSet(
					ManagerObjectType.SET_LINEAR );
			
			assingLinearSet( newObject );
			break;
			
		case CREATE_SET_PLANAR:
			newObject = (ISet) refSetManager.createSet(
					ManagerObjectType.SET_PLANAR );
			
			assingPlanarOrMultiDimensionalSet( newObject );
			break;
			
		default:
			refGeneralManager.getSingelton().getLoggerManager().logMsg(
						"CmdDataCreateSet.doCommand() failed because type=[" +
						set_type + "] is not supported!",
						LoggerType.ERROR_ONLY );
			return;
		}
				
		newObject.setId( iUniqueTargetId );
		newObject.setLabel( sLabel );
		
		
		/**
		 * Register Set...
		 */
		refGeneralManager.getSingelton().getSetManager().registerItem( 
				newObject, 
				newObject.getId(),
				newObject.getBaseType() );
		
		refGeneralManager.getSingelton().getLoggerManager().logMsg(
				"SET: " +
				newObject.getClass().getSimpleName() + 
				" done; " + 
				newObject.toString() ,
				LoggerType.FULL );
		

		refGeneralManager.getSingelton().getLoggerManager().logMsg( 
				"DO new SET: " + 
				iUniqueTargetId,
				LoggerType.VERBOSE );	
	}

	private boolean assingLinearSet( ISet newObject )
	{
		if (( llRefStorage_nDim.isEmpty() )||
				( llRefSelection_nDim.isEmpty()))
		{
			refGeneralManager.getSingelton().getLoggerManager().logMsg(
					"CmdDataCreateSet.setAttributes().assingLinearSet() not sufficient data available!",
					LoggerType.MINOR_ERROR );
			
			return false;
		}
		
		try {
				
			LinkedList <String> ll_Selection_1dim = 
				llRefSelection_nDim.getFirst();
			Iterator <String> iter_ll_Selection_1dim = 
				ll_Selection_1dim.iterator();
	
			ISelectionManager refSelectionManager = 
				refGeneralManager.getSingelton().getSelectionManager();
			
			/**
			 * init data structures..
			 */
			
			Vector <IVirtualArray> vecSelection = 
				new Vector <IVirtualArray> ( ll_Selection_1dim.size() );
			
			
			while ( iter_ll_Selection_1dim.hasNext() )
			{
				int iBufferdId = 
					Integer.valueOf( iter_ll_Selection_1dim.next() );
				
				vecSelection.addElement( 
					refSelectionManager.getItemSelection( iBufferdId ) );	
					
			} //while ( iter_ll_Selection_1dim.hasNext() )
			
			newObject.setSelectionByDim( vecSelection, 0 );
			
			
			/**
			 * assign Storage ...
			 */
			LinkedList <String> ll_Storage_1dim = 
				llRefStorage_nDim.getFirst();
			Iterator <String> iter_ll_Storage_1dim = 
				ll_Storage_1dim.iterator();
			
			
			IStorageManager refStorageManager = 
				refGeneralManager.getSingelton().getStorageManager();
			
			Vector <IStorage> vecStorage = 
				new Vector <IStorage> (ll_Storage_1dim.size());
			
			while ( iter_ll_Storage_1dim.hasNext() )
			{
				int iBufferdId = 
					Integer.valueOf( iter_ll_Storage_1dim.next() );
				
				vecStorage.addElement( 
					refStorageManager.getItemStorage( iBufferdId ) );	
					
			} //while ( iter_ll_Storage_1dim.hasNext() )
			
			newObject.setStorageByDim( vecStorage, 0 );

			if ( vecStorage.size() != vecSelection.size() ) {
				refGeneralManager.getSingelton().getLoggerManager().logMsg(
						"CmdDataCreateSet.setAttributes().assingLinearSet() # Selections differs from # Storages! Skip it!",
						LoggerType.MINOR_ERROR );
				
				return false;
			}

			
		} catch (NumberFormatException nfe) {
			refGeneralManager.getSingelton().getLoggerManager().logMsg(
					"error while creation of ISet!");
		}
		
		return true;
	}
	
	
	private boolean assingPlanarOrMultiDimensionalSet( ISet newObject )
	{
		if (( llRefStorage_nDim.isEmpty() )||
				( llRefSelection_nDim.isEmpty()))
		{
			refGeneralManager.getSingelton().getLoggerManager().logMsg(
					"CmdDataCreateSet.setAttributes().assingLinearSet() not sufficient data available!",
					LoggerType.MINOR_ERROR );
			
			return false;
		}
		
		boolean bErrorWhileParsing = false;
		
		int iIndexDimensionStorage = 0;
		int iIndexDimensionSelection = 0;		
		
		
		try {
				
			ISelectionManager refSelectionManager = 
				refGeneralManager.getSingelton().getSelectionManager();
			IStorageManager refStorageManager = 
				refGeneralManager.getSingelton().getStorageManager();
			
			
			Iterator < LinkedList <String> > iter_Selection_nDim =
				llRefSelection_nDim.iterator();
			Iterator < LinkedList <String> > iter_Storage_nDim =
				llRefStorage_nDim.iterator();
			
			while (( iter_Selection_nDim.hasNext() )&&
				( iter_Storage_nDim.hasNext() ))
			{
				/**
				 * read Selection data..
				 */
				LinkedList <String> ll_Selection_1dim = 
					iter_Selection_nDim.next();
				Iterator <String> iter_ll_Selection_1dim = 
					ll_Selection_1dim.iterator();
							
				/**
				 * init data structures..
				 */
				
				Vector <IVirtualArray> vecSelection = 
					new Vector <IVirtualArray> ( ll_Selection_1dim.size() );
				
				
				while ( iter_ll_Selection_1dim.hasNext() )
				{
					int iBufferdId = 
						Integer.valueOf( iter_ll_Selection_1dim.next() );
					
					vecSelection.addElement( 
						refSelectionManager.getItemSelection( iBufferdId ) );	
						
				} //while ( iter_ll_Selection_1dim.hasNext() )
				
				newObject.setSelectionByDim( vecSelection,
						iIndexDimensionSelection );				
				iIndexDimensionSelection++;
				
				/**
				 * read Storage data..
				 */
				
		
				LinkedList <String> ll_Storage_1dim = 
					iter_Storage_nDim.next();
				Iterator <String> iter_ll_Storage_1dim = 
					ll_Storage_1dim.iterator();
				
				
				Vector <IStorage> vecStorage = 
					new Vector <IStorage> (ll_Storage_1dim.size());
				
				while ( iter_ll_Storage_1dim.hasNext() )
				{
					int iBufferdId = 
						Integer.valueOf( iter_ll_Storage_1dim.next() );
					
					vecStorage.addElement( 
						refStorageManager.getItemStorage( iBufferdId ) );	
						
				} //while ( iter_ll_Storage_1dim.hasNext() )
				
				newObject.setStorageByDim( vecStorage,
						iIndexDimensionStorage );				
				iIndexDimensionStorage++;
				
				/**
				 * Consistency check...
				 */
				if ( vecStorage.size() != vecSelection.size() )
				{
					refGeneralManager.getSingelton().getLoggerManager().logMsg(
							"SET: #storages=" +
							ll_Storage_1dim.size() + 
							" differs from #selections=" +
							ll_Selection_1dim.size() + 
							"; storage=[" +	
							ll_Storage_1dim.toString() +
							"] selection=[" +
							ll_Selection_1dim.toString() +
							"]",
							LoggerType.VERBOSE );
					
					bErrorWhileParsing = true;
				} // if ( vecStorage.size() != vecSelection.size() )
				
			} // while (( iter_Selection_nDim.hasNext() )&&( iter_Storage_nDim.hasNext() ))

			/**
			 * Consistency check..
			 */
			if ( iter_Selection_nDim.hasNext() ) {
				refGeneralManager.getSingelton().getLoggerManager().logMsg(
						"SET: WARNING: there are more selection-is's available, than storage-id's, skip remainig selection-is's.",
						LoggerType.VERBOSE );
			} 
			else
			{
				if ( iter_Storage_nDim.hasNext() ) {
				refGeneralManager.getSingelton().getLoggerManager().logMsg(
						"SET: WARNING: there are more storage-id's available, than selection-id's, skip remainig storage-id's.",
						LoggerType.VERBOSE );
			} 
			}
			
			if ( iIndexDimensionStorage != iIndexDimensionSelection)
			{
				refGeneralManager.getSingelton().getLoggerManager().logMsg(
						"SET: dimension(storage) != dimension(selection) ",
						LoggerType.VERBOSE );
				
				bErrorWhileParsing = true;
			}
			
			if ( bErrorWhileParsing ) 
			{
				refGeneralManager.getSingelton().getLoggerManager().logMsg(
						"SET: error while parsing!",
						LoggerType.MINOR_ERROR );
				return false;
			}
			
	
			
		} 
		catch (NumberFormatException nfe) 
		{
			refGeneralManager.getSingelton().getLoggerManager().logMsg(
					"error while creation of ISet! " + 
					nfe.toString(),
					LoggerType.ERROR_ONLY );
		}
		
		return true;
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
	
	private void wipeLinkedLists() 
	{
		/**
		 * Wipe all lists
		 */
		if ( ! llRefSelection_nDim.isEmpty() ) 
		{
			llRefSelection_nDim.clear();
		}
		if ( ! llRefStorage_nDim.isEmpty() ) 
		{
			llRefStorage_nDim.clear();
		}
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
	protected boolean setAttributes( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "can not handle null object!";		
		
		boolean bErrorOnLoadingXMLData = false;
			
		/**
		 * Wipe all lists
		 */
		wipeLinkedLists();
		
		/**
		 * Read TAG_ATTRIBUTE1 "attrib1" for storage!
		 */
		
		/**
		String sDetail = 
			refParameterHandler.getValueString( 
					CommandQueueSaxType.TAG_DETAIL.getXmlKey() );
	
		if ( sDetail.length() > 0 ) {
			this.set_type = CommandQueueSaxType.valueOf( sDetail );
		}
		
		/**
		 * Separate "text1@text2"
		 */
		
		StringTokenizer strToken_SelectionBlock = 
			new StringTokenizer( 
					refParameterHandler.getValueString( 
							CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() ),	
					CommandFactory.sDelimiter_Paser_DataItemBlock);
		
		while ( strToken_SelectionBlock.hasMoreTokens() ) 
		{
			/**
			 * Separate "id1 id2 .."
			 */
			StringTokenizer strToken_SelectionId = 
				new StringTokenizer( 
						strToken_SelectionBlock.nextToken(),	
						CommandFactory.sDelimiter_Parser_DataItems); 
			
			/**
			 * Create buffer list...
			 */
			LinkedList<String> llRefSelection_1dim 	= 
				new LinkedList<String> ();
			
			while ( strToken_SelectionId.hasMoreTokens() ) 
			{
				llRefSelection_1dim.addLast( strToken_SelectionId.nextToken() );
			} // while ( strToken_SelectionId.hasMoreTokens() ) 
			
			if ( ! llRefSelection_1dim.isEmpty() ) {
				/**
				 * insert this list into global list..
				 */
				llRefSelection_nDim.addLast( llRefSelection_1dim );
			}
			else
			{
				refGeneralManager.getSingelton().getLoggerManager().logMsg(
						"CmdDataCreateSet.setAttributes() empty token inside [" +
						CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() + "]='" +
						strToken_SelectionBlock.toString() + "'",
						LoggerType.STATUS );
				bErrorOnLoadingXMLData = true;
			}
			
		} // while ( strToken_SelectionBlock.hasMoreTokens() )
		
		strToken_SelectionBlock = null;
	
		/**
		 * Read TAG_ATTRIBUTE2 "attrib2" for storage!
		 */
		
		/**
		 * Separate "text1@text2"
		 */
		StringTokenizer strToken_StorageBlock = 
			new StringTokenizer( 
					refParameterHandler.getValueString( 
							CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey() ),	
					CommandFactory.sDelimiter_Paser_DataItemBlock);
		
		while ( strToken_StorageBlock.hasMoreTokens() ) 
		{
			/**
			 * Separate "id1 id2 .."
			 */
			StringTokenizer strToken_StorageId = 
				new StringTokenizer( 
						strToken_StorageBlock.nextToken(),	
						CommandFactory.sDelimiter_Parser_DataItems); 
			
			/**
			 * Create buffer list...
			 */
			LinkedList<String> llRefStorage_1dim 	= 
				new LinkedList<String> ();
			
			while ( strToken_StorageId.hasMoreTokens() ) 
			{
				llRefStorage_1dim.addLast( strToken_StorageId.nextToken() );
			} // while ( strToken_SelectionId.hasMoreTokens() ) 
			
			if ( ! llRefStorage_1dim.isEmpty() ) {
				/**
				 * insert this list into global list..
				 */
				llRefStorage_nDim.addLast( llRefStorage_1dim );
			}
			else
			{
				refGeneralManager.getSingelton().getLoggerManager().logMsg(
						"CmdDataCreateSet.setAttributes() empty token inside [" +
						CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey() + "]='" +
						strToken_StorageBlock.toString() + "'",
						LoggerType.STATUS );
				bErrorOnLoadingXMLData = true;
			}
			
		} // while ( strToken_SelectionBlock.hasMoreTokens() ) 
		
		/**
		 * read "detail" key ...
		 */
		String sDetailTag = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_DETAIL.getXmlKey() );		
		if ( sDetailTag.length() > 0 ) 
		{
			set_type = CommandQueueSaxType.valueOf( sDetailTag );
		}
		
		
		if ( bErrorOnLoadingXMLData ) 
		{
			refGeneralManager.getSingelton().getLoggerManager().logMsg(
					"CmdDataCreateSet.setAttributes() empty token! skip line!",
					LoggerType.MINOR_ERROR );
			bErrorOnLoadingXMLData = true;
			
			wipeLinkedLists();
			set_type = CommandQueueSaxType.NO_OPERATION;
			return false;
		}
		
		return true;
	}
	
	public String toString() {
		String result = this.getClass().getSimpleName();
		
		if ( newObject == null ) 
		{
			result += " SET: ---"; 
		}
		else
		{
			result += " SET: " + newObject.toString();
		}
		
		return result;
	}

}
