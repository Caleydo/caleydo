package org.caleydo.core.view.opengl.canvas;

/**
 * Enum determines the detail level of the views.
 * 
 * @author Marc Streit
 * 
 */
public enum EDetailLevel
{

	/**
	 * bucket pool/memo
	 */
	VERY_LOW,
	
	/**
	 * bucket walls
	 */
	LOW,
	
	/**
	 * bucket center (but not zoomed)
	 */
	MEDIUM,
	
	/**
	 * bucket zoomed in or standalone
	 */
	HIGH
}