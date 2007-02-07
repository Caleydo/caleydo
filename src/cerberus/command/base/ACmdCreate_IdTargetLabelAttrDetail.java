/**
 * 
 */
package cerberus.command.base;

import cerberus.command.ICommand;
import cerberus.manager.IGeneralManager;
import cerberus.xml.parser.command.CommandQueueSaxType;
import cerberus.xml.parser.parameter.IParameterHandler;

/**
 * @author Michael Kalkusch
 *
 */
public abstract class ACmdCreate_IdTargetLabelAttrDetail 
extends ACmdCreate_IdTargetLabelAttr
implements ICommand {

	protected String sDetail;
	
	protected String sAttribute3;
	
	/**
	 * @param refGeneralManager
	 * @param refParameterHandler
	 */
	protected ACmdCreate_IdTargetLabelAttrDetail(IGeneralManager refGeneralManager )
	{
		super(refGeneralManager);
	}
	
	/**
	 * 
	 * @deprecated
	 * 
	 * @param refGeneralManager
	 * @param refParameterHandler
	 */
	protected ACmdCreate_IdTargetLabelAttrDetail(final IGeneralManager refGeneralManager,
			IParameterHandler refParameterHandler)
	{
		super(refGeneralManager, refParameterHandler);
		
	}
	
	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		super.setParameterHandler(refParameterHandler);
		
		sDetail = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_DETAIL.getXmlKey() );
		
		sAttribute3 = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE3.getXmlKey() );
	}

}
