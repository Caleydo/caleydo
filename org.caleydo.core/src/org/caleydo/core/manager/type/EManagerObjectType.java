package org.caleydo.core.manager.type;

/**
 * Types of managers
 * 
 * @see org.caleydo.core.manager.enum.ManagerType
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public enum EManagerObjectType
{

	/**
	 * see org.caleydo.core.command.CommandQueueSaxType.CREATE_EVENT_MEDIATOR
	 */
	EVENT_MEDIATOR_CREATE(EManagerType.EVENT_PUBLISHER), EVENT_MEDIATOR_ADD_OBJECT(
			EManagerType.EVENT_PUBLISHER),

	/*
	 * STORAGE
	 */
	STORAGE(EManagerType.DATA_STORAGE), STORAGE_NUMERICAL(EManagerType.DATA_STORAGE), STORAGE_NOMINAL(
			EManagerType.DATA_STORAGE),

	/*
	 * SELECTION
	 */
	VIRTUAL_ARRAY(EManagerType.DATA_VIRTUAL_ARRAY), SELECTION(EManagerType.DATA_SELECTION),

	/*
	 * SET
	 */
	SET(EManagerType.DATA_SET),

	SET_VIEWDATA(EManagerType.DATA_VIEWDATA),

	/*
	 * VIEW
	 */
	VIEW(EManagerType.VIEW),

	VIEW_CANVAS_FORWARDER(EManagerType.VIEW),

	VIEW_SWT_PATHWAY(EManagerType.VIEW),

	VIEW_SWT_JOGL_MULTI_GLCANVAS(EManagerType.VIEW),

	VIEW_SWT_TEST_TABLE(EManagerType.VIEW),

	VIEW_SWT_GEARS(EManagerType.VIEW),

	VIEW_SWT_DATA_EXPLORER(EManagerType.VIEW),

	VIEW_SWT_DATA_TABLE(EManagerType.VIEW),

	VIEW_SWT_DATA_EXCHANGER(EManagerType.VIEW),

	VIEW_SWT_PROGRESS_BAR(EManagerType.VIEW),

	VIEW_SWT_STORAGE_TABLE(EManagerType.VIEW),

	VIEW_SWT_SELECTION_TABLE(EManagerType.VIEW),

	VIEW_SWT_SELECTION_SLIDER(EManagerType.VIEW),

	VIEW_SWT_DATA_SET_EDITOR(EManagerType.VIEW),

	VIEW_SWT_STORAGE_SLIDER(EManagerType.VIEW),

	VIEW_SWT_MIXER(EManagerType.VIEW),

	VIEW_SWT_GLYPH_MAPPINGCONFIGURATION(EManagerType.VIEW),

	VIEW_SWT_BROWSER(EManagerType.VIEW),

	VIEW_SWT_IMAGE(EManagerType.VIEW),

	VIEW_SWT_UNDO_REDO(EManagerType.VIEW),

	VIEW_SWT_DATA_ENTITY_SEARCHER(EManagerType.VIEW),

	VIEW_META_TABLE(EManagerType.VIEW),

	VIEW_NEW_FRAME(EManagerType.VIEW),

	/*
	 * GUI_COMPONENT
	 */
	GUI_AWT(EManagerType.VIEW_GUI_AWT),

	GUI_SWT(EManagerType.VIEW_GUI_SWT),

	GUI_SWT_WINDOW(EManagerType.VIEW_GUI_SWT),

	GUI_SWT_NATIVE_WIDGET(EManagerType.VIEW_GUI_SWT),

	GUI_SWT_EMBEDDED_JOGL_WIDGET(EManagerType.VIEW_GUI_SWT),

	GUI_SWT_EMBEDDED_JGRAPH_WIDGET(EManagerType.VIEW_GUI_SWT),

	/*
	 * COMMAND QUEUE
	 */

	CMD_QUEUE(EManagerType.COMMAND),

	CMD_QUEUE_RUN(EManagerType.COMMAND),

	/*
	 * MEMENTO
	 */
	MEMENTO(EManagerType.MEMENTO),

	/*
	 * Command
	 */
	COMMAND(EManagerType.COMMAND),

	/*
	 * Pathway
	 */

	PATHWAY(EManagerType.DATA_PATHWAY),

	PATHWAY_ELEMENT(EManagerType.DATA_PATHWAY_ELEMENT),

	PATHWAY_VERTEX(EManagerType.DATA_PATHWAY_ELEMENT, 62),

	PATHWAY_VERTEX_REP(EManagerType.DATA_PATHWAY_ELEMENT, 63),

	PATHWAY_EDGE(EManagerType.DATA_PATHWAY_ELEMENT, 64),

	PATHWAY_EDGE_REP(EManagerType.DATA_PATHWAY_ELEMENT, 65),

	/*
	 * Event Publisher
	 */

	EVENT_PUBLISHER(EManagerType.EVENT_PUBLISHER),

	/*
	 * Logger
	 */
	LOGGER(EManagerType.LOGGER);

	/**
	 * Define type of manager group
	 */
	private final EManagerType eGroupType;

	private final int iId_TypeOffset;

	/**
	 * Constructor.
	 * 
	 * @param setGroupType
	 *            type of manager group
	 */
	private EManagerObjectType(final EManagerType setGroupType)
	{

		eGroupType = setGroupType;
		iId_TypeOffset = setGroupType.getId_OffsetType();
	}

	/**
	 * Constructor.
	 * 
	 * @param setGroupType
	 *            type of manager group
	 */
	private EManagerObjectType(final EManagerType setGroupType,
			final int iSetUniqueId_TypeOffset)
	{

		eGroupType = setGroupType;
		iId_TypeOffset = iSetUniqueId_TypeOffset;
	}

	/**
	 * Get the group type for this manager.
	 * 
	 * @return group type
	 */
	public final EManagerType getGroupType()
	{

		return this.eGroupType;
	}

	public int getId_TypeOffset()
	{

		return iId_TypeOffset;
	}

	/**
	 * Create a list with all valid Id_TypeOffset values.
	 * 
	 * @param delimiter
	 *            String between Id and String
	 * @return
	 */
	public String toString_Id_TypeOffset(final String delimiter)
	{

		StringBuffer strBuffer = new StringBuffer();

		EManagerObjectType[] array = EManagerObjectType.values();

		for (int i = 0; i < array.length; i++)
		{
			int iCurrentId = array[i].getId_TypeOffset();

			if (iCurrentId > 0)
			{
				strBuffer.append(array[i].getId_TypeOffset());
				strBuffer.append(delimiter);
				strBuffer.append(array[i].name());
				strBuffer.append("\n");
			}
		}

		return strBuffer.toString();
	}
}
