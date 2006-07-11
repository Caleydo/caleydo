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
 * @see cerberus.manager.enum.BaseManagerGroupType
 * 
 * @author Michael Kalkusch
 *
 */
public enum BaseManagerType {

	/*
	 * STORAGE
	 */
	
	/**
	 * This class is the fabtik itself befor assinging a type.
	 */
	STORAGE(BaseManagerGroupType.STORAGE),
	
	/**
	 * sub of Storage
	 */
	STORAGE_FLAT(BaseManagerGroupType.STORAGE),
	
	
	/*
	 * SELECTION
	 */
	SELECTION(BaseManagerGroupType.SELECTION),
	
	SELECTION_SINGLE_BLOCK(BaseManagerGroupType.SELECTION),
	
	SELECTION_MULTI_BLOCK(BaseManagerGroupType.SELECTION),
	
	SELECTION_MULTI_BLOCK_RLE(BaseManagerGroupType.SELECTION),
	
	SELECTION_RANDOM_BLOCK(BaseManagerGroupType.SELECTION),
	
	/**
	 * Load a microarray dataset. Is defined as Selection, because 
	 * all storage decives are loaded prior.
	 */
	SELECTION_LOAD_MICROARRAY(BaseManagerGroupType.SELECTION),
	
	
	/*
	 * SET
	 */
	SET(BaseManagerGroupType.SET),
	
	SET_LINEAR(BaseManagerGroupType.SET),
	
	SET_PLANAR(BaseManagerGroupType.SET),
	
	SET_CUBIC(BaseManagerGroupType.SET),
	
	SET_MULTI_DIM(BaseManagerGroupType.SET),
	
	SET_MULTI_DIM_VARIABLE(BaseManagerGroupType.SET),
	
	
	/*
	 * VIEW
	 */
	VIEW(BaseManagerGroupType.VIEW),
	
	VIEW_HISTOGRAM2D(BaseManagerGroupType.VIEW),
	
	VIEW_HEATMAP2D(BaseManagerGroupType.VIEW),
	
	/** Create new JOGL canvas with a single GLEventListener */
	VIEW_JOGL_CANVAS_SINGLE(BaseManagerGroupType.VIEW),
	
	/** Create new JOGL canvas with a multiple GLEventListener */
	VIEW_JOGL_CANVAS_MULTIPLE(BaseManagerGroupType.VIEW),
	
	VIEW_JOGL_HISTOGRAM(BaseManagerGroupType.VIEW),
	
	VIEW_NEW_FRAME(BaseManagerGroupType.VIEW),
	
	/** Creates a new internal frame */
	VIEW_NEW_IFRAME(BaseManagerGroupType.VIEW),
	
	
	/*
	 * GUI_COMPONENT
	 */
	GUI_COMPONENT(BaseManagerGroupType.GUI_COMPONENT),

	
	/*
	 * MEMENTO
	 */
	MEMENTO(BaseManagerGroupType.MEMENTO),

	
	/*
	 * MENU
	 */
	MENU(BaseManagerGroupType.MENU),
	
	
	/*
	 * FABRIK
	 */
	FABRIK(BaseManagerGroupType.FABRIK),
	
	
	/*
	 * Command 
	 */
	COMMAND(BaseManagerGroupType.COMMAND ),
	
	ALL_IN_ONE(BaseManagerGroupType.FABRIK);
	
	
	
	/**
	 * Define type of manager group
	 */
	private final BaseManagerGroupType eGroupType;
	
	
	/**
	 * Constructor.
	 * 
	 * @param setGroupType type of manager group
	 */
	private BaseManagerType( final BaseManagerGroupType setGroupType ) {
		eGroupType = setGroupType;
	}
	
	
	/**
	 * Get the group type for this manager.
	 * 
	 * @return group type
	 */
	public final BaseManagerGroupType getGroupType() {
		return this.eGroupType;
	}
	
	/**
	 * Convert a String to a type.
	 * 
	 * @param sParseString String to be parsed
	 * @return type or null on failure
	 */
	public static final BaseManagerType getType( String sParseString) {
		if ( sParseString.equalsIgnoreCase( 
				BaseManagerType.ALL_IN_ONE.name()) ) 
			return BaseManagerType.ALL_IN_ONE;
		
		if ( sParseString.equalsIgnoreCase( 
				BaseManagerType.COMMAND.name()) ) 
			return BaseManagerType.COMMAND;
		
		if ( sParseString.equalsIgnoreCase(  BaseManagerType.FABRIK.name()) ) 
			return BaseManagerType.FABRIK;
		
		if ( sParseString.equalsIgnoreCase( 
				BaseManagerType.GUI_COMPONENT.name()) ) 
			return BaseManagerType.GUI_COMPONENT;
		
		if ( sParseString.equalsIgnoreCase( 
				BaseManagerType.MEMENTO.name() )) 
			return BaseManagerType.MEMENTO;
		
		if ( sParseString.startsWith( 
				BaseManagerType.SELECTION.name()) ) {

			if ( sParseString.equalsIgnoreCase( 
					BaseManagerType.SELECTION.name() )) 
				return BaseManagerType.SELECTION;
			
			if ( sParseString.equalsIgnoreCase(  
					BaseManagerType.SELECTION_SINGLE_BLOCK.name()) ) 
				return BaseManagerType.SELECTION_SINGLE_BLOCK;
			
			if ( sParseString.equalsIgnoreCase( 
					BaseManagerType.SELECTION_MULTI_BLOCK.name() )) 
				return BaseManagerType.SELECTION_MULTI_BLOCK;
			
			if ( sParseString.equalsIgnoreCase( 
					BaseManagerType.SELECTION_MULTI_BLOCK_RLE.name() )) 
				return BaseManagerType.SELECTION_MULTI_BLOCK_RLE;
			
			if ( sParseString.equalsIgnoreCase( 
					BaseManagerType.SELECTION_RANDOM_BLOCK.name() )) 
				return BaseManagerType.SELECTION_RANDOM_BLOCK;
			
			if ( sParseString.equalsIgnoreCase( 
					BaseManagerType.SELECTION_LOAD_MICROARRAY.name() )) 
				return BaseManagerType.SELECTION_LOAD_MICROARRAY;
		}
	
		if ( sParseString.startsWith( 
				BaseManagerType.SET.name() )) {
			
			if ( sParseString.equalsIgnoreCase(  BaseManagerType.SET.name() )) 
				return BaseManagerType.SET;	
			
			if ( sParseString.equalsIgnoreCase(  BaseManagerType.SET_LINEAR.name() )) 
				return BaseManagerType.SET_LINEAR;	
					
			if ( sParseString.equalsIgnoreCase(  BaseManagerType.SET_PLANAR.name() )) 
				return BaseManagerType.SET_PLANAR;	
			
			if ( sParseString.equalsIgnoreCase(  BaseManagerType.SET_CUBIC.name() )) 
				return BaseManagerType.SET_CUBIC;	
			
			if ( sParseString.equalsIgnoreCase(  BaseManagerType.SET_MULTI_DIM.name() )) 
				return BaseManagerType.SET_MULTI_DIM;
			
			if ( sParseString.equalsIgnoreCase(  BaseManagerType.SET_MULTI_DIM_VARIABLE.name() )) 
				return BaseManagerType.SET_MULTI_DIM_VARIABLE;
		}
		
		if ( sParseString.startsWith( 
				BaseManagerType.VIEW.name() )) {
			
			if ( sParseString.equalsIgnoreCase(  BaseManagerType.VIEW.name() )) 
				return BaseManagerType.VIEW;	
			
			if ( sParseString.equalsIgnoreCase(  BaseManagerType.VIEW_JOGL_CANVAS_MULTIPLE.name() )) 
				return BaseManagerType.VIEW_JOGL_CANVAS_MULTIPLE;	
			
			if ( sParseString.equalsIgnoreCase(  BaseManagerType.VIEW_JOGL_CANVAS_SINGLE.name() )) 
				return BaseManagerType.VIEW_JOGL_CANVAS_SINGLE;	
			
			if ( sParseString.equalsIgnoreCase(  BaseManagerType.VIEW_NEW_FRAME.name() )) 
				return BaseManagerType.VIEW_NEW_FRAME;	
		}
		
		if ( sParseString.equalsIgnoreCase(  BaseManagerType.STORAGE.name() )) 
			return BaseManagerType.STORAGE;
		
		if ( sParseString.equalsIgnoreCase( 
				BaseManagerType.STORAGE_FLAT.name() )) 
			return BaseManagerType.STORAGE_FLAT;
		
		assert false: "unkown (BaseManagerType) " + sParseString;
		
		return null;
	}
	
}
