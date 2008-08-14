package org.caleydo.core.command.event;

import java.util.ArrayList;
import java.util.StringTokenizer;
import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
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
public class CmdEventCreateMediator
	extends ACmdExternalAttributes
{
	private IMediator mediator;
	
	protected ArrayList<Integer> iArSenderIDs;

	protected ArrayList<Integer> iArReceiverIDs;

	protected MediatorType mediatorType;

	/**
	 * Constructor.
	 */
	public CmdEventCreateMediator(final CommandType cmdType)
	{
		super(cmdType);

		iArSenderIDs = new ArrayList<Integer>();
		iArReceiverIDs = new ArrayList<Integer>();
	}

	@Override
	public void doCommand() throws CaleydoRuntimeException
	{
		mediator = generalManager.getEventPublisher().createMediator(iArSenderIDs,
				iArReceiverIDs, mediatorType, MediatorUpdateType.MEDIATOR_DEFAULT);

		if (iExternalID != -1)
		{
			generalManager.getIDManager().mapInternalToExternalID(mediator.getID(), iExternalID);
		}
		
		commandManager.runDoCommand(this);
	}

	@Override
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

		if (sMediatorType.length() > 0)
		{
			mediatorType = MediatorType.valueOf(sMediatorType);
		}
		else
		{
			/* assume DATA_MEDIATOR as default */
			mediatorType = MediatorType.DATA_MEDIATOR;
		}
		
		// Convert external to internal IDs
		iArSenderIDs = generalManager.getIDManager().convertExternalToInternalIDs(iArSenderIDs);
		iArReceiverIDs = generalManager.getIDManager().convertExternalToInternalIDs(iArReceiverIDs);
	}

	public void setAttributes(ArrayList<Integer> iArSenderIDs,
			ArrayList<Integer> iArReceiverIDs, MediatorType mediatorType)
	{
		this.iArSenderIDs = iArSenderIDs;
		this.iArReceiverIDs = iArReceiverIDs;
		this.mediatorType = mediatorType;
	}

	@Override
	public void undoCommand() throws CaleydoRuntimeException
	{

		commandManager.runUndoCommand(this);
	}
	
	public int getMediatorID()
	{
		return mediator.getID();
	}
}
