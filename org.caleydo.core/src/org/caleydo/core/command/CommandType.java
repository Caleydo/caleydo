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
package org.caleydo.core.command;

/**
 * Type of Command Queue "tag's" and "key's" Example: LOAD_DATA_FILE("cmd","type") in XML: <cmd
 * type="LOAD_DATA_FILE" /> Example 2: LOAD_ON_DEMAND("cmd","process") in XML: <cmd process="LOAD_ON_DEMAND"
 * />
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public enum CommandType {
	/**
	 * XML-value ( XML-Tag , XML-key )
	 */
	PARSE_ID_MAPPING("cmd", "type", "", "Load a lookup table"),


	CREATE_ID_CATEGORY("cmd", "type", "Create ID Category"),
	CREATE_ID_TYPE("cmd", "type", "-1", "Create ID Type"),

	TAG_CMD("cmd", "Cmd", ""),
	TAG_CMD_QUEUE("cmd", "CmdQueue", ""),
	TAG_UNIQUE_ID("cmd", "uniqueId", "-1"),
	TAG_TYPE("cmd", "type", "NO_OPERATION"),
	TAG_ATTRIBUTE1("cmd", "attrib1", ""),
	TAG_ATTRIBUTE2("cmd", "attrib2", ""),
	TAG_ATTRIBUTE3("cmd", "attrib3", ""),
	TAG_ATTRIBUTE4("cmd", "attrib4", ""),
	TAG_ATTRIBUTE5("cmd", "attrib5", ""),
	TAG_ATTRIBUTE6("cmd", "attrib6", ""),
	TAG_DETAIL("cmd", "detail", ""),
	TAG_LABEL("cmd", "label", "");

	/**
	 * * Tag: Example: <CmdQueue type="COMMAND_QUEUE_OPEN"> <br>
	 * "type" is the Key.<br>
	 * "CmdQueue" is the Tag.<br>
	 * "COMMAND_QUEUE_OPEN" is the attribute.<br>
	 */
	private String sXmlTag;

	/**
	 * * Tag: Example: <CmdQueue type="COMMAND_QUEUE_OPEN"> <br>
	 * "type" is the Key.<br>
	 * "CmdQueue" is the Tag.<br>
	 * "COMMAND_QUEUE_OPEN" is the attribute.<br>
	 */
	private String sXmlKey;

	private String sDefaultValue;

	/**
	 * Text that should describe the command. This is mainly used for the UNDO/REDO function for showing extra
	 * information to the commands.
	 */
	private String sInfoText;

	/**
	 * Constructor.
	 * 
	 * @param sXmlTag
	 * @param sXmlKey
	 * @param sDefaultValue
	 * @param sInfoText
	 */
	private CommandType(String sXmlTag, String sXmlKey, String sDefaultValue, String sInfoText) {
		this.sXmlTag = sXmlTag;
		this.sXmlKey = sXmlKey;
		this.sDefaultValue = sDefaultValue;
		this.sInfoText = sInfoText;
	}

	private CommandType(String sXmlTag, String sXmlKey, String sDefaultValue) {
		this(sXmlTag, sXmlKey, sDefaultValue, "Description is not valid! This is a TAG.");
	}

	/**
	 * * Tag: Example: <CmdQueue type="COMMAND_QUEUE_OPEN"> <br>
	 * "type" is the Key.<br>
	 * "CmdQueue" is the Tag.<br>
	 * "COMMAND_QUEUE_OPEN" is the attribute.<br>
	 * 
	 * @return key
	 */
	public String getXmlKey() {

		return this.sXmlKey;
	}

	/**
	 * Tag: Example: <CmdQueue type="COMMAND_QUEUE_OPEN"> <br>
	 * "CmdQueue" is the Tag.<br>
	 * "type" is the Key.<br>
	 * "COMMAND_QUEUE_OPEN" is the attribute.
	 * 
	 * @return tag
	 */
	public String getXmlTag() {

		return this.sXmlTag;
	}

	/**
	 * Return the default value, if it is known.
	 * 
	 * @return default value
	 */
	public String getDefault() {

		return this.sDefaultValue;
	}

	public String getInfoText() {

		return this.sInfoText;
	}
}
