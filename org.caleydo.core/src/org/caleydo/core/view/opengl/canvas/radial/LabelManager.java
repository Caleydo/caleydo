package org.caleydo.core.view.opengl.canvas.radial;

import java.awt.Font;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.j2d.TextRenderer;

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

	private ArrayList<Label> alLabels;
	private ArrayList<LabelContainer> alLeftContainers;
	private ArrayList<LabelContainer> alRightContainers;
	private TextRenderer textRenderer;
	private int iMaxSegmentDepth;
	private LabelContainer lcMouseOver;
	private static LabelManager instance;

	private LabelManager() {
		alLabels = new ArrayList<Label>();
		alLeftContainers = new ArrayList<LabelContainer>();
		alRightContainers = new ArrayList<LabelContainer>();
		textRenderer = new TextRenderer(new Font(LABEL_FONT_NAME, LABEL_FONT_STYLE, LABEL_FONT_SIZE), false);
		textRenderer.setColor(0, 0, 0, 1);
		iMaxSegmentDepth = 0;
	}

	public void addLabel(Label label) {
		alLabels.add(label);
		if (iMaxSegmentDepth < label.getSegmentDepth()) {
			iMaxSegmentDepth = label.getSegmentDepth();
		}
	}

	public void drawAllLabels(GL gl, GLU glu, float fScreenWidth, float fScreenHeight,
		float fHierarchyOuterRadius) {

		float fXCenter = fScreenWidth / 2.0f;
		float fYCenter = fScreenHeight / 2.0f;

		for (Label label : alLabels) {

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
			LabelContainer labelContainer =
				createLabelContainer(label, LEFT_CONTAINER_SPACING, fYCenter + fBendPointY, fScreenHeight);
			ArrayList<LabelContainer> alContainers = alLeftContainers;

			float fXMouseOverContainerPosition = fXCenter + fSegmentXCenter + MARKER_RADIUS;
			if (fSegmentXCenter > 0) {
				labelContainer.setContainerPosition(fScreenWidth - RIGHT_CONTAINER_SPACING
					- labelContainer.getWidth(), labelContainer.getYContainerCenter());
				alContainers = alRightContainers;
				fXMouseOverContainerPosition =
					fXCenter + fSegmentXCenter - MARKER_RADIUS - labelContainer.getWidth();
			}
			if (label.getSegmentDepth() >= iMaxSegmentDepth) {
				labelContainer.setContainerPosition(fXMouseOverContainerPosition, fYCenter + fSegmentYCenter);
				labelContainer.draw(gl, true);
				drawSegmentMarker(gl, glu, fXCenter + fSegmentXCenter, fYCenter + fSegmentYCenter);
			}
			else if (!doesLabelCollide(labelContainer, alContainers, fXCenter + fSegmentXCenter, fYCenter
				+ fSegmentYCenter, fXCenter, fBendPointX)) {
				alContainers.add(labelContainer);

				labelContainer.draw(gl, false);
				drawLink(gl, glu, fXCenter, fYCenter, fSegmentXCenter, fSegmentYCenter, fBendPointX,
					fBendPointY, labelContainer);
			}
		}
	}

	private void drawLink(GL gl, GLU glu, float fXCenter, float fYCenter, float fSegmentXCenter,
		float fSegmentYCenter, float fBendPointX, float fBendPointY, LabelContainer labelContainer) {

		gl.glLoadIdentity();

		gl.glColor3f(0.2f, 0.2f, 0.2f);
		gl.glBegin(GL.GL_LINE_STRIP);
		gl.glVertex3f(fXCenter + fSegmentXCenter, fYCenter + fSegmentYCenter, 0);
		gl.glVertex3f(fXCenter + fBendPointX, fYCenter + fBendPointY, 0);
		if (fSegmentXCenter <= 0) {
			gl.glVertex3f(LEFT_CONTAINER_SPACING + labelContainer.getWidth(), fYCenter + fBendPointY, 0);
		}
		else {
			gl.glVertex3f(labelContainer.getLeft(), fYCenter + fBendPointY, 0);
		}
		gl.glEnd();

		drawSegmentMarker(gl, glu, fXCenter + fSegmentXCenter, fYCenter + fSegmentYCenter);
	}

	private void drawSegmentMarker(GL gl, GLU glu, float fXPosition, float fYPosition) {
		gl.glColor3f(0.2f, 0.2f, 0.2f);
		gl.glPushMatrix();
		gl.glTranslatef(fXPosition, fYPosition, 0);
		GLPrimitives.renderCircle(gl, glu, MARKER_RADIUS, 10);
		GLPrimitives.renderCircleBorder(gl, glu, MARKER_RADIUS, 10, 2);
		gl.glPopMatrix();
	}

	private LabelContainer createLabelContainer(Label label, float fXContainerLeft, float fYContainerCenter,
		float fScreenHeight) {

		float fLabelScaling;
		LabelContainer labelContainer = null;

		if (iMaxSegmentDepth <= label.getSegmentDepth()) {
			fLabelScaling = LABEL_FONT_SCALING_FACTOR;
		}
		else {
			float fSegmentScalingFactor =
				LABEL_FONT_SCALING_FACTOR
					* (((float) (iMaxSegmentDepth - 1) - (float) label.getSegmentDepth()) * LABEL_SEGMENT_DEPTH_SCALING_PERCENT);
			fLabelScaling =
				(fSegmentScalingFactor > 1) ? LABEL_MIN_FONT_SCALING_FACTOR
					: (LABEL_FONT_SCALING_FACTOR - fSegmentScalingFactor);
		}

		labelContainer =
			new LabelContainer(LEFT_CONTAINER_SPACING, fYContainerCenter, fLabelScaling, textRenderer);
		if (iMaxSegmentDepth <= label.getSegmentDepth()) {
			lcMouseOver = labelContainer;
		}

		ArrayList<String> alLines = label.getLines();

		for (String currentLine : alLines) {
			labelContainer.addTextLine(currentLine);
		}

		if (labelContainer.getTop() > fScreenHeight) {
			labelContainer.setContainerPosition(labelContainer.getLeft(), fScreenHeight
				- (labelContainer.getHeight() / 2.0f));
		}
		if (labelContainer.getBottom() < 0) {
			labelContainer.setContainerPosition(labelContainer.getLeft(), labelContainer.getHeight() / 2.0f);
		}

		return labelContainer;
	}

	private boolean doesLabelCollide(LabelContainer containerToTest, ArrayList<LabelContainer> alContainers,
		float fSegmentXCenter, float fSegmentYCenter, float fXCenter, float fBendPointX) {

		for (LabelContainer currentContainer : alContainers) {
			if (currentContainer.doContainersCollide(containerToTest)) {
				return true;
			}
		}
		if((fBendPointX >= 0) && (fXCenter + fBendPointX > containerToTest.getLeft()))
			return true;
		if((fBendPointX < 0) && (fXCenter + fBendPointX < containerToTest.getRight()))
			return true;
		// It is assumed that the LabelContainer for the MouseOver Element is created first, since it is
		// rendered first. So the the following marker collision detection should work for now.
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

	public void clearLabels() {
		alLabels.clear();
		alLeftContainers.clear();
		alRightContainers.clear();
		iMaxSegmentDepth = 0;
	}

	public synchronized static LabelManager get() {
		if (instance == null) {
			instance = new LabelManager();
		}
		return instance;
	}
}
