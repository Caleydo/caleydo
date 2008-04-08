/**
 * 
 */
package org.caleydo.core.command.base;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.parameter.IParameterHandler;

/**
 * @see org.caleydo.core.command.ICommand
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class ACmdCreate_IdTargetLabelAttrDetail 
extends ACmdCreate_IdTargetLabelAttr {

	protected String sDetail;
	
	protected String sAttribute3;
	
	/**
	 * @param refGeneralManager
	 * @param refParameterHandler
	 */
	protected ACmdCreate_IdTargetLabelAttrDetail(
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
		
		sDetail = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_DETAIL.getXmlKey() );
		
		sAttribute3 = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE3.getXmlKey() );
	}

}
