package org.caleydo.core.view.opengl.layout2;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.awt.Color;
import java.util.Arrays;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.util.color.IColor;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.renderer.RoundedRectRenderer;
import org.caleydo.core.view.opengl.util.GLPrimitives;
import org.caleydo.core.view.opengl.util.text.ITextRenderer;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.data.loader.ResourceLocators.IResourceLocator;
import org.caleydo.data.loader.StackedResourceLocator;

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

	public final TextureManager textures;

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

	private GLU glu = null; // lazy

	private GLGraphicsStats stats = new GLGraphicsStats();

	public GLGraphics(GL2 gl, ITextRenderer text, TextureManager textures, IResourceLocator loader,
			boolean originInTopLeft) {
		this.gl = gl;
		this.text = text;
		text.setColor(Color.BLACK);
		this.locator.push(loader);
		this.textures = textures;
		this.originInTopLeft = originInTopLeft;
	}

	/**
	 * @return the stats, see {@link #stats}
	 */
	GLGraphicsStats getStats() {
		return stats;
	}

	/**
	 * @return the text, see {@link #text}
	 */
	public ITextRenderer getText() {
		return text;
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
			glu.destroy();
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
		int[] r = new int[1];
		gl.glGetIntegerv(GL2.GL_RENDER_MODE, r, 0);
		return r[0] == GL2.GL_SELECT;
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

	public GLGraphics color(IColor color) {
		return color(color.getRGBA());
	}

	public GLGraphics color(Color color) {
		return color(color.getComponents(null));
	}

	public GLGraphics color(float grey) {
		return color(grey, grey, grey);
	}
	public GLGraphics color(float r, float g, float b) {
		return color(r, g, b, 1);
	}

	public GLGraphics color(float r, float g, float b, float a) {
		gl.glColor4f(r, g, b, a);
		return this;
	}

	public GLGraphics color(float[] rgba) {
		if (rgba.length == 3)
			return color(rgba[0], rgba[1], rgba[2], 1);
		else
			return color(rgba[0], rgba[1], rgba[2], rgba[3]);
	}

	public GLGraphics textColor(IColor color) {
		return textColor(color.getRGBA());
	}

	public GLGraphics textColor(Color color) {
		text.setColor(color);
		return this;
	}

	public GLGraphics textColor(float r, float g, float b) {
		return textColor(r, g, b, 1);
	}

	public GLGraphics textColor(float r, float g, float b, float a) {
		text.setColor(r, g, b, a);
		return this;
	}

	public GLGraphics textColor(float[] rgba) {
		if (rgba.length == 3)
			return textColor(rgba[0], rgba[1], rgba[2], 1);
		else
			return textColor(rgba[0], rgba[1], rgba[2], rgba[3]);
	}

	// ######### attribute setters

	public GLGraphics lineWidth(float lineWidth) {
		gl.glLineWidth(lineWidth);
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
	public GLGraphics withName(int id, IRenderProcedure toRun) {
		pushName(id);
		toRun.render(this);
		popName();
		return this;
	}

	/**
	 * renders a filled rect
	 */
	public GLGraphics fillRect(float x, float y, float w, float h) {
		return renderRect(true, x, y, w, h);
	}

	public GLGraphics renderRect(boolean fill, float x, float y, float w, float h) {
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
		int count = RoundedRectRenderer.render(this, x, y, w, h, radius, segments, RoundedRectRenderer.FLAG_FILL
				| RoundedRectRenderer.FLAG_ALL);
		stats.incRoundedRect(count);
		return this;
	}

	/**
	 * renders a texture within the given rect
	 */
	public GLGraphics fillImage(String texture, float x, float y, float w, float h) {
		return fillImage(getTexture(texture), x, y, w, h);
	}

	public Texture getTexture(String texture) {
		return textures.get(texture, new ResourceLoader(locator));
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
		stats.incImage();
		Vec3f lowerLeftCorner = new Vec3f(x, y, z);
		Vec3f lowerRightCorner = new Vec3f(x + w, y, z);
		Vec3f upperRightCorner = new Vec3f(x + w, y + h, z);
		Vec3f upperLeftCorner = new Vec3f(x, y + h, z);

		org.caleydo.core.util.color.Color tmp = new org.caleydo.core.util.color.Color(color.getRed(), color.getGreen(),
				color.getBlue(), color.getAlpha());

		if (originInTopLeft)
			textures.renderTexture(gl, texture, upperLeftCorner, upperRightCorner, lowerRightCorner, lowerLeftCorner,
					tmp.r, tmp.g, tmp.b, tmp.a);
		else
			textures.renderTexture(gl, texture, lowerLeftCorner, lowerRightCorner, upperRightCorner, upperLeftCorner,
					tmp.r, tmp.g, tmp.b, tmp.a);
		return this;
	}

	public GLGraphics fillPolygon(Vec2f... points) {
		return fillPolygon(Arrays.asList(points));
	}

	public GLGraphics fillPolygon(Iterable<Vec2f> points) {
		return render(GL2.GL_POLYGON, points);
	}

	public GLU glu() {
		if (this.glu != null)
			return this.glu;
		this.glu = new GLU();
		return this.glu;
	}

	public GLGraphics fillCircle(float x, float y, float radius) {
		return fillCircle(x, y, radius, 16);
	}

	public GLGraphics fillCircle(float x, float y, float radius, int numSlices) {
		return renderCircle(true, x, y, radius, numSlices);
	}

	private GLGraphics renderCircle(boolean fill, float x, float y, float radius, int numSlices) {
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
		if (text == null)
			return this;
		stats.incText(text.length());
		if (originInTopLeft) {
			gl.glPushMatrix();
			gl.glTranslatef(0, y + h, 0);
			y = 0;
			gl.glScalef(1, -1, 1);
		}
		float textWidth = Math.min(this.text.getTextWidth(text, h), w);
		switch (valign) {
		case CENTER:
			x += w * 0.5f - textWidth * 0.5f;
			break;
		case RIGHT:
			x += w - textWidth;
			break;
		default:
			break;
		}
		this.text.renderTextInBounds(gl, text, x, y, z + 0.25f, w, h);

		if (originInTopLeft)
			gl.glPopMatrix();
		return this;
	}

	/**
	 * see {@link #drawText(String, float, float, float, float)}
	 */
	public GLGraphics drawText(ILabelProvider text, float x, float y, float w, float h) {
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
	 * returns a set of points as lines
	 *
	 * @param points
	 * @param closed
	 *            close the path?
	 * @return
	 */
	public GLGraphics drawPath(Iterable<Vec2f> points, boolean closed) {
		return render(closed ? GL.GL_LINE_LOOP : GL.GL_LINE_STRIP, points);
	}

	public GLGraphics drawPath(boolean closed, Vec2f... points) {
		return render(closed ? GL.GL_LINE_LOOP : GL.GL_LINE_STRIP, Arrays.asList(points));
	}

	private GLGraphics render(int mode, Iterable<Vec2f> points) {
		int count = 0;
		gl.glBegin(mode);
		for (Vec2f p : points) {
			count++;
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
	public GLGraphics incZ(float zDelta) {
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
		gl.glTranslatef(x, y, 0);
		return this;
	}

	/**
	 * runs the given {@link IRenderProcedure} a moved environment by x and y
	 *
	 * @return
	 */
	public GLGraphics withMove(float x, float y, IRenderProcedure toRun) {
		move(x, y);
		toRun.render(this);
		move(-x, -y);
		return this;
	}

	/**
	 * shortcut to {@link GL2#glPushMatrix()}
	 */
	public GLGraphics save() {
		gl.glPushMatrix();
		return this;
	}

	/**
	 * shortcut to {@link GL2#glPopMatrix()}
	 */
	public GLGraphics restore() {
		gl.glPopMatrix();
		return this;
	}

	/**
	 * runs the given procedure, in a save-restore environment
	 */
	public GLGraphics withSaveRestore(IRenderProcedure toRun) {
		save();
		toRun.render(this);
		restore();
		return this;
	}

	public interface IRenderProcedure {
		public void render(GLGraphics g);
	}

	/**
	 *
	 */
	void destroy() {
		if (this.glu != null) {
			this.glu.destroy();
			this.glu = null;
		}
	}
}