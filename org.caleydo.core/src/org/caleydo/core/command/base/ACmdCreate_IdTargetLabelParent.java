package org.caleydo.core.command.base;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.parameter.IParameterHandler;

/**
 * Abstract command that reads parent container ID.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public abstract class ACmdCreate_IdTargetLabelParent 
extends ACmdCreate_IdTargetLabelAttrDetail {

	protected int iParentContainerId;

	/**
	 * @param generalManager
	 * @param parameterHandler
	 */
	protected ACmdCreate_IdTargetLabelParent(
			final IGeneralManager generalManager,
			final ICommandManager commandManager,
			final CommandQueueSaxType commandQueueSaxType)
	{
		super(generalManager,
				commandManager,
				commandQueueSaxType);
	}

	public void setParameterHandler( final IParameterHandler parameterHandler ) {
		
		super.setParameterHandler(parameterHandler);
		
		iParentContainerId = parameterHandler.getValueInt(
				CommandQueueSaxType.TAG_PARENT.getXmlKey() );
	}
}
