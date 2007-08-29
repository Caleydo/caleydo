package cerberus.command.base;



import cerberus.command.CommandQueueSaxType;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.parser.parameter.IParameterHandler;

/**
 * Abstract command class stores and handles commandId, tragertId and label of object.
 * 
 *  @see cerberus.command.ICommand
 *  @author Michael Kalkusch
 *
 */
public abstract class ACmdCreate_IdTargetLabel 
extends ACommand {
	/**
	 * Command Id to identify this command.
	 * 
	 * identify this command by its id
	 */
	protected int iCommandId;
	
	/**
	 * Unique Id of the object, that will be created.
	 */
	protected int iUniqueTargetId;
	
	/**
	 * Label of the new object, that will be created.
	 */
	protected String sLabel = "";

	
	/**
	 * Constructor.
	 * 
	 * @param refGeneralManager
	 */
	protected ACmdCreate_IdTargetLabel(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType)
	{
		// set unique ID to -1 because it is unknown at this moment
		super(-1, 
				refGeneralManager,
				refCommandManager,
				refCommandQueueSaxType);
	}
	
	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		/*
		 * do not call empty method super.setParameterHandler()
		 */
		
		this.setId( 
				refParameterHandler.getValueInt( 
						CommandQueueSaxType.TAG_CMD_ID.getXmlKey() ) );
	
		iUniqueTargetId = 
			refParameterHandler.getValueInt( 
					CommandQueueSaxType.TAG_TARGET_ID.getXmlKey() );
		
		sLabel = refParameterHandler.getValueString( 
					CommandQueueSaxType.TAG_LABEL.getXmlKey() );
	}
	
}
