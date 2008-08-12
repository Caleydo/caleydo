package org.caleydo.core.command.event;

import java.util.ArrayList;
import java.util.StringTokenizer;
import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IEventPublisher.MediatorType;
import org.caleydo.core.manager.event.mediator.IMediator;
import org.caleydo.core.manager.event.mediator.MediatorUpdateType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.system.StringConversionTool;

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

	protected MediatorType mediatorType;

	/**
	 * Constructor.
	 */
	public CmdEventMediatorAddObject(final CommandType cmdType)
	{
		super(cmdType);

		iArSenderIDs = new ArrayList<Integer>();
		iArReceiverIDs = new ArrayList<Integer>();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#doCommand()
	 */
	public void doCommand() throws CaleydoRuntimeException
	{
		IEventPublisher eventPublisher = generalManager.getEventPublisher();
		IMediator mediator = eventPublisher.getItem(iExternalID);

		if (mediator == null)
		{
			assert false : "can not find mediator";
			return;
		}

		eventPublisher.addSendersAndReceiversToMediator(mediator, iArSenderIDs,
				iArReceiverIDs, mediatorType, MediatorUpdateType.MEDIATOR_DEFAULT);

		commandManager.runDoCommand(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.base.ACmdCreate_IdTargetLabelAttrDetail#setParameterHandler(org.caleydo.core.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		super.setParameterHandler(parameterHandler);

		StringTokenizer senderToken = new StringTokenizer(sAttribute1,
				IGeneralManager.sDelimiter_Parser_DataItems);

		StringTokenizer receiverToken = new StringTokenizer(sAttribute2,
				IGeneralManager.sDelimiter_Parser_DataItems);

		while (senderToken.hasMoreTokens())
		{
			iArSenderIDs.add(StringConversionTool.convertStringToInt(senderToken.nextToken(),
					-1));
		}

		while (receiverToken.hasMoreTokens())
		{
			iArReceiverIDs.add(StringConversionTool.convertStringToInt(receiverToken
					.nextToken(), -1));
		}

		String sMediatorType = parameterHandler.getValueString(CommandType.TAG_DETAIL
				.getXmlKey());

		if (sMediatorType.length() < 1)
		{
			mediatorType = MediatorType.DATA_MEDIATOR;
		}
		else
		{
			mediatorType = MediatorType.valueOf(sMediatorType);
		}
	}

	public void setAttributes(int iEventMediatorId, ArrayList<Integer> iArSenderIDs,
			ArrayList<Integer> iArReceiverIDs, MediatorType mediatorType)
	{

		this.iExternalID = iEventMediatorId;
		this.iArSenderIDs = iArSenderIDs;
		this.iArReceiverIDs = iArReceiverIDs;
		this.mediatorType = mediatorType;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.command.ICommand#undoCommand()
	 */
	public void undoCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
	}
}
