package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.ADrawableNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.EDrawAbleNodeDetailLevel;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.IDrawAbleNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.lineartree.Tree;

public final class LinearTreeLayouter
	extends ATreeLayouter {

	public LinearTreeLayouter(IViewFrustum frustum, Tree<ADrawableNode> tree) {
		super(frustum, tree);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void renderTreeLayout(GL gl) {
		
		if (tree == null)
			return;
		
		int deph = tree.getDeph();
		IDrawAbleNode rootNode = tree.getRoot();
		
		
		
		
		ArrayList<Vec3f> a = tree.getRoot().drawAtPostion(gl, fHigh/2f, fWidth/2f, 0.0f, 1f, 2f, EDrawAbleNodeDetailLevel.VeryHigh);
		ArrayList<Vec3f> b =tree.getRoot().drawAtPostion(gl, fHigh/4f, fWidth/4f, 0.0f, 1.5f, 3f, EDrawAbleNodeDetailLevel.High);
		
		gl.glColor4f(1, 0, 0, 1);
		gl.glBegin(GL.GL_LINE);
		
		for(int i = 0; i < HyperbolicRenderStyle.DA_OBJ_NUM_CONTACT_POINTS; i++)
		{
			gl.glVertex3f(a.get(i).get(0), a.get(i).get(1), a.get(i).get(2));
			gl.glVertex3f(b.get(i).get(0), b.get(i).get(1), b.get(i).get(2));
		}
		//gl.glVertex3f(fXCoord + fDrawWidth, fYCoord - fDrawHeight, fZCoord);
		//gl.glVertex3f(fXCoord - fDrawWidth, fYCoord - fDrawHeight, fZCoord);
		//gl.glVertex3f(fXCoord - fDrawWidth, fYCoord + fDrawHeight, fZCoord);
		gl.glEnd();
		gl.glFlush();
		
		return;
	}
}
