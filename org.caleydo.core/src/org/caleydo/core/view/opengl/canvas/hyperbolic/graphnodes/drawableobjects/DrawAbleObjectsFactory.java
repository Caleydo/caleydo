package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawableobjects;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Map;

import javax.media.opengl.GL;


/**
 * Factory to auto-generate draw able objects.
 * 
 * @author Georg Neubauer
 */
public final class DrawAbleObjectsFactory {
	public static ADrawAbleObject getDrawAbleObject(String str) {
		if (str == "Fallback")
			return new DrawAbleObjectFallback();
		else if (str == "Square")
			return new DrawAbleObjectSquare();
		return null;
	}
}
