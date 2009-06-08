package org.caleydo.core.view.opengl.canvas.radial;

import java.util.ArrayList;

import javax.media.opengl.GL;

import com.sun.opengl.util.j2d.TextRenderer;

public class LabelContainer {

	private static float CONTAINER_BOUNDARY_SPACING = 0.03f;
	private static float CONTAINER_LINE_SPACING = 0.02f;

	private float fWidth;
	private float fHeight;
	private float fXContainerLeft;
	private float fYContainerCenter;
	private float fLabelScaling;

	private ArrayList<LabelLine> alLabelLines;
	private TextRenderer textRenderer;

	public LabelContainer(float fXContainerLeft, float fYContainerCenter, float fLabelScaling,
		TextRenderer textRenderer) {

		this.fXContainerLeft = fXContainerLeft;
		this.fYContainerCenter = fYContainerCenter;
		this.fLabelScaling = fLabelScaling;
		this.textRenderer = textRenderer;
		alLabelLines = new ArrayList<LabelLine>();
		fWidth = 0;
		fHeight = 0;
	}

	public void addLabelLines(ArrayList<LabelLine> alLines) {

		for (LabelLine currentLine : alLines) {
			currentLine.calculateSize(textRenderer, fLabelScaling);
			addLine(currentLine);
		}
	}

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
			fYLinePosition = fYContainerCenter + (fHeight / 2.0f) - CONTAINER_BOUNDARY_SPACING - fLineHeight;
		}
		else {
			fHeight += CONTAINER_LINE_SPACING + fLineHeight;
			updateLinePositions();
			LabelLine lastLine = alLabelLines.get(alLabelLines.size() - 1);
			fYLinePosition = lastLine.getPosition().y() - CONTAINER_LINE_SPACING - fLineHeight;
		}
		
		labelLine.setPosition(fXLinePosition, fYLinePosition);
		alLabelLines.add(labelLine);
	}

	public void setContainerPosition(float fXContainerLeft, float fYContainerCenter) {
		this.fXContainerLeft = fXContainerLeft;
		this.fYContainerCenter = fYContainerCenter;
		updateLinePositions();
	}

	private void updateLinePositions() {

		if (alLabelLines.size() == 0) {
			return;
		}
		else {
			float fXLinePosition = fXContainerLeft + CONTAINER_BOUNDARY_SPACING;

			LabelLine firstLine = alLabelLines.get(0);
			float fYLinePosition =
				fYContainerCenter + (fHeight / 2.0f) - CONTAINER_BOUNDARY_SPACING - firstLine.getHeight();
			firstLine.setPosition(fXLinePosition, fYLinePosition);

			for (int i = 1; i < alLabelLines.size(); i++) {
				LabelLine currentLine = alLabelLines.get(i);
				fYLinePosition -= (currentLine.getHeight() + CONTAINER_LINE_SPACING);
				currentLine.setPosition(fXLinePosition, fYLinePosition);
			}
		}
	}

	public boolean doContainersCollide(LabelContainer container) {

		if (getTop() < container.getBottom() || container.getTop() < getBottom()
			|| container.getRight() < getLeft() || getRight() < container.getLeft()) {
			return false;
		}
		return true;
	}

	public void draw(GL gl, boolean bDrawLabelBackground) {

		gl.glLoadIdentity();

		if (bDrawLabelBackground) {
			gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);
			gl.glColor4f(1, 1, 1, 0.6f);

			gl.glBegin(GL.GL_POLYGON);
			gl.glVertex3f(fXContainerLeft, getTop(), 0);
			gl.glVertex3f(getRight(), getTop(), 0);
			gl.glVertex3f(getRight(), getBottom(), 0);
			gl.glVertex3f(fXContainerLeft, getBottom(), 0);
			gl.glEnd();
			gl.glPopAttrib();
		}
		
		for(LabelLine currentLine : alLabelLines) {
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

	public float getLabelScaling() {
		return fLabelScaling;
	}

	public void setLabelScaling(float fLabelScaling) {
		this.fLabelScaling = fLabelScaling;
	}

}