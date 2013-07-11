/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas;

import gleem.linalg.Vec2f;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL2;

import org.caleydo.core.internal.MyPreferences;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class AGLCanvas implements IGLCanvas {
	private float scale = MyPreferences.getViewZoomFactor();


	@Override
	public final float getDIPWidth() {
		return dip(asGLAutoDrawAble().getWidth());
	}

	@Override
	public final float getDIPHeight() {
		return dip(asGLAutoDrawAble().getHeight());
	}

	private float dip(int px) {
		return Units.px(px) * scale;
	}

	public final float toDIP(int value_px) {
		return dip(value_px);
	}

	public final Vec2f toDIP(Point point) {
		return new Vec2f(dip(point.x), dip(point.y));
	}

	@Override
	public final int toRawPixel(float value_dip) {
		return Units.dip2px(value_dip, scale);
	}

	@Override
	public final float getWidth(Units unit) {
		return unit.unapply(getDIPWidth());
	}

	@Override
	public final float getHeight(Units unit) {
		return unit.unapply(getDIPHeight());
	}

	@Override
	public final void applyScaling(GL2 gl) {
		gl.glScalef(scale, scale, 1);
	}

	@Override
	public final Rectangle toRawPixel(Rectangle2D.Float viewArea_dip) {
		return new Rectangle(toRawPixel(viewArea_dip.x), toRawPixel(viewArea_dip.y), toRawPixel(viewArea_dip.width),
				toRawPixel(viewArea_dip.height));
	}
}
