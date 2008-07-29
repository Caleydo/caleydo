package org.caleydo.core.manager.type;

/**
 * Types of managers
 * 
 * @see org.caleydo.core.manager.enum.ManagerType
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public enum ManagerObjectType {

	
	/**
	 * see org.caleydo.core.command.CommandQueueSaxType.CREATE_EVENT_MEDIATOR
	 */
	EVENT_MEDIATOR_CREATE(ManagerType.EVENT_PUBLISHER),
	EVENT_MEDIATOR_ADD_OBJECT(ManagerType.EVENT_PUBLISHER),
	
	/*
	 * STORAGE
	 */
	/**
	 * This class is the fabrik itself before assigning a type.
	 */
	STORAGE(ManagerType.DATA_STORAGE),
	
	/*
	 * SELECTION
	 */
	VIRTUAL_ARRAY(ManagerType.DATA_VIRTUAL_ARRAY),
	
	/*
	 * SET
	 */
	SET(ManagerType.DATA_SET),
	
	SET_VIEWDATA(ManagerType.DATA_VIEWDATA),
	
	/*
	 * SELECTION
	 */
	SELECTION(ManagerType.DATA_SELECTION),
	
	/*
	 * VIEW
	 */
	VIEW(ManagerType.VIEW),
	
	VIEW_CANVAS_FORWARDER(ManagerType.VIEW),
	
	VIEW_HISTOGRAM(ManagerType.VIEW),
	
	VIEW_SWT_DENDROGRAM(ManagerType.VIEW),
	
	VIEW_SWT_PATHWAY(ManagerType.VIEW),
	
	VIEW_SWT_JOGL_MULTI_GLCANVAS(ManagerType.VIEW),
	
	VIEW_SWT_TEST_TABLE(ManagerType.VIEW),
	
	VIEW_SWT_GEARS(ManagerType.VIEW),
	
	VIEW_SWT_DATA_EXPLORER(ManagerType.VIEW),
	
	VIEW_SWT_DATA_TABLE(ManagerType.VIEW),
	
	VIEW_SWT_DATA_EXCHANGER(ManagerType.VIEW),
	
	VIEW_SWT_PROGRESS_BAR(ManagerType.VIEW),
	
	VIEW_SWT_STORAGE_TABLE(ManagerType.VIEW),
	
	VIEW_SWT_SELECTION_TABLE(ManagerType.VIEW),
	
	VIEW_SWT_SELECTION_SLIDER(ManagerType.VIEW),
	
	VIEW_SWT_DATA_SET_EDITOR(ManagerType.VIEW),
	
	VIEW_SWT_STORAGE_SLIDER(ManagerType.VIEW),	
	
	VIEW_SWT_MIXER(ManagerType.VIEW),
	
	VIEW_SWT_BROWSER(ManagerType.VIEW),
	
	VIEW_SWT_IMAGE(ManagerType.VIEW),
	
	VIEW_SWT_UNDO_REDO(ManagerType.VIEW),
	
	VIEW_SWT_DATA_ENTITY_SEARCHER(ManagerType.VIEW),
	
	VIEW_META_TABLE(ManagerType.VIEW),

	VIEW_NEW_FRAME(ManagerType.VIEW),
	
	/*
	 * GUI_COMPONENT
	 */
	GUI_AWT(ManagerType.VIEW_GUI_AWT),
	
	GUI_SWT(ManagerType.VIEW_GUI_SWT),
	
	GUI_SWT_WINDOW(ManagerType.VIEW_GUI_SWT),
	
	GUI_SWT_NATIVE_WIDGET(ManagerType.VIEW_GUI_SWT),
	
	GUI_SWT_EMBEDDED_JOGL_WIDGET(ManagerType.VIEW_GUI_SWT),
	
	GUI_SWT_EMBEDDED_JGRAPH_WIDGET(ManagerType.VIEW_GUI_SWT),
	
	/*
	 * COMMAND QUEUE
	 */
	
	CMD_QUEUE(ManagerType.COMMAND),
	
	CMD_QUEUE_RUN(ManagerType.COMMAND),
	
	
	/*
	 * MEMENTO
	 */
	MEMENTO(ManagerType.MEMENTO),

	/*
	 * Command 
	 */
	COMMAND(ManagerType.COMMAND ),
	
	/*
	 * Pathway
	 */
	
	PATHWAY(ManagerType.DATA_PATHWAY),
	
	PATHWAY_ELEMENT(ManagerType.DATA_PATHWAY_ELEMENT),
		
	PATHWAY_VERTEX(ManagerType.DATA_PATHWAY_ELEMENT, 62),
	
	PATHWAY_VERTEX_REP(ManagerType.DATA_PATHWAY_ELEMENT, 63),
	
	PATHWAY_EDGE(ManagerType.DATA_PATHWAY_ELEMENT, 64),

	PATHWAY_EDGE_REP(ManagerType.DATA_PATHWAY_ELEMENT, 65),
	
	/*
	 * Event Publisher
	 */
	
	EVENT_PUBLISHER(ManagerType.EVENT_PUBLISHER),
	
	/*
	 * Logger
	 */
	LOGGER(ManagerType.LOGGER );
	
	/**
	 * Define type of manager group
	 */
	private final ManagerType eGroupType;
	
	private final int iId_TypeOffset;
	
	/**
	 * Constructor.
	 * 
	 * @param setGroupType type of manager group
	 */
	private ManagerObjectType( final ManagerType setGroupType ) {
		eGroupType = setGroupType;
		iId_TypeOffset = setGroupType.getId_OffsetType();
	}
	
	/**
	 * Constructor.
	 * 
	 * @param setGroupType type of manager group
	 */
	private ManagerObjectType( final ManagerType setGroupType,
			final int iSetUniqueId_TypeOffset) {
		eGroupType = setGroupType;
		iId_TypeOffset = iSetUniqueId_TypeOffset;
	}
	
	/**
	 * Get the group type for this manager.
	 * 
	 * @return group type
	 */
	public final ManagerType getGroupType() {
		return this.eGroupType;
	}
	
	public int getId_TypeOffset() {
		return iId_TypeOffset;
	}
	
	/**
	 * Create a list with all valid Id_TypeOffset values.
	 * 
	 * @param delimiter String between Id and String
	 * @return
	 */
	public String toString_Id_TypeOffset( final String delimiter) {
		
		StringBuffer strBuffer = new StringBuffer();
		
		ManagerObjectType[] array = ManagerObjectType.values();
				
		for (int i=0; i < array.length; i++ ) 
		{
			int iCurrentId = array[i].getId_TypeOffset();
			
			if ( iCurrentId > 0 ) {
				strBuffer.append( array[i].getId_TypeOffset() );
				strBuffer.append( delimiter );
				strBuffer.append( array[i].name() );
				strBuffer.append( "\n" );
			}
		}
		
		return strBuffer.toString();
	}
}
