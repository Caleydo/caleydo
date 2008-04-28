package org.caleydo.core.command.data;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;


import org.caleydo.core.data.collection.IVirtualArray;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.SetDataType;
import org.caleydo.core.data.collection.SetType;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelAttr;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ILoggerManager.LoggerType;
import org.caleydo.core.manager.data.IVirtualArrayManager;
import org.caleydo.core.manager.data.ISetManager;
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
public class CmdDataCreateSet 
extends ACmdCreate_IdTargetLabelAttr {
	
	private SetType setType;

	private ISet newObject = null;
	
	/**
	 * This list contains the data types for org.caleydo.core.data.collection.StorageType as String.
	 * 
	 * Note: llDataRaw.size() == llDataTypes.size() must be equal!
	 *
	 * @see org.caleydo.core.data.collection.StorageType
	 * @see org.caleydo.core.command.data.CmdDataCreateSet#llRefVirtualArray
	 * @see org.caleydo.core.command.data.CmdDataCreateSet#bDisposeDataAfterDoCommand
	 */
	protected LinkedList< LinkedList<String> > llRefStorage_nDim;
	
	/**
	 * This list contains the raw data as String.
	 * The data type for each "String" is defined in the linked list llDataTypes.
	 * 
	 * Note: llDataRaw.size() == llDataTypes.size() must be equal!
	 * 
	 * @see org.caleydo.core.command.data.CmdDataCreateSet#llRefStorage
	 * @see org.caleydo.core.command.data.CmdDataCreateSet#bDisposeDataAfterDoCommand	 
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
		
		setType = SetType.SET_NONE;
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
	

//	private boolean assingLinearSet( ISet newObject )
//	{
//		if (( llRefStorage_nDim.isEmpty() )||
//				( llRefVirtualArray_nDim.isEmpty()))
//		{
//			generalManager.getSingleton().logMsg(
//					"CmdDataCreateSet.setAttributes().assingLinearSet() not sufficient data available!",
//					LoggerType.MINOR_ERROR );
//			
//			return false;
//		}
//		
//		try {
//				
//			LinkedList <String> ll_VirtualArray_1dim = 
//				llRefVirtualArray_nDim.getFirst();
//			Iterator <String> iter_ll_VirtualArray_1dim = 
//				ll_VirtualArray_1dim.iterator();
//	
//			IVirtualArrayManager refVirtualArrayManager = 
//				generalManager.getSingleton().getVirtualArrayManager();
//			
//			/**
//			 * init data structures..
//			 */
//			
//			Vector <IVirtualArray> vecVirtualArray = 
//				new Vector <IVirtualArray> ( ll_VirtualArray_1dim.size() );
//			
//			
//			while ( iter_ll_VirtualArray_1dim.hasNext() )
//			{
//				int iBufferdId = 
//					Integer.valueOf( iter_ll_VirtualArray_1dim.next() );
//				
//				vecVirtualArray.addElement( 
//					refVirtualArrayManager.getItemVirtualArray( iBufferdId ) );	
//					
//			} //while ( iter_ll_Selection_1dim.hasNext() )
//			
//			newObject.setVirtualArrayByDim( vecVirtualArray, 0 );
//			
//			
//			/**
//			 * assign Storage ...
//			 */
//			LinkedList <String> ll_Storage_1dim = 
//				llRefStorage_nDim.getFirst();
//			Iterator <String> iter_ll_Storage_1dim = 
//				ll_Storage_1dim.iterator();
//			
//			
//			IStorageManager refStorageManager = 
//				generalManager.getSingleton().getStorageManager();
//			
//			Vector <IStorage> vecStorage = 
//				new Vector <IStorage> (ll_Storage_1dim.size());
//			
//			while ( iter_ll_Storage_1dim.hasNext() )
//			{
//				int iBufferdId = 
//					Integer.valueOf( iter_ll_Storage_1dim.next() );
//				
//				vecStorage.addElement( 
//					refStorageManager.getItemStorage( iBufferdId ) );	
//					
//			} //while ( iter_ll_Storage_1dim.hasNext() )
//			
//			newObject.setStorageByDim( vecStorage, 0 );
//
//			if ( vecStorage.size() != vecVirtualArray.size() ) {
//				generalManager.getSingleton().logMsg(
//						"CmdDataCreateSet.setAttributes().assingLinearSet() # Selections differs from # Storages! Skip it!",
//						LoggerType.MINOR_ERROR );
//				
//				return false;
//			}
//
//			
//		} catch (NumberFormatException nfe) {
//			generalManager.getSingleton().logMsg(
//					"error while creation of ISet!",
//					LoggerType.MINOR_ERROR );
//		}
//		
//		return true;
//	}
	
	
	private boolean assingPlanarOrMultiDimensionalSet( ISet newObject )
	{
		if (( llRefStorage_nDim.isEmpty() )||
				( llRefVirtualArray_nDim.isEmpty()))
		{
			generalManager.getSingleton().logMsg(
					"CmdDataCreateSet.setAttributes().assingPlanarOrMultiDimensionalSet() not sufficient data available!",
					LoggerType.MINOR_ERROR );
			
			return false;
		}
		
		boolean bErrorWhileParsing = false;
		
		int iIndexDimensionStorage = 0;
		int iIndexDimensionVirtualArray = 0;		
		
		
		try {
				
			IVirtualArrayManager refVirtualArrayManager = 
				generalManager.getSingleton().getVirtualArrayManager();
			IStorageManager refStorageManager = 
				generalManager.getSingleton().getStorageManager();
			
			
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
				
				/**
				 * Empty Vector indicates miss configuration!
				 */
				if ( vecVirtualArray.isEmpty() )
				{
					generalManager.getSingleton().logMsg( this.getClass().getSimpleName() + 
							" parsing; VirtualArray[" + iIndexDimensionVirtualArray +
							"] contains no data! This is most probably a miss configuration! uniqueId=[" +
							newObject.getId() + "]", 
							LoggerType.MINOR_ERROR_XML);
				}
				
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
				
				int iIndexStorage = 0;
				while ( iter_ll_Storage_1dim.hasNext() )
				{
					int iBufferdId = 
						Integer.valueOf( iter_ll_Storage_1dim.next() );
					
					vecStorage.addElement( 
						refStorageManager.getItemStorage( iBufferdId ) );	
						
					//newObject.setStorageByDim(refStorageManager.getItemStorage( iBufferdId ), iIndexStorage);
					
					iIndexStorage++;
				} //while ( iter_ll_Storage_1dim.hasNext() )
				
				/**
				 * Empty Vector indicates miss configuration!
				 */
				if ( vecStorage.isEmpty() )
				{
					generalManager.getSingleton().logMsg( this.getClass().getSimpleName() + 
							" parsing; Storage[" + iIndexDimensionVirtualArray +
							"] contains no data! This is most probably a miss configuration! uniqueId=[" +
							newObject.getId() + "]", 
							LoggerType.MINOR_ERROR_XML);
				}
				newObject.setStorageByDim( vecStorage,
						iIndexDimensionStorage );				
				iIndexDimensionStorage++;
				
				/**
				 * Consistency check...
				 */
				if ( vecStorage.size() != vecVirtualArray.size() )
				{
					generalManager.getSingleton().logMsg(
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
					
					// make both equal length using larger array as set-parameter..
					if ( vecStorage.size() > vecVirtualArray.size() )
					{
						vecVirtualArray.setSize( vecStorage.size()-1 );
					}
					else
					{
						vecStorage.setSize( vecVirtualArray.size()-1 );
					}
					
					
				} // if ( vecStorage.size() != vecVirtualArray.size() )
				
			} // while (( iter_VirtualArray_nDim.hasNext() )&&( iter_Storage_nDim.hasNext() ))

			/**
			 * Consistency check..
			 */
			if ( iter_VirtualArray_nDim.hasNext() ) {
				generalManager.getSingleton().logMsg(
						"SET: WARNING: there are more VirtualArray-is's available, than storage-id's, skip remainig VirtualArray-is's.",
						LoggerType.VERBOSE );
			} 
			else
			{
				if ( iter_Storage_nDim.hasNext() ) {
				generalManager.getSingleton().logMsg(
						"SET: WARNING: there are more storage-id's available, than VirtualArray-id's, skip remainig storage-id's.",
						LoggerType.VERBOSE );
			} 
			}
			
			if ( iIndexDimensionStorage != iIndexDimensionVirtualArray)
			{
				generalManager.getSingleton().logMsg(
						"SET: dimension(storage) != dimension(VirtualArray) ",
						LoggerType.VERBOSE );
				
				bErrorWhileParsing = true;
			}
			
			if ( bErrorWhileParsing ) 
			{
				generalManager.getSingleton().logMsg(
						"SET: error while parsing!",
						LoggerType.MINOR_ERROR );
				return false;
			}
			
	
			
		} 
		catch (NumberFormatException nfe) 
		{
			generalManager.getSingleton().logMsg(
					"error while creation of ISet! " + 
					nfe.toString(),
					LoggerType.ERROR );
		}
		
		return true;
	}
	
	
	/**
	 * Load data from file using a token pattern.
	 * 
	 * @see org.caleydo.core.parser.ascii.microarray.MicroArrayLoader1Storage#loadData()
	 * 
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException {
		
		assert llRefStorage_nDim != null : "Probably this doCommand() was already executed once!";
		
		ISetManager refSetManager = 
			generalManager.getSingleton().getSetManager();
		
		newObject = null;

		SetDataType setDataType = setType.getDataType();
		
		switch ( setDataType ) 
		{
		case SET_LINEAR:
		case SET_PLANAR:
		case SET_MULTI_DIM:
			newObject = (ISet) refSetManager.createSet(setType);
			
			/* set id now to make debugging XML file easier in case of an XML miss configuration */
			newObject.setId( iUniqueId );
			assingPlanarOrMultiDimensionalSet( newObject );
			break;
			
		case SET_MULTI_DIM_VARIABLE:
		case SET_CUBIC:
			generalManager.getSingleton().logMsg(
					"CmdDataCreateSet.doCommand() known type=[" +
					setDataType + "] but yet now usb-class exists",
					LoggerType.VERBOSE );
			
		default:
			generalManager.getSingleton().logMsg(
						"CmdDataCreateSet.doCommand() failed because type=[" +
						setDataType + "] is not supported!",
						LoggerType.ERROR );
			return;
		}
				
		newObject.setLabel( sLabel );
		
		
		/**
		 * Register Set...
		 */
		generalManager.getSingleton().getSetManager().registerItem( 
				newObject, 
				newObject.getId(),
				newObject.getBaseType() );
		
		generalManager.getSingleton().logMsg(
				"SET: " +
				newObject.getClass().getSimpleName() + 
				" done; " + 
				newObject.toString() ,
				LoggerType.FULL );
		

		generalManager.getSingleton().logMsg( 
				"DO new SET: " + 
				iUniqueId,
				LoggerType.VERBOSE );
		
//		// Set detailed set data type
//		newObject.set(setDetailedDataType);
		
		refCommandManager.runDoCommand(this);
	}


	
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException {
		generalManager.getSingleton().getVirtualArrayManager().unregisterItem( 
				iUniqueId,
				ManagerObjectType.VIRTUAL_ARRAY_MULTI_BLOCK );
		
		generalManager.getSingleton().logMsg( 
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
		
//		/**
//		 * Read TAG_ATTRIBUTE1 "attrib1" for storage!
//		 */		
//		String sDetail = 
//			refParameterHandler.getValueString( 
//					CommandQueueSaxType.TAG_DETAIL.getXmlKey() );
//	
//		if ( sDetail.length() > 0 ) {
//			try 
//			{
//				setDataType = SetDataType.valueOf( sDetail );
//			}
//			catch ( IllegalArgumentException iae )
//			{
//				setDataType = SetDataType.getDefault();
//			}
//		}
		
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
				generalManager.getSingleton().logMsg(
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
				generalManager.getSingleton().logMsg(
						"CmdDataCreateSet.setAttributes() empty token inside [" +
						CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey() + "]='" +
						strToken_StorageBlock.toString() + "'",
						LoggerType.STATUS );
				bErrorOnLoadingXMLData = true;
			}
			
		} // while ( strToken_VirtualArrayBlock.hasMoreTokens() ) 
		
		
		
		if ( bErrorOnLoadingXMLData ) 
		{
			generalManager.getSingleton().logMsg(
					"CmdDataCreateSet.setAttributes() empty token! skip line!",
					LoggerType.MINOR_ERROR );
			bErrorOnLoadingXMLData = true;
			
			wipeLinkedLists();
			
		}
		
		/**
		 * read "attrib3" key ...
		 */
		String sAttrib3 = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE3.getXmlKey() );
		
		if ( sAttrib3.length() > 0 ) 
		{
			setType = SetType.valueOf(sAttrib3);
		}
		else
		{
			setType = SetType.SET_RAW_DATA;
		}
	}
	
	public void setAttributes(int iSetId, 
			String sVirtualArrayIDs, 
			String sStorageIDs,
			SetType setType) {
		
		iUniqueId = iSetId;
		this.setType = setType;
		
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
				generalManager.getSingleton().logMsg(
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
				generalManager.getSingleton().logMsg(
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
