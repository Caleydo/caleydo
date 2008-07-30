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
 */
public abstract class ACmdCreate_IdTargetLabelAttrDetail
	extends ACmdCreate_IdTargetLabel
{

	protected String sAttribute1;

	protected String sAttribute2;

	protected String sAttribute3;

	protected String sAttribute4;

	protected String sDetail;

	/**
	 * Constructor
	 * 
	 * @param generalManager
	 */
	protected ACmdCreate_IdTargetLabelAttrDetail(final IGeneralManager generalManager,
			final ICommandManager commandManager, final CommandQueueSaxType commandQueueSaxType)
	{

		super(generalManager, commandManager, commandQueueSaxType);
	}

	public void setParameterHandler(final IParameterHandler parameterHandler)
	{

		super.setParameterHandler(parameterHandler);

		sAttribute1 = parameterHandler.getValueString(CommandQueueSaxType.TAG_ATTRIBUTE1
				.getXmlKey());

		sAttribute2 = parameterHandler.getValueString(CommandQueueSaxType.TAG_ATTRIBUTE2
				.getXmlKey());

		sAttribute3 = parameterHandler.getValueString(CommandQueueSaxType.TAG_ATTRIBUTE3
				.getXmlKey());

		sAttribute4 = parameterHandler.getValueString(CommandQueueSaxType.TAG_ATTRIBUTE4
				.getXmlKey());

		sDetail = parameterHandler.getValueString(CommandQueueSaxType.TAG_DETAIL.getXmlKey());
	}

}
