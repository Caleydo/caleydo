package org.caleydo.core.command.data;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.StringTokenizer;


import org.caleydo.core.data.collection.IStorage;
//import org.caleydo.core.data.collection.StorageType;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabel;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ILoggerManager.LoggerType;
import org.caleydo.core.manager.data.IStorageManager;
import org.caleydo.core.manager.data.pathway.EPathwayDatabaseType;
import org.caleydo.core.util.exception.CaleydoRuntimeException;


import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;

/**
 * Command creates a new pathway storage.
 * The pathway storage is assigned to a set.
 * The pathways parsing is initiated here too.
 * 
 * @author Marc Streit
 *
 */
public class CmdDataCreatePathwayStorage 
extends ACmdCreate_IdTargetLabel {

	/**
	 * List contains KEGG pathway IDs that needs to be loaded
	 */
	protected LinkedList<Integer> llKEGGPathwayIDs;
	

	/**
	 * Constructor. 
	 * 
	 * @param refGeneralManager
	 */
	public CmdDataCreatePathwayStorage( 
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
		
		llKEGGPathwayIDs = new LinkedList<Integer> ();
	}
	

	protected void triggerPathwayParsing() {

		Iterator<Integer> iterPathwayIDs = llKEGGPathwayIDs.iterator();
		String sPathwayFilePath = "";
		int iPathwayId = 0;
		
		while (iterPathwayIDs.hasNext()) {

//			generalManager.logMsg(
//					"Load pathway with ID " +iPathwayId,
//					LoggerType.VERBOSE);
			
			iPathwayId = iterPathwayIDs.next();
			
			if (iPathwayId < 10)
			{
				sPathwayFilePath = "hsa0000" + Integer.toString(iPathwayId);
			}
			else if (iPathwayId < 100 && iPathwayId >= 10)
			{
				sPathwayFilePath = "hsa000" + Integer.toString(iPathwayId);
			}
			else if (iPathwayId < 1000 && iPathwayId >= 100)
			{
				sPathwayFilePath = "hsa00" + Integer.toString(iPathwayId);
			}
			else if (iPathwayId < 10000 && iPathwayId >= 1000)
			{
				sPathwayFilePath = "hsa0" + Integer.toString(iPathwayId);
			}
			
			sPathwayFilePath = generalManager
					.getPathwayManager().getPathwayDatabaseByType(EPathwayDatabaseType.KEGG).getXMLPath()
							+ sPathwayFilePath + ".xml";			
			
			generalManager.
				getXmlParserManager().parseXmlFileByName(sPathwayFilePath);
		}
	}
	
	
	/**
	 * Load data from file using a token pattern.
	 *
	 */
	public void doCommand() throws CaleydoRuntimeException {
		
		assert llKEGGPathwayIDs != null : "Probably this doCommand() was already executed once!";
		
		IStorageManager refStorageManager = 
			generalManager.getStorageManager();
		
		IStorage newObject = (IStorage) refStorageManager.createStorage(
				ManagerObjectType.STORAGE_FLAT );
		
		newObject.setId( iUniqueId );
		newObject.setLabel( sLabel );
		
		newObject.allocate();
		
		int[] bufferedArray = new int[llKEGGPathwayIDs.size()];	
		for (int iIndex = 0; iIndex < llKEGGPathwayIDs.size(); iIndex++) 
		{
			bufferedArray[iIndex] = llKEGGPathwayIDs.get(iIndex);
		}

		newObject.setArrayInt(bufferedArray);

//			//String strToParse = iterKEGGPathwayIDs.next();
//			
////			StringTokenizer tokenizer = 
////				new StringTokenizer( strToParse, 
////						IGeneralManager.sDelimiter_Parser_DataItems );
////				
//			int iSizeArray = tokenizer.countTokens();
////			int iTokenIndex = 0;
////			iTokenIndex = 0;
//			
//			String[] bufferedArray = new String[iSizeArray];							
//			bufferedArray
//			
//			while (tokenizer.hasMoreTokens()) 
//			{				
//				bufferedArray[iTokenIndex] = tokenizer.nextToken();	
//				iTokenIndex++;
//			} // end while (tokenizer.hasMoreTokens()) 
//					
//			/**
//			 * copy result to IStorage...
//			 */
//			newObject.setArrayString(bufferedArray);
//			
//			bufferedArray = null;
//
//		} //end: while (iterKEGGPathwayIDs.hasNext()) {
			
		refStorageManager.registerItem( newObject, 
				newObject.getId(), 
				newObject.getBaseType() );

		generalManager.logMsg( 
				"DO new Pathway STO: " + 
				newObject.toString(),
				LoggerType.VERBOSE );

		triggerPathwayParsing();
	
		llKEGGPathwayIDs = null;
		
		refCommandManager.runDoCommand(this);
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(refParameterHandler);	
		
		/**
		 * Fill pathway IDs
		 */
		StringTokenizer strToken_DataTypes = 
			new StringTokenizer( 
					refParameterHandler.getValueString( 
							CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey() ),
					IGeneralManager.sDelimiter_Parser_DataItems); 

		while ( strToken_DataTypes.hasMoreTokens() ) {
			llKEGGPathwayIDs.add(new Integer(strToken_DataTypes.nextToken()));
		}
	}
	
	public void setAttributes(String sPathwayIDs) {
		
		/**
		 * Fill pathway IDs
		 */
		StringTokenizer strToken_DataTypes = 
			new StringTokenizer(sPathwayIDs, 
					IGeneralManager.sDelimiter_Parser_DataItems); 

		while (strToken_DataTypes.hasMoreTokens()) {
			llKEGGPathwayIDs.add(new Integer(strToken_DataTypes.nextToken()));
		}		
	}

	
	/* (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException {
		refCommandManager.runUndoCommand(this);		
	}
}
