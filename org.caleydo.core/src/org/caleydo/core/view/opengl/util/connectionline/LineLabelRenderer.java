/**
 * 
 */
package org.caleydo.core.view.opengl.util.connectionline;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

/**
 * Renders a label at the connection line.
 * 
 * @author Christian
 * 
 */
public class LineLabelRenderer extends ARelativeLinePositionRenderer {
	
	public static final int DEFAULT_TEXT_HEIGHT = 13;
	public static final int DEFAULT_LINE_OFFSET = 0;
	public static final float[] DEFAULT_TEXT_COLOR = {0,0,0,1};
	public static final float[] DEFAULT_BACK_GROUND_COLOR = {1,1,1,1};

	private CaleydoTextRenderer textRenderer;

	/**
	 * The text of the label.
	 */
	private String text;

	/**
	 * The height of the text in pixels.
	 */
	private int textHeightPixels = DEFAULT_TEXT_HEIGHT;

	/**
	 * The offset between the connection line and the label in pixels. If 0 the
	 * label is rendered at the center of the line.
	 */
	private int lineOffsetPixels = DEFAULT_LINE_OFFSET;
	
	/**
	 * RGBA color of the text.
	 */
	private float[] textColor = DEFAULT_TEXT_COLOR;
	
	/**
	 * RGBA color of the text's background.
	 */
	private float[] backGroundColor = DEFAULT_BACK_GROUND_COLOR;

	/**
	 * @param linePositionProportion
	 * @param pixelGLConverter
	 */
	public LineLabelRenderer(float linePositionProportion,
			PixelGLConverter pixelGLConverter, String text,
			CaleydoTextRenderer textRenderer) {
		super(linePositionProportion, pixelGLConverter);
		this.textRenderer = textRenderer;
		this.text = text;
	}

	@Override
	protected void render(GL2 gl, List<Vec3f> linePoints, Vec3f relativePositionOnLine,
			Vec3f enclosingPoint1, Vec3f enclosingPoint2) {

		float height = pixelGLConverter.getGLHeightForPixelHeight(textHeightPixels);
		float width = textRenderer.getRequiredTextWidth(text, height);
		float lineOffset = pixelGLConverter.getGLWidthForPixelWidth(lineOffsetPixels);
		float xPosition;
		float yPosition;

		if (lineOffsetPixels == 0) {
			xPosition = relativePositionOnLine.x() - width / 2.0f;
			yPosition = relativePositionOnLine.y() - height / 2.0f;
		} else {
			Vec3f direction = enclosingPoint2.minus(enclosingPoint1);
			Vec2f normalVector = new Vec2f(-direction.y(), direction.x());
			float scalingFactor = lineOffset / normalVector.length();
			normalVector.scale(scalingFactor);
			xPosition = relativePositionOnLine.x() + normalVector.x();
			if (xPosition < relativePositionOnLine.x()) {
				xPosition-= width;
			}
				
			yPosition = relativePositionOnLine.y() + normalVector.y();
			if (yPosition < relativePositionOnLine.y()) {
				yPosition-= height;
			}
		}

		gl.glColor3f(1, 1, 1);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(xPosition, yPosition, relativePositionOnLine.z());
		gl.glVertex3f(xPosition + width, yPosition, relativePositionOnLine.z());
		gl.glVertex3f(xPosition + width, yPosition + height, relativePositionOnLine.z());
		gl.glVertex3f(xPosition, yPosition + height, relativePositionOnLine.z());
		gl.glEnd();
		textRenderer.setColor(0, 0, 0, 1);
		textRenderer.renderTextInBounds(gl, text,
				xPosition + pixelGLConverter.getGLWidthForPixelWidth(2), yPosition
						+ pixelGLConverter.getGLHeightForPixelHeight(2),
				relativePositionOnLine.z(), width, height);

	}

	/**
	 * @param text
	 *            setter, see {@link #text}
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the text, see {@link #text}
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param textHeight
	 *            setter, see {@link #textHeightPixels}
	 */
	public void setTextHeight(int textHeight) {
		this.textHeightPixels = textHeight;
	}

	/**
	 * @return the textHeight, see {@link #textHeightPixels}
	 */
	public int getTextHeight() {
		return textHeightPixels;
	}

	/**
	 * @param lineOffsetPixels
	 *            setter, see {@link #lineOffsetPixels}
	 */
	public void setLineOffsetPixels(int lineOffsetPixels) {
		this.lineOffsetPixels = lineOffsetPixels;
	}

	/**
	 * @return the lineOffsetPixels, see {@link #lineOffsetPixels}
	 */
	public int getLineOffsetPixels() {
		return lineOffsetPixels;
	}
	
	/**
	 * @param textColor setter, see {@link #textColor}
	 */
	public void setTextColor(float[] textColor) {
		this.textColor = textColor;
	}
	
	/**
	 * @return the textColor, see {@link #textColor}
	 */
	public float[] getTextColor() {
		return textColor;
	}
	
	/**
	 * @param backGroundColor setter, see {@link #backGroundColor}
	 */
	public void setBackGroundColor(float[] backGroundColor) {
		this.backGroundColor = backGroundColor;
	}
	
	/**
	 * @return the backGroundColor, see {@link #backGroundColor}
	 */
	public float[] getBackGroundColor() {
		return backGroundColor;
	}

}
