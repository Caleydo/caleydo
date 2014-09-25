/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.IGLGraphicsTracer.ERenderPass;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.layout2.internal.GLGraphicsTracers;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.layout2.renderer.RoundedRectRenderer;
import org.caleydo.core.view.opengl.layout2.util.GLGraphicsUtils;
import org.caleydo.core.view.opengl.util.GLPrimitives;
import org.caleydo.core.view.opengl.util.gleem.ColoredVec2f;
import org.caleydo.core.view.opengl.util.gleem.TexturedVec2f;
import org.caleydo.core.view.opengl.util.spline.ITesselatedPolygon;
import org.caleydo.core.view.opengl.util.text.ETextStyle;
import org.caleydo.core.view.opengl.util.text.ITextRenderer;
import org.caleydo.data.loader.ResourceLocators.IResourceLocator;
import org.caleydo.data.loader.StackedResourceLocator;
import org.caleydo.data.loader.TextureResourceLoader;

import com.google.common.collect.ImmutableList;
import com.jogamp.opengl.util.texture.Texture;

/**
 * wrapper around opengl for simpler usage of common tasks
 *
 * @author Samuel Gratzl
 *
 */
public class GLGraphics {
	private static final float DEFAULT_Z_INC = 0.1f;
	/**
	 * direct access to {@link GL2}
	 */
	public final GL2 gl;

	public final ITextRenderer text;

	/**
	 * indicator whether the origin is in the top left or in the bottom left corner
	 */
	protected final boolean originInTopLeft;
	/**
	 * the z value to use for rendering, as only 2d coordinates are used
	 */
	protected float z = 0;

	/**
	 * the stack of {@link IResourceLocator}s
	 */
	private final StackedResourceLocator locator = new StackedResourceLocator();

	/**
	 * simple statistics
	 */
	private GLGraphicsStats stats = new GLGraphicsStats();

	/**
	 * delta time of the current pass
	 */
	private final int deltaTimeMs;

	/**
	 * gl context locals for once per context
	 */
	private final GLContextLocal local;

	private final Vec3f[] pool = { new Vec3f(), new Vec3f(), new Vec3f(), new Vec3f() };

	private final IGLGraphicsTracer tracer;

	public GLGraphics(GL2 gl, GLContextLocal local, boolean originInTopLeft, int deltaTimeMs, IGLGraphicsTracer tracer) {
		this.gl = gl;
		this.local = local;
		this.text = local.getText();
		this.deltaTimeMs = deltaTimeMs;
		this.locator.push(local.getLoader());
		this.originInTopLeft = originInTopLeft;
		this.tracer = tracer == null ? GLGraphicsTracers.DUMMY : tracer;

		textColor(Color.BLACK);
	}

	void switchTo(ERenderPass pass) {
		tracer.switchTo(pass);
	}

	boolean forceNoCache() {
		return tracer.forceRepaint();
	}


	/**
	 * @return the deltaTimeMs, see {@link #deltaTimeMs}
	 */
	public int getDeltaTimeMs() {
		return deltaTimeMs;
	}

	/**
	 * @return the stats, see {@link #stats}
	 */
	GLGraphicsStats getStats() {
		return stats;
	}

	/**
	 * see {@link #checkError(String)} with no text
	 *
	 * @return whether an error was found
	 */
	public boolean checkError() {
		return checkErrorImpl("");
	}

	/**
	 * checks for errors and prints a {@link System#err} message
	 *
	 * @param text
	 *            description message
	 * @return whether an error was found
	 */
	public boolean checkError(String text) {
		return checkErrorImpl(text);
	}

	private boolean checkErrorImpl(String text) {
		int error = gl.glGetError();
		if (error > 0) {
			StackTraceElement[] stackTrace = new Throwable().fillInStackTrace().getStackTrace();
			StackTraceElement caller = stackTrace[2];
			System.err.println(caller.toString() + " " + error + " " + glu().gluErrorString(error) + ": " + text);
			return true;
		}
		return false;
	}

	/**
	 * similar to {@link #checkError()} but just return the state without printing a message
	 *
	 * @return whether an error was found
	 */
	public boolean clearError() {
		return clearError(gl);
	}

	/**
	 * similar to {@link #checkError()} but just return the state without printing a message
	 *
	 * @return whether an error was found
	 */
	public static boolean clearError(GL2 gl) {
		int error = gl.glGetError();
		return error > 0;
	}

	/**
	 * checks for errors and prints a {@link System#err} message
	 *
	 * @param text
	 *            description message
	 * @return whether an error was found
	 */
	public static boolean checkError(GL2 gl) {
		int error = gl.glGetError();
		if (error > 0) {
			StackTraceElement[] stackTrace = new Throwable().fillInStackTrace().getStackTrace();
			StackTraceElement caller = stackTrace[1];
			GLU glu = new GLU();
			System.err.println(caller.toString() + " " + error + " " + glu.gluErrorString(error) + " ");
			return true;
		}
		return false;
	}

	/**
	 * returns whether we are currently in the picking pass
	 *
	 * @return
	 */
	public boolean isPickingPass() {
		return GLGraphicsUtils.isPickingPass(gl);
	}

	// #############

	/**
	 * pushes another {@link IResourceLocator}, e.g. a local one for plugin specific textures
	 *
	 * @param locator
	 * @return
	 */
	public GLGraphics pushResourceLocator(IResourceLocator locator) {
		this.locator.push(locator);
		return this;
	}

	/**
	 * opposite of {@link #pushResourceLocator(IResourceLocator)}
	 *
	 * @return
	 */
	public GLGraphics popResourceLocator() {
		locator.pop();
		return this;
	}

	// ############## color setters

	public GLGraphics color(Color color) {
		return color(color.r, color.g, color.b, color.a);
	}

	public GLGraphics color(float gray) {
		return color(gray, gray, gray);
	}

	public GLGraphics color(float r, float g, float b) {
		return color(r, g, b, 1);
	}

	public GLGraphics color(float r, float g, float b, float a) {
		tracer.color(r, g, b, a);
		gl.glColor4f(r, g, b, a);
		return this;
	}

	public GLGraphics color(String hex) {
		return color(new Color(hex));
	}

	public GLGraphics color(float[] rgba) {
		if (rgba.length == 3)
			return color(rgba[0], rgba[1], rgba[2], 1);
		else
			return color(rgba[0], rgba[1], rgba[2], rgba[3]);
	}

	public Color getColor() {
		float[] rgba = new float[4];
		gl.glGetFloatv(GL2ES1.GL_CURRENT_COLOR, rgba, 0);
		return new Color(rgba);
	}

	public GLGraphics textColor(Color color) {
		tracer.textColor(color);
		text.setColor(color);
		local.getText_bold().setColor(color);
		local.getText_italic().setColor(color);
		return this;
	}

	public GLGraphics textColor(float r, float g, float b) {
		return textColor(r, g, b, 1);
	}

	public GLGraphics textColor(float r, float g, float b, float a) {
		return textColor(new Color(r, g, b, a));
	}

	public GLGraphics textColor(float[] rgba) {
		if (rgba.length == 3)
			return textColor(rgba[0], rgba[1], rgba[2], 1);
		else
			return textColor(rgba[0], rgba[1], rgba[2], rgba[3]);
	}

	// ######### attribute setters

	public GLGraphics lineWidth(float lineWidth) {
		tracer.lineWidth(lineWidth);
		gl.glLineWidth(lineWidth);
		return this;
	}

	public GLGraphics pointSize(float pointSize) {
		tracer.pointSize(pointSize);
		gl.glPointSize(pointSize);
		return this;
	}

	public final GLGraphics lineStippled(boolean enable) {
		return lineStippled(enable ? 2 : -1, 0xAAAA);
	}

	/**
	 *
	 * @param factor
	 *            <= 0 to disable
	 * @param pattern
	 * @return
	 */
	public GLGraphics lineStippled(int factor, int pattern) {
		if (factor > 0) {
			gl.glEnable(GL2.GL_LINE_STIPPLE);
			gl.glLineStipple(factor, (short) pattern);
		} else {
			gl.glDisable(GL2.GL_LINE_STIPPLE);
		}
		tracer.lineStippled(factor, pattern);
		return this;
	}

	// ######### picking setters

	public GLGraphics pushName(int id) {
		gl.glPushName(id);
		return this;
	}

	public GLGraphics popName() {
		gl.glPopName();
		return this;
	}

	/**
	 * runs the given {@link IRenderProcedure} with the pushed name
	 *
	 * @param id
	 *            the name to push
	 * @param toRun
	 *            the thing to run within the pushed name environment
	 * @return
	 */
	public GLGraphics withName(int id, IGLRenderer renderer, float w, float h, GLElement parent) {
		pushName(id);
		renderer.render(this, w, h, parent);
		popName();
		return this;
	}

	/**
	 * renders a filled rect
	 */
	public GLGraphics fillRect(float x, float y, float w, float h) {
		return renderRect(true, x, y, w, h);
	}

	/**
	 * renders a filled rect
	 */
	public GLGraphics fillRect(Rect bounds) {
		return fillRect(bounds.x(), bounds.y(), bounds.width(), bounds.height());
	}

	private GLGraphics renderRect(boolean fill, float x, float y, float w, float h) {
		if (isInvalidOrZero(w) || isInvalidOrZero(h) || isInvalid(x) || isInvalid(y))
			return this;
		tracer.renderRect(fill, x, y, w, h);
		stats.incRect();
		gl.glBegin(fill ? GL2.GL_POLYGON : GL.GL_LINE_LOOP);
		gl.glVertex3f(x, y, z);
		gl.glVertex3f(x + w, y, z);
		gl.glVertex3f(x + w, y + h, z);
		gl.glVertex3f(x, y + h, z);
		gl.glEnd();
		return this;
	}

	public GLGraphics fillRoundedRect(float x, float y, float w, float h, float radius) {
		int segments;
		if (radius < 4)
			segments = 0;
		else if (radius < 10)
			segments = 2;
		else
			segments = 8;
		return fillRoundedRect(x, y, w, h, radius, segments);
	}

	public GLGraphics fillRoundedRect(float x, float y, float w, float h, float radius, int segments) {
		if (isInvalidOrZero(w) || isInvalidOrZero(h))
			return this;
		tracer.renderRoundedRect(true, x, y, w, h, radius, segments);

		int count = RoundedRectRenderer.render(this, x, y, w, h, radius, segments, RoundedRectRenderer.FLAG_FILL
				| RoundedRectRenderer.FLAG_ALL);
		stats.incRoundedRect(count);
		return this;
	}

	/**
	 * renders a texture within the given rect
	 */
	public GLGraphics fillImage(String texture, float x, float y, float w, float h) {
		tracer.fillImage(texture, locator, x, y, w, h);
		return fillImageImpl(getTexture(texture), x, y, w, h, Color.WHITE);
	}

	/**
	 * renders a texture within the given rect
	 */
	public GLGraphics fillImage(URL texture, float x, float y, float w, float h) {
		tracer.fillImage(texture, x, y, w, h);
		return fillImageImpl(getTexture(texture), x, y, w, h, Color.WHITE);
	}

	public Texture getTexture(String texture) {
		return local.getTextures().get(texture, new TextureResourceLoader(locator));
	}

	public Texture getTexture(URL texture) {
		return local.getTextures().get(texture);
	}

	/**
	 * see {@link #fillImage(String, float, float, float, float)} for a texture object
	 */
	public GLGraphics fillImage(Texture texture, float x, float y, float w, float h) {
		return fillImage(texture, x, y, w, h, Color.WHITE);
	}

	/**
	 * see {@link #fillImage(Texture, float, float, float, float)} but with a dedicated static color to use
	 *
	 * @param color
	 * @return
	 */
	public GLGraphics fillImage(Texture texture, float x, float y, float w, float h, Color color) {
		tracer.fillImage(texture, x, y, w, h, color);
		return fillImageImpl(texture, x, y, w, h, color);
	}

	private GLGraphics fillImageImpl(Texture texture, float x, float y, float w, float h, Color color) {
		if (isInvalidOrZero(w) || isInvalidOrZero(h) || isInvalid(x) || isInvalid(y))
			return this;
		stats.incImage();

		Vec3f lowerLeftCorner = pool[0];
		lowerLeftCorner.set(x, y, z);
		Vec3f lowerRightCorner = pool[1];
		lowerRightCorner.set(x + w, y, z);
		Vec3f upperRightCorner = pool[2];
		upperRightCorner.set(x + w, y + h, z);
		Vec3f upperLeftCorner = pool[3];
		upperLeftCorner.set(x, y + h, z);

		if (originInTopLeft)
			local.getTextures().renderTexture(gl, texture, upperLeftCorner, upperRightCorner, lowerRightCorner,
					lowerLeftCorner, color);
		else
			local.getTextures().renderTexture(gl, texture, lowerLeftCorner, lowerRightCorner, upperRightCorner,
					upperLeftCorner, color);
		return this;
	}

	public GLGraphics fillPolygon(Vec2f... points) {
		if (points.length <= 0)
			return this;
		return fillPolygon(Arrays.asList(points));
	}

	public GLGraphics fillPolygon(Iterable<Vec2f> points) {
		return render(GL2.GL_POLYGON, points);
	}

	public GLGraphics fillPolygon(ITesselatedPolygon polygon) {
		if (polygon.size() <= 0)
			return this;
		tracer.renderPolygon(true, polygon);
		stats.incPath(polygon.size());
		polygon.fill(this, local.getTesselationRenderer());
		return this;
	}

	public GLU glu() {
		return local.getGlu();
	}

	public GLGraphics fillCircle(float x, float y, float radius) {
		return fillCircle(x, y, radius, 16);
	}

	public GLGraphics fillCircle(float x, float y, float radius, int numSlices) {
		return renderCircle(true, x, y, radius, numSlices);
	}

	private GLGraphics renderCircle(boolean fill, float x, float y, float radius, int numSlices) {
		if (isInvalidOrZero(radius) || isInvalid(x) || isInvalid(y))
			return this;
		tracer.renderCircle(fill, x, y, radius, numSlices);
		stats.incCircle(numSlices);
		gl.glTranslatef(x, y, z);
		if (fill)
			GLPrimitives.renderCircle(glu(), radius, numSlices);
		else
			GLPrimitives.renderCircleBorder(glu(), radius, numSlices);
		gl.glTranslatef(-x, -y, -z);
		return this;
	}

	public GLGraphics drawCircle(float x, float y, float radius) {
		return drawCircle(x, y, radius, 16);
	}

	public GLGraphics drawCircle(float x, float y, float radius, int numSlices) {
		return renderCircle(false, x, y, radius, numSlices);
	}

	/**
	 * draws the given text within the given bounds
	 */
	public GLGraphics drawText(String text, float x, float y, float w, float h) {
		return drawText(text, x, y, w, h, VAlign.LEFT);
	}

	/**
	 * see {@link #drawText(String, float, float, float, float)} with a dedicated horizontal alignment
	 */
	public GLGraphics drawText(String text, float x, float y, float w, float h, VAlign valign) {
		return drawText(text, x, y, w, h, valign, ETextStyle.PLAIN);
	}

	public GLGraphics drawText(String text, float x, float y, float w, float h, VAlign valign, ETextStyle style) {
		return drawRotatedText(text, x, y, w, h, valign, style, 0);
	}

	public GLGraphics drawRotatedText(String text, float x, float y, float textWidth, float textHeight, VAlign valign, ETextStyle style,
			float angle) {
		if (isInvalidOrZero(textWidth) || isInvalidOrZero(textHeight) || isInvalid(x) || isInvalid(y))
			return this;
		if (text == null || text.trim().isEmpty())
			return this;
		if (text.indexOf('\n') < 0) {
			if (angle == 0) {
				tracer.drawText(Collections.singletonList(text), x, y, textWidth, textHeight, 0, valign, style);
				return drawSingleTextLine(text, x, y, textWidth, textHeight, valign, style);
			} else {
				tracer.drawRotatedText(Collections.singletonList(text), x, y, textWidth, textHeight, 0, valign, style,
						angle);
				save().move(x, y);
				gl.glRotatef(angle, 0, 0, 1);
				drawSingleTextLine(text, 0, 0, textWidth, textHeight, valign, style);
				restore();
				return this;
			}
		} else {
			return drawRotatedText(Arrays.asList(text.split("\n")), x, y, textWidth, textHeight, 0, valign, style, angle);
		}
	}


	private GLGraphics drawSingleTextLine(String text, float x, float y, float textWidth, float textHeight, VAlign valign,
			ETextStyle style) {
		stats.incText(text.length());
		if (isInvalidOrZero(textWidth) || isInvalidOrZero(textHeight) || isInvalid(x) || isInvalid(y))
			return this;
		ITextRenderer font = selectFont(style);
		if (originInTopLeft && !font.isOriginTopLeft()) {
			gl.glPushMatrix();
			gl.glTranslatef(0, y + textHeight, 0);
			y = 0;
			gl.glScalef(1, -1, 1);
		}
		float hi = textHeight;
		float xi = x;
		switch (valign) {
		case CENTER:
			xi += textWidth * 0.5f - Math.min(font.getTextWidth(text, hi), textWidth) * 0.5f;
			break;
		case RIGHT:
			xi += textWidth - Math.min(font.getTextWidth(text, hi), textWidth);
			break;
		default:
			break;
		}
		font.renderTextInBounds(gl, text, xi, y, z + 0.25f, textWidth, hi);

		if (font.isDirty())
			stats.dirtyTextTexture();

		if (originInTopLeft && !font.isOriginTopLeft())
			gl.glPopMatrix();
		return this;
	}

	public GLGraphics drawText(List<String> lines, float x, float y, float w, float h, float lineSpace, VAlign valign,
			ETextStyle style) {
		return drawTextImpl(lines, x, y, w, h, lineSpace, valign, style);
	}

	private GLGraphics drawTextImpl(List<String> lines, float x, float y, float w, float h, float lineSpace,
			VAlign valign, ETextStyle style) {
		if (isInvalidOrZero(w) || isInvalidOrZero(h) || isInvalid(x) || isInvalid(y) || isInvalid(lineSpace))
			return this;
		if (lines == null || lines.isEmpty())
			return this;
		tracer.drawText(lines, x, y, w, h, lineSpace, valign, style);
		ITextRenderer font = selectFont(style);
		if (originInTopLeft && !font.isOriginTopLeft()) {
			gl.glPushMatrix();
			gl.glTranslatef(0, y + h, 0);
			y = 0;
			gl.glScalef(1, -1, 1);
		}
		float hi = (h - lineSpace * (lines.size() - 1)) / lines.size();
		for (ListIterator<String> it = lines.listIterator(lines.size()); it.hasPrevious();) {
			String text = it.previous();
			stats.incText(text.length());
			float xi = x;
			switch (valign) {
			case CENTER:
				xi += w * 0.5f - Math.min(font.getTextWidth(text, hi), w) * 0.5f;
				break;
			case RIGHT:
				xi += w - Math.min(font.getTextWidth(text, hi), w);
				break;
			default:
				break;
			}
			font.renderTextInBounds(gl, text, xi, y, z + 0.25f, w, hi);
			y += lineSpace + hi;
		}

		if (font.isDirty())
			stats.dirtyTextTexture();

		if (originInTopLeft && !font.isOriginTopLeft())
			gl.glPopMatrix();
		return this;
	}

	public GLGraphics drawRotatedText(List<String> lines, float x, float y, float w, float h, float lineSpace,
			VAlign valign, ETextStyle style, float angle) {
		if (angle == 0) {
			return drawTextImpl(lines, x, y, w, h, lineSpace, valign, style);
		} else {
			tracer.drawRotatedText(lines, x, y, w, h, lineSpace, valign, style, angle);
			save();
			gl.glRotatef(angle, 0, 0, 1);
			drawTextImpl(lines, x, y, w, h, lineSpace, valign, style);
			restore();
		}
		return this;
	}

	private ITextRenderer selectFont(ETextStyle style) {
		if (style == null)
			return text;
		switch (style) {
		case BOLD:
			return local.getText_bold();
		case ITALIC:
			return local.getText_italic();
		default:
			return text;
		}
	}

	/**
	 * @param w
	 * @return checks if the value is invalid
	 */
	private static boolean isInvalidOrZero(float v) {
		return v == 0 || Float.isNaN(v) || Float.isInfinite(v);
	}

	private static boolean isInvalid(float v) {
		return Float.isNaN(v) || Float.isInfinite(v);
	}

	/**
	 * see {@link #drawText(String, float, float, float, float)}
	 */
	public GLGraphics drawText(ILabeled text, float x, float y, float w, float h) {
		if (text == null)
			return this;
		return drawText(text.getLabel(), x, y, w, h);
	}

	/**
	 * render an empty rect, i.e. just the frame
	 */
	public GLGraphics drawRect(float x, float y, float w, float h) {
		return renderRect(false, x, y, w, h);
	}

	public GLGraphics drawRoundedRect(float x, float y, float w, float h, float radius) {
		return drawRoundedRect(x, y, w, h, radius, guessRoundedSegments(radius));
	}

	/**
	 * guesses the number of segments given the radius
	 *
	 * @param radius
	 * @return
	 */
	private int guessRoundedSegments(float radius) {
		int segments;
		if (radius < 4)
			segments = 0;
		else if (radius < 10)
			segments = 2;
		else
			segments = 4;
		return segments;
	}

	public GLGraphics drawRoundedRect(float x, float y, float w, float h, float radius, int segments) {
		int count = RoundedRectRenderer.render(this, x, y, w, h, radius, segments, RoundedRectRenderer.FLAG_ALL);
		stats.incRoundedRect(count);
		return this;
	}

	/**
	 * renders a line between the two given points
	 */
	public GLGraphics drawLine(float x, float y, float x2, float y2) {
		tracer.drawLine(x, y, x2, y2);
		stats.incLine();
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(x, y, z);
		gl.glVertex3f(x2, y2, z);
		gl.glEnd();
		return this;
	}

	/**
	 * renders the diagonal line within the given rect
	 */
	public GLGraphics drawDiagonalLine(float x, float y, float w, float h) {
		return drawLine(x, y, x + w, y + h);
	}

	/**
	 * renders a set of points as lines
	 *
	 * @param points
	 * @param closed
	 *            close the path?
	 * @return
	 */
	public GLGraphics drawPath(Iterable<Vec2f> points, boolean closed) {
		return render(closed ? GL.GL_LINE_LOOP : GL.GL_LINE_STRIP, points);
	}

	/**
	 * see {@link #drawPath(Iterable, boolean)} but with varargs
	 *
	 * @param closed
	 * @param points
	 * @return
	 */
	public GLGraphics drawPath(boolean closed, Vec2f... points) {
		return render(closed ? GL.GL_LINE_LOOP : GL.GL_LINE_STRIP, Arrays.asList(points));
	}

	public GLGraphics drawPath(ITesselatedPolygon polygon) {
		stats.incPath(polygon.size());
		tracer.renderPolygon(false, polygon);
		polygon.draw(this);
		return this;
	}

	/**
	 * draws a single point
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public GLGraphics drawPoint(float x, float y) {
		if (isInvalid(x) || isInvalid(y))
			return this;
		tracer.render(GL.GL_POINTS, ImmutableList.of(new Vec2f(x, y)));

		gl.glBegin(GL.GL_POINTS);
		gl.glVertex3f(x, y, z);
		gl.glEnd();
		return this;
	}

	/**
	 * draws a set of points, see {@link #drawPoints(Iterable)} with varargs
	 *
	 * @param points
	 * @return
	 */
	public GLGraphics drawPoints(Vec2f... points) {
		if (points.length == 0)
			return this;
		return render(GL.GL_POINTS, Arrays.asList(points));
	}

	/**
	 * draws a set of points
	 *
	 * @param points
	 * @return
	 */
	public GLGraphics drawPoints(Iterable<Vec2f> points) {
		return render(GL.GL_POINTS, points);
	}

	private GLGraphics render(int mode, Iterable<Vec2f> points) {
		if (points instanceof Collection && ((Collection<?>) points).isEmpty())
			return this;
		tracer.render(mode, points);
		int count = 0;
		gl.glBegin(mode);
		for (Vec2f p : points) {
			count++;
			if (p instanceof ColoredVec2f)
				color(((ColoredVec2f) p).getColor());
			if (p instanceof TexturedVec2f) {
				Vec2f t = ((TexturedVec2f) p).getTexCoords();
				if (t != null)
					gl.glTexCoord2f(t.x(), t.y());
			}
			gl.glVertex3f(p.x(), p.y(), z);
		}
		gl.glEnd();
		stats.incPath(count);
		return this;
	}

	// ############## transformation

	/**
	 * increases the z value with {@link #DEFAULT_Z_INC}
	 *
	 * @return
	 */
	public GLGraphics incZ() {
		return incZ(DEFAULT_Z_INC);
	}

	/**
	 * increases the z value with a defined delta value
	 *
	 * @param zDelta
	 * @return
	 */
	public final GLGraphics incZ(float zDelta) {
		tracer.incZ(zDelta);
		this.z += zDelta;
		return this;
	}

	/**
	 * decreases the z value iwth {@link #DEFAULT_Z_INC}
	 *
	 * @return
	 */
	public GLGraphics decZ() {
		return incZ(-DEFAULT_Z_INC);
	}

	/**
	 * @return returns the current z value
	 */
	public float z() {
		return z;
	}

	/**
	 * see {@link #move(float, float)}
	 */
	public GLGraphics move(Vec2f xy) {
		return move(xy.x(), xy.y());
	}

	/**
	 * shortcut to {@link GL2#glTranslatef(float, float, float)
	 */
	public GLGraphics move(float x, float y) {
		tracer.move(x, y);
		if (x != 0 || y != 0)
			gl.glTranslatef(x, y, 0);
		return this;
	}

	public AdvancedGraphics asAdvanced() {
		return new AdvancedGraphics();
	}

	/**
	 * runs the given {@link IRenderProcedure} a moved environment by x and y
	 *
	 * @return
	 */
	public GLGraphics withMove(float x, float y, IGLRenderer renderer, float w, float h, GLElement parent) {
		move(x, y);
		renderer.render(this, w, h, parent);
		move(-x, -y);
		return this;
	}

	/**
	 * shortcut to {@link GL2#glPushMatrix()}
	 */
	public GLGraphics save() {
		tracer.save();
		gl.glPushMatrix();
		return this;
	}

	/**
	 * shortcut to {@link GL2#glPopMatrix()}
	 */
	public GLGraphics restore() {
		tracer.restore();
		gl.glPopMatrix();
		return this;
	}

	/**
	 * runs the given procedure, in a save-restore environment
	 */
	public GLGraphics withSaveRestore(IGLRenderer renderer, float w, float h, GLElement parent) {
		save();
		renderer.render(this, w, h, parent);
		restore();
		return this;
	}

	public class AdvancedGraphics {
		public AdvancedGraphics scale(float x, float y) {
			tracer.scale(x, y);
			gl.glScalef(x, y, 1);
			return this;
		}

		public AdvancedGraphics rotate(float angle) {
			tracer.rotate(angle);
			gl.glRotatef(angle, 0, 0, 1);
			return this;
		}

		public void fillImage(URL texture, Rect pos, Rect texel, ETextureWrappingMode wrapS,
 ETextureWrappingMode wrapT) {
			Texture tex = getTexture(texture);
			gl.glPushAttrib(GL2.GL_TEXTURE_BIT);
			tex.enable(gl);
			tex.bind(gl);
			tex.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_S, wrapS.asOpenGL());
			tex.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_T, wrapT.asOpenGL());
			fillPolygon(new TexturedVec2f(pos.xy(), texel.xy()), new TexturedVec2f(pos.x2y(), texel.x2y()),
					new TexturedVec2f(pos.x2y2(), texel.x2y2()), new TexturedVec2f(pos.xy2(), texel.xy2()));
			tex.disable(gl);
			gl.glPopAttrib();
		}
	}

	public enum ETextureWrappingMode {
		REPEAT, MIRROR_REPEAT, CLAMP_TO_EDGE;

	/**
	 * @return
	 */
		public int asOpenGL() {
			switch (this) {
			case REPEAT:
				return GL.GL_REPEAT;
			case MIRROR_REPEAT:
				return GL2.GL_MIRRORED_REPEAT;
			case CLAMP_TO_EDGE:
				return GL2.GL_CLAMP_TO_EDGE;
			}
			throw new IllegalStateException("unknown this");
		}

	}
}
