package org.caleydo.core.command.base;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.parser.parameter.IParameterHandler;

/**
 * Abstract command that reads parent container ID.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class ACmdCreate_IdTargetLabelParent
	extends ACmdCreate_IdTargetLabelAttrDetail
{

	protected int iParentContainerId;

	/**
	 * Constructor.
	 */
	protected ACmdCreate_IdTargetLabelParent(final CommandType cmdType)
	{
		super(cmdType);
	}

	public void setParameterHandler(final IParameterHandler parameterHandler)
	{

		super.setParameterHandler(parameterHandler);

		iParentContainerId = parameterHandler.getValueInt(CommandType.TAG_PARENT
				.getXmlKey());
	}
}
