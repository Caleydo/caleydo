package cerberus.command.base;



import cerberus.command.CommandType;
import cerberus.command.ICommand;
import cerberus.manager.IGeneralManager;
import cerberus.xml.parser.command.CommandQueueSaxType;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * Abstract command class stores and handles commandId, tragertId and label of object.
 * @author Michael Kalkusch
 *
 */
public abstract class ACmdCreate_IdTargetLabel 
extends AManagedCmd
implements ICommand
{
	/**
	 * Command Id to identify this command.
	 */
	protected int iCommandId;
	
	/**
	 * Unique Id of the IStorage, that will be created.
	 */
	protected int iUniqueTargetId;
	
	/**
	 * Label of the new IStorage, that will be created.
	 */
	protected String sLabel = "";
	
	protected IParameterHandler refParameterHandler;


	/**
	 * Constructor.
	 * 
	 * @param refGeneralManager
	 * @param refParameterHandler
	 */
	protected ACmdCreate_IdTargetLabel(IGeneralManager refGeneralManager,
			final IParameterHandler refParameterHandler)
	{
		// set unique ID to -1 because it is unknown at this moment
		super(-1, refGeneralManager);

		this.refParameterHandler = refParameterHandler;

		this.setId( 
				refParameterHandler.getValueInt( 
						CommandQueueSaxType.TAG_CMD_ID.getXmlKey() ) );
	
		iUniqueTargetId = 
			refParameterHandler.getValueInt( 
					CommandQueueSaxType.TAG_TARGET_ID.getXmlKey() );
		
		sLabel = refParameterHandler.getValueString( 
					CommandQueueSaxType.TAG_LABEL.getXmlKey() );
	}


	/**
	 * Method needs to be implemented in the subclass!
	 */
	public final CommandType getCommandType()
	{
		return null;
	}
	
}
