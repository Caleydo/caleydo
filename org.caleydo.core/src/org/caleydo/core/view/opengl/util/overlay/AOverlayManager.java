package org.caleydo.core.view.opengl.util.overlay;

import java.awt.Point;

/**
 * Base class for overlays such as info areas or context menus
 * 
 * @author Alexander Lex
 */
public abstract class AOverlayManager {

	/** The 2D coordinates where the mouse was clicked */
	protected Point pickedPoint;
	/** The width of the window where the overlay is rendered */
	protected int windowWidth;
	/** The height of the window where the overlay is rendered */
	protected int windowHeight;

	/** Flag determining whether the overlay is enabled and should be rendered or not */
	protected boolean isEnabled = false;
	/**
	 * Flag determining whether the overlay is beeing rendered for the first time. This is important since the
	 * overlay should not follow the mouse, but remain static once initialized
	 */
	protected boolean isFirstTime = false;

	/**
	 * Set the location where the overlay should appear. The picked point specifies the region next to which
	 * the overlay will be appear, windowWidth and windowHeight are needed for optimal placement of the
	 * rendered elements
	 * 
	 * @param pickedPoint
	 * @param windowWidth
	 * @param windowHeight
	 */
	public void setLocation(Point pickedPoint, int windowWidth, int windowHeight) {

		this.pickedPoint = pickedPoint;
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
		isFirstTime = true;
		isEnabled = true;
	}

	/**
	 * Disables the overlay and resets the relevant elements. This method is intended to be overridden but
	 * should be called by its overriding method
	 */
	public void disable() {
		pickedPoint = null;
		isEnabled = false;
	}

}
