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
public abstract class ACmdCreate_IdTargetLabelParent 
extends ACmdCreate_IdTargetLabel
implements ICommand
{

	protected int iParentContainerId;
	
	
	/**
	 * @param refGeneralManager
	 * @param refParameterHandler
	 */
	protected ACmdCreate_IdTargetLabelParent(final IGeneralManager refGeneralManager,
			final IParameterHandler refParameterHandler )
	{
		super(refGeneralManager, refParameterHandler);
	}
	
	/**
	 * @param refGeneralManager
	 * @param refParameterHandler
	 */
	protected ACmdCreate_IdTargetLabelParent(final IGeneralManager refGeneralManager )
	{
		super(refGeneralManager);
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		super.setParameterHandler(refParameterHandler);
		
		iParentContainerId = refParameterHandler.getValueInt(
				CommandQueueSaxType.TAG_PARENT.getXmlKey() );
	}
}
