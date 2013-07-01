/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.radial;

import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

/**
 * Represents a box that can be positioned containing all label lines of one
 * label.
 * 
 * @author Christian Partl
 */
public class LabelContainer {

	private static float CONTAINER_BOUNDARY_SPACING = 0.03f;
	private static float CONTAINER_LINE_SPACING = 0.02f;

	private float fWidth;
	private float fHeight;
	private float fXContainerLeft;
	private float fYContainerCenter;
	private float fLabelScaling;

	private ArrayList<LabelLine> alLabelLines;
	private CaleydoTextRenderer textRenderer;

	/**
	 * Constructor.
	 * 
	 * @param fXContainerLeft
	 *            X coordinate of the left border of the label container.
	 * @param fYContainerCenter
	 *            Y coordinate of the center of the label container.
	 * @param fLabelScaling
	 *            Scaling value for the text items in all label lines of the
	 *            label container.
	 * @param textRenderer
	 *            TextRenderer that shall be used for drawing by the text items
	 *            in all label lines of the label container.
	 */
	public LabelContainer(float fXContainerLeft, float fYContainerCenter,
			float fLabelScaling, CaleydoTextRenderer textRenderer) {

		this.fXContainerLeft = fXContainerLeft;
		this.fYContainerCenter = fYContainerCenter;
		this.fLabelScaling = fLabelScaling;
		this.textRenderer = textRenderer;
		alLabelLines = new ArrayList<LabelLine>();
		fWidth = 0;
		fHeight = 0;
	}

	/**
	 * Adds a list of label lines to the label container.
	 * 
	 * @param alLines
	 *            List of label lines that shall be added.
	 */
	public void addLabelLines(GL2 gl, ArrayList<LabelLine> alLines) {

		for (LabelLine currentLine : alLines) {
			currentLine.calculateSize(gl, textRenderer, fLabelScaling,
					LabelItemTypes.LABEL_ITEM_TYPE_TEXT);
			addLine(currentLine);
		}
	}

	/**
	 * Adds one label line to the label container.
	 * 
	 * @param labelLine
	 *            Label line that shall be added.
	 */
	public void addLine(LabelLine labelLine) {

		float fLineHeight = labelLine.getHeight();
		float fLineWidth = labelLine.getWidth();

		if ((fLineWidth + 2.0f * CONTAINER_BOUNDARY_SPACING) > fWidth) {
			fWidth = fLineWidth + 2.0f * CONTAINER_BOUNDARY_SPACING;
		}

		float fXLinePosition = fXContainerLeft + CONTAINER_BOUNDARY_SPACING;
		float fYLinePosition;

		if (alLabelLines.size() == 0) {
			fHeight += 2.0f * CONTAINER_BOUNDARY_SPACING + fLineHeight;
			fYLinePosition = fYContainerCenter + (fHeight / 2.0f)
					- CONTAINER_BOUNDARY_SPACING - fLineHeight;
		} else {
			fHeight += CONTAINER_LINE_SPACING + fLineHeight;
			updateLinePositions();
			LabelLine lastLine = alLabelLines.get(alLabelLines.size() - 1);
			fYLinePosition = lastLine.getPosition().y() - CONTAINER_LINE_SPACING
					- fLineHeight;
		}

		labelLine.setPosition(fXLinePosition, fYLinePosition);
		alLabelLines.add(labelLine);
	}

	/**
	 * Sets the position of the label container.
	 * 
	 * @param fXContainerLeft
	 *            X coordinate of the left border of the label container.
	 * @param fYContainerCenter
	 *            Y coordinate of the center of the label container.
	 */
	public void setContainerPosition(float fXContainerLeft, float fYContainerCenter) {
		this.fXContainerLeft = fXContainerLeft;
		this.fYContainerCenter = fYContainerCenter;
		updateLinePositions();
	}

	/**
	 * Updates the positions of all contained lines. This is necessary when
	 * repositioning the container or adding new label lines.
	 */
	private void updateLinePositions() {

		if (alLabelLines.size() == 0) {
			return;
		} else {
			float fXLinePosition = fXContainerLeft + CONTAINER_BOUNDARY_SPACING;

			LabelLine firstLine = alLabelLines.get(0);
			float fYLinePosition = fYContainerCenter + (fHeight / 2.0f)
					- CONTAINER_BOUNDARY_SPACING - firstLine.getHeight();
			firstLine.setPosition(fXLinePosition, fYLinePosition);

			for (int i = 1; i < alLabelLines.size(); i++) {
				LabelLine currentLine = alLabelLines.get(i);
				fYLinePosition -= (currentLine.getHeight() + CONTAINER_LINE_SPACING);
				currentLine.setPosition(fXLinePosition, fYLinePosition);
			}
		}
	}

	/**
	 * Checks if the current label container collides (overlaps) with the
	 * specified one.
	 * 
	 * @param container
	 *            Label container that should be tested for collision with the
	 *            current one.
	 * @return True, if the containers overlap, false otherwise.
	 */
	public boolean doContainersCollide(LabelContainer container) {

		if (getTop() < container.getBottom() || container.getTop() < getBottom()
				|| container.getRight() < getLeft() || getRight() < container.getLeft()) {
			return false;
		}
		return true;
	}

	/**
	 * Draws all label lines contained.
	 * 
	 * @param gl
	 *            GL2 object that shall be used for drawing.
	 * @param bDrawLabelBackground
	 *            Determines, whether a background rectangle with the size of
	 *            the label container shall be drawn or not.
	 */
	public void draw(GL2 gl, boolean bDrawLabelBackground) {

		gl.glLoadIdentity();

		if (bDrawLabelBackground) {
			gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);
			gl.glColor4fv(RadialHierarchyRenderStyle.LABEL_BACKGROUND_COLOR, 0);

			gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex3f(fXContainerLeft, getTop(), 0);
			gl.glVertex3f(getRight(), getTop(), 0);
			gl.glVertex3f(getRight(), getBottom(), 0);
			gl.glVertex3f(fXContainerLeft, getBottom(), 0);
			gl.glEnd();
			gl.glPopAttrib();
		}

		for (LabelLine currentLine : alLabelLines) {
			currentLine.draw(gl);
		}
	}

	public float getWidth() {
		return fWidth;
	}

	public float getHeight() {
		return fHeight;
	}

	public float getLeft() {
		return fXContainerLeft;
	}

	public float getYContainerCenter() {
		return fYContainerCenter;
	}

	public float getTop() {
		return fYContainerCenter + (fHeight / 2.0f);
	}

	public float getBottom() {
		return fYContainerCenter - (fHeight / 2.0f);
	}

	public float getRight() {
		return fXContainerLeft + fWidth;
	}

	/**
	 * @return Scaling value of the text items in all label lines of the label
	 *         container.
	 */
	public float getLabelScaling() {
		return fLabelScaling;
	}

	/**
	 * Sets the scaling value that shall be used by the text items in all label
	 * lines of the label container.
	 * 
	 * @param fLabelScaling
	 *            Scaling value for the text items in all label lines of the
	 *            label container.
	 */
	public void setLabelScaling(float fLabelScaling) {
		this.fLabelScaling = fLabelScaling;
	}

}
