/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.radial;

import gleem.linalg.Vec2f;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.view.opengl.picking.PickingManager;

/**
 * APDDrawingStrategy encapsulates the functionality of drawing child
 * indicators, which indicate, that a partial disc has children.
 *
 * @author Christian Partl
 */
public abstract class APDDrawingStrategyChildIndicator extends APDDrawingStrategy {
	private static final float MAX_TRIANGLE_FITTING_TEST_ANGLE = 45.0f;

	private float[] fArChildIndicatorColor;
	/**
	 * Transparency value for the partial discs
	 */
	protected float fTransparency;

	/**
	 * Constructor.
	 *
	 * @param pickingManager
	 *            The picking manager that should handle the picking of the
	 *            drawn elements.
	 * @param viewID
	 *            ID of the view where the elements will be displayed. Needed
	 *            for picking.
	 */
	public APDDrawingStrategyChildIndicator(PickingManager pickingManager, int viewID) {
		super(pickingManager, viewID);
		fArChildIndicatorColor = RadialHierarchyRenderStyle.CHILD_INDICATOR_COLOR;
		fTransparency = 1.0f;
	}

	@Override
	public abstract void drawFullCircle(GL2 gl, GLU glu, PartialDisc pdDiscToDraw);

	@Override
	public abstract void drawPartialDisc(GL2 gl, GLU glu, PartialDisc pdDiscToDraw);

	/**
	 * Draws a child indicator (triangle) according to the parameters of a
	 * partial disc.
	 *
	 * @param gl
	 *            GL2 object that shall be used for drawing.
	 * @param fInnerRadius
	 *            Inner radius of the partial disc.
	 * @param fWidth
	 *            Width of the partial disc.
	 * @param fStartAngle
	 *            Start angle of the partial disc.
	 * @param fAngle
	 *            Angle of the partial disc.
	 */
	protected void drawChildIndicator(GL2 gl, float fInnerRadius, float fWidth,
			float fStartAngle, float fAngle) {

		float fMidAngle = fStartAngle + (fAngle / 2.0f);
		float fOuterRadius = fInnerRadius + fWidth;
		float fTriangleTopRadius = fOuterRadius
				* RadialHierarchyRenderStyle.TRIANGLE_TOP_RADIUS_PERCENTAGE;
		float fTriangleHeight = fOuterRadius
				* RadialHierarchyRenderStyle.TRIANGLE_HEIGHT_PERCENTAGE;

		Vec2f vecTriangleTop = getRadialPosition(fMidAngle, fTriangleTopRadius);

		// Calculation of triangle width is only necessary if angle is small
		if (fAngle < MAX_TRIANGLE_FITTING_TEST_ANGLE) {
			Vec2f vecTriangleLeft = getRadialPosition(fStartAngle, fOuterRadius);
			Vec2f vecTriangleRight = getRadialPosition(fStartAngle + fAngle, fOuterRadius);

			float fTriangleWidth = (float) Math.sqrt(Math.pow(vecTriangleLeft.x()
					- vecTriangleRight.x(), 2)
					+ Math.pow(vecTriangleLeft.y() - vecTriangleRight.y(), 2));

			if (fTriangleWidth < 2.0f * fTriangleHeight) {
				drawIsoscelesTriangle(gl, fTriangleHeight, fTriangleWidth / 2.0f,
						vecTriangleTop, -fMidAngle);
				return;
			}
		}
		drawIsoscelesTriangle(gl, fTriangleHeight, fTriangleHeight, vecTriangleTop,
				-fMidAngle);
	}

	/**
	 * Calculates the position of a point using the angle and radius (distance)
	 * from the center that is assumed at (0,0).
	 *
	 * @param fAngle
	 *            Angle that determines the direction of the radius.
	 * @param fRadius
	 *            Distance from the center.
	 * @return Radial Position.
	 */
	private Vec2f getRadialPosition(float fAngle, float fRadius) {
		fAngle = -1 * (fAngle - 90);
		Vec2f vecPosition = new Vec2f();
		float fAngleRadiants = fAngle * (float) Math.PI / 180.0f;
		vecPosition.setX((float) Math.cos(fAngleRadiants) * fRadius);
		vecPosition.setY((float) Math.sin(fAngleRadiants) * fRadius);
		return vecPosition;
	}

	/**
	 * Draws an isosceles triangle.
	 *
	 * @param gl
	 *            GL2 object that shall be used for drawing.
	 * @param fHeight
	 *            Height of the triangle. Distance from the baseline to the top.
	 * @param fHalfWidth
	 *            The half of the width of the baseline.
	 * @param vecTriangleTop
	 *            Position of the top of the triangle.
	 * @param fRotationAngle
	 *            Rotation of the triangle.
	 */
	private void drawIsoscelesTriangle(GL2 gl, float fHeight, float fHalfWidth,
			Vec2f vecTriangleTop, float fRotationAngle) {

		gl.glPushMatrix();
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);

		gl.glTranslatef(vecTriangleTop.x(), vecTriangleTop.y(), -0.1f);
		gl.glRotatef(fRotationAngle, 0, 0, 1);
		gl.glColor4fv(fArChildIndicatorColor, 0);

		gl.glBegin(GL.GL_TRIANGLES);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(fHalfWidth, -fHeight, 0);
		gl.glVertex3f(-fHalfWidth, -fHeight, 0);
		gl.glEnd();

		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(fHalfWidth, -fHeight, 0);
		gl.glVertex3f(-fHalfWidth, -fHeight, 0);
		gl.glEnd();

		gl.glPopAttrib();
		gl.glPopMatrix();
	}

	/**
	 * Gets the color which is used to draw the child indicator.
	 *
	 * @return RGB-Color which is used to draw the child indicator.
	 */
	public float[] getChildIndicatorColor() {
		return fArChildIndicatorColor;
	}

	/**
	 * Sets the color which is used to draw the child indicator.
	 *
	 * @param fArChildIndicatorColor
	 *            RGB-Color which shall be used to draw the child indicator.
	 *            Only the first three values of the array will be used.
	 */
	public void setChildIndicatorColor(float[] fArChildIndicatorColor) {
		if (fArChildIndicatorColor.length >= 3) {
			this.fArChildIndicatorColor = fArChildIndicatorColor;
		}
	}

	public void setTransparency(float fTransparency) {
		this.fTransparency = fTransparency;
	}

	public float getTransparency() {
		return fTransparency;
	}

}
