package org.caleydo.core.command.event;

import java.util.ArrayList;
import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.manager.event.mediator.EMediatorType;
import org.caleydo.core.parser.parameter.IParameterHandler;

/**
 * Class creates a mediator, extracts the sender and receiver IDs and calls the
 * methods that handle the registration.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 */
public class CmdEventMediatorAddObject
	extends ACmdExternalAttributes
{

	protected ArrayList<Integer> iArSenderIDs;

	protected ArrayList<Integer> iArReceiverIDs;

	protected EMediatorType mediatorType;

	/**
	 * Constructor.
	 */
	public CmdEventMediatorAddObject(final ECommandType cmdType)
	{
		super(cmdType);

		iArSenderIDs = new ArrayList<Integer>();
		iArReceiverIDs = new ArrayList<Integer>();

		throw new IllegalStateException("Not implemented.");
	}

	@Override
	public void doCommand()
	{
		// IEventPublisher eventPublisher = generalManager.getEventPublisher();
		// IMediator mediator = eventPublisher.getItem(iExternalID);
		//
		// if (mediator == null)
		// {
		// assert false : "can not find mediator";
		// return;
		// }
		//
		// eventPublisher.addSendersAndReceiversToMediator(mediator,
		// iArSenderIDs,
		// iArReceiverIDs, mediatorType, EMediatorUpdateType.MEDIATOR_DEFAULT);
		//
		// commandManager.runDoCommand(this);
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		// super.setParameterHandler(parameterHandler);
		//
		// StringTokenizer senderToken = new StringTokenizer(sAttribute1,
		// IGeneralManager.sDelimiter_Parser_DataItems);
		//
		// StringTokenizer receiverToken = new StringTokenizer(sAttribute2,
		// IGeneralManager.sDelimiter_Parser_DataItems);
		//
		// while (senderToken.hasMoreTokens())
		// {
		// iArSenderIDs.add(StringConversionTool.convertStringToInt(senderToken.nextToken(),
		// -1));
		// }
		//
		// while (receiverToken.hasMoreTokens())
		// {
		// iArReceiverIDs.add(StringConversionTool.convertStringToInt(receiverToken
		// .nextToken(), -1));
		// }
		//
		// String sMediatorType =
		// parameterHandler.getValueString(ECommandType.TAG_DETAIL
		// .getXmlKey());
		//
		// if (sMediatorType.length() < 1)
		// {
		// mediatorType = EMediatorType.DATA_MEDIATOR;
		// }
		// else
		// {
		// mediatorType = EMediatorType.valueOf(sMediatorType);
		// }
	}

	public void setAttributes(int iEventMediatorId, ArrayList<Integer> iArSenderIDs,
			ArrayList<Integer> iArReceiverIDs, EMediatorType mediatorType)
	{

		this.iExternalID = iEventMediatorId;
		this.iArSenderIDs = iArSenderIDs;
		this.iArReceiverIDs = iArReceiverIDs;
		this.mediatorType = mediatorType;
	}

	@Override
	public void undoCommand()
	{
		commandManager.runUndoCommand(this);
	}
}
