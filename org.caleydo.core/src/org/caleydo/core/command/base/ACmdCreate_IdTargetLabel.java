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
	 * @param refGeneralManager
	 */
	protected ACmdCreate_IdTargetLabel(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		// set unique ID to -1 because it is unknown at this moment
		super(-1, 
				refGeneralManager,
				refCommandManager,
				refCommandQueueSaxType);
	}
	
	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		this.setId(refParameterHandler.getValueInt( 
					CommandQueueSaxType.TAG_CMD_ID.getXmlKey() ) );
	
		iUniqueId = refParameterHandler.getValueInt( 
					CommandQueueSaxType.TAG_UNIQUE_ID.getXmlKey() );
		
		sLabel = refParameterHandler.getValueString( 
					CommandQueueSaxType.TAG_LABEL.getXmlKey() );
	}
}
