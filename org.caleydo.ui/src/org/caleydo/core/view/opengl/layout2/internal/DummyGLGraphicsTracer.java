/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.internal;

import gleem.linalg.Vec2f;

import java.net.URL;
import java.util.List;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.IGLGraphicsTracer;
import org.caleydo.core.view.opengl.util.spline.ITesselatedPolygon;
import org.caleydo.core.view.opengl.util.text.ETextStyle;
import org.caleydo.data.loader.ResourceLocators.IResourceLocator;

import com.jogamp.opengl.util.texture.Texture;

/**
 * @author Samuel Gratzl
 *
 */
public class DummyGLGraphicsTracer implements IGLGraphicsTracer {

	DummyGLGraphicsTracer() {

	}

	@Override
	public void color(float r, float g, float b, float a) {

	}

	@Override
	public void textColor(Color color) {

	}

	@Override
	public void lineWidth(float lineWidth) {

	}

	@Override
	public void pointSize(float pointSize) {

	}

	@Override
	public void lineStippled(int factor, int pattern) {

	}

	@Override
	public void renderRect(boolean fill, float x, float y, float w, float h) {

	}

	@Override
	public void renderRoundedRect(boolean fill, float x, float y, float w, float h, float radius, int segments) {

	}

	@Override
	public void renderCircle(boolean fill, float x, float y, float radius, int numSlices) {

	}

	@Override
	public void renderPolygon(boolean fill, ITesselatedPolygon polygon) {

	}

	@Override
	public void render(int mode, Iterable<Vec2f> points) {

	}

	@Override
	public void fillImage(String texture, IResourceLocator locator, float x, float y, float w, float h) {

	}

	@Override
	public void fillImage(URL texture, float x, float y, float w, float h) {

	}

	@Override
	public void fillImage(Texture texture, float x, float y, float w, float h, Color color) {

	}

	@Override
	public void drawText(List<String> lines, float x, float y, float w, float h, float lineSpace, VAlign valign,
			ETextStyle style) {

	}

	@Override
	public void drawRotatedText(List<String> lines, float x, float y, float w, float h, float lineSpace, VAlign valign,
			ETextStyle style, float angle) {

	}

	@Override
	public void drawLine(float x, float y, float x2, float y2) {

	}

	@Override
	public void incZ(float zDelta) {

	}

	@Override
	public void move(float x, float y) {

	}

	@Override
	public void rotate(float angle) {

	}

	@Override
	public void scale(float x, float y) {

	}

	@Override
	public void save() {

	}

	@Override
	public void restore() {

	}
	@Override
	public void switchTo(ERenderPass pass) {

	}

	@Override
	public boolean forceRepaint() {
		return false;
	}

}
