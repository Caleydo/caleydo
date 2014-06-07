/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.core.view.opengl.util.connectionline;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Vec2i;

import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;

import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.util.color.Color;
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
	public static final float[] DEFAULT_TEXT_COLOR = { 0, 0, 0, 1 };
	public static final float[] DEFAULT_BACK_GROUND_COLOR = { 1, 1, 1, 1 };

	public static enum EAlignmentX {
		LEFT, CENTER, RIGHT;
	}

	public static enum EAlignmentY {
		TOP, MIDDLE, BOTTOM;
	}

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
	 * The offset between the connection line and the label in pixels. If 0 the label is rendered at the center of the
	 * line.
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
	 * Specifies whether the text is centered at the x coordinate it is rendered.
	 */
	private boolean isXCentered = false;

	/**
	 * Specifies whether the text is centered at the y coordinate it is rendered.
	 */
	private boolean isYCentered = false;

	/**
	 * Provider of the label text.
	 */
	private ILabelProvider labelProvider;

	/**
	 * Fixed translation in x and y that adapts the label's position. This attribute overrides {@link #lineOffsetPixels}
	 * if set.
	 */
	private Vec2i labelTranslation;

	private EAlignmentX alignmentX = EAlignmentX.LEFT;

	private EAlignmentY alignmentY = EAlignmentY.BOTTOM;

	public LineLabelRenderer(float linePositionProportion, PixelGLConverter pixelGLConverter, String text,
			CaleydoTextRenderer textRenderer) {
		super(linePositionProportion, pixelGLConverter);
		this.textRenderer = textRenderer;
		this.text = text;
	}

	public LineLabelRenderer(float linePositionProportion, PixelGLConverter pixelGLConverter,
			ILabelProvider labelProvider, CaleydoTextRenderer textRenderer) {
		super(linePositionProportion, pixelGLConverter);
		this.textRenderer = textRenderer;
	}

	@Override
	protected void render(GL2 gl, List<Vec3f> linePoints, Vec3f relativePositionOnLine, Vec3f enclosingPoint1,
			Vec3f enclosingPoint2) {

		if (labelProvider != null)
			text = labelProvider.getLabel();

		float height = pixelGLConverter.getGLHeightForPixelHeight(textHeightPixels);
		// Add some spacing because required width calculation is not always
		// accurate
		float width = textRenderer.getRequiredTextWidth(text, height) + pixelGLConverter.getGLWidthForPixelWidth(3);
		float lineOffset = pixelGLConverter.getGLWidthForPixelWidth(lineOffsetPixels);
		float xPosition = relativePositionOnLine.x();
		float yPosition = relativePositionOnLine.y();

		switch (alignmentX) {
		case CENTER:
			xPosition -= width / 2.0f;
			break;
		case RIGHT:
			xPosition -= width;
			break;
		default:
			break;
		}

		switch (alignmentY) {
		case MIDDLE:
			yPosition -= height / 2.0f;
			break;
		case TOP:
			yPosition -= height;
			break;
		default:
			break;
		}

		if (labelTranslation != null) {
			xPosition += pixelGLConverter.getGLWidthForPixelWidth(labelTranslation.x());
			yPosition += pixelGLConverter.getGLHeightForPixelHeight(labelTranslation.y());
		} else if (lineOffsetPixels != 0) {
			Vec3f direction = enclosingPoint2.minus(enclosingPoint1);
			Vec2f normalVector = new Vec2f(-direction.y(), direction.x());
			float scalingFactor = lineOffset / normalVector.length();
			normalVector.scale(scalingFactor);
			xPosition += normalVector.x();
			yPosition += normalVector.y();

			if (alignmentX == EAlignmentX.LEFT && xPosition < relativePositionOnLine.x()) {
				xPosition -= width;
			}

			if (alignmentY == EAlignmentY.BOTTOM && yPosition < relativePositionOnLine.y()) {
				yPosition -= height;
			}
		}

		gl.glColor4fv(backGroundColor, 0);
		gl.glBegin(GL2GL3.GL_QUADS);
		gl.glVertex3f(xPosition, yPosition, relativePositionOnLine.z());
		gl.glVertex3f(xPosition + width, yPosition, relativePositionOnLine.z());
		gl.glVertex3f(xPosition + width, yPosition + height, relativePositionOnLine.z());
		gl.glVertex3f(xPosition, yPosition + height, relativePositionOnLine.z());
		gl.glEnd();
		textRenderer.setColor(new Color(textColor));
		textRenderer.renderTextInBounds(gl, text, xPosition + pixelGLConverter.getGLWidthForPixelWidth(2), yPosition
				+ pixelGLConverter.getGLHeightForPixelHeight(2), relativePositionOnLine.z(), width, height);

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
	 * @param textColor
	 *            setter, see {@link #textColor}
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
	 * @param backGroundColor
	 *            setter, see {@link #backGroundColor}
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

	/**
	 * @param isCentered
	 *            setter, see {@link #isXCentered}
	 */
	public void setXCentered(boolean isCentered) {
		this.isXCentered = isCentered;
	}

	/**
	 * @return the isCentered, see {@link #isXCentered}
	 */
	public boolean isXCentered() {
		return isXCentered;
	}

	/**
	 * @param labelProvider
	 *            setter, see {@link #labelProvider}
	 */
	public void setLabelProvider(ILabelProvider labelProvider) {
		this.labelProvider = labelProvider;
	}

	/**
	 * @return the labelProvider, see {@link #labelProvider}
	 */
	public ILabelProvider getLabelProvider() {
		return labelProvider;
	}

	/**
	 * @param isYCentered
	 *            setter, see {@link #isYCentered}
	 */
	public void setYCentered(boolean isYCentered) {
		this.isYCentered = isYCentered;
	}

	/**
	 * @return the isYCentered, see {@link #isYCentered}
	 */
	public boolean isYCentered() {
		return isYCentered;
	}

	public void setLabelTranslation(int translationX, int translationY) {
		this.labelTranslation = new Vec2i();
		labelTranslation.setX(translationX);
		labelTranslation.setY(translationY);
	}

	/**
	 * @param alignmentX
	 *            setter, see {@link alignmentX}
	 */
	public void setAlignmentX(EAlignmentX alignmentX) {
		this.alignmentX = alignmentX;
	}

	/**
	 * @param alignmentY
	 *            setter, see {@link alignmentY}
	 */
	public void setAlignmentY(EAlignmentY alignmentY) {
		this.alignmentY = alignmentY;
	}

}
