package cerberus.command.data;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.StringTokenizer;


import cerberus.data.collection.IStorage;
//import cerberus.data.collection.StorageType;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabel;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.data.IStorageManager;
import cerberus.util.exception.CerberusRuntimeException;

import cerberus.xml.parser.parameter.IParameterHandler;

import cerberus.manager.type.ManagerObjectType;

/**
 * Command creates a new pathway storage.
 * The pathway storage is assigned to a set.
 * The pathways parsing is initiated here too.
 * 
 * @author Marc Streit
 *
 */
public class CmdDataCreatePathwayStorage 
extends ACmdCreate_IdTargetLabel
implements ICommand {

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
			final ICommandManager refCommandManager) {
		
		super(refGeneralManager, refCommandManager);
		
		llKEGGPathwayIDs = new LinkedList<Integer> ();
	}
	

	protected void triggerPathwayParsing() {

		Iterator<Integer> iterPathwayIDs = llKEGGPathwayIDs.iterator();
		String sPathwayFilePath = "";
		int iPathwayId = 0;
		
		while (iterPathwayIDs.hasNext()) {

//			refGeneralManager.getSingelton().logMsg(
//					"Load pathway with ID " +iPathwayId,
//					LoggerType.VERBOSE);
			
			iPathwayId = iterPathwayIDs.next();
			
			if (iPathwayId < 10)
			{
				sPathwayFilePath = "map0000" + Integer.toString(iPathwayId);
			}
			else if (iPathwayId < 100 && iPathwayId >= 10)
			{
				sPathwayFilePath = "map000" + Integer.toString(iPathwayId);
			}
			else if (iPathwayId < 1000 && iPathwayId >= 100)
			{
				sPathwayFilePath = "map00" + Integer.toString(iPathwayId);
			}
			else if (iPathwayId < 10000 && iPathwayId >= 1000)
			{
				sPathwayFilePath = "map0" + Integer.toString(iPathwayId);
			}
			
			sPathwayFilePath = "data/XML/pathways/" + sPathwayFilePath +".xml";			
			
			refGeneralManager.getSingelton().
				getXmlParserManager().parseXmlFileByName(sPathwayFilePath);
		}
	}
	
	
	/**
	 * Load data from file using a token pattern.
	 *
	 */
	public void doCommand() throws CerberusRuntimeException {
		
		assert llKEGGPathwayIDs != null : "Probably this doCommand() was already executed once!";
		
		IStorageManager refStorageManager = 
			refGeneralManager.getSingelton().getStorageManager();
		
		IStorage newObject = (IStorage) refStorageManager.createStorage(
				ManagerObjectType.STORAGE_FLAT );
		
		newObject.setId( iUniqueTargetId );
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

		refGeneralManager.getSingelton().logMsg( 
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
	 * @see cerberus.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CerberusRuntimeException {
		refCommandManager.runUndoCommand(this);		
	}
}
