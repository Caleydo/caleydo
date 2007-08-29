/**
 * 
 */
package cerberus.command.base;

import cerberus.command.CommandQueueSaxType;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.parser.parameter.IParameterHandler;
import cerberus.command.base.ACmdCreate_IdTargetLabel;

/**
 * @see cerberus.command.ICommand
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
