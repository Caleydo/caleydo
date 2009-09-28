package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.projections;

import java.util.List;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;

public class DefaultProjection
	extends ATreeProjection {

//	public DefaultProjection(int iID, float fHeight, float fWidth, float fDepth, float[] fViewSpaceX,
//		float fViewSpaceXAbs, float[] fViewSpaceY, float fViewSpaceYAbs) {
//		super(iID, fHeight, fWidth, fDepth, fViewSpaceX, fViewSpaceXAbs, fViewSpaceY, fViewSpaceYAbs);
//	}

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

		gl.glVertex3f(fViewSpaceX[0], fViewSpaceY[0], -0.1f);
		gl.glVertex3f(fViewSpaceX[1], fViewSpaceY[0], -0.1f);
		gl.glVertex3f(fViewSpaceX[1], fViewSpaceY[1], -0.1f);
		gl.glVertex3f(fViewSpaceX[0], fViewSpaceY[1], -0.1f);
		
		gl.glEnd();

	}

	@Override
	public Vec3f projectCoordinates(Vec3f fvCoords) {
		return fvCoords;
	}
	
	@Override
	public List<Vec3f> getEuclidianCanvas() {
		return null;
	}
	
	

}
