/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.command.data;


import java.util.StringTokenizer;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabel;
import cerberus.data.collection.IVirtualArray;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.data.IVirtualArrayManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.IGeneralManager;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.util.system.StringConversionTool;
import cerberus.xml.parser.command.CommandQueueSaxType;
import cerberus.xml.parser.parameter.IParameterHandler;



/**
 * Command, load data from file using a token pattern and a target ISet.
 * Use MicroArrayLoader1Storage to laod dataset.
 * 
 * @author Michael Kalkusch
 *
 * @see cerberus.data.collection.ISet
 * @see cerberus.xml.parser.handler.importer.ascii.MicroArrayLoader1Storage
 */
public class CmdDataCreateVirtualArray 
extends ACmdCreate_IdTargetLabel
implements ICommand {
	
	protected int iOffset;
	
	protected int iLength;
	
	protected int iMultiRepeat = 1;
	
	protected int iMultiOffset = 0;

	protected String sTokenPattern;


	
	public CmdDataCreateVirtualArray( IGeneralManager refGeneralManager ) {
	
		super( refGeneralManager );		
				
	}

	/**
	 * Load data from file using a token pattern.
	 * 
	 * @see cerberus.xml.parser.handler.importer.ascii.MicroArrayLoader1Storage#loadData()
	 * 
	 * @see cerberus.command.ICommand#doCommand()
	 */
	public void doCommand() throws CerberusRuntimeException {
		
		IVirtualArrayManager refSelectionManager = 
			refGeneralManager.getSingelton().getVirtualArrayManager();
		
		IVirtualArray newObject = (IVirtualArray) refSelectionManager.createSelection(
				ManagerObjectType.VIRTUAL_ARRAY_MULTI_BLOCK );
		
		newObject.setId( iUniqueTargetId );
		newObject.setLabel( sLabel );
		newObject.setOffset( iOffset );
		newObject.setLength( iLength );
		newObject.setMultiOffset( iMultiOffset );
		newObject.setMultiRepeat( iMultiRepeat );
		
		refSelectionManager.registerItem( newObject, 
				iUniqueTargetId, 
				ManagerObjectType.VIRTUAL_ARRAY_MULTI_BLOCK );

		refGeneralManager.getSingelton().logMsg( 
				"DO new SEL: " + 
				newObject.toString(),
				LoggerType.VERBOSE );
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

	
	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "can not handle null object!";		
			
		super.setParameterHandler(refParameterHandler);
			
			
		/**
		 * Handle selection parameters...
		 */
		
		StringTokenizer token = new StringTokenizer(
				refParameterHandler.getValueString( 
						CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() ),
						IGeneralManager.sDelimiter_Parser_DataItems );
		
		
		int iSizeSelectionTokens = token.countTokens();
		
		iLength = StringConversionTool.convertStringToInt( 
				token.nextToken(), 
				0 );
		
		iOffset = StringConversionTool.convertStringToInt( 
				token.nextToken(), 
				0 );
		
		if ( iSizeSelectionTokens >= 4 ) 
		{
			iMultiRepeat = 
				StringConversionTool.convertStringToInt( 
						token.nextToken(), 
						1 );
			
			iMultiOffset = 
				StringConversionTool.convertStringToInt( 
						token.nextToken(), 
						0 );
		}
	}

}
