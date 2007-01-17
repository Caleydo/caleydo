package cerberus.command.event;

import java.util.ArrayList;
import java.util.StringTokenizer;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabelAttrDetail;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IEventPublisher.MediatorType;
import cerberus.manager.command.factory.CommandFactory;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.util.system.StringConversionTool;
import cerberus.xml.parser.command.CommandQueueSaxType;
import cerberus.xml.parser.parameter.IParameterHandler;
import cerberus.manager.IEventPublisher;

/**
 * Class creates a mediator, extracts the sender and receiver IDs
 * and calls the methods that handle the registration.
 * 
 * @author Marc Streit
 */
public class CmdEventCreateMediator 
extends ACmdCreate_IdTargetLabelAttrDetail 
implements ICommand {
	
	protected ArrayList<Integer> arSenderIDs;

	protected ArrayList<Integer> arReceiverIDs;
	
	protected MediatorType mediatorType;
	
	public CmdEventCreateMediator(IGeneralManager refGeneralManager,
			final IParameterHandler refParameterHandler) {
		
		// set unique ID to -1 because it is unknown at this moment
		super(refGeneralManager, refParameterHandler);
		
		arSenderIDs = new ArrayList<Integer>();
		arReceiverIDs = new ArrayList<Integer>();
		
		setAttributes();
		
	}

	public void doCommand() throws CerberusRuntimeException {
			
		((IEventPublisher)refGeneralManager.
				getManagerByBaseType(ManagerObjectType.EVENT_PUBLISHER)).
					createMediator(iUniqueTargetId,
							arSenderIDs, arReceiverIDs, mediatorType);
	}
	
	/**
	 * Extracts sender and receiver ID of the Event Relation.
	 *
	 */
	protected void setAttributes() {
		
		StringTokenizer senderToken = new StringTokenizer(
				sAttribute1,
				CommandFactory.sDelimiter_Parser_DataItems);

		StringTokenizer receiverToken = new StringTokenizer(
				sAttribute2,
				CommandFactory.sDelimiter_Parser_DataItems);

		while (senderToken.hasMoreTokens())
		{
			arSenderIDs.add(StringConversionTool.convertStringToInt(
					senderToken.nextToken(), -1));
		}
		
		while (receiverToken.hasMoreTokens())
		{
			arReceiverIDs.add(StringConversionTool.convertStringToInt(
					receiverToken.nextToken(), -1));
		}
		
		String sMediatorType = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_DETAIL.getXmlKey());
		
		mediatorType = MediatorType.valueOf( sMediatorType );
	}

	public void undoCommand() throws CerberusRuntimeException {
		
		// TODO Auto-generated method stub
		
	}
}
