package org.caleydo.core.command.base;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;

/**
 * Abstract command class stores and handles commandId, targettId and label of
 * object.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class ACmdCreate_IdTargetLabel
	extends ACommand
{
	/**
	 * Unique Id of the object, that will be created.
	 */
	protected int iExternalID;

	/**
	 * Label of the new object, that will be created.
	 */
	protected String sLabel = "";

	/**
	 * Constructor.
	 * 
	 */
	protected ACmdCreate_IdTargetLabel(final CommandType cmdType)
	{
		super(cmdType);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.command.base.ACommand#setParameterHandler(org.caleydo
	 * .core.parser.parameter.IParameterHandler)
	 */
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		iExternalID = parameterHandler.getValueInt(CommandType.TAG_UNIQUE_ID.getXmlKey());
		sLabel = parameterHandler.getValueString(CommandType.TAG_LABEL.getXmlKey());
	}
}
