package org.caleydo.core.view.opengl.canvas.radial;

import gleem.linalg.Vec2f;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

public abstract class PDDrawingStrategyChildIndicator
	extends PDDrawingStrategy {
	
	private static final float TRIANGLE_HEIGHT_PERCENTAGE = 0.03f;
	private static final float TRIANGLE_TOP_RADIUS_PERCENTAGE = 1.025f;
	private static final float MAX_TRIANGLE_FITTING_TEST_ANGLE = 45.0f;

	@Override
	public abstract void drawFullCircle(GL gl, GLU glu, PartialDisc pdDiscToDraw);

	@Override
	public abstract void drawPartialDisc(GL gl, GLU glu, PartialDisc pdDiscToDraw);
	
	protected void drawChildIndicator(GL gl, float fInnerRadius, float fWidth, float fStartAngle, float fAngle) {

		float fMidAngle = fStartAngle + (fAngle / 2.0f);
		float fOuterRadius = fInnerRadius + fWidth;
		float fTriangleTopRadius = fOuterRadius * TRIANGLE_TOP_RADIUS_PERCENTAGE;
		float fTriangleHeight = fOuterRadius * TRIANGLE_HEIGHT_PERCENTAGE;

		Vec2f vecTriangleTop = getRadialPosition(fMidAngle, fTriangleTopRadius);

		//Calculation of triangle width is only necessary if angle is small
		if (fAngle < MAX_TRIANGLE_FITTING_TEST_ANGLE) {
			Vec2f vecTriangleLeft = getRadialPosition(fStartAngle, fOuterRadius);
			Vec2f vecTriangleRight = getRadialPosition(fStartAngle + fAngle, fOuterRadius);

			float fTriangleWidth =
				(float) Math.sqrt(Math.pow(vecTriangleLeft.x() - vecTriangleRight.x(), 2)
					+ Math.pow(vecTriangleLeft.y() - vecTriangleRight.y(), 2));

			if (fTriangleWidth < 2.0f * fTriangleHeight) {
				drawIsoscelesTriangle(gl, fTriangleHeight, fTriangleWidth / 2.0f, vecTriangleTop, -fMidAngle);
				return;
			}
		}
		drawIsoscelesTriangle(gl, fTriangleHeight, fTriangleHeight, vecTriangleTop, -fMidAngle);
	}

	private Vec2f getRadialPosition(float fAngle, float fRadius) {
		fAngle = -1 * (fAngle - 90);
		Vec2f vecPosition = new Vec2f();
		float fAngleRadiants = fAngle * (float) Math.PI / 180.0f;
		vecPosition.setX((float) Math.cos(fAngleRadiants) * fRadius);
		vecPosition.setY((float) Math.sin(fAngleRadiants) * fRadius);
		return vecPosition;
	}

	private void drawIsoscelesTriangle(GL gl, float fHeight, float fHalfWidth, Vec2f vecTriangleTop,
		float fRotationAngle) {

		gl.glPushMatrix();
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);

		gl.glTranslatef(vecTriangleTop.x(), vecTriangleTop.y(), -0.1f);
		gl.glRotatef(fRotationAngle, 0, 0, 1);
		gl.glColor3f(0.3f, 0.3f, 0.3f);

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

}
