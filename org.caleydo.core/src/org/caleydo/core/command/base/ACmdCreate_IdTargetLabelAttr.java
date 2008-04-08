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
 * @see org.caleydo.core.command.ICommand
 * @author Michael Kalkusch
 *
 */
public abstract class ACmdCreate_IdTargetLabelAttr 
extends ACmdCreate_IdTargetLabel {

	protected String sAttribute1;
	
	protected String sAttribute2;
	
	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 */
	protected ACmdCreate_IdTargetLabelAttr(
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
		
		sAttribute1 = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() );
		
		sAttribute2 = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey() );
	}
	
}
