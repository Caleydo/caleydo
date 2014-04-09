/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import gleem.linalg.Vec2f;

import java.net.URL;
import java.util.List;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.util.spline.ITesselatedPolygon;
import org.caleydo.core.view.opengl.util.text.ETextStyle;
import org.caleydo.data.loader.ResourceLocators.IResourceLocator;

import com.jogamp.opengl.util.texture.Texture;

/**
 * wrapper around opengl for simpler usage of common tasks
 *
 * @author Samuel Gratzl
 *
 */
public interface IGLGraphicsTracer {

	void color(float r, float g, float b, float a);

	void textColor(Color color);

	void lineWidth(float lineWidth);

	void pointSize(float pointSize);

	void lineStippled(int factor, int pattern);

	void renderRect(boolean fill, float x, float y, float w, float h);

	void renderRoundedRect(boolean fill, float x, float y, float w, float h, float radius, int segments);

	void renderCircle(boolean fill, float x, float y, float radius, int numSlices);

	void renderPolygon(boolean fill, ITesselatedPolygon polygon);
	void render(int mode, Iterable<Vec2f> points);

	void fillImage(String texture, IResourceLocator locator, float x, float y, float w, float h);
	void fillImage(URL texture, float x, float y, float w, float h);
	void fillImage(Texture texture, float x, float y, float w, float h, Color color);

	void drawText(List<String> lines, float x, float y, float w, float h, float lineSpace, VAlign valign, ETextStyle style);

	void drawRotatedText(List<String> lines, float x, float y, float w, float h, float lineSpace, VAlign valign, ETextStyle style, float angle);

	void drawLine(float x, float y, float x2, float y2);

	void incZ(float zDelta);

	void move(float x, float y);

	void save();

	void restore();

	boolean forceRepaint();

	void switchTo(ERenderPass pass);

	public enum ERenderPass {
		PICKING, RENDERING, DONE
	}

	public interface IFactory {
		IGLGraphicsTracer create(IView view);
	}

}
