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
public enum ECommandType {
	/**
	 * XML-value ( XML-Tag , XML-key )
	 */
	LOAD_DATA_FILE("cmd", "type", "", "No description available!"),
	LOAD_LOOKUP_TABLE_FILE("cmd", "type", "", "Load a lookup table"),

	SET_DATA_REPRESENTATION("cmd", "type", "-1", "Set data representation (Raw, Log2, Log10)"),
	DATA_FILTER_MIN_MAX("cmd", "type", "-1", "Evaluate min and max of an entity"),

	CREATE_GL_VIEW("cmd", "type", "-1", "Create GL View"),

	CREATE_DATA_DOMAIN("cmd", "type", "-1", "Create Data Domain"),
	CREATE_SET_DATA("cmd", "type", "-1", "Create SET"),
	CREATE_STORAGE("cmd", "type", "-1", "Create Storage"),
	CREATE_VIRTUAL_ARRAY("cmd", "type", "-1", "Create VirtualArray"),

	CREATE_ID_CATEGORY("cmd", "type", "Create ID Category"),
	CREATE_ID_TYPE("cmd", "type", "-1", "Create ID Type"),

	CREATE_SWT_WINDOW("cmd", "type", "-1", "Create SWT window"),
	CREATE_SWT_CONTAINER("cmd", "type", "-1", "Create SWTContainer"),
	CREATE_VIEW_RCP_GLCANVAS("cmd", "type", "-1", "Create RCP GL Canvas"),

	SYSTEM_SHUT_DOWN("cmd", "type", "-1", "Caleydo system shut down"),

	TAG_CMD("cmd", "Cmd", ""),
	TAG_CMD_QUEUE("cmd", "CmdQueue", ""),
	TAG_UNIQUE_ID("cmd", "uniqueId", "-1"),
	TAG_TYPE("cmd", "type", "NO_OPERATION"),
	TAG_ATTRIBUTE1("cmd", "attrib1", ""),
	TAG_ATTRIBUTE2("cmd", "attrib2", ""),
	TAG_ATTRIBUTE3("cmd", "attrib3", ""),
	TAG_ATTRIBUTE4("cmd", "attrib4", ""),
	TAG_ATTRIBUTE5("cmd", "attrib5", ""),
	TAG_DETAIL("cmd", "detail", ""),
	TAG_LABEL("cmd", "label", ""),
	// TAG_PARENT("cmd", "label", ""),
	TAG_POS_GL_ORIGIN("cmd", "gl_origin", "0 0 0"),

	/** Values indicate axis: (X,Y,Z) and rotation-angle (ALPHA) in (radiant). */
	TAG_POS_GL_ROTATION("cmd", "gl_rotation", "0 0 1 0.0");

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
	private ECommandType(String sXmlTag, String sXmlKey, String sDefaultValue, String sInfoText) {
		this.sXmlTag = sXmlTag;
		this.sXmlKey = sXmlKey;
		this.sDefaultValue = sDefaultValue;
		this.sInfoText = sInfoText;
	}

	private ECommandType(String sXmlTag, String sXmlKey, String sDefaultValue) {
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
