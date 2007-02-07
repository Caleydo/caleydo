/**
 * 
 */
package cerberus.command.base;

import cerberus.command.ICommand;
import cerberus.manager.IGeneralManager;
import cerberus.xml.parser.command.CommandQueueSaxType;
import cerberus.xml.parser.parameter.IParameterHandler;
import cerberus.command.base.ACmdCreate_IdTargetLabel;

/**
 * @author Michael Kalkusch
 *
 */
public abstract class ACmdCreate_IdTargetLabelAttr 
extends ACmdCreate_IdTargetLabel
implements ICommand
{

	protected String sAttribute1;
	
	protected String sAttribute2;
	
	/**
	 * @param refGeneralManager
	 * @param refParameterHandler
	 * 
	 * @deprecated
	 */
	protected ACmdCreate_IdTargetLabelAttr(final IGeneralManager refGeneralManager,
			IParameterHandler refParameterHandler)
	{
		super(refGeneralManager, refParameterHandler);
		
	}
	
	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 */
	protected ACmdCreate_IdTargetLabelAttr(final IGeneralManager refGeneralManager)
	{
		super(refGeneralManager);
		
	}
	
	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		super.setParameterHandler(refParameterHandler);
		
		sAttribute1 = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() );
		
		sAttribute2 = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey() );
	}
	
}
