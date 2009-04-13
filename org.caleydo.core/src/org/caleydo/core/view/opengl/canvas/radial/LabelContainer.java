package org.caleydo.core.view.opengl.canvas.radial;

import gleem.linalg.Vec2f;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.media.opengl.GL;

import com.sun.opengl.util.j2d.TextRenderer;


public class LabelContainer {
	
	private static float CONTAINER_BOUNDARY_SPACING = 0.02f;
	private static float CONTAINER_LINE_SPACING = 0.01f;
	
	private float fWidth;
	private float fHeight;
	private float fXContainerLeft;
	private float fYContainerCenter;
	private float fLabelScaling;

	private ArrayList<String> alLineTexts;
	private ArrayList<Vec2f> alLinePositions;
	private ArrayList<Float> alLineHeights;
	private TextRenderer textRenderer;

	public LabelContainer(float fXContainerLeft, float fYContainerCenter, float fLabelScaling, TextRenderer textRenderer) {

		this.fXContainerLeft = fXContainerLeft;
		this.fYContainerCenter = fYContainerCenter;
		this.fLabelScaling = fLabelScaling;
		this.textRenderer = textRenderer;
		alLineTexts = new ArrayList<String>();
		alLinePositions = new ArrayList<Vec2f>();
		alLineHeights = new ArrayList<Float>();
		fWidth = 0;
		fHeight = 0;
	}

	public void addTextLine(String sText) {

		Rectangle2D bounds = textRenderer.getBounds(sText);
		float fTextHeight = (float) bounds.getHeight() * fLabelScaling;
		float fTextWidth = (float) bounds.getWidth() * fLabelScaling;
		float fYLinePosition;

		if ((fTextWidth + 2.0f * CONTAINER_BOUNDARY_SPACING) > fWidth) {
			fWidth = 2.0f * CONTAINER_BOUNDARY_SPACING + fTextWidth;
		}

		float fXLinePosition = fXContainerLeft + CONTAINER_BOUNDARY_SPACING;

		if (alLineTexts.size() == 0) {
			fHeight += 2.0f * CONTAINER_BOUNDARY_SPACING + fTextHeight;
			fYLinePosition =
				fYContainerCenter + (fHeight / 2.0f) - CONTAINER_BOUNDARY_SPACING - fTextHeight;
		}
		else {
			fHeight += CONTAINER_LINE_SPACING + fTextHeight;
			updateLinePositions();
			fYLinePosition =
				alLinePositions.get(alLinePositions.size() - 1).y() - CONTAINER_LINE_SPACING
					- fTextHeight;
		}

		alLineTexts.add(sText);
		alLinePositions.add(new Vec2f(fXLinePosition, fYLinePosition));
		alLineHeights.add(new Float(fTextHeight));
	}

	public void setContainerPosition(float fXContainerLeft, float fYContainerCenter) {
		this.fXContainerLeft = fXContainerLeft;
		this.fYContainerCenter = fYContainerCenter;
		updateLinePositions();
	}

	private void updateLinePositions() {

		if (alLineTexts.size() == 0) {
			return;
		}
		else {
			float fXLinePosition = fXContainerLeft + CONTAINER_BOUNDARY_SPACING;

			Vec2f vecCurrentPosition = alLinePositions.get(0);
			float fCurrentYPosition =
				fYContainerCenter + (fHeight / 2.0f) - CONTAINER_BOUNDARY_SPACING - alLineHeights.get(0);
			vecCurrentPosition.set(fXLinePosition, fCurrentYPosition);

			for (int i = 1; i < alLinePositions.size(); i++) {
				vecCurrentPosition = alLinePositions.get(i);
				vecCurrentPosition.set(fXLinePosition, alLinePositions.get(i - 1).y()
					- CONTAINER_LINE_SPACING - alLineHeights.get(i));
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
		
		if(bDrawLabelBackground) {
			gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);
			gl.glColor4f(1, 1, 1, 0.4f);
			
			gl.glBegin(GL.GL_POLYGON);
			gl.glVertex3f(fXContainerLeft, getTop(), 0);
			gl.glVertex3f(getRight(), getTop(), 0);
			gl.glVertex3f(getRight(), getBottom(), 0);
			gl.glVertex3f(fXContainerLeft, getBottom(), 0);
			gl.glEnd();
			gl.glPopAttrib();
		}
		
		textRenderer.begin3DRendering();
		for (int i = 0; i < alLineTexts.size(); i++) {
			textRenderer.draw3D(alLineTexts.get(i), alLinePositions.get(i).x(), alLinePositions.get(i).y(),
				0, fLabelScaling);
		}
		textRenderer.end3DRendering();
		textRenderer.flush();
		
	}

	public ArrayList<String> getLineTexts() {
		return alLineTexts;
	}

	public ArrayList<Vec2f> getLinePositions() {
		return alLinePositions;
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

	public float getFLabelScaling() {
		return fLabelScaling;
	}

	public void setFLabelScaling(float labelScaling) {
		fLabelScaling = labelScaling;
	}
	
}