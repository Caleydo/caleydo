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
	 * 
	 * @deprecated remove this
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
	 * @deprecated remove thie reference
	 */
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
	}

	
	/**
	 * Constructor.
	 * 
	 * @param refGeneralManager
	 */
	protected ACmdCreate_IdTargetLabel(IGeneralManager refGeneralManager)
	{
		// set unique ID to -1 because it is unknown at this moment
		super(-1, refGeneralManager);
	}
	

	/**
	 * Method needs to be implemented in the subclass!
	 */
	public final CommandType getCommandType()
	{
		return null;
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
