package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.projections;

import java.util.Arrays;
import java.util.List;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.IDrawAbleNode;

public class DefaultProjection
	extends ATreeProjection {

	private boolean bIsRadialCanvasRequested;

	// public DefaultProjection(int iID, float fHeight, float fWidth, float fDepth, float[] fViewSpaceX,
	// float fViewSpaceXAbs, float[] fViewSpaceY, float fViewSpaceYAbs) {
	// super(iID, fHeight, fWidth, fDepth, fViewSpaceX, fViewSpaceXAbs, fViewSpaceY, fViewSpaceYAbs);
	// }

	public DefaultProjection(int iID) {
		super(iID);
	}

	@Override
	public void drawCanvas(GL gl) {
		if (HyperbolicRenderStyle.PROJECTION_DRAW_CANVAS) {
			gl.glEnable(GL.GL_BLEND);
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			gl.glColor4fv(HyperbolicRenderStyle.DA_TREE_PROJECTION_COLORSHEME, 0);
			gl.glBegin(GL.GL_POLYGON);
		}
		else {
			gl.glColor4fv(HyperbolicRenderStyle.DA_TREE_PROJECTION_CANVAS_COLORSCHEME, 0);
			gl.glLineWidth(HyperbolicRenderStyle.DA_TREE_PROJECTION_CANVAS_THICKNESS);
			gl.glBegin(GL.GL_LINE_LOOP);
		}

		if (bIsRadialCanvasRequested) {
			for (int i = 0; i < 360; i++) {
				float angle = (float) (i * 2 * Math.PI / 180f);
				gl.glVertex3f((float) (fCenterPoint.x() + Math.cos(angle) * radius), (float) (fCenterPoint
					.y() + Math.sin(angle) * radius), -0.1f);
			}
		}
		else {
			gl.glVertex3f(fViewSpaceX[0], fViewSpaceY[0], -0.1f);
			gl.glVertex3f(fViewSpaceX[1], fViewSpaceY[0], -0.1f);
			gl.glVertex3f(fViewSpaceX[1], fViewSpaceY[1], -0.1f);
			gl.glVertex3f(fViewSpaceX[0], fViewSpaceY[1], -0.1f);
		}
		gl.glEnd();

	}

	@Override
	public Vec3f projectCoordinates(Vec3f fvCoords) {
		return fvCoords;
	}

	@Override
	public float[][] getEuclidianCanvas() {
		float[][] canvas = { fViewSpaceX, fViewSpaceY };
		return canvas;
	}

	@Override
	public Vec3f getNearestPointOnEuclidianBorder(Vec3f point) {

		

		Vec3f vec = point.minus(fCenterPoint);
		vec.normalize();
		vec.scale(radius);
		vec.add(fCenterPoint);
		return vec;
//		if (point.y() >= fViewSpaceY[1] / 2.0f)
//			return new Vec3f(point.x(), fViewSpaceY[1], point.z());
//		else
//			return new Vec3f(point.x(), fViewSpaceY[0], point.z());
		
		
	}

	@Override
	public float getProjectedLineFromCenterToBorder() {
		bIsRadialCanvasRequested = true;
		return Math.min(fViewSpaceXAbs/2, fViewSpaceYAbs/2);
	}

}
