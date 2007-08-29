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
import cerberus.data.collection.SetDataType;
import cerberus.data.collection.SetDetailedDataType;
import cerberus.data.collection.SetType;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.base.ACmdCreate_IdTargetLabelAttr;
//import cerberus.command.window.CmdWindowPopupInfo;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.data.IVirtualArrayManager;
import cerberus.manager.data.ISetManager;
import cerberus.manager.data.IStorageManager;
import cerberus.util.exception.GeneViewRuntimeException;


import cerberus.manager.type.ManagerObjectType;
import cerberus.parser.parameter.IParameterHandler;

/**
 * Command, creates a new storage.
 * 
 * @author Michael Kalkusch
 *
 * @see cerberus.data.collection.IStorage
 */
public class CmdDataCreateSet 
extends ACmdCreate_IdTargetLabelAttr {
	
	private SetDataType setDataType;
	
	private SetDetailedDataType setDetailedDataType;

	private ISet newObject = null;
	
	/**
	 * This list contains the data types for cerberus.data.collection.StorageType as String.
	 * 
	 * Note: llDataRaw.size() == llDataTypes.size() must be equal!
	 *
	 * @see cerberus.data.collection.StorageType
	 * @see cerberus.command.data.CmdDataCreateSet#llRefVirtualArray
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
	protected LinkedList< LinkedList<String> > llRefVirtualArray_nDim;
	
		
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
	public CmdDataCreateSet( final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType,
			final boolean bDisposeDataAfterDoCommand ) {
		
		super(refGeneralManager,
				refCommandManager,
				refCommandQueueSaxType);
		
		this.bDisposeDataAfterDoCommand = bDisposeDataAfterDoCommand;
		
		llRefStorage_nDim 	= new LinkedList< LinkedList<String> > ();
		llRefVirtualArray_nDim 	= new LinkedList< LinkedList<String> > ();
		
		setDataType = SetDataType.SET_LINEAR;
	}
	

	
	private void wipeLinkedLists() 
	{
		/**
		 * Wipe all lists
		 */
		if ( ! llRefVirtualArray_nDim.isEmpty() ) 
		{
			llRefVirtualArray_nDim.clear();
		}
		if ( ! llRefStorage_nDim.isEmpty() ) 
		{
			llRefStorage_nDim.clear();
		}
	}
	

	private boolean assingLinearSet( ISet newObject )
	{
		if (( llRefStorage_nDim.isEmpty() )||
				( llRefVirtualArray_nDim.isEmpty()))
		{
			refGeneralManager.getSingelton().logMsg(
					"CmdDataCreateSet.setAttributes().assingLinearSet() not sufficient data available!",
					LoggerType.MINOR_ERROR );
			
			return false;
		}
		
		try {
				
			LinkedList <String> ll_VirtualArray_1dim = 
				llRefVirtualArray_nDim.getFirst();
			Iterator <String> iter_ll_VirtualArray_1dim = 
				ll_VirtualArray_1dim.iterator();
	
			IVirtualArrayManager refVirtualArrayManager = 
				refGeneralManager.getSingelton().getVirtualArrayManager();
			
			/**
			 * init data structures..
			 */
			
			Vector <IVirtualArray> vecVirtualArray = 
				new Vector <IVirtualArray> ( ll_VirtualArray_1dim.size() );
			
			
			while ( iter_ll_VirtualArray_1dim.hasNext() )
			{
				int iBufferdId = 
					Integer.valueOf( iter_ll_VirtualArray_1dim.next() );
				
				vecVirtualArray.addElement( 
					refVirtualArrayManager.getItemVirtualArray( iBufferdId ) );	
					
			} //while ( iter_ll_Selection_1dim.hasNext() )
			
			newObject.setVirtualArrayByDim( vecVirtualArray, 0 );
			
			
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

			if ( vecStorage.size() != vecVirtualArray.size() ) {
				refGeneralManager.getSingelton().logMsg(
						"CmdDataCreateSet.setAttributes().assingLinearSet() # Selections differs from # Storages! Skip it!",
						LoggerType.MINOR_ERROR );
				
				return false;
			}

			
		} catch (NumberFormatException nfe) {
			refGeneralManager.getSingelton().logMsg(
					"error while creation of ISet!",
					LoggerType.MINOR_ERROR );
		}
		
		return true;
	}
	
	
	private boolean assingPlanarOrMultiDimensionalSet( ISet newObject )
	{
		if (( llRefStorage_nDim.isEmpty() )||
				( llRefVirtualArray_nDim.isEmpty()))
		{
			refGeneralManager.getSingelton().logMsg(
					"CmdDataCreateSet.setAttributes().assingLinearSet() not sufficient data available!",
					LoggerType.MINOR_ERROR );
			
			return false;
		}
		
		boolean bErrorWhileParsing = false;
		
		int iIndexDimensionStorage = 0;
		int iIndexDimensionVirtualArray = 0;		
		
		
		try {
				
			IVirtualArrayManager refVirtualArrayManager = 
				refGeneralManager.getSingelton().getVirtualArrayManager();
			IStorageManager refStorageManager = 
				refGeneralManager.getSingelton().getStorageManager();
			
			
			Iterator < LinkedList <String> > iter_VirtualArray_nDim =
				llRefVirtualArray_nDim.iterator();
			Iterator < LinkedList <String> > iter_Storage_nDim =
				llRefStorage_nDim.iterator();
			
			while (( iter_VirtualArray_nDim.hasNext() )&&
				( iter_Storage_nDim.hasNext() ))
			{
				/**
				 * read Selection data..
				 */
				LinkedList <String> ll_VirtualArray_1dim = 
					iter_VirtualArray_nDim.next();
				Iterator <String> iter_ll_VirtualArray_1dim = 
					ll_VirtualArray_1dim.iterator();
							
				/**
				 * init data structures..
				 */
				
				Vector <IVirtualArray> vecVirtualArray = 
					new Vector <IVirtualArray> ( ll_VirtualArray_1dim.size() );
				
				
				while ( iter_ll_VirtualArray_1dim.hasNext() )
				{
					int iBufferdId = 
						Integer.valueOf( iter_ll_VirtualArray_1dim.next() );
					
					vecVirtualArray.addElement( 
						refVirtualArrayManager.getItemVirtualArray( iBufferdId ) );	
						
				} //while ( iter_ll_VirtualArray_1dim.hasNext() )
				
				newObject.setVirtualArrayByDim( vecVirtualArray,
						iIndexDimensionVirtualArray );				
				iIndexDimensionVirtualArray++;
				
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
				if ( vecStorage.size() != vecVirtualArray.size() )
				{
					refGeneralManager.getSingelton().logMsg(
							"SET: #storages=" +
							ll_Storage_1dim.size() + 
							" differs from #VirtualArrays=" +
							ll_VirtualArray_1dim.size() + 
							"; storage=[" +	
							ll_Storage_1dim.toString() +
							"] VirtualArray=[" +
							ll_VirtualArray_1dim.toString() +
							"]",
							LoggerType.VERBOSE );
					
					bErrorWhileParsing = true;
				} // if ( vecStorage.size() != vecVirtualArray.size() )
				
			} // while (( iter_VirtualArray_nDim.hasNext() )&&( iter_Storage_nDim.hasNext() ))

			/**
			 * Consistency check..
			 */
			if ( iter_VirtualArray_nDim.hasNext() ) {
				refGeneralManager.getSingelton().logMsg(
						"SET: WARNING: there are more VirtualArray-is's available, than storage-id's, skip remainig VirtualArray-is's.",
						LoggerType.VERBOSE );
			} 
			else
			{
				if ( iter_Storage_nDim.hasNext() ) {
				refGeneralManager.getSingelton().logMsg(
						"SET: WARNING: there are more storage-id's available, than VirtualArray-id's, skip remainig storage-id's.",
						LoggerType.VERBOSE );
			} 
			}
			
			if ( iIndexDimensionStorage != iIndexDimensionVirtualArray)
			{
				refGeneralManager.getSingelton().logMsg(
						"SET: dimension(storage) != dimension(VirtualArray) ",
						LoggerType.VERBOSE );
				
				bErrorWhileParsing = true;
			}
			
			if ( bErrorWhileParsing ) 
			{
				refGeneralManager.getSingelton().logMsg(
						"SET: error while parsing!",
						LoggerType.MINOR_ERROR );
				return false;
			}
			
	
			
		} 
		catch (NumberFormatException nfe) 
		{
			refGeneralManager.getSingelton().logMsg(
					"error while creation of ISet! " + 
					nfe.toString(),
					LoggerType.ERROR_ONLY );
		}
		
		return true;
	}
	
	
	/**
	 * Load data from file using a token pattern.
	 * 
	 * @see cerberus.parser.ascii.microarray.MicroArrayLoader1Storage#loadData()
	 * 
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws GeneViewRuntimeException {
		
		assert llRefStorage_nDim != null : "Probably this doCommand() was already executed once!";
		
		ISetManager refSetManager = 
			refGeneralManager.getSingelton().getSetManager();
		
		newObject = null;
		
		if ( setDataType != null ) {
			
			switch ( setDataType ) 
			{
			case SET_LINEAR:
				newObject = (ISet) refSetManager.createSet(
						setDataType);
				
				assingLinearSet( newObject );
				break;
				
			case SET_PLANAR:
				newObject = (ISet) refSetManager.createSet(
						setDataType );
				
				assingPlanarOrMultiDimensionalSet( newObject );
				break;
				
			case SET_MULTI_DIM:
				newObject = (ISet) refSetManager.createSet(
						setDataType );
				
				assingPlanarOrMultiDimensionalSet( newObject );
				break;
				
			case SET_MULTI_DIM_VARIABLE:
			case SET_CUBIC:
				refGeneralManager.getSingelton().logMsg(
						"CmdDataCreateSet.doCommand() known type=[" +
						setDataType + "] but yet now usb-class exists",
						LoggerType.VERBOSE );
				
			default:
				refGeneralManager.getSingelton().logMsg(
							"CmdDataCreateSet.doCommand() failed because type=[" +
							setDataType + "] is not supported!",
							LoggerType.ERROR_ONLY );
				return;
			}
		}  //if ( setDataType != null ) {
		else
		{  //if ( setDataType != null ) {..} else
			newObject = (ISet) refSetManager.createSet( SetType.SET_SELECTION, null);
			
			assingPlanarOrMultiDimensionalSet( newObject );
		} //if ( setDataType != null ) {..} else {..}
		
		newObject.setId( iUniqueTargetId );
		newObject.setLabel( sLabel );
		
		
		/**
		 * Register Set...
		 */
		refGeneralManager.getSingelton().getSetManager().registerItem( 
				newObject, 
				newObject.getId(),
				newObject.getBaseType() );
		
		refGeneralManager.getSingelton().logMsg(
				"SET: " +
				newObject.getClass().getSimpleName() + 
				" done; " + 
				newObject.toString() ,
				LoggerType.FULL );
		

		refGeneralManager.getSingelton().logMsg( 
				"DO new SET: " + 
				iUniqueTargetId,
				LoggerType.VERBOSE );
		
		// Set detailed set data type
		newObject.setRawDataSetType(setDetailedDataType);
		
		refCommandManager.runDoCommand(this);
	}


	
	
	/* (non-Javadoc)
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws GeneViewRuntimeException {
		refGeneralManager.getSingelton().getVirtualArrayManager().unregisterItem( 
				iUniqueId,
				ManagerObjectType.VIRTUAL_ARRAY_MULTI_BLOCK );
		
		refGeneralManager.getSingelton().logMsg( 
				"UNDO new SEL: " + 
				iUniqueId,
				LoggerType.MINOR_ERROR);
		
		refCommandManager.runUndoCommand(this);
	}
	

	
	/**
	 * 
	 */
	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "can not handle null object!";		
		
		super.setParameterHandler(refParameterHandler);
		
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
			this.setDataType = CommandQueueSaxType.valueOf( sDetail );
		}
		
		/**
		 * Separate "text1@text2"
		 */
		
		StringTokenizer strToken_VirtualArrayBlock = 
			new StringTokenizer( 
					refParameterHandler.getValueString( 
							CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() ),	
					IGeneralManager.sDelimiter_Paser_DataItemBlock);
		
		while ( strToken_VirtualArrayBlock.hasMoreTokens() ) 
		{
			/**
			 * Separate "id1 id2 .."
			 */
			StringTokenizer strToken_VirtualArrayId = 
				new StringTokenizer( 
						strToken_VirtualArrayBlock.nextToken(),	
						IGeneralManager.sDelimiter_Parser_DataItems); 
			
			/**
			 * Create buffer list...
			 */
			LinkedList<String> llRefVirtualArray_1dim 	= 
				new LinkedList<String> ();
			
			while ( strToken_VirtualArrayId.hasMoreTokens() ) 
			{
				llRefVirtualArray_1dim.addLast( strToken_VirtualArrayId.nextToken() );
			} // while ( strToken_VirtualArrayId.hasMoreTokens() ) 
			
			if ( ! llRefVirtualArray_1dim.isEmpty() ) {
				/**
				 * insert this list into global list..
				 */
				llRefVirtualArray_nDim.addLast( llRefVirtualArray_1dim );
			}
			else
			{
				refGeneralManager.getSingelton().logMsg(
						"CmdDataCreateSet.setAttributes() empty token inside [" +
						CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() + "]='" +
						strToken_VirtualArrayBlock.toString() + "'",
						LoggerType.STATUS );
				bErrorOnLoadingXMLData = true;
			}
			
		} // while ( strToken_VirtualArrayBlock.hasMoreTokens() )
		
		strToken_VirtualArrayBlock = null;
	
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
							IGeneralManager.sDelimiter_Paser_DataItemBlock);
		
		while ( strToken_StorageBlock.hasMoreTokens() ) 
		{
			/**
			 * Separate "id1 id2 .."
			 */
			StringTokenizer strToken_StorageId = 
				new StringTokenizer( 
						strToken_StorageBlock.nextToken(),	
						IGeneralManager.sDelimiter_Parser_DataItems); 
			
			/**
			 * Create buffer list...
			 */
			LinkedList<String> llRefStorage_1dim 	= 
				new LinkedList<String> ();
			
			while ( strToken_StorageId.hasMoreTokens() ) 
			{
				llRefStorage_1dim.addLast( strToken_StorageId.nextToken() );
			} // while ( strToken_VirtualArrayId.hasMoreTokens() ) 
			
			if ( ! llRefStorage_1dim.isEmpty() ) {
				/**
				 * insert this list into global list..
				 */
				llRefStorage_nDim.addLast( llRefStorage_1dim );
			}
			else
			{
				refGeneralManager.getSingelton().logMsg(
						"CmdDataCreateSet.setAttributes() empty token inside [" +
						CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey() + "]='" +
						strToken_StorageBlock.toString() + "'",
						LoggerType.STATUS );
				bErrorOnLoadingXMLData = true;
			}
			
		} // while ( strToken_VirtualArrayBlock.hasMoreTokens() ) 
		
		/**
		 * read "detail" key ...
		 */
		String sDetailTag = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_DETAIL.getXmlKey() );		
		if ( sDetailTag.length() > 0 ) 
		{
			setDataType = SetDataType.convert( CommandQueueSaxType.valueOf( sDetailTag ) );
		}
		
		
		if ( bErrorOnLoadingXMLData ) 
		{
			refGeneralManager.getSingelton().logMsg(
					"CmdDataCreateSet.setAttributes() empty token! skip line!",
					LoggerType.MINOR_ERROR );
			bErrorOnLoadingXMLData = true;
			
			wipeLinkedLists();
			
			assert false : "did not test assigning setDataType= null!";
			setDataType = null;
		}
		
		/**
		 * read "attrib3" key ...
		 */
		String sAttrib3 = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE3.getXmlKey() );
		
		if ( sAttrib3.length() > 0 ) 
		{
			setDetailedDataType = SetDetailedDataType.valueOf(sAttrib3);
		}
		else
		{
			setDetailedDataType = SetDetailedDataType.NONE;
		}
	}
	
	public void setAttributes(int iSetId, 
			String sVirtualArrayIDs, 
			String sStorageIDs,
			SetDataType setType) {
		
		iUniqueTargetId = iSetId;
		setDataType = setType;
				
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
			this.setDataType = CommandQueueSaxType.valueOf( sDetail );
		}
		
		/**
		 * Separate "text1@text2"
		 */
		
		StringTokenizer strToken_VirtualArrayBlock = 
			new StringTokenizer(sVirtualArrayIDs,	
					IGeneralManager.sDelimiter_Paser_DataItemBlock);
		
		while ( strToken_VirtualArrayBlock.hasMoreTokens() ) 
		{
			/**
			 * Separate "id1 id2 .."
			 */
			StringTokenizer strToken_VirtualArrayId = 
				new StringTokenizer( 
						strToken_VirtualArrayBlock.nextToken(),	
						IGeneralManager.sDelimiter_Parser_DataItems); 
			
			/**
			 * Create buffer list...
			 */
			LinkedList<String> llRefVirtualArray_1dim 	= 
				new LinkedList<String> ();
			
			while ( strToken_VirtualArrayId.hasMoreTokens() ) 
			{
				llRefVirtualArray_1dim.addLast( strToken_VirtualArrayId.nextToken() );
			} // while ( strToken_VirtualArrayId.hasMoreTokens() ) 
			
			if ( ! llRefVirtualArray_1dim.isEmpty() ) {
				/**
				 * insert this list into global list..
				 */
				llRefVirtualArray_nDim.addLast( llRefVirtualArray_1dim );
			}
			else
			{
				refGeneralManager.getSingelton().logMsg(
						"CmdDataCreateSet.setAttributes() empty token inside [" +
						CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() + "]='" +
						strToken_VirtualArrayBlock.toString() + "'",
						LoggerType.STATUS );
			}
			
		} // while ( strToken_VirtualArrayBlock.hasMoreTokens() )
		
		strToken_VirtualArrayBlock = null;
	
		/**
		 * Read TAG_ATTRIBUTE2 "attrib2" for storage!
		 */
		
		/**
		 * Separate "text1@text2"
		 */
		StringTokenizer strToken_StorageBlock = 
			new StringTokenizer(sStorageIDs,	
				IGeneralManager.sDelimiter_Paser_DataItemBlock);
		
		while ( strToken_StorageBlock.hasMoreTokens() ) 
		{
			/**
			 * Separate "id1 id2 .."
			 */
			StringTokenizer strToken_StorageId = 
				new StringTokenizer( 
						strToken_StorageBlock.nextToken(),	
						IGeneralManager.sDelimiter_Parser_DataItems); 
			
			/**
			 * Create buffer list...
			 */
			LinkedList<String> llRefStorage_1dim 	= 
				new LinkedList<String> ();
			
			while ( strToken_StorageId.hasMoreTokens() ) 
			{
				llRefStorage_1dim.addLast( strToken_StorageId.nextToken() );
			} // while ( strToken_VirtualArrayId.hasMoreTokens() ) 
			
			if ( ! llRefStorage_1dim.isEmpty() ) {
				/**
				 * insert this list into global list..
				 */
				llRefStorage_nDim.addLast( llRefStorage_1dim );
			}
			else
			{
				refGeneralManager.getSingelton().logMsg(
						"CmdDataCreateSet.setAttributes() empty token inside [" +
						CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey() + "]='" +
						strToken_StorageBlock.toString() + "'",
						LoggerType.STATUS );
			}
			
		} // while ( strToken_VirtualArrayBlock.hasMoreTokens() ) 
		
		//setDataType = CommandQueueSaxType.NO_OPERATION;
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
