package cerberus.command.event;


import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreateGui;
import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.util.system.StringConversionTool;
import cerberus.xml.parser.parameter.IParameterHandler;
import cerberus.manager.IEventPublisher;

public class CmdEventRelationCreate extends ACmdCreateGui implements ICommand
{
	protected int iSenderId;

	protected int iReceiverId;
	
	public CmdEventRelationCreate(IGeneralManager refGeneralManager,
			final IParameterHandler refParameterHandler)
	{
		// set unique ID to -1 because it is unknown at this moment
		super(refGeneralManager, refParameterHandler);
		
		setAttributes();
	}

	public void doCommand() throws CerberusRuntimeException
	{
		
		((IEventPublisher)refGeneralManager.
				getManagerByBaseType(ManagerObjectType.EVENT_PUBLISHER)).
					registerSenderToReceiver(iSenderId, iReceiverId);
	}
	
	/**
	 * Extracts sender and receiver ID of the Event Relation.
	 *
	 */
	protected void setAttributes()
	{
		// Extract attrib1 which represents sender
		iSenderId = (StringConversionTool.convertStringToInt(
				sAttribute1, -1));
		
		// Extract attrib2 which represents receiver
		iReceiverId = (StringConversionTool.convertStringToInt(
				sAttribute2, -1));
	}
}
