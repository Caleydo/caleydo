package org.caleydo.core.command.event;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelAttrDetail;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IEventPublisher.MediatorType;
import org.caleydo.core.manager.event.mediator.IMediator;
import org.caleydo.core.manager.event.mediator.MediatorUpdateType;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.system.StringConversionTool;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.parser.parameter.IParameterHandler;

/**
 * Class creates a mediator, extracts the sender and receiver IDs
 * and calls the methods that handle the registration.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 */
public class CmdEventMediatorAddObject 
extends ACmdCreate_IdTargetLabelAttrDetail {
	
	protected ArrayList<Integer> iArSenderIDs;

	protected ArrayList<Integer> iArReceiverIDs;
	
	protected MediatorType mediatorType;
	
	public CmdEventMediatorAddObject(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {

		super(refGeneralManager,
				refCommandManager,
				refCommandQueueSaxType);
		
		super.setId( generalManager.getEventPublisher().createId( 
				ManagerObjectType.EVENT_MEDIATOR_ADD_OBJECT));
		
		iArSenderIDs = new ArrayList<Integer>();
		iArReceiverIDs = new ArrayList<Integer>();
	}

	public void doCommand() throws CaleydoRuntimeException {
			
		IEventPublisher refEventPublisher = generalManager.getEventPublisher();
		IMediator refMediator = refEventPublisher.getItemMediator(iUniqueId);
		
		if  (refMediator == null ) {
			assert false : "can not find mediator";
			return;
		}
		
		refEventPublisher.addSendersAndReceiversToMediator( refMediator,
				iArSenderIDs, 
				iArReceiverIDs, 
				mediatorType,
				MediatorUpdateType.MEDIATOR_DEFAULT);
		
		commandManager.runDoCommand(this);
	}
	
	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(refParameterHandler);	

		StringTokenizer senderToken = new StringTokenizer(
				sAttribute1,
				IGeneralManager.sDelimiter_Parser_DataItems);

		StringTokenizer receiverToken = new StringTokenizer(
				sAttribute2,
				IGeneralManager.sDelimiter_Parser_DataItems);

		while (senderToken.hasMoreTokens())
		{
			iArSenderIDs.add(StringConversionTool.convertStringToInt(
					senderToken.nextToken(), -1));
		}
		
		while (receiverToken.hasMoreTokens())
		{
			iArReceiverIDs.add(StringConversionTool.convertStringToInt(
					receiverToken.nextToken(), -1));
		}
		
		String sMediatorType = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_DETAIL.getXmlKey());
		
		if ( sMediatorType.length() < 1 ) {
			mediatorType = MediatorType.DATA_MEDIATOR;
		}
		else
		{
			mediatorType = MediatorType.valueOf( sMediatorType );
		}
	}

	public void setAttributes(int iEventMediatorId,
			ArrayList<Integer> iArSenderIDs,
			ArrayList<Integer> iArReceiverIDs, 
			MediatorType mediatorType) {
		
		this.iUniqueId = iEventMediatorId;
		this.iArSenderIDs = iArSenderIDs;
		this.iArReceiverIDs = iArReceiverIDs;
		this.mediatorType = mediatorType;
	}
	
	public void undoCommand() throws CaleydoRuntimeException {
		
		commandManager.runUndoCommand(this);		
	}
	
	public String getInfoText() {
		return super.getInfoText() + " -> " + this.iUniqueId + ": " + this.sLabel;
	}
}
