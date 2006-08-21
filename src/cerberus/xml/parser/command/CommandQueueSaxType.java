/**
 * 
 */
package cerberus.xml.parser.command;

/**
 * Type of Command Queue "tag's" and "key's"
 * 
 * Example: LOAD_DATA_FILE("cmd","type")
 * 
 * in XML:   <cmd type="LOAD_DATA_FILE" />
 * 
 * Example 2: LOAD_ON_DEMAND("cmd","process")
 * 
 * in XML: <cmd process="LOAD_ON_DEMAND" />
 * 
 * @author kalkusch
 *
 */
public enum CommandQueueSaxType
{
	/*
	 * -------  COMMAND  --------
	 */ 
	
	/**
	 * XML-value  ( XML-Tag , XML-key ) 
	 */
	LOAD_DATA_FILE("cmd","type"),
	//OPEN_VIEW("cmd","type"),
	
	CREATE_SELECTION("cmd","type"),
	CREATE_SET("cmd","type"),
	CREATE_STORAGE("cmd","type"),
	
	CREATE_VIEW_HEATMAP("cmd", "type"),
	CREATE_VIEW_PATHWAY("cmd", "type"),
	CREATE_VIEW_GEARS("cmd", "type"),
	CREATE_VIEW_DATA_EXPLORER("cmd", "type"),
	CREATE_VIEW_PROGRESSBAR("cmd", "type"),
	CREATE_VIEW_SLIDER("cmd", "type"),
	
	CREATE_SWT_WINDOW("cmd", "type"),
	CREATE_SWT_CONTAINER("cmd", "type"),
	
	RUN_CMD_NOW("cmd", "process"),	
	LOAD_ON_DEMAND("cmd", "process"),
	MEMENTO("cmd", "process"),	
	
	TAG_CMD("cmd","Cmd"),
	TAG_CMD_QUEUE("cmd","CmdQueue"),
	TAG_CMD_ID("cmd","cmdId"),
	TAG_TARGET_ID("cmd","targetId"),
	TAG_MEMENTO_ID("cmd","mementoId"),
	TAG_TYPE("cmd","type"),
	TAG_ATTRIBUTE1("cmd","attrib1"),	
	TAG_ATTRIBUTE2("cmd","attrib2"),	
	TAG_DETAIL("cmd","detail"),
	TAG_PROCESS("cmd","process"),
	TAG_LABEL("cmd","label"),
	
	/*
	 * -------  COMMAND QUEUE  --------
	 */ 
	COMMAND_QUEUE_OPEN("cmdqueue","type"),
	COMMAND_QUEUE_RUN("cmdqueue","type"),
	
	CMD_ID("cmdqueue","cmdId"),
	CMDQUEUE_ID("cmdqueue","cmdQueueId"),
	
	RUN_QUEUE_ON_DEMAND("cmdqueue","process"),
	RUN_QUEUE("cmdqueue","process"),

	
	CMD_THREAD_POOL_ID("cmdqueue","queue_thread"),
	CMD_THREAD_POOL_WAIT_ID("cmdqueue","queue_thread_wait");
	
	
	/**
	 *  * Tag: Example: <CmdQueue type="COMMAND_QUEUE_OPEN"> <br>
	 * "type" is the Key.<br>
	 * "CmdQueue" is the Tag.<br>
	 * "COMMAND_QUEUE_OPEN" is the attribute.<br>
	 */
	private String sXmlTag;
	
	/**
	 *  * Tag: Example: <CmdQueue type="COMMAND_QUEUE_OPEN"> <br>
	 * "type" is the Key.<br>
	 * "CmdQueue" is the Tag.<br>
	 * "COMMAND_QUEUE_OPEN" is the attribute.<br>
	 */
	private String sXmlKey;
	
	private CommandQueueSaxType( String sXmlTag, 
			String sXmlKey ) 
	{
		this.sXmlTag = sXmlTag;		
		this.sXmlKey = sXmlKey;
	}
	
//	public static final CommandQueueSaxType parse( final String sData) {
//		
//	}
	
	/**
	 *  * Tag: Example: <CmdQueue type="COMMAND_QUEUE_OPEN"> <br>
	 * "type" is the Key.<br>
	 * "CmdQueue" is the Tag.<br>
	 * "COMMAND_QUEUE_OPEN" is the attribute.<br>
	 * 
	 * @return key
	 */
	public String getXmlKey() 
	{
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
	public String getXmlTag() 
	{
		return this.sXmlTag;
	}
}
