package org.caleydo.core.command.base;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabel;

/**
 * Abstract command for reading in attributes and detail tag.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public abstract class ACmdCreate_IdTargetLabelAttrDetail 
extends ACmdCreate_IdTargetLabel {

	protected String sAttribute1;
	protected String sAttribute2;
	protected String sAttribute3;
	protected String sAttribute4;
	
	protected String sDetail;
	
	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
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
		
		sAttribute1 = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey());
		
		sAttribute2 = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey());

		sAttribute3 = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE3.getXmlKey());	

		sAttribute4 = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE4.getXmlKey());
		
		sDetail = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_DETAIL.getXmlKey());
	}
	
}
