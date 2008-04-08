/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.geneview.core.command.data;


import java.util.StringTokenizer;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.base.ACmdCreate_IdTargetLabel;
import org.geneview.core.data.collection.IVirtualArray;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.manager.data.IVirtualArrayManager;
import org.geneview.core.manager.type.ManagerObjectType;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.parser.parameter.IParameterHandler;
import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.util.system.StringConversionTool;



/**
 * Command, load data from file using a token pattern and a target ISet.
 * Use MicroArrayLoader1Storage to laod dataset.
 * 
 * @author Michael Kalkusch
 *
 * @see org.geneview.core.data.collection.ISet
 * @see org.geneview.core.parser.ascii.microarray.MicroArrayLoader1Storage
 */
public class CmdDataCreateVirtualArray 
extends ACmdCreate_IdTargetLabel {
	
	protected int iOffset;
	
	protected int iLength;
	
	protected int iMultiRepeat = 1;
	
	protected int iMultiOffset = 0;

	protected String sTokenPattern;


	/**
	 * Constructor.
	 * 
	 */
	public CmdDataCreateVirtualArray(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
	
		super(refGeneralManager,
				refCommandManager,
				refCommandQueueSaxType);			
	}

	/**
	 * Load data from file using a token pattern.
	 * 
	 * @see org.geneview.core.parser.ascii.microarray.MicroArrayLoader1Storage#loadData()
	 * 
	 * @see org.geneview.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws GeneViewRuntimeException {
		
		IVirtualArrayManager refVirtualArrayManager = 
			generalManager.getSingelton().getVirtualArrayManager();
		
		IVirtualArray newObject = (IVirtualArray) refVirtualArrayManager.createVirtualArray(
				ManagerObjectType.VIRTUAL_ARRAY_MULTI_BLOCK );
		
		newObject.setId( iUniqueId );
		newObject.setLabel( sLabel );
		newObject.setOffset( iOffset );
		newObject.setLength( iLength );
		newObject.setMultiOffset( iMultiOffset );
		newObject.setMultiRepeat( iMultiRepeat );
		
		refVirtualArrayManager.registerItem( newObject, 
				iUniqueId, 
				ManagerObjectType.VIRTUAL_ARRAY_MULTI_BLOCK );

		generalManager.getSingelton().logMsg( 
				"DO new SEL: " + 
				newObject.toString(),
				LoggerType.VERBOSE );
		
		refCommandManager.runDoCommand(this);
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws GeneViewRuntimeException {
		
		generalManager.getSingelton().getVirtualArrayManager().unregisterItem( 
				iUniqueId,
				ManagerObjectType.VIRTUAL_ARRAY_MULTI_BLOCK );
		
		generalManager.getSingelton().logMsg( 
				"UNDO new SEL: " + 
				iUniqueId,
				LoggerType.VERBOSE );
		
		refCommandManager.runUndoCommand(this);
	}

	
	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "can not handle null object!";		
			
		super.setParameterHandler(refParameterHandler);
			
			
		/**
		 * Handle VirtualArray parameters...
		 */
		
		StringTokenizer token = new StringTokenizer(
				refParameterHandler.getValueString( 
						CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() ),
						IGeneralManager.sDelimiter_Parser_DataItems );
		
		
		int iSizeVirtualArrayTokens = token.countTokens();
		
		iLength = StringConversionTool.convertStringToInt( 
				token.nextToken(), 
				0 );
		
		iOffset = StringConversionTool.convertStringToInt( 
				token.nextToken(), 
				0 );
		
		if ( iSizeVirtualArrayTokens >= 4 ) 
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

	public void setAttributes(int iVirtualArrayId,
			int iLength, 
			int iOffset,
			int iMultiRepeat,
			int iMultiOffset) {
		
		iUniqueId = iVirtualArrayId;
		this.iLength = iLength;
		this.iOffset = iOffset;
		this.iMultiRepeat = iMultiRepeat;
		this.iMultiOffset = iMultiOffset;
	}
}
