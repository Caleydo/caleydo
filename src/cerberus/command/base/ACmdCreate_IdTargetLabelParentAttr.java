/**
 * 
 */
package cerberus.command.base;

import cerberus.manager.IGeneralManager;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.xml.parser.command.CommandQueueSaxType;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * @author java
 *
 */
public abstract class ACmdCreate_IdTargetLabelParentAttr 
extends ACmdCreate_IdTargetLabelParent {

	protected String sAttribute1;
	
	protected String sAttribute2;
	
	
	/**
	 * @param refGeneralManager
	 * @param refParameterHandler
	 */
	protected ACmdCreate_IdTargetLabelParentAttr(IGeneralManager refGeneralManager,
			IParameterHandler refParameterHandler)
	{
		super(refGeneralManager, refParameterHandler);
		
		setAttributesAttr( refParameterHandler );
	}

	/**
	 * Note: This methode does not call setAttributesBase(IParameterHandler) internal.
	 * 
	 * @see cerberus.command.base.ACmdCreate_IdTargetLabel#setAttributesBase(IParameterHandler)
	 * 
	 * @param refParameterHandler
	 */
	protected final void setAttributesAttr( final IParameterHandler refParameterHandler ) {
		
		sAttribute1 = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() );
		
		sAttribute2 = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey() );
	}
}
