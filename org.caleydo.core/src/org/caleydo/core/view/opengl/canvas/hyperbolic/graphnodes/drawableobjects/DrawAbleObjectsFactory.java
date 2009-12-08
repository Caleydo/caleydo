package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawableobjects;

/**
 * Factory to auto-generate draw able objects.
 * 
 * @author Georg Neubauer
 */
public final class DrawAbleObjectsFactory {

	/**
	 * Returns an object of type specified by the parameter
	 * 
	 * @param str
	 * @return
	 */
	public static IDrawAbleObject getDrawAbleObject(String str) {
		if (str == "Fallback")
			return new DrawAbleObjectFallback();
		else if (str == "Quad")
			return new DrawAbleObjectQuad();
		return null;
	}
}
