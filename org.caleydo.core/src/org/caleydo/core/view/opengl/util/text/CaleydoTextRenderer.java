package org.caleydo.core.view.opengl.util.text;

import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

import com.jogamp.opengl.util.awt.TextRenderer;

/**
 * Wrapper for TextRenderer that provides methods to draw text with a specified
 * minimum size (no matter what's the current size of the view).
 * 
 * @author Christian Partl
 * @author Alexander Lex
 */
public class CaleydoTextRenderer extends TextRenderer {

	static private final String REFERENCE_TEXT = "Reference Text";
	float fontScaling = GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR;

	private Rectangle2D referenceBounds;

	/**
	 * Constructor.
	 * 
	 * @param font
	 * @param antialiased
	 * @param useFractionalMetrics
	 */
	public CaleydoTextRenderer(Font font) {
		super(font, true, true, new DefaultRenderDelegate(), true);
		referenceBounds = super.getBounds(REFERENCE_TEXT);
	}

	public CaleydoTextRenderer(int size) {
		super(new Font("Arial", Font.PLAIN, size), true, true,
				new DefaultRenderDelegate(), true);
		referenceBounds = super.getBounds(REFERENCE_TEXT);
	}

	/**
	 * Convenience method to render text with a specified minimum size without
	 * having to call begin3DRendering and end3DRendering.
	 * 
	 * @param gl
	 *            GL2 context.
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
	 *            Minimum size of the text. Note that the minimum size is scaled
	 *            with the specified scaling vector.
	 */
	public void renderText(GL2 gl, String text, float x, float y, float z, float scaling,
			int minSize) {

		scaling = calculateScaling(gl, scaling, minSize);

		begin3DRendering();
		draw3D(text, x, y, z, scaling);
		flush();
		end3DRendering();
	}

	/**
	 * Renders text with a specified minimum size. Use this only if you want to
	 * render several instances at a time. If you have only one string, use
	 * {@link #renderText(GL2, String, float, float, float, float, int)}
	 * instead.
	 * 
	 * @param gl
	 *            GL2 context.
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
	 *            Minimum size of the text. Note that the minimum size is scaled
	 *            with the specified scaling vector.
	 */
	public void draw3D(GL2 gl, String text, float x, float y, float z, float scaling,
			int minSize) {

		scaling = calculateScaling(gl, scaling, minSize);

		draw3D(text, x, y, z, scaling);
	}

	/**
	 * Gets scaled bounds of the specified text according to the specified
	 * parameters.
	 * 
	 * @param gl
	 *            GL2 context.
	 * @param text
	 *            Text to calculate the bounds for.
	 * @param scaling
	 *            Scaling of the text.
	 * @param minSize
	 *            Minimum size of the text. Note that the bound's size is
	 *            therefore dependent on the size of the current viewport.
	 * @return Scaled bounds of the specified text.
	 */
	public Rectangle2D getScaledBounds(GL2 gl, String text, float scaling, int minSize) {

		scaling = calculateScaling(gl, scaling, minSize);

		Rectangle2D rect = super.getBounds(text);
		rect.setRect(rect.getX(), rect.getY(), rect.getWidth() * scaling,
				rect.getHeight() * scaling);

		return rect;
	}

	/**
	 * Calculates the scaling factor taking the minimum text size into
	 * consideration.
	 * 
	 * @param gl
	 *            GL2 context.
	 * @param scaling
	 *            Normal scaling of the text.
	 * @param minSize
	 *            Minimum text size.
	 * @return Scaling considering the minimum text size.
	 */
	private float calculateScaling(GL2 gl, float scaling, int minSize) {
		IntBuffer buffer = IntBuffer.allocate(4);
		gl.glGetIntegerv(GL2.GL_VIEWPORT, buffer);
		int currentWidth = buffer.get(2);

		float referenceWidth = minSize / (float) referenceBounds.getHeight() * 500.0f;

		if (referenceWidth > currentWidth)
			scaling = scaling * referenceWidth / currentWidth;

		return scaling;
	}

	Rectangle2D getReferenceBounds() {
		return referenceBounds;
	}

	/**
	 * Render the text at the position specified (lower left corner) within the
	 * bounding box The height is scaled to fit, the string is truncated to fit
	 * the width
	 * 
	 * @param gl
	 * @param text
	 * @param xPosition
	 *            x of lower left corner
	 * @param yPosition
	 *            y of lower left corner
	 * @param zPositon
	 * @param width
	 *            width fo the bounding box
	 * @param height
	 *            height of the bounding box
	 */
	public void renderTextInBounds(GL2 gl, String text, float xPosition, float yPosition,
			float zPositon, float width, float height) {

		// we use the height of a standard string so we don't have varying
		// height	 
		double scaling = height / super.getBounds("Sgfy").getHeight();;

		Rectangle2D boundsForWidth = super.getBounds(text);
		double requiredWidth = boundsForWidth.getWidth() * scaling;
		if (requiredWidth > width + 0.001) {
			double truncateFactor = width / requiredWidth;
			int length = (int) (text.length() * truncateFactor);
			if (length >= 0)
				text = text.substring(0, length);
		}

		begin3DRendering();
		draw3D(text, xPosition, yPosition, zPositon, (float) scaling);
		flush();
		end3DRendering();
	}

	/**
	 * Calculates the required width of a text with a specified height.
	 * 
	 * @param text
	 * @param height
	 * @return Required width of the text
	 */
	public float getRequiredTextWidth(String text, float height) {

		Rectangle2D bounds = super.getBounds(text);

		double scaling = height / bounds.getHeight();

		return (float) (bounds.getWidth() * scaling);
	}

	/**
	 * Same as {@link #getRequiredTextWidth(String, float)}, but returns the
	 * specified maximum width if the required text width exceeds this maximum.
	 * 
	 * @param text
	 * @param height
	 * @param maxWidth
	 * @return
	 */
	public float getRequiredTextWidthWithMax(String text, float height, float maxWidth) {

		float requiredWidth = getRequiredTextWidth(text, height);
		return (requiredWidth > maxWidth) ? maxWidth : requiredWidth;
	}

	/**
	 * Set the color of the text
	 * 
	 * @param color
	 */
	public void setColor(float[] color) {
		setColor(color[0], color[1], color[2], 1);
	}
}
