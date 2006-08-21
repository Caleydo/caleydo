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
	SELECTION(ManagerType.SELECTION),
	
	SELECTION_SINGLE_BLOCK(ManagerType.SELECTION),
	
	SELECTION_MULTI_BLOCK(ManagerType.SELECTION),
	
	SELECTION_MULTI_BLOCK_RLE(ManagerType.SELECTION),
	
	SELECTION_RANDOM_BLOCK(ManagerType.SELECTION),
	
	/**
	 * Load a microarray dataset. Is defined as ISelection, because 
	 * all storage decives are loaded prior.
	 */
	SELECTION_LOAD_MICROARRAY(ManagerType.SELECTION),
	
	
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
	
	VIEW_SWT_HEATMAP2D(ManagerType.VIEW),
	
	VIEW_SWT_HISTOGRAM2D(ManagerType.VIEW),
	
	VIEW_SWT_SCATTERPLOT2D(ManagerType.VIEW),
	
	VIEW_SWT_SCATTERPLOT3D(ManagerType.VIEW),
	
	VIEW_DENDROGRAM(ManagerType.VIEW),
	
	VIEW_PATHWAY(ManagerType.VIEW),
	
	VIEW_TEST_TABLE(ManagerType.VIEW),
	
	VIEW_SWT_GEARS(ManagerType.VIEW),
	
	VIEW_SWT_DATA_EXPLORER(ManagerType.VIEW),
	
	VIEW_SWT_DATA_TABLE(ManagerType.VIEW),
	
	VIEW_SWT_PROGRESS_BAR(ManagerType.VIEW),
	
	VIEW_SWT_STORAGE_TABLE(ManagerType.VIEW),
	
	VIEW_SWT_SELECTION_TABLE(ManagerType.VIEW),
	
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
	
	/**
	 * Logger
	 */
	LOGGER(ManagerType.LOGGER),
	
	ALL_IN_ONE(ManagerType.FABRIK);
	
	
	
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
	
	/**
	 * Convert a String to a type.
	 * 
	 * @param sParseString String to be parsed
	 * @return type or null on failure
	 */
	public static final ManagerObjectType getType( String sParseString) {
		if ( sParseString.equalsIgnoreCase( 
				ManagerObjectType.ALL_IN_ONE.name()) ) 
			return ManagerObjectType.ALL_IN_ONE;
		
		if ( sParseString.equalsIgnoreCase( 
				ManagerObjectType.COMMAND.name()) ) 
			return ManagerObjectType.COMMAND;
		
		if ( sParseString.equalsIgnoreCase(  ManagerObjectType.FABRIK.name()) ) 
			return ManagerObjectType.FABRIK;
		
		if ( sParseString.equalsIgnoreCase( 
				ManagerObjectType.GUI_COMPONENT.name()) ) 
			return ManagerObjectType.GUI_COMPONENT;
		
		if ( sParseString.equalsIgnoreCase( 
				ManagerObjectType.MEMENTO.name() )) 
			return ManagerObjectType.MEMENTO;
		
		if ( sParseString.startsWith( 
				ManagerObjectType.SELECTION.name()) ) {

			if ( sParseString.equalsIgnoreCase( 
					ManagerObjectType.SELECTION.name() )) 
				return ManagerObjectType.SELECTION;
			
			if ( sParseString.equalsIgnoreCase(  
					ManagerObjectType.SELECTION_SINGLE_BLOCK.name()) ) 
				return ManagerObjectType.SELECTION_SINGLE_BLOCK;
			
			if ( sParseString.equalsIgnoreCase( 
					ManagerObjectType.SELECTION_MULTI_BLOCK.name() )) 
				return ManagerObjectType.SELECTION_MULTI_BLOCK;
			
			if ( sParseString.equalsIgnoreCase( 
					ManagerObjectType.SELECTION_MULTI_BLOCK_RLE.name() )) 
				return ManagerObjectType.SELECTION_MULTI_BLOCK_RLE;
			
			if ( sParseString.equalsIgnoreCase( 
					ManagerObjectType.SELECTION_RANDOM_BLOCK.name() )) 
				return ManagerObjectType.SELECTION_RANDOM_BLOCK;
			
			if ( sParseString.equalsIgnoreCase( 
					ManagerObjectType.SELECTION_LOAD_MICROARRAY.name() )) 
				return ManagerObjectType.SELECTION_LOAD_MICROARRAY;
		}
	
		if ( sParseString.startsWith( 
				ManagerObjectType.SET.name() )) {
			
			if ( sParseString.equalsIgnoreCase(  ManagerObjectType.SET.name() )) 
				return ManagerObjectType.SET;	
			
			if ( sParseString.equalsIgnoreCase(  ManagerObjectType.SET_LINEAR.name() )) 
				return ManagerObjectType.SET_LINEAR;	
					
			if ( sParseString.equalsIgnoreCase(  ManagerObjectType.SET_PLANAR.name() )) 
				return ManagerObjectType.SET_PLANAR;	
			
			if ( sParseString.equalsIgnoreCase(  ManagerObjectType.SET_CUBIC.name() )) 
				return ManagerObjectType.SET_CUBIC;	
			
			if ( sParseString.equalsIgnoreCase(  ManagerObjectType.SET_MULTI_DIM.name() )) 
				return ManagerObjectType.SET_MULTI_DIM;
			
			if ( sParseString.equalsIgnoreCase(  ManagerObjectType.SET_MULTI_DIM_VARIABLE.name() )) 
				return ManagerObjectType.SET_MULTI_DIM_VARIABLE;
		}
		
		if ( sParseString.startsWith( 
				ManagerObjectType.VIEW.name() )) {
			
			if ( sParseString.equalsIgnoreCase(  ManagerObjectType.VIEW.name() )) 
				return ManagerObjectType.VIEW;	
			
			if ( sParseString.equalsIgnoreCase(  ManagerObjectType.VIEW_JOGL_CANVAS_MULTIPLE.name() )) 
				return ManagerObjectType.VIEW_JOGL_CANVAS_MULTIPLE;	
			
			if ( sParseString.equalsIgnoreCase(  ManagerObjectType.VIEW_JOGL_CANVAS_SINGLE.name() )) 
				return ManagerObjectType.VIEW_JOGL_CANVAS_SINGLE;	
			
			if ( sParseString.equalsIgnoreCase(  ManagerObjectType.VIEW_NEW_FRAME.name() )) 
				return ManagerObjectType.VIEW_NEW_FRAME;	
		}
		
		if ( sParseString.equalsIgnoreCase(  ManagerObjectType.STORAGE.name() )) 
			return ManagerObjectType.STORAGE;
		
		if ( sParseString.equalsIgnoreCase( 
				ManagerObjectType.STORAGE_FLAT.name() )) 
			return ManagerObjectType.STORAGE_FLAT;
		
		assert false: "unkown (ManagerObjectType) " + sParseString;
		
		return null;
	}
	
}
