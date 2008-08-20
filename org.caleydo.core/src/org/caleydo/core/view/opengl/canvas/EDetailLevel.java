package org.caleydo.core.view.opengl.canvas;

/**
 * Enum determines the detail level of the views.
 * 
 * @author Marc Streit
 *
 */
public enum EDetailLevel
{
	HIGH,	 // bucket zoomed in
	MEDIUM,  // bucket center (but not zoomed)	
	LOW,     // bucket walls
	VERY_LOW // bucket pool/memo
}