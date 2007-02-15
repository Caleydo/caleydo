/**
 * 
 */
package cerberus.command;

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
 * @author Michael Kalkusch
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
	LOAD_DATA_FILE("cmd","type","", "No description available!"),
	LOAD_DATA_FILE_N_STORAGES("cmd","type","", "No description available!"),
	LOAD_DATA_FILE_BY_IMPORTER("cmd","type","", "No description available!"),
	LOAD_LOOKUP_TABLE_FILE("cmd","type","", "No description available!"),
	LOAD_ON_DEMAND("cmd", "process","LOAD_ON_DEMAND", "No description available!"),
	LOAD_URL_IN_BROWSER("cmd", "type", "-1", "No description available!"),
	//OPEN_VIEW("cmd","type"),

	CREATE_EVENT_MEDIATOR("cmd", "type", null, "No description available!"),
		
	CREATE_GL_TRIANGLE_TEST("cmd","type","-1", "No description available!"),
	CREATE_GL_TEXTURE2D("cmd","type","-1", "No description available!"),
	CREATE_GL_HEATMAP("cmd","type","-1", "No description available!"),
	CREATE_GL_HEATMAP2D("cmd","type","-1", "No description available!"),
	CREATE_GL_HISTOGRAM2D("cmd","type","-1", "No description available!"),
	CREATE_GL_SCATTERPLOT2D("cmd","type","-1", "No description available!"),
	CREATE_GL_LAYERED_PATHWAY_3D("cmd","type","-1", "No description available!"),
	CREATE_GL_PANEL_PATHWAY_3D("cmd","type","-1", "No description available!"),	
	CREATE_GL_MINMAX_SCATTERPLOT2D("cmd","type","-1", "No description available!"),
	CREATE_GL_MINMAX_SCATTERPLOT3D("cmd","type","-1", "No description available!"),
	CREATE_GL_ISOSURFACE3D("cmd","type","-1", "No description available!"),
	
	CREATE_PATHWAY_STORAGE("cmd","type","-1", "No description available!"),
	CREATE_SET("cmd","type","-1", "No description available!"),
	CREATE_SET_PLANAR("cmd","type","-1", "No description available!"),
	CREATE_SET_MULTIDIM("cmd","type","-1", "No description available!"),
	CREATE_STORAGE("cmd","type","-1", "No description available!"),	
	CREATE_SWT_WINDOW("cmd", "type","-1", "No description available!"),
	CREATE_SWT_CONTAINER("cmd", "type","-1", "No description available!"),	
	CREATE_VIRTUAL_ARRAY("cmd","type","-1", "No description available!"),
	
	CREATE_VIEW_HEATMAP("cmd", "type","-1", "No description available!"),
	CREATE_VIEW_PATHWAY("cmd", "type","-1", "No description available!"),
	CREATE_VIEW_GEARS("cmd", "type","-1", "No description available!"),
	CREATE_VIEW_DATA_EXPLORER("cmd", "type","-1", "No description available!"),
	CREATE_VIEW_DATA_EXCHANGER("cmd", "type","-1", "No description available!"),	
	CREATE_VIEW_PROGRESSBAR("cmd", "type","-1", "No description available!"),
	CREATE_VIEW_STORAGE_SLIDER("cmd", "type","-1", "No description available!"),
	CREATE_VIEW_SELECTION_SLIDER("cmd", "type","-1", "No description available!"),	
	CREATE_VIEW_MIXER("cmd", "type","-1", "No description available!"),
	CREATE_VIEW_BROWSER("cmd", "type", "-1", "No description available!"),	
	CREATE_VIEW_IMAGE("cmd", "type", "-1", "No description available!"),	
	CREATE_VIEW_TEST_TRIANGLE("cmd", "type","-1", "No description available!"),
	CREATE_VIEW_SWT_GLCANVAS("cmd", "type","-1", "No description available!"),
	CREATE_VIEW_SET_EDITOR("cmd", "type","-1", "No description available!"),
	CREATE_VIEW_UNDO_REDO("cmd", "type","-1", "No description available!"),

	RUN_CMD_NOW("cmd", "process","RUN_CMD_NOW", "No description available!"),	
	MEMENTO("cmd", "process", null, "No description available!"),	
	
	NO_OPERATION("cmd","type","NO_OPERATION", "No description available!"),
	
	
	
	/*
	 * -------  COMMAND QUEUE  --------
	 */ 
	COMMAND_QUEUE_OPEN("cmdqueue","type",null, "No description available!"),
	COMMAND_QUEUE_RUN("cmdqueue","type",null, "No description available!"),
	
	CMD_ID("cmdqueue","cmdId","-1", "No description available!"),
	CMDQUEUE_ID("cmdqueue","cmdQueueId","-1", "No description available!"),
	
	RUN_QUEUE_ON_DEMAND("cmdqueue","process","RUN_QUEUE_ON_DEMAND", "No description available!"),
	RUN_QUEUE("cmdqueue","process","RUN_QUEUE", "No description available!"),

	
	CMD_THREAD_POOL_ID("cmdqueue","queue_thread","-1", "No description available!"),
	CMD_THREAD_POOL_WAIT_ID("cmdqueue","queue_thread_wait","-1", "No description available!"),

	
	/*
	 * =================================================
	 *    Import from former Type "CommandType"
	 * =================================================
	 */
	
	WINDOW_SET_ACTIVE_FRAME("cmd","type","-1", "No description available!"),
	WINDOW_IFRAME_NEW_INTERNAL_FRAME("cmd","type","-1", "No description available!"),
	
	SYSTEM_SHUT_DOWN("cmd","type","-1", "No description available!"),
	
	/*
	 * ==================================================
	 *       TAG's used only while parsing XML files  
	 * ==================================================
	 */
	TAG_CMD("cmd","Cmd",null, "No description available!"),
	TAG_CMD_QUEUE("cmd","CmdQueue",null, "No description available!"),
	TAG_CMD_ID("cmd","cmdId","-1", "No description available!"),
	TAG_TARGET_ID("cmd","targetId","-1", "No description available!"),
	TAG_MEMENTO_ID("cmd","mementoId","-1", "No description available!"),
	TAG_TYPE("cmd","type","NO_OPERATION", "No description available!"),
	TAG_ATTRIBUTE1("cmd","attrib1","", "No description available!"),	
	TAG_ATTRIBUTE2("cmd","attrib2","", "No description available!"),
	TAG_ATTRIBUTE3("cmd","attrib3","", "No description available!"),	
	TAG_DETAIL("cmd","detail","", "No description available!"),
	TAG_PARENT("cmd","parent","-1", "No description available!"),
	TAG_GLCANVAS("cmd","glcanvas","-1", "No description available!"),
	TAG_GLCANVAS_LISTENER("cmd","gllistener","-1", "No description available!"),
	TAG_PROCESS("cmd","process","RUN_CMD_NOW", "No description available!"),
	TAG_LABEL("cmd","label","", "No description available!"),
	
	TAG_POS_WIDTH_X("cmd","iWidthX","-1", "No description available!"),
	TAG_POS_HEIGHT_Y("cmd","iHeightY","-1", "No description available!"),
	
	TAG_POS_GL_ORIGIN("cmd","GL_ORIGIN","0 0 0", "No description available!"),
	
	/** Values indicate axis: (X,Y,Z) and rotation-angle (ALPHA) in (radiant). */
	TAG_POS_GL_ROTATION("cmd","GL_ROTATION","0 0 1 0.0", "No description available!");
	
	
	
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
	
	/**
	 * Text that should describe the command.
	 * This is mainly used for the UNDO/REDO function for
	 * showing extra information to the commands.
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
	private CommandQueueSaxType( String sXmlTag, 
			String sXmlKey,
			String sDefaultValue,
			String sInfoText) 
	{
		this.sXmlTag = sXmlTag;		
		this.sXmlKey = sXmlKey;
		this.sDefaultValue = sDefaultValue;
		this.sInfoText = sInfoText;
	}
	
	
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
	
	public String getInfoText() {
		
		return this.sInfoText;
	}
	
}
