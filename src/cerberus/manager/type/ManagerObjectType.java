/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager.type;

//import prometheus.util.exception.PrometheusRuntimeException;

/**
 * Types of managers
 * 
 * @see cerberus.manager.enum.ManagerType
 * 
 * @author Michael Kalkusch
 *
 */
public enum ManagerObjectType {

	/*
	 * STORAGE
	 */
	
	/**
	 * This class is the fabrik itself befor assinging a type.
	 */
	STORAGE(ManagerType.STORAGE),
	
	/**
	 * sub of IStorage
	 */
	STORAGE_FLAT(ManagerType.STORAGE),
	
	
	/*
	 * SELECTION
	 */
	VIRTUAL_ARRAY(ManagerType.VIRTUAL_ARRAY),
	
	VIRTUAL_ARRAY_SINGLE_BLOCK(ManagerType.VIRTUAL_ARRAY),
	
	VIRTUAL_ARRAY_MULTI_BLOCK(ManagerType.VIRTUAL_ARRAY),
	
	VIRTUAL_ARRAY_MULTI_BLOCK_RLE(ManagerType.VIRTUAL_ARRAY),
	
	VIRTUAL_ARRAY_RANDOM_BLOCK(ManagerType.VIRTUAL_ARRAY),
	
//	/**
//	 * Load a microarray dataset. Is defined as IVirtualArray, because 
//	 * all storage decives are loaded prior.
//	 */
//	SELECTION_LOAD_MICROARRAY(ManagerType.VIRTUAL_ARRAY),
	
	
	/*
	 * SET
	 */
	SET(ManagerType.SET),
	
	SET_LINEAR(ManagerType.SET),
	
	SET_PLANAR(ManagerType.SET),
	
	SET_CUBIC(ManagerType.SET),
	
	SET_MULTI_DIM(ManagerType.SET),
	
	SET_MULTI_DIM_VARIABLE(ManagerType.SET),
	
	
	/*
	 * VIEW
	 */
	VIEW(ManagerType.VIEW),
	
	VIEW_HISTOGRAM(ManagerType.VIEW),
	
	// VIEW_OPENGL_CANVAS(ManagerType.VIEW),
	
//	VIEW_OPENGL_TEST_TRIANGLE(ManagerType.VIEW),
//	
//	VIEW_OPENGL_HEATMAP(ManagerType.VIEW),
//	
//	VIEW_OPENGL_SCATTERPLOT2D(ManagerType.VIEW),
	
	
	VIEW_SWT_HEATMAP2D(ManagerType.VIEW),
	
	VIEW_SWT_HISTOGRAM2D(ManagerType.VIEW),
	
	VIEW_SWT_SCATTERPLOT2D(ManagerType.VIEW),
	
	VIEW_SWT_SCATTERPLOT3D(ManagerType.VIEW),
	
	VIEW_SWT_DENDROGRAM(ManagerType.VIEW),
	
	VIEW_SWT_PATHWAY(ManagerType.VIEW),
	
	VIEW_SWT_JOGL_TEST_TRIANGLE(ManagerType.VIEW),	
	
	VIEW_SWT_JOGL_MULTI_GLCANVAS(ManagerType.VIEW),
	
	VIEW_SWT_TEST_TABLE(ManagerType.VIEW),
	
	VIEW_SWT_GEARS(ManagerType.VIEW),
	
	VIEW_SWT_DATA_EXPLORER(ManagerType.VIEW),
	
	VIEW_SWT_DATA_TABLE(ManagerType.VIEW),
	
	VIEW_SWT_PROGRESS_BAR(ManagerType.VIEW),
	
	VIEW_SWT_STORAGE_TABLE(ManagerType.VIEW),
	
	VIEW_SWT_SELECTION_TABLE(ManagerType.VIEW),
	
	VIEW_SWT_SELECTION_SLIDER(ManagerType.VIEW),
	
	VIEW_SWT_STORAGE_SLIDER(ManagerType.VIEW),	
	
	VIEW_SWT_MIXER(ManagerType.VIEW),
	
	VIEW_SWT_BROWSER(ManagerType.VIEW),
	
	VIEW_SWT_IMAGE(ManagerType.VIEW),
	
	VIEW_META_TABLE(ManagerType.VIEW),
	
	//deprecated
	
	VIEW_HISTOGRAM2D(ManagerType.VIEW),
	
	VIEW_HEATMAP2D(ManagerType.VIEW),
	
	/** Create new JOGL canvas with a single GLEventListener */
	VIEW_JOGL_CANVAS_SINGLE(ManagerType.VIEW),
	
	/** Create new JOGL canvas with a multiple GLEventListener */
	VIEW_JOGL_CANVAS_MULTIPLE(ManagerType.VIEW),
	
	VIEW_JOGL_HISTOGRAM(ManagerType.VIEW),

	VIEW_NEW_FRAME(ManagerType.VIEW),
	
	/** Creates a new internal frame */
	VIEW_NEW_IFRAME(ManagerType.VIEW),
	
	
	/*
	 * GUI_COMPONENT
	 */
	GUI_COMPONENT(ManagerType.GUI_COMPONENT),
	
	GUI_WINDOW(ManagerType.GUI_SWT),
	
	GUI_SWT(ManagerType.GUI_SWT),
	
	GUI_SWING(ManagerType.GUI_SWING),
	
	GUI_SWT_NATIVE_WIDGET(ManagerType.GUI_SWT),
	
	GUI_SWT_EMBEDDED_JOGL_WIDGET(ManagerType.GUI_SWT),
	
	GUI_SWT_EMBEDDED_JGRAPH_WIDGET(ManagerType.GUI_SWT),
	
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
	 * MENU
	 */
	MENU(ManagerType.MENU),
	
	
	/*
	 * FABRIK
	 */
	FABRIK(ManagerType.FABRIK),
	
	
	/*
	 * Command 
	 */
	COMMAND(ManagerType.COMMAND ),
	
	/*
	 * Pathway
	 */
	
	PATHWAY(ManagerType.PATHWAY),
	
	PATHWAY_ELEMENT(ManagerType.PATHWAY_ELEMENT),
	
	/*
	 * Event Publisher
	 */
	
	EVENT_PUBLISHER(ManagerType.EVENT_PUBLISHER),
	
	/*
	 * Logger
	 */
	LOGGER(ManagerType.LOGGER),
	
	ALL_IN_ONE(ManagerType.FABRIK);
	
	/*
	 * Pathway
	 */
	
	
	/**
	 * Define type of manager group
	 */
	private final ManagerType eGroupType;
	
	
	/**
	 * Constructor.
	 * 
	 * @param setGroupType type of manager group
	 */
	private ManagerObjectType( final ManagerType setGroupType ) {
		eGroupType = setGroupType;
	}
	
	
	/**
	 * Get the group type for this manager.
	 * 
	 * @return group type
	 */
	public final ManagerType getGroupType() {
		return this.eGroupType;
	}
	
}
