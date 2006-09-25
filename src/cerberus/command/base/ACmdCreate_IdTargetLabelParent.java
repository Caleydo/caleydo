/**
 * 
 */
package cerberus.command.base;

import cerberus.command.ICommand;
import cerberus.manager.IGeneralManager;
import cerberus.xml.parser.command.CommandQueueSaxType;
import cerberus.xml.parser.parameter.IParameterHandler;
import cerberus.command.base.ACmdCreate_IdTargetLabel;
import cerberus.util.system.StringConversionTool;

/**
 * @author java
 *
 */
public abstract class ACmdCreate_IdTargetLabelParent 
	extends ACmdCreate_IdTargetLabel
		implements ICommand
{

	protected int iParentContainerId;
		
	//protected String sAttribute2;
	
	/**
	 * @param refGeneralManager
	 * @param refParameterHandler
	 */
	protected ACmdCreate_IdTargetLabelParent(final IGeneralManager refGeneralManager,
			final IParameterHandler refParameterHandler)
	{
		super(refGeneralManager, refParameterHandler);
		
		setAttributesBaseParent( refParameterHandler );
	}


	/**
	 * Note: This methode does not call setAttributesBase(IParameterHandler) internal.
	 * 
	 * @see cerberus.command.base.ACmdCreate_IdTargetLabel#setAttributesBase(IParameterHandler)
	 * 
	 * @param refParameterHandler
	 */
	protected final void setAttributesBaseParent( final IParameterHandler refParameterHandler ) {
		
		iParentContainerId = refParameterHandler.getValueInt(
				CommandQueueSaxType.TAG_PARENT.getXmlKey() );
	}

}
