package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes;

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
		if (str == "Polygon")
			return new DrawAbleObjectSquare();
		return null;
	}
}
