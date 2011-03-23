package org.caleydo.view.visbricks.brick.ui;

import org.caleydo.core.view.opengl.layout.LayoutRenderer;

/**
 * Abstract base class for all view renderers that should be displayed within a
 * brick.
 * 
 * @author Christian Partl
 * 
 */
public abstract class AContainedViewRenderer extends LayoutRenderer {

	/**
	 * @return The minimum height in pixels required by the view renderer to
	 *         render its data.
	 */
	public abstract int getMinHeightPixels();
	
	/**
	 * @return The minimum width in pixels required by the view renderer to
	 *         render its data.
	 */
	public abstract int getMinWidthPixels();

}
