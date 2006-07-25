/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.view;

import cerberus.base.type.WindowToolkitType;

/**
 * Enumeration of internal and external Frame commands.
 * 
 * 
 * @author Michael Kalkusch
 *
 */
public enum FrameBaseType {
	
	//--- OpenGL canvas ----
	/**
	 * ( WINDOWS-Toolkit Type  use "null" as default-type , 
	 *   internal_frame==TRUE or external_frame==FALSE, 
	 *   OpenGL==TRUE or AWT==FALSE,
	 *   Longe title shown in frame,
	 *   short title for menu  ) 
	 */
	
	GEARS( null, true, true, "gears", "greas" ),
	HWSHADOWS( null, true, true,"Hardware Shadows", "HWShadow"),
	REFRACT( null, true, true,"REFACTOR","recactor"),
	VBO( null, true, true,"vertex Buffer Objects","VBO"),
	WARP( null, true, true,"Warp in CG","Warp"),
	INFINITE ( null, true, true,"Infinit Shadow Volumes", "Infinit Shadow Volumes"),
	
	HEATMAP ( null, true,  true,"Heatmap", "Heatmap" ),
	
	MAIN_FRAME( null, false,  false,"Cerberus Main Frame","ext. Frame"),
	EMPTY_INTERNAL_AWTFRAME( null, true,  false,"Cerberus Internal Frame","int. AWTFrame"),
	EMPTY_INTERNAL_GLFRAME( null, true,  true,"Cerberus Internal Frame","int. GLFrame"),
	
	HISTOGRAM( null, true,  true,"Histogram","Histogram"),
	SCATTERPLOT2D( null, true,  true,"Scatter Plot 2D","Scatter Plot -- 2D"),
	SCATTERPLOT3D( null, true,  true,"Scatter Plot 3D","Scatter Plot -- 3D"),
	PARALLELCOORDINATES2D( null, true,  true,"Parallel Coord 2D","Parallel Coordinates -- 2D"),
	TREEVIEW( null, true,  true,"TREEVIEW","TreeView"),
	
	LOADIMAGE( null, true,  true,"Load Image","Load image"),
	
	//--- AWT canvas ----
	JBROWSER_SELECTION(null, true, false,"Selection Browser","select.."),
	JBROWSER_SET(null, true, false,"Set Browser","set.."),
	JBROWSER_STORAGE(null, true, false,"Storage Browser","store..");
	
	/**
	 * Default window toolkit.
	 * Used in constructor when first argument for WindowToolkitType is null.
	 */
	private final WindowToolkitType defaultWindowingToolkitType = WindowToolkitType.SWING;
		
	/**
	 * Frame title.
	 */
	private String sFrameTitle;
	
	private String sFrameMenuTitle;
	
	/**
	 * TRUE if this is an InternalFrame, FALSE if this is an external Frame.
	 */
	private boolean bInternalFrameState;
	
	private boolean bIsGLCanvas;
	
	/**
	 * Define windowing toolkit type.
	 */
	private WindowToolkitType enumWindowToolkitType;
	
	
	/**
	 * Constructor.
	 * 
	 * @param eSetWindowToolkitType windowing toolkit type, if null defaultWindowingToolkitType is used as WindowToolkitType
	 * @param bSetInternalFrameState internal frame (TRUE) or external frame (FALSE)
	 * @param bIsGLCanvas is a GLcanvas (TRUE) or is a regular AWT frame (FALSE)
	 * @param sSetFrameTitle default title for frame
	 */
	private FrameBaseType( final WindowToolkitType eSetWindowToolkitType,
			final boolean bSetInternalFrameState,
			final boolean bSetIsGLCanvas,
			final String sSetFrameTitle,
			final String sSetFrameMenuTitle ) {
		
		if ( eSetWindowToolkitType == null ) {
			this.enumWindowToolkitType = defaultWindowingToolkitType;
		} else {
			this.enumWindowToolkitType = eSetWindowToolkitType;
		}
		
		this.sFrameTitle = sSetFrameTitle;
		this.sFrameMenuTitle = sSetFrameMenuTitle;
		this.bInternalFrameState= bSetInternalFrameState;
		this.bIsGLCanvas = bSetIsGLCanvas;
	}
	
	/**
	 * Returns the applyToType with the settings specified.
	 * Is like calling setInternalFrameState() and setWindowingToolkitType() in a row.
	 * 
	 * @see cerberus.view.FrameBaseType#setInternalFrameState(boolean)
	 * @see cerberus.view.FrameBaseType#setWindowToolkitType(WindowToolkitType)
	 * 
	 * @param defaultType input type, with is changed an returned after applying the other two parametes
	 * @param eSetWindowToolkitType like setWindowingToolkitType()
	 * @param bSetInternalFrameState like setInternalFrameState()
	 * @return input type applyToType after applying the other two parametes
	 */
	public FrameBaseType applySettings(FrameBaseType applyToType,
			final WindowToolkitType eSetWindowToolkitType,
			final boolean bSetInternalFrameState ) {
		
		applyToType.setInternalFrameState(bSetInternalFrameState);
		applyToType.setWindowToolkitType(eSetWindowToolkitType);
		
		return applyToType;
	}

	/**
	 * Get the default frame title.
	 * 
	 * @return get default frame title
	 */
	public String getFrameTitle() {
		return sFrameTitle;
	}
	
	/**
	 * Get the default frame menu title.
	 * 
	 * @return get default frame menu title
	 */
	public String getFrameMenuTitle() {
		if ( bInternalFrameState ) {
			return sFrameMenuTitle;
		}
		return sFrameMenuTitle + " (E)";
	}
	
	
	/**
	 * Get the default frame menu title.
	 * 
	 * @return get default frame menu title
	 */
	public void setFrameMenuTitle( final String setFrameMenutTitle ) {
		this.sFrameMenuTitle = setFrameMenutTitle;
	}


	/**
	 * Set the default frame title.
	 * 
	 * @param sSetFrameTitle default frame title
	 */
	public void setFrameTitle( final String sSetFrameTitle) {
		this.sFrameTitle = sSetFrameTitle;
	}
	
	/**
	 * Set type of windowing toolkit used.
	 * 
	 * @see cerberus.view.FrameBaseType#getWindowToolkitType()
	 * @see cerberus.view.FrameBaseType#setInternalFrameState(boolean)
	 * @see cerberus.view.FrameBaseType#applySettings(FrameBaseType, WindowToolkitType, boolean)
	 * 
	 * @param eSetWindowToolkitType type of windowing toolkit used
	 */
	public void setWindowToolkitType( final WindowToolkitType eSetWindowToolkitType) {
		enumWindowToolkitType = eSetWindowToolkitType; 
	}
	
	/**
	 * Get type of windowing toolkit used.
	 * 
	 * @see cerberus.view.FrameBaseType#setWindowToolkitType(WindowToolkitType)
	 *  
	 * @return type of windowing toolkit used
	 */
	public WindowToolkitType getWindowToolkitType() {
		return this.enumWindowToolkitType;
	}
	
	/**
	 * Get the name of this type to be used inside the XML-file.
	 * 
	 * @return name of type used inside XML-file
	 */
	public String getTypeNameForXML() {
		return this.name();
	}
	
	/**
	 * Set status wether it is an internal or external frame.
	 * 
	 * @see cerberus.view.FrameBaseType#getInternalFrameState()
	 * @see cerberus.view.FrameBaseType#setWindowToolkitType(WindowToolkitType)
	 * @see cerberus.view.FrameBaseType#applySettings(FrameBaseType, WindowToolkitType, boolean)
	 * 
	 * @param bSetInternalFrameState TRUE is for internal frames, FALSE is for external frames.
	 */
	public void setInternalFrameState( final boolean bSetInternalFrameState ) {
		this.bInternalFrameState = bSetInternalFrameState;
	}
	

	/**
	 * Get status wether it is an internal or external frame.
	 * 
	 * @see cerberus.view.FrameBaseType#setInternalFrameState(boolean)
	 * 
	 * @return TRUE if it is an internal frame and FALSE if it is an external frame.
	 */
	public boolean getInternalFrameState( ) {
		return this.bInternalFrameState;
	}
	
	
	/**
	 * Set status wether it is an internal or external frame.
	 * 
	 * @see cerberus.view.FrameBaseType#isGLCanvas()
	 * 
	 * @param bSetIsGLCanvas TRUE if it is a GLCanvas, FALSE if it is an AWT-frame.
	 */
	public void setIsGLCanvas( final boolean bSetIsGLCanvas ) {
		this.bIsGLCanvas = bSetIsGLCanvas;
	}
	

	/**
	 * TRUE if this is a GLCanvas, false else if it is an AWT frame.
	 * 
	 * @see cerberus.view.FrameBaseType#setIsGLCanvas(boolean)
	 * 
	 * @return TRUE if it is a GLCanvas, FALSE if it is an AWT-frame.
	 */
	public boolean isGLCanvas() {
		return this.bIsGLCanvas ;
	}
	
	
}
