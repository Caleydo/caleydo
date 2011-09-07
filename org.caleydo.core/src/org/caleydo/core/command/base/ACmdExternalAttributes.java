package org.caleydo.core.command.base;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.parser.parameter.ParameterHandler;

/**
 * Abstract command for reading in attributes and detail tag.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class ACmdExternalAttributes
	extends ACommand {

	/**
	 * Unique Id of the object, that will be created.
	 */
	protected Integer externalID = -1;

	protected String attrib1;
	protected String attrib2;
	protected String attrib3;
	protected String attrib4;
	protected String attrib5;
	protected String attrib6;

	protected String detail = "";

	/**
	 * Constructor
	 */
	protected ACmdExternalAttributes(final CommandType cmdType) {
		super(cmdType);
	}

	@Override
	public void setParameterHandler(final ParameterHandler parameterHandler) {
		super.setParameterHandler(parameterHandler);

		externalID = parameterHandler.getValueInt(CommandType.TAG_UNIQUE_ID.getXmlKey());
		attrib1 = parameterHandler.getValueString(CommandType.TAG_ATTRIBUTE1.getXmlKey());
		attrib2 = parameterHandler.getValueString(CommandType.TAG_ATTRIBUTE2.getXmlKey());
		attrib3 = parameterHandler.getValueString(CommandType.TAG_ATTRIBUTE3.getXmlKey());
		attrib4 = parameterHandler.getValueString(CommandType.TAG_ATTRIBUTE4.getXmlKey());
		attrib5 = parameterHandler.getValueString(CommandType.TAG_ATTRIBUTE5.getXmlKey());
		attrib6 = parameterHandler.getValueString(CommandType.TAG_ATTRIBUTE6.getXmlKey());

		detail = parameterHandler.getValueString(CommandType.TAG_DETAIL.getXmlKey());
	}
}
