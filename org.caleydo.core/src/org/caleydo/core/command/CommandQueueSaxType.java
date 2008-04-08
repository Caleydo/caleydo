package org.caleydo.core.command;

import org.caleydo.core.manager.type.ManagerType;
import org.caleydo.core.util.ICaleydoDefaultType;

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
 * @author Marc Streit
 *
 */
public enum CommandQueueSaxType
implements ICaleydoDefaultType <CommandQueueSaxType> {

	/*
	 * -------  COMMAND  --------
	 */ 
	
	/**
	 * XML-value  ( XML-Tag , XML-key ) 
	 */
	LOAD_DATA_FILE(ManagerType.SYSTEM,"cmd","type","", "No description available!"),
	LOAD_DATA_FILE_N_STORAGES(ManagerType.SYSTEM,"cmd","type","", "Load a file into n storages"),
	LOAD_DATA_FILE_BY_IMPORTER(ManagerType.SYSTEM,"cmd","type","", "Load a file via importer"),
	LOAD_LOOKUP_TABLE_FILE(ManagerType.SYSTEM,"cmd","type","", "Load a lookup table"),
	LOAD_ON_DEMAND(ManagerType.SYSTEM,"cmd", "process","LOAD_ON_DEMAND", "No description available!"),
	LOAD_URL_IN_BROWSER(ManagerType.EVENT_PUBLISHER,"cmd", "type", "-1", "Load URL in browser"),
	//OPEN_VIEW("cmd","type"),

	DATA_FILTER_MATH(ManagerType.DATA_STORAGE, "cmd", "type", "-1", "Filter data by using math operations"),
	DATA_FILTER_MIN_MAX(ManagerType.DATA_STORAGE, "cmd", "type", "-1", "Evaluate min and max of an entity"),
	
	CREATE_EVENT_MEDIATOR(ManagerType.EVENT_PUBLISHER,"cmd", "type", null, "Create Event Mediator"),
	EVENT_MEDIATOR_ADD_OBJECT("cmd", "type", null, "Add Objects ad sender or receiver to Event Mediator"),
	
	CREATE_GL_TRIANGLE_TEST(ManagerType.VIEW,"cmd","type","-1", "No description available!"),
	CREATE_GL_TEXTURE2D(ManagerType.VIEW,"cmd","type","-1", "No description available!"),
	CREATE_GL_HEATMAP(ManagerType.VIEW,"cmd","type","-1", "Create Heat Map"),
	CREATE_GL_HEATMAP2D(ManagerType.VIEW,"cmd","type","-1", "No description available!"),
	CREATE_GL_HEATMAP2DCOLUMN(ManagerType.VIEW,"cmd","type","-1", "No description available!"),
	CREATE_GL_HEAT_MAP_3D(ManagerType.VIEW,"cmd","type","-1", "Create Heat Map"),
	CREATE_GL_HISTOGRAM2D(ManagerType.VIEW,"cmd","type","-1", "No description available!"),
	CREATE_GL_SCATTERPLOT2D(ManagerType.VIEW,"cmd","type","-1", "No description available!"),
	CREATE_GL_LAYERED_PATHWAY_3D(ManagerType.VIEW,"cmd","type","-1", "Create Layered Pathway 3D"),
	CREATE_GL_PANEL_PATHWAY_3D(ManagerType.VIEW,"cmd","type","-1", "Create Panel Pathway 3D"),	
	CREATE_GL_JUKEBOX_PATHWAY_3D(ManagerType.VIEW,"cmd","type","-1", "Create Jukebox Pathway 3D"),		
	CREATE_GL_PATHWAY_3D(ManagerType.VIEW,"cmd","type","-1", "Create Pathway 3D"),		
	CREATE_GL_PARALLEL_COORDINATES_3D(ManagerType.VIEW,"cmd","type","-1", "Create Parallel Coordinates 3D"),		
	CREATE_GL_BUCKET_3D(ManagerType.VIEW,"cmd","type","-1", "Create Bucket 3D"),
	CREATE_GL_MINMAX_SCATTERPLOT2D(ManagerType.VIEW,"cmd","type","-1", "No description available!"),
	CREATE_GL_MINMAX_SCATTERPLOT3D(ManagerType.VIEW,"cmd","type","-1", "No description available!"),
	CREATE_GL_ISOSURFACE3D(ManagerType.VIEW,"cmd","type","-1", "No description available!"),
	CREATE_GL_WIDGET(ManagerType.VIEW,"cmd","type","-1", "No description available!"),
	
	EXTERNAL_FLAG_SETTER(ManagerType.VIEW, "cmd", "type", "-1", "External flag setter"),
	EXTERNAL_ACTION_TRIGGER(ManagerType.VIEW, "cmd", "type", "-1", "External action trigger"),
	
	CREATE_PATHWAY_STORAGE(ManagerType.DATA_STORAGE,"cmd","type","-1", "Create Storage Pathway"),
	CREATE_SET_DATA(ManagerType.DATA,"cmd","type","-1", "Create SET"),	
	CREATE_SET_SELECTION(ManagerType.DATA_SET,"cmd","type","-1", "Create Selection SET!"),
	CREATE_SET_SELECTION_MAKRO(ManagerType.DATA_SET,"cmd", "type","-1", "Create Selection SET incl. Storage and Virtual Array!"),
	CREATE_SET_VIEW(ManagerType.DATA_SET,"cmd", "type","-1", "Create Set for a view"),
	CREATE_STORAGE(ManagerType.DATA_STORAGE,"cmd","type","-1", "Create Storage"),		
	CREATE_VIRTUAL_ARRAY(ManagerType.DATA_VIRTUAL_ARRAY,"cmd","type","-1", "Create VirtualArray"),

	CREATE_SWT_WINDOW(ManagerType.VIEW,"cmd", "type","-1", "Create SWT window"),
	CREATE_SWT_CONTAINER(ManagerType.VIEW,"cmd", "type","-1", "Create SWTContainer"),
	CREATE_VIEW_PATHWAY(ManagerType.VIEW,"cmd", "type","-1", "Create Pathway 2D"),
	CREATE_VIEW_GEARS(ManagerType.VIEW,"cmd", "type","-1", "Create Gears Demo"),
	CREATE_VIEW_DATA_EXPLORER(ManagerType.VIEW,"cmd", "type","-1", "Create Data Explorer"),
	CREATE_VIEW_DATA_EXCHANGER(ManagerType.VIEW,"cmd", "type","-1", "Create Data Exchanger"),	
	CREATE_VIEW_PROGRESSBAR(ManagerType.VIEW,"cmd", "type","-1", "Create Progress Bar"),
	CREATE_VIEW_STORAGE_SLIDER(ManagerType.VIEW,"cmd", "type","-1", "Create Slider"),
	CREATE_VIEW_SELECTION_SLIDER(ManagerType.VIEW,"cmd", "type","-1", "Create Slider"),	
	CREATE_VIEW_MIXER(ManagerType.VIEW,"cmd", "type","-1", "Create Mixer"),
	CREATE_VIEW_BROWSER(ManagerType.VIEW,"cmd", "type", "-1", "Create Browser"),	
	CREATE_VIEW_IMAGE(ManagerType.VIEW,"cmd", "type", "-1", "Create Image"),	
	CREATE_VIEW_SET_EDITOR(ManagerType.VIEW,"cmd", "type","-1", "No description available!"),
	CREATE_VIEW_UNDO_REDO(ManagerType.VIEW,"cmd", "type","-1", "Create UNDO/REDO"),
	CREATE_VIEW_DATA_ENTITY_SEARCHER("cmd", "type", null, "Create Data Entity Searcher"),

	CREATE_VIEW_SWT_GLCANVAS(ManagerType.VIEW,"cmd", "type","-1", "Create SWT GL Canvas"),
	CREATE_VIEW_RCP_GLCANVAS(ManagerType.VIEW,"cmd", "type", "-1", "Create RCP GL Canvas"),
	
	/* switches to create different Set's */
	SET_DATA_LINEAR("cmd","type","-1", "Create planar SET"),
	SET_DATA_PLANAR("cmd","type","-1", "Create planar SET"),
	SET_DATA_CUBIC("cmd","type","-1", "Create cubic-dim SET"),
	SET_DATA_MULTIDIM("cmd","type","-1", "Create multi-dim SET"),
	SET_DATA_MULTIDIM_VARIABLE("cmd","type","-1", "Create multi-dim RLE SET"),
	
	/**
	 * Set path for pathway XML files, images and imagemaps.
	 */
	SET_SYSTEM_PATH_PATHWAYS("cmd", "type", "-1", "Set path to pathway files"),	

	RUN_CMD_NOW("cmd", "process","RUN_CMD_NOW", "No description available!"),	
	MEMENTO("cmd", "process", null, "No description available!"),	
	
	NO_OPERATION("cmd","type","NO_OPERATION", "No description available!"),
	
	
	/*
	 * -------  COMMAND QUEUE  --------
	 */ 
	COMMAND_QUEUE_OPEN(ManagerType.SYSTEM,"cmdqueue","type",null, "Open a command queue"),
	COMMAND_QUEUE_RUN(ManagerType.SYSTEM,"cmdqueue","type",null, "execute a command queue"),
	
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
	
	SYSTEM_SHUT_DOWN(ManagerType.SYSTEM,"cmd","type","-1", "Caleydo system shut down"),
	
	WINDOW_IFRAME_OPEN_HEATMAP2D(),
	WINDOW_IFRAME_OPEN_HISTOGRAM2D(),
	WINDOW_IFRAME_OPEN_SELECTION(),
	WINDOW_IFRAME_OPEN_STORAGE(),
	WINDOW_IFRAME_OPEN_JOGL_HISTOGRAM(),
	WINDOW_IFRAME_OPEN_JOGL_HEATMAP(),
	WINDOW_IFRAME_OPEN_JOGL_SCATTERPLOT(),
	WINDOW_POPUP_CREDITS(),
	WINDOW_POPUP_INFO(),
	SYSTEM_NEW_FRAME(),
	SYSTEM_NOP(),
	
	/*
	 * ==================================================
	 *       TAG's used only while parsing XML files  
	 * ==================================================
	 */
	TAG_CMD("cmd","Cmd",null),
	TAG_CMD_QUEUE("cmd","CmdQueue",null),
	TAG_CMD_ID("cmd","cmdId","-1"),
	TAG_UNIQUE_ID("cmd","uniqueId","-1"),
	TAG_MEMENTO_ID("cmd","mementoId","-1"),
	TAG_TYPE("cmd","type","NO_OPERATION"),
	TAG_ATTRIBUTE1("cmd","attrib1",""),	
	TAG_ATTRIBUTE2("cmd","attrib2",""),
	TAG_ATTRIBUTE3("cmd","attrib3",""),	
	TAG_ATTRIBUTE4("cmd","attrib4",""),
	TAG_DETAIL("cmd","detail",""),
	TAG_PARENT("cmd","parent","-1"),
	TAG_GLCANVAS("cmd","gl_canvas","-1"),
	TAG_PROCESS("cmd","process","RUN_CMD_NOW"),
	TAG_LABEL("cmd","label",""),
	
	TAG_POS_WIDTH_X("cmd","iWidthX","-1"),
	TAG_POS_HEIGHT_Y("cmd","iHeightY","-1"),
	
	TAG_POS_GL_ORIGIN("cmd","gl_origin","0 0 0"),
	
	/** Values indicate axis: (X,Y,Z) and rotation-angle (ALPHA) in (radiant). */
	TAG_POS_GL_ROTATION("cmd","gl_rotation","0 0 1 0.0");
	

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
	 * Define type of manager group
	 */
	private final ManagerType eGroupType;
	
	private CommandQueueSaxType() 
	{
		this.sXmlTag = null;		
		this.sXmlKey = null;
		this.sDefaultValue = null;
		this.sInfoText = null;
		this.eGroupType = null;
		
	}
	
	/**
	 * Constructor.
	 * 
	 * @param type
	 * @param sXmlTag
	 * @param sXmlKey
	 * @param sDefaultValue
	 * @param sInfoText
	 */
	private CommandQueueSaxType( ManagerType type,
			String sXmlTag, 
			String sXmlKey,
			String sDefaultValue,
			String sInfoText) {
		
		this.sXmlTag = sXmlTag;		
		this.sXmlKey = sXmlKey;
		this.sDefaultValue = sDefaultValue;
		this.sInfoText = sInfoText;
		this.eGroupType = type;	
	}
	
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
			String sInfoText) {
		
		this( ManagerType.NONE,
				sXmlTag, 
				sXmlKey,
				sDefaultValue,
				sInfoText);
		
	}
	
	private CommandQueueSaxType( String sXmlTag, 
			String sXmlKey,
			String sDefaultValue) {
		
		this( ManagerType.NONE,
				sXmlTag, 
				sXmlKey,
				sDefaultValue,
				"Description is not valid! This is a TAG.");
	}
	
	/**
	 * Get the group type for this manager.
	 * 
	 * @return group type
	 */
	public final ManagerType getGroupType() {
		return this.eGroupType;
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

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.util.ICaleydoDefaultType#getTypeDefault()
	 */
	public CommandQueueSaxType getTypeDefault() {

		return CommandQueueSaxType.NO_OPERATION;
	}
	
}
