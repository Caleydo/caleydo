package org.caleydo.core.command.base;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.parser.parameter.IParameterHandler;

/**
 * Abstract command for reading in attributes and detail tag.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class ACmdExternalAttributes
	extends ACommand
{

	/**
	 * Unique Id of the object, that will be created.
	 */
	protected int iExternalID = -1;

	/**
	 * Label of the new object, that will be created.
	 */
	protected String sLabel = "";

	protected String sAttribute1;

	protected String sAttribute2;

	protected String sAttribute3;

	protected String sAttribute4;

	protected String sDetail = "";

	protected int iParentContainerId = -1;

	/**
	 * Constructor
	 * 
	 */
	protected ACmdExternalAttributes(final ECommandType cmdType)
	{
		super(cmdType);
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler)
	{
		super.setParameterHandler(parameterHandler);

		iExternalID = parameterHandler.getValueInt(ECommandType.TAG_UNIQUE_ID.getXmlKey());

		sLabel = parameterHandler.getValueString(ECommandType.TAG_LABEL.getXmlKey());

		sAttribute1 = parameterHandler.getValueString(ECommandType.TAG_ATTRIBUTE1.getXmlKey());

		sAttribute2 = parameterHandler.getValueString(ECommandType.TAG_ATTRIBUTE2.getXmlKey());

		sAttribute3 = parameterHandler.getValueString(ECommandType.TAG_ATTRIBUTE3.getXmlKey());

		sAttribute4 = parameterHandler.getValueString(ECommandType.TAG_ATTRIBUTE4.getXmlKey());

		sDetail = parameterHandler.getValueString(ECommandType.TAG_DETAIL.getXmlKey());

		iParentContainerId = parameterHandler.getValueInt(ECommandType.TAG_PARENT.getXmlKey());
	}
}
