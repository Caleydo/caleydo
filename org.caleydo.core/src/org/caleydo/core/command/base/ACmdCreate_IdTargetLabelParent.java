/**
 * 
 */
package org.caleydo.core.command.base;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabel;

/**
 * 
 * @see org.caleydo.core.command.ICommand
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class ACmdCreate_IdTargetLabelParent 
extends ACmdCreate_IdTargetLabel {

	protected int iParentContainerId;

	/**
	 * @param refGeneralManager
	 * @param refParameterHandler
	 */
	protected ACmdCreate_IdTargetLabelParent(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType)
	{
		super(refGeneralManager,
				refCommandManager,
				refCommandQueueSaxType);
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		super.setParameterHandler(refParameterHandler);
		
		iParentContainerId = refParameterHandler.getValueInt(
				CommandQueueSaxType.TAG_PARENT.getXmlKey() );
	}
}
