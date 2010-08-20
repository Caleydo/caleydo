package org.caleydo.core.view.opengl.util.text;

import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.nio.IntBuffer;

import javax.media.opengl.GL;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.j2d.TextRenderer;

/**
 * Wrapper for TextRenderer that provides methods to draw text with a specified minimum size (no matter what's
 * the current size of the view).
 * 
 * @author Christian Partl
 */
public class CaleydoTextRenderer
	extends TextRenderer {

	static private final String REFERENCE_TEXT = "Reference Text";

	private Rectangle2D referenceBounds;

	/**
	 * Constructor.
	 * 
	 * @param font
	 */
	public CaleydoTextRenderer(Font font) {
		super(font);
		referenceBounds = getBounds(REFERENCE_TEXT);
	}

	/**
	 * Constructor.
	 * 
	 * @param font
	 * @param mipmap
	 */
	public CaleydoTextRenderer(Font font, boolean mipmap) {
		super(font, mipmap);
		referenceBounds = getBounds(REFERENCE_TEXT);
	}

	/**
	 * Constructor.
	 * 
	 * @param font
	 * @param antialiased
	 * @param useFractionalMetrics
	 */
	public CaleydoTextRenderer(Font font, boolean antialiased, boolean useFractionalMetrics) {
		super(font, antialiased, useFractionalMetrics);
		referenceBounds = getBounds(REFERENCE_TEXT);
	}

	/**
	 * Constructor.
	 * 
	 * @param font
	 * @param antialiased
	 * @param useFractionalMetrics
	 * @param renderDelegate
	 */
	public CaleydoTextRenderer(Font font, boolean antialiased, boolean useFractionalMetrics,
		TextRenderer.RenderDelegate renderDelegate) {
		super(font, antialiased, useFractionalMetrics, renderDelegate);
		referenceBounds = getBounds(REFERENCE_TEXT);
	}

	/**
	 * Constructor.
	 * 
	 * @param font
	 * @param antialiased
	 * @param useFractionalMetrics
	 * @param renderDelegate
	 * @param mipmap
	 */
	public CaleydoTextRenderer(Font font, boolean antialiased, boolean useFractionalMetrics,
		TextRenderer.RenderDelegate renderDelegate, boolean mipmap) {
		super(font, antialiased, useFractionalMetrics, renderDelegate, mipmap);
		referenceBounds = getBounds(REFERENCE_TEXT);
	}

	/**
	 * Convenience method to render text with a specified minimum size without having to call begin3DRendering
	 * and end3DRendering.
	 * 
	 * @param gl
	 *            GL context.
	 * @param text
	 *            Text to render
	 * @param x
	 *            X coordinate of the text.
	 * @param y
	 *            Y coordinate of the text.
	 * @param z
	 *            Z coordinate of the text.
	 * @param scaling
	 *            Factor the text is scaled with.
	 * @param minSize
	 *            Minimum size of the text. Note that the minimum size is scaled with the specified scaling
	 *            vector.
	 */
	public void renderText(GL gl, String text, float x, float y, float z, float scaling, int minSize) {

		scaling = calculateScaling(gl, scaling, minSize);

		begin3DRendering();
		draw3D(text, x, y, z, scaling);
		flush();
		end3DRendering();
	}

	/**
	 * Renders text with a specified minimum size.
	 * 
	 * @param gl
	 *            GL context.
	 * @param text
	 *            Text to render
	 * @param x
	 *            X coordinate of the text.
	 * @param y
	 *            Y coordinate of the text.
	 * @param z
	 *            Z coordinate of the text.
	 * @param scaling
	 *            Factor the text is scaled with.
	 * @param minSize
	 *            Minimum size of the text. Note that the minimum size is scaled with the specified scaling
	 *            vector.
	 */
	public void draw3D(GL gl, String text, float x, float y, float z, float scaling, int minSize) {

		scaling = calculateScaling(gl, scaling, minSize);

		draw3D(text, x, y, z, scaling);
	}

	/**
	 * Gets scaled bounds of the specified text according to the specified parameters.
	 * 
	 * @param gl
	 *            GL context.
	 * @param text
	 *            Text to calculate the bounds for.
	 * @param scaling
	 *            Scaling of the text.
	 * @param minSize
	 *            Minimum size of the text. Note that the bound's size is therefore dependent on the size of
	 *            the current viewport.
	 * @return Scaled bounds of the specified text.
	 */
	public Rectangle2D getScaledBounds(GL gl, String text, float scaling, int minSize) {

		scaling = calculateScaling(gl, scaling, minSize);

		Rectangle2D rect = getBounds(text);
		rect.setRect(rect.getX(), rect.getY(), rect.getWidth() * scaling, rect.getHeight() * scaling);

		return rect;
	}

	/**
	 * Calculates the scaling factor taking the minimum text size into consideration.
	 * 
	 * @param gl
	 *            GL context.
	 * @param scaling
	 *            Normal scaling of the text.
	 * @param minSize
	 *            Minimum text size.
	 * @return Scaling considering the minimum text size.
	 */
	private float calculateScaling(GL gl, float scaling, int minSize) {
		IntBuffer buffer = BufferUtil.newIntBuffer(4);
		gl.glGetIntegerv(GL.GL_VIEWPORT, buffer);
		int currentWidth = buffer.get(2);

		float referenceWidth = minSize / (float) referenceBounds.getHeight() * 500.0f;

		if (referenceWidth > currentWidth)
			scaling = scaling * referenceWidth / currentWidth;

		return scaling;
	}

}
