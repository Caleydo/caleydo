package org.caleydo.core.view.opengl.util.overlay;

import java.awt.Point;

/**
 * Base class for overlays such as info areas or context menues
 * 
 * @author Alexander Lex
 */
public abstract class AOverlayManager {

	protected Point pickedPoint;
	protected int windowWidth;
	protected int windowHeight;

	protected boolean isEnabled = false;
	protected boolean isFirstTime = false;

	public void setLocation(Point pickedPoint, int windowWidth, int windowHeight) {
		this.pickedPoint = pickedPoint;
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
		isFirstTime = true;
		isEnabled = true;
	}

	public void disable() {
		isEnabled = false;
	}

}
