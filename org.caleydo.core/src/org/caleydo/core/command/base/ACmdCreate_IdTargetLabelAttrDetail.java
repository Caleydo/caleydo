package org.caleydo.core.command.base;

import org.caleydo.core.command.CommandType;
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
	protected ACmdCreate_IdTargetLabelAttrDetail(final CommandType cmdType)
	{
		super(cmdType);
	}

	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		super.setParameterHandler(parameterHandler);

		sAttribute1 = parameterHandler.getValueString(CommandType.TAG_ATTRIBUTE1
				.getXmlKey());

		sAttribute2 = parameterHandler.getValueString(CommandType.TAG_ATTRIBUTE2
				.getXmlKey());

		sAttribute3 = parameterHandler.getValueString(CommandType.TAG_ATTRIBUTE3
				.getXmlKey());

		sAttribute4 = parameterHandler.getValueString(CommandType.TAG_ATTRIBUTE4
				.getXmlKey());

		sDetail = parameterHandler.getValueString(CommandType.TAG_DETAIL.getXmlKey());
	}

}
