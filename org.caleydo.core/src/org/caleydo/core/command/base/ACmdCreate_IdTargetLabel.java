package org.caleydo.core.command.base;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.parameter.IParameterHandler;

/**
 * Abstract command class stores and handles commandId, tragertId and label of object.
 * 
 *  @author Michael Kalkusch
 *  @author Marc Streit
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
	protected int iUniqueId;
	
	/**
	 * Label of the new object, that will be created.
	 */
	protected String sLabel = "";

	
	/**
	 * Constructor.
	 * 
	 * @param generalManager
	 */
	protected ACmdCreate_IdTargetLabel(
			final IGeneralManager generalManager,
			final ICommandManager commandManager,
			final CommandQueueSaxType commandQueueSaxType) {
		
		// set unique ID to -1 because it is unknown at this moment
		super(-1, 
				generalManager,
				commandManager,
				commandQueueSaxType);
	}
	
	public void setParameterHandler( final IParameterHandler parameterHandler ) {
		
		this.setId(parameterHandler.getValueInt( 
					CommandQueueSaxType.TAG_CMD_ID.getXmlKey() ) );
	
		iUniqueId = parameterHandler.getValueInt( 
					CommandQueueSaxType.TAG_UNIQUE_ID.getXmlKey() );
		
		sLabel = parameterHandler.getValueString( 
					CommandQueueSaxType.TAG_LABEL.getXmlKey() );
	}
}
