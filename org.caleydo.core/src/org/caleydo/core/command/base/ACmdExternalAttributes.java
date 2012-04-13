/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.command.base;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.io.parser.parameter.ParameterHandler;

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
