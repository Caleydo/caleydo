/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.radial;

import java.awt.Font;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.view.opengl.util.GLPrimitives;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

/**
 * The LabelManager is responsible for positioning all labels (
 * {@link LabelContainer}), determining the labels that can be drawn and those
 * who can't (due to label overlapping) and finally for drawing the labels.
 * 
 * @author Christian Partl
 */
public class LabelManager {

	private static final float LABEL_FONT_SCALING_FACTOR = 0.005f;
	private static final int LABEL_FONT_SIZE = 32;
	private static final String LABEL_FONT_NAME = "Arial";
	private static final int LABEL_FONT_STYLE = Font.PLAIN;
	private static float LABEL_SEGMENT_DEPTH_SCALING_PERCENT = 0.15f;
	private static float LABEL_MIN_FONT_SCALING_FACTOR = 0.001f;
	private static float LEFT_CONTAINER_SPACING = 0.1f;
	private static float RIGHT_CONTAINER_SPACING = 0.1f;
	private static float MARKER_RADIUS = 0.05f;

	private ArrayList<LabelInfo> alLabels;
	private ArrayList<LabelContainer> alLeftContainers;
	private ArrayList<LabelContainer> alRightContainers;
	private CaleydoTextRenderer textRenderer;
	private int iMaxSegmentDepth;
	private LabelContainer lcMouseOver;
	private Rectangle rectControlBox;
	private static LabelManager instance;

	/**
	 * Constructor.
	 */
	private LabelManager() {
		alLabels = new ArrayList<LabelInfo>();
		alLeftContainers = new ArrayList<LabelContainer>();
		alRightContainers = new ArrayList<LabelContainer>();
		iMaxSegmentDepth = 0;
	}

	/**
	 * Registers a label that shall be drawn.
	 * 
	 * @param label
	 *            Label that shall be drawn.
	 */
	public void addLabel(LabelInfo label) {
		alLabels.add(label);
		if (iMaxSegmentDepth < label.getSegmentLabelDepth()) {
			iMaxSegmentDepth = label.getSegmentLabelDepth();
		}
	}

	/**
	 * Tries to draw all registered labels. Possibly not all labels will be
	 * drawn due to overlapping of the labels.
	 * 
	 * @param gl
	 *            GL2 object that shall be used for drawing.
	 * @param glu
	 *            GLU object that shall be used for drawing.
	 * @param fScreenWidth
	 *            Width of the screen.
	 * @param fScreenHeight
	 *            Height of the screen.
	 * @param fHierarchyOuterRadius
	 *            Radius of the whole radial hierarchy.
	 */
	public void drawAllLabels(GL2 gl, GLU glu, float fScreenWidth, float fScreenHeight,
			float fHierarchyOuterRadius) {

		textRenderer = new CaleydoTextRenderer(new Font(LABEL_FONT_NAME,
				LABEL_FONT_STYLE, LABEL_FONT_SIZE));
		textRenderer.setColor(0, 0, 0, 1);

		float fXCenter = fScreenWidth / 2.0f;
		float fYCenter = fScreenHeight / 2.0f;

		for (LabelInfo label : alLabels) {

			float fSegmentXCenter = label.getSegmentXCenter();
			float fSegmentYCenter = label.getSegmentYCenter();
			float fSegmentCenterRadius = label.getSegmentCenterRadius();
			float fUnitVectorX = 0;
			float fUnitVectorY = 0;
			float fBendPointX = 0;
			float fBendPointY = 0;

			if (fSegmentCenterRadius > 0) {
				fUnitVectorX = (1.0f / fSegmentCenterRadius) * fSegmentXCenter;
				fUnitVectorY = (1.0f / fSegmentCenterRadius) * fSegmentYCenter;
				fBendPointX = fUnitVectorX * fHierarchyOuterRadius * 1.05f;
				fBendPointY = fUnitVectorY * fHierarchyOuterRadius * 1.05f;
			}
			LabelContainer labelContainer = createLabelContainer(gl, label,
					LEFT_CONTAINER_SPACING, fYCenter + fBendPointY, fScreenHeight);
			ArrayList<LabelContainer> alContainers = alLeftContainers;

			float fXMouseOverContainerPosition = fXCenter + fSegmentXCenter
					+ MARKER_RADIUS;
			if (fSegmentXCenter > 0) {
				labelContainer.setContainerPosition(fScreenWidth
						- RIGHT_CONTAINER_SPACING - labelContainer.getWidth(),
						labelContainer.getYContainerCenter());
				alContainers = alRightContainers;
				fXMouseOverContainerPosition = fXCenter + fSegmentXCenter - MARKER_RADIUS
						- labelContainer.getWidth();
			}

			updateContainerPositionOnControlBoxCollision(labelContainer, fXCenter);

			if (label.getSegmentLabelDepth() >= iMaxSegmentDepth) {
				labelContainer.setContainerPosition(fXMouseOverContainerPosition,
						fYCenter + fSegmentYCenter);
				labelContainer.draw(gl, true);
				drawSegmentMarker(gl, glu, fXCenter + fSegmentXCenter, fYCenter
						+ fSegmentYCenter);
			} else if (!doesLabelCollide(labelContainer, alContainers, fXCenter
					+ fSegmentXCenter, fYCenter + fSegmentYCenter, fXCenter, fBendPointX)) {
				alContainers.add(labelContainer);

				labelContainer.draw(gl, true);
				drawLink(gl, glu, fXCenter, fYCenter, fSegmentXCenter, fSegmentYCenter,
						fBendPointX, fBendPointY, labelContainer);
			}
		}
	}

	/**
	 * Draws a link from a disc segment (partial disc) to the corresponding
	 * label.
	 * 
	 * @param gl
	 *            GL2 object that shall be used for drawing.
	 * @param glu
	 *            GLU object that shall be used for drawing.
	 * @param fXCenter
	 *            X coordinate of the radial hierarchie's center.
	 * @param fYCenter
	 *            Y coordinate of the radial hierarchie's center.
	 * @param fSegmentXCenter
	 *            X coordinate of the disc segment's center.
	 * @param fSegmentYCenter
	 *            Y coordinate of the disc segment's center.
	 * @param fBendPointX
	 *            X coordinate of the bend point of the link to the label.
	 * @param fBendPointY
	 *            Y coordinate of the bend point of the link to the label.
	 * @param labelContainer
	 *            Label container the link shall be drawn to.
	 */
	private void drawLink(GL2 gl, GLU glu, float fXCenter, float fYCenter,
			float fSegmentXCenter, float fSegmentYCenter, float fBendPointX,
			float fBendPointY, LabelContainer labelContainer) {

		gl.glLoadIdentity();

		gl.glColor4fv(RadialHierarchyRenderStyle.LABEL_TEXT_COLOR, 0);
		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex3f(fXCenter + fSegmentXCenter, fYCenter + fSegmentYCenter, 0);
		gl.glVertex3f(fXCenter + fBendPointX, fYCenter + fBendPointY, 0);
		if (fSegmentXCenter <= 0) {
			gl.glVertex3f(labelContainer.getRight(), fYCenter + fBendPointY, 0);
		} else {
			gl.glVertex3f(labelContainer.getLeft(), fYCenter + fBendPointY, 0);
		}
		gl.glEnd();

		drawSegmentMarker(gl, glu, fXCenter + fSegmentXCenter, fYCenter + fSegmentYCenter);
	}

	/**
	 * Draws a marker (small circle) at the specified position.
	 * 
	 * @param gl
	 *            GL2 object that shall be used for drawing.
	 * @param glu
	 *            GLU object that shall be used for drawing.
	 * @param fXPosition
	 *            X coordinate of the marker.
	 * @param fYPosition
	 *            Y coordinate of the marker.
	 */
	private void drawSegmentMarker(GL2 gl, GLU glu, float fXPosition, float fYPosition) {
		gl.glColor4fv(RadialHierarchyRenderStyle.LABEL_TEXT_COLOR, 0);
		gl.glPushMatrix();
		gl.glTranslatef(fXPosition, fYPosition, 0);
		GLPrimitives.renderCircle(glu, MARKER_RADIUS, 10);
		GLPrimitives.renderCircleBorder(gl, glu, MARKER_RADIUS, 10, 2);
		gl.glPopMatrix();
	}

	/**
	 * Creates a label container for the specified label.
	 * 
	 * @param label
	 *            Label the label container shall be created for.
	 * @param fXContainerLeft
	 *            X coordinate of the left border of the label container.
	 * @param fYContainerCenter
	 *            Y coordinate of the center of the label container.
	 * @param fScreenHeight
	 *            Height of the screen.
	 * @return Label container that has been created using the specified
	 *         parameters.
	 */
	private LabelContainer createLabelContainer(GL2 gl, LabelInfo label,
			float fXContainerLeft, float fYContainerCenter, float fScreenHeight) {

		float fLabelScaling;
		LabelContainer labelContainer = null;

		if (iMaxSegmentDepth <= label.getSegmentLabelDepth()) {
			fLabelScaling = LABEL_FONT_SCALING_FACTOR;
		} else {
			float fSegmentScalingFactor = LABEL_FONT_SCALING_FACTOR
					* (((float) (iMaxSegmentDepth - 1) - (float) label
							.getSegmentLabelDepth()) * LABEL_SEGMENT_DEPTH_SCALING_PERCENT);
			fLabelScaling = (fSegmentScalingFactor > LABEL_FONT_SCALING_FACTOR) ? LABEL_MIN_FONT_SCALING_FACTOR
					: (LABEL_FONT_SCALING_FACTOR - fSegmentScalingFactor);
		}

		labelContainer = new LabelContainer(fXContainerLeft, fYContainerCenter,
				fLabelScaling, textRenderer);
		if (iMaxSegmentDepth <= label.getSegmentLabelDepth()) {
			lcMouseOver = labelContainer;
		}

		labelContainer.addLabelLines(gl, label.getLines());

		if (labelContainer.getTop() > fScreenHeight) {
			labelContainer.setContainerPosition(labelContainer.getLeft(), fScreenHeight
					- (labelContainer.getHeight() / 2.0f));
		}
		if (labelContainer.getBottom() < 0) {
			labelContainer.setContainerPosition(labelContainer.getLeft(),
					labelContainer.getHeight() / 2.0f);
		}

		return labelContainer;
	}

	/**
	 * If the specified container overlaps with the control box, the container
	 * is shifted to the side so that it does not overlap any more.
	 * 
	 * @param labelContainer
	 *            Label container to test for collision.
	 * @param fXCenter
	 *            X coordinate of the center of the radial hierarchy.
	 */
	private void updateContainerPositionOnControlBoxCollision(
			LabelContainer labelContainer, float fXCenter) {

		if (rectControlBox != null) {
			if (rectControlBox.getMinY() > labelContainer.getTop())
				return;
			if (rectControlBox.getMaxY() < labelContainer.getBottom())
				return;
			if (rectControlBox.getMinX() > labelContainer.getRight())
				return;
			if (rectControlBox.getMaxX() < labelContainer.getLeft())
				return;
		}

		if (labelContainer.getLeft() < fXCenter) {
			labelContainer.setContainerPosition(rectControlBox.getMaxX()
					+ LEFT_CONTAINER_SPACING, labelContainer.getYContainerCenter());
		} else {
			labelContainer.setContainerPosition(rectControlBox.getMinX()
					- RIGHT_CONTAINER_SPACING - labelContainer.getWidth(),
					labelContainer.getYContainerCenter());
		}
	}

	/**
	 * Checks if the specified label container would collide with any other
	 * containers that already have been drawn.
	 * 
	 * @param containerToTest
	 *            Container for which the collision test should be made.
	 * @param alContainers
	 *            List of containers the new container would be added to (left
	 *            or right side of the screen).
	 * @param fSegmentXCenter
	 *            X coordinate of the center of the disc segment the label
	 *            container corresponds to.
	 * @param fSegmentYCenter
	 *            Y coordinate of the center of the disc segment the label
	 *            container corresponds to.
	 * @param fXCenter
	 *            X coordinate of the radial hierarchy's center.
	 * @param fBendPointX
	 *            X coordinate of the link's bend point that corresponds to the
	 *            label container.
	 * @return True, if a collision occurs, false otherwise.
	 */
	private boolean doesLabelCollide(LabelContainer containerToTest,
			ArrayList<LabelContainer> alContainers, float fSegmentXCenter,
			float fSegmentYCenter, float fXCenter, float fBendPointX) {

		for (LabelContainer currentContainer : alContainers) {
			if (currentContainer.doContainersCollide(containerToTest)) {
				return true;
			}
		}
		if ((fBendPointX >= 0) && (fXCenter + fBendPointX > containerToTest.getLeft()))
			return true;
		if ((fBendPointX < 0) && (fXCenter + fBendPointX < containerToTest.getRight()))
			return true;
		// It is assumed that the LabelContainer for the MouseOver Element is
		// created first, since it is
		// rendered first. So the the following marker collision detection
		// should work for now.
		if (lcMouseOver != null) {
			if (lcMouseOver.getTop() < fSegmentYCenter - MARKER_RADIUS)
				return false;
			if (lcMouseOver.getBottom() > fSegmentYCenter + MARKER_RADIUS)
				return false;
			if (lcMouseOver.getLeft() > fSegmentXCenter + MARKER_RADIUS)
				return false;
			if (lcMouseOver.getRight() < fSegmentXCenter - MARKER_RADIUS)
				return false;
			return true;
		}
		return false;
	}

	/**
	 * Clears all registered labels.
	 */
	public void clearLabels() {
		alLabels.clear();
		alLeftContainers.clear();
		alRightContainers.clear();
		iMaxSegmentDepth = 0;
	}

	/**
	 * Sets the control box the labels shall not collide with.
	 * 
	 * @param rectControlBox
	 *            Control box.
	 */
	public void setControlBox(Rectangle rectControlBox) {
		this.rectControlBox = rectControlBox;
	}

	/**
	 * @return Instance of the LabelManager.
	 */
	public static LabelManager get() {
		if (instance == null) {
			instance = new LabelManager();
		}
		return instance;
	}
}
