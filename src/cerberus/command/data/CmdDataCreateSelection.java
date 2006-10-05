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

import cerberus.command.ICommand;
import cerberus.command.CommandType;
import cerberus.command.base.AManagedCmd;
import cerberus.manager.command.factory.CommandFactory;
import cerberus.manager.data.ISelectionManager;
//import cerberus.command.window.CmdWindowPopupInfo;
import cerberus.manager.IGeneralManager;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.util.system.StringConversionTool;
import cerberus.xml.parser.command.CommandQueueSaxType;
import cerberus.xml.parser.parameter.IParameterHandler;

import cerberus.data.collection.ISelection;
//import cerberus.xml.parser.CerberusDefaultSaxHandler;

import cerberus.manager.type.ManagerObjectType;

/**
 * Command, load data from file using a token pattern and a target ISet.
 * Use MicroArrayLoader to laod dataset.
 * 
 * @author Michael Kalkusch
 *
 * @see cerberus.data.collection.ISet
 * @see cerberus.xml.parser.handler.importer.ascii.MicroArrayLoader
 */
public class CmdDataCreateSelection 
extends AManagedCmd
implements ICommand {
	
	protected int iOffset;
	
	protected int iLength;
	
	protected int iMultiRepeat = 1;
	
	protected int iMultiOffset = 0;
	
	protected String sLabel;
	
	
	protected String sTokenPattern;
	
	protected int iUniqueTargetId;
	
	
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
	 * @deprecated
	 * 
	 * @see cerberus.xml.parser.handler.importer.ascii.MicroArrayLoader
	 */
	public CmdDataCreateSelection( IGeneralManager refGeneralManager,
			final LinkedList <String> listAttributes ) {
		
		super( -1, refGeneralManager );		
		
		setAttributesLL( listAttributes );
		
	}
	
	public CmdDataCreateSelection( IGeneralManager refGeneralManager,
			final IParameterHandler refParameterHandler ) {
	
		super( -1, refGeneralManager );		
		
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
		
		ISelectionManager refSelectionManager = 
			refGeneralManager.getSingelton().getSelectionManager();
		
		ISelection newObject = (ISelection) refSelectionManager.createSelection(
				ManagerObjectType.SELECTION_MULTI_BLOCK );
		
		newObject.setId( iUniqueTargetId );
		newObject.setLabel( sLabel );
		newObject.setOffset( iOffset );
		newObject.setLength( iLength );
		newObject.setMultiOffset( iMultiOffset );
		newObject.setMultiRepeat( iMultiRepeat );
		
		refSelectionManager.registerItem( newObject, 
				iUniqueTargetId, 
				ManagerObjectType.SELECTION_MULTI_BLOCK );

		refGeneralManager.getSingelton().getLoggerManager().logMsg( 
				"DO new SEL: " + 
				newObject.toString() );
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
		return CommandType.SELECT_NEW; 
	}
	
	
	/**
	 * ISet new target ISet.
	 * 
	 * excpected format: sUniqueId sLabel [sOffset sLength [sMultiRepeat sMultiOffset]]
	 * 
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
	 * @deprecated
	 * 
	 * @param sUniqueId uniqueId of new target ISet
	 * @return TRUE on successful conversion of Strgin to interger
	 */
	protected boolean setAttributesLL( final LinkedList <String> listAttrib ) {
		
		assert listAttrib != null: "can not handle null object!";		
				
		Iterator <String> iter = listAttrib.iterator();		
		final int iSizeList= listAttrib.size();
		
		assert iSizeList > 1 : "can not handle empty argument list!";					
		
		try {	
			
			this.setId( StringConversionTool.convertStringToInt( iter.next(), -1 ) );
			
			iUniqueTargetId = StringConversionTool.convertStringToInt( iter.next(), -1 );
			
			sLabel = StringConversionTool.convertStringToString( iter.next(), 
					Integer.toString(iUniqueTargetId) );			
			
			iter.next();
			iter.next();
			iter.next();
			
			StringTokenizer token = new StringTokenizer( iter.next(),
					CommandFactory.sDelimiter_CreateSelection_DataItems );
			
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
			
			return true;
		}
		catch ( NumberFormatException nfe ) 
		{
			refGeneralManager.getSingelton().getLoggerManager().logMsg(
					"CmdDataCreateSelection::doCommand() error on attributes!");			
			return false;
		}
		
	}
	
	protected boolean setAttributes( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "can not handle null object!";		
				
				
			this.setId( 
					refParameterHandler.getValueInt( 
							CommandQueueSaxType.TAG_CMD_ID.getXmlKey() ) );
			
			iUniqueTargetId = 
				refParameterHandler.getValueInt( 
						CommandQueueSaxType.TAG_TARGET_ID.getXmlKey() );
			
			sLabel = refParameterHandler.getValueString( 
					CommandQueueSaxType.TAG_LABEL.getXmlKey() );
			
				
			/**
			 * Handle selection parameters...
			 */
			
			StringTokenizer token = new StringTokenizer(
					refParameterHandler.getValueString( 
							CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() ),
					CommandFactory.sDelimiter_CreateSelection_DataItems );
			
			
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
			
			return true;
	}

}
