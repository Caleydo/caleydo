package org.caleydo.core.view.opengl.util;

import java.util.ArrayList;

import javax.media.opengl.GL;

/**
 * @author Marc Streit
 */
public class GLStarEffectRenderer
{

	private ArrayList<float[]> fAlStarPoints = new ArrayList<float[]>();

	public void calculateStarPoints(int iVertexCount, float fRadius, float fCenterPointX,
			float fCenterPointY)
	{

		float fAngleRad = (float) (2 * Math.PI / iVertexCount);
		float[] fArPoint = null;
		fAlStarPoints.clear();

		// Store center point in index 0
		fArPoint = new float[2];
		fArPoint[0] = fCenterPointX;
		fArPoint[1] = fCenterPointY;
		fAlStarPoints.add(fArPoint);

		fCenterPointY += fRadius;

		for (int iVertexIndex = 0; iVertexIndex < iVertexCount; iVertexIndex++)
		{
			fArPoint = new float[2];

			fArPoint[0] = (float) (fCenterPointX * Math.cos(fAngleRad * iVertexIndex) - fCenterPointY
					* Math.sin(fAngleRad * iVertexIndex));
			fArPoint[1] = (float) (fCenterPointY * Math.cos(fAngleRad * iVertexIndex) + fCenterPointX
					* Math.sin(fAngleRad * iVertexIndex));

			fAlStarPoints.add(fArPoint);

		}
	}

	public ArrayList<float[]> getStarPoints()
	{

		return fAlStarPoints;
	}

	public static void drawStar(final GL gl, final ArrayList<float[]> alStarPoints)
	{

		float[] fArPoint = new float[2];

		gl.glLineWidth(3);
		gl.glColor4f(0.2f, 0.2f, 0.2f, 1f);

		float[] fArCenterPoint = alStarPoints.get(0);

		gl.glBegin(GL.GL_LINES);
		for (int iVertexIndex = 1; iVertexIndex < alStarPoints.size(); iVertexIndex++)
		{
			fArPoint = alStarPoints.get(iVertexIndex);

			gl.glVertex3f(fArCenterPoint[0], fArCenterPoint[1], 0f);
			gl.glVertex3f(fArPoint[0], fArPoint[1], 1f);
		}
		gl.glEnd();
	}
}
