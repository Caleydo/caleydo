package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines;

import java.util.List;

import gleem.linalg.Vec3f;
import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;

public final class DrawAbleLinearConnection
	extends ADrawAbleConnection {

	// TODO: define line color scheme!!!
	public DrawAbleLinearConnection() {
		this.fRed = HyperbolicRenderStyle.DA_OBJ_FALLBACK_COLORSCHEME[0];
		this.fGreen = HyperbolicRenderStyle.DA_OBJ_FALLBACK_COLORSCHEME[1];
		this.fBlue = HyperbolicRenderStyle.DA_OBJ_FALLBACK_COLORSCHEME[2];
		this.fAlpha = HyperbolicRenderStyle.DA_OBJ_FALLBACK_ALPHA;
	}

	// TODO: maybe define thickness in renderstyle
	@Override
	public void drawLineFromStartToEnd(GL gl, List<Vec3f> lPoints, float fThickness) {
		gl.glColor4f(this.fRed, this.fGreen, this.fBlue, this.fAlpha);
		gl.glLineWidth(fThickness);
		
		for (int i = 0; i < lPoints.size()-1; i++){	
			gl.glBegin(GL.GL_LINE);
			gl.glVertex3f(lPoints.get(i).x(), lPoints.get(i).y(), lPoints.get(i).z());
			gl.glVertex3f(lPoints.get(i+1).x(), lPoints.get(i+1).y(), lPoints.get(i+1).z());
			gl.glEnd();
		}
		gl.glFlush();
	}

}
