package cerberus.command.event;

import java.util.LinkedList;
import java.util.StringTokenizer;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate;
import cerberus.manager.IGeneralManager;
import cerberus.manager.command.factory.CommandFactory;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.util.system.StringConversionTool;
import cerberus.manager.IEventPublisher;

public class CmdEventRelationCreate extends ACmdCreate implements ICommand
{
	protected int iSenderId;

	protected int iReceiverId;
	
	public CmdEventRelationCreate(IGeneralManager refGeneralManager,
			final LinkedList<String> listAttributes)
	{
		// set unique ID to -1 because it is unknown at this moment
		super(refGeneralManager, listAttributes);
	}

	public void doCommand() throws CerberusRuntimeException
	{
		extractAttributes();
		
		((IEventPublisher)refGeneralManager.
				getManagerByBaseType(ManagerObjectType.EVENT_PUBLISHER)).
					registerSenderToReceiver(iSenderId, iReceiverId);
	}
	
	/**
	 * Extracts sender and receiver ID of the Event Relation.
	 *
	 */
	protected void extractAttributes()
	{
		// Extract attrib1 which represents sender
		iSenderId = (StringConversionTool.convertStringToInt(
				refVecAttributes.get(1), -1));
		
		// Extract attrib2 which represents receiver
		iReceiverId = (StringConversionTool.convertStringToInt(
				refVecAttributes.get(2), -1));
	}
}
