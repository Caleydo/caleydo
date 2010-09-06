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

	protected String detail = "";

	/**
	 * Constructor
	 */
	protected ACmdExternalAttributes(final ECommandType cmdType) {
		super(cmdType);
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler) {
		super.setParameterHandler(parameterHandler);

		externalID = parameterHandler.getValueInt(ECommandType.TAG_UNIQUE_ID.getXmlKey());
		attrib1 = parameterHandler.getValueString(ECommandType.TAG_ATTRIBUTE1.getXmlKey());
		attrib2 = parameterHandler.getValueString(ECommandType.TAG_ATTRIBUTE2.getXmlKey());
		attrib3 = parameterHandler.getValueString(ECommandType.TAG_ATTRIBUTE3.getXmlKey());
		attrib4 = parameterHandler.getValueString(ECommandType.TAG_ATTRIBUTE4.getXmlKey());
		attrib5 = parameterHandler.getValueString(ECommandType.TAG_ATTRIBUTE5.getXmlKey());
		detail = parameterHandler.getValueString(ECommandType.TAG_DETAIL.getXmlKey());
	}
}
