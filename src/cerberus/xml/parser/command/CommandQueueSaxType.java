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
	LOAD_DATA_FILE("cmd","type",""),
	//OPEN_VIEW("cmd","type"),
	
	NO_OPERATION("cmd","type","NO_OPERATION"),
	
	CREATE_SELECTION("cmd","type","-1"),
	CREATE_SET("cmd","type","-1"),
	CREATE_SET_PLANAR("cmd","type","-1"),
	//CREATE_SET_MULTIDIM("cmd","type","-1"),
	CREATE_STORAGE("cmd","type","-1"),
	
	CREATE_VIEW_HEATMAP("cmd", "type","-1"),
	CREATE_VIEW_PATHWAY("cmd", "type","-1"),
	CREATE_VIEW_GEARS("cmd", "type","-1"),
	CREATE_VIEW_DATA_EXPLORER("cmd", "type","-1"),
	CREATE_VIEW_PROGRESSBAR("cmd", "type","-1"),
	CREATE_VIEW_STORAGE_SLIDER("cmd", "type","-1"),
	CREATE_VIEW_SELECTION_SLIDER("cmd", "type","-1"),	
	CREATE_VIEW_MIXER("cmd", "type","-1"),
	CREATE_VIEW_TEST_TRIANGLE("cmd", "type","-1"),
	CREATE_VIEW_SWT_GLCANVAS("cmd", "type","-1"),
	
	CREATE_GL_TRIANGLE_TEST("cmd","type","-1"),
	CREATE_GL_HEATMAP("cmd","type","-1"),
	CREATE_GL_HISTOGRAM2D("cmd","type","-1"),
	CREATE_GL_SCATTERPLOT2D("cmd","type","-1"),
	
	CREATE_SWT_WINDOW("cmd", "type","-1"),
	CREATE_SWT_CONTAINER("cmd", "type","-1"),
	
	// replaced by CREATE_EVENT_MEDIATOR
	//CREATE_EVENT_RELATION("cmd","type",null),
	// TODO: remove lines
	
	CREATE_EVENT_MEDIATOR("cmd", "type", null),
	
	RUN_CMD_NOW("cmd", "process","RUN_CMD_NOW"),	
	LOAD_ON_DEMAND("cmd", "process","LOAD_ON_DEMAND"),
	MEMENTO("cmd", "process",null),	
	
	TAG_CMD("cmd","Cmd",null),
	TAG_CMD_QUEUE("cmd","CmdQueue",null),
	TAG_CMD_ID("cmd","cmdId","-1"),
	TAG_TARGET_ID("cmd","targetId","-1"),
	TAG_MEMENTO_ID("cmd","mementoId","-1"),
	TAG_TYPE("cmd","type","NO_OPERATION"),
	TAG_ATTRIBUTE1("cmd","attrib1",""),	
	TAG_ATTRIBUTE2("cmd","attrib2",""),
	TAG_ATTRIBUTE3("cmd","attrib3",""),	
	TAG_DETAIL("cmd","detail",""),
	TAG_PARENT("cmd","parent","-1"),
	TAG_PROCESS("cmd","process","RUN_CMD_NOW"),
	TAG_LABEL("cmd","label",""),
	
	TAG_POS_WIDTH_X("cmd","iWidthX","-1"),
	TAG_POS_HEIGHT_Y("cmd","iHeightY","-1"),
	
	TAG_POS_GL_ORIGIN("cmd","GL_ORIGIN","0 0 0"),
	
	/** Values indicate axis: (X,Y,Z) and rotation-angle (ALPHA) in (radiant). */
	TAG_POS_GL_ROTATION("cmd","GL_ROTATION","0 0 1 0.0"),
	
	/*
	 * -------  COMMAND QUEUE  --------
	 */ 
	COMMAND_QUEUE_OPEN("cmdqueue","type",null),
	COMMAND_QUEUE_RUN("cmdqueue","type",null),
	
	CMD_ID("cmdqueue","cmdId","-1"),
	CMDQUEUE_ID("cmdqueue","cmdQueueId","-1"),
	
	RUN_QUEUE_ON_DEMAND("cmdqueue","process","RUN_QUEUE_ON_DEMAND"),
	RUN_QUEUE("cmdqueue","process","RUN_QUEUE"),

	
	CMD_THREAD_POOL_ID("cmdqueue","queue_thread","-1"),
	CMD_THREAD_POOL_WAIT_ID("cmdqueue","queue_thread_wait","-1");
	
	
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
	
	private String sDefaultValue;
	
	private CommandQueueSaxType( String sXmlTag, 
			String sXmlKey,
			String sDefaultValue ) 
	{
		this.sXmlTag = sXmlTag;		
		this.sXmlKey = sXmlKey;
		this.sDefaultValue = sDefaultValue;
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
	
	/**
	 * Return the default value, if it is known.
	 * 
	 * @return default value
	 */
	public String getDefault() {
		return this.sDefaultValue;
	}
	
}
