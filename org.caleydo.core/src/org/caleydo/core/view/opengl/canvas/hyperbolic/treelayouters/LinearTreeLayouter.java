package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.ADrawAbleNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.EDrawAbleNodeDetailLevel;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.IDrawAbleNode;

public final class LinearTreeLayouter
	extends ATreeLayouter {

	public LinearTreeLayouter(IViewFrustum frustum) {
		super(frustum);
	}

	@Override
	public void renderTreeLayout(GL gl, Tree<ADrawAbleNode> tree) {

		updateSizeInfo();
		if (tree == null)
			return;

		int deph = tree.getDepth();
		//int iNumNodesInLayer = 1;
		IDrawAbleNode rootNode = tree.getRoot();

		float f = 0;
		for (int i = 0; i < deph; i++)
			f = f + (float) Math.pow((double) HyperbolicRenderStyle.LIN_TREE_Y_SCALING_PER_LAYER, i);
		float fLayerSpacing = fViewSpaceYAbs / f;
		// float fNodeSpacing = fViewSpaceXAbs / (iNumNodesInLayer + 1);
		float fYOff = fViewSpaceY[1] - fLayerSpacing;
		
		for (int i = 1; i <= deph; ++i) {
			float fYCoord = fYOff + fLayerSpacing / 2f;
			float fNodeSpacing = fViewSpaceXAbs / (i + 1);
			for (int j = 1; j <= i; j++) {
				float fXCoord = fViewSpaceX[0] + j * fNodeSpacing;
				float fZCoord = 0;
				rootNode.drawAtPostion(gl, fXCoord, fYCoord, fZCoord, fLayerSpacing * 0.8f, fNodeSpacing,
					EDrawAbleNodeDetailLevel.Low);
				// positionAndDrawNode(gl, rootNode, i, fLayerSpacing, j,
				// fNodeSpacing,EDrawAbleNodeDetailLevel.VeryHigh);

			}
			fLayerSpacing = fLayerSpacing * HyperbolicRenderStyle.LIN_TREE_Y_SCALING_PER_LAYER;
			fYOff = fYOff - fLayerSpacing;
		}

		// first place root node

		// ArrayList<Vec3f> a = tree.getRoot().drawAtPostion(gl, fHigh/2f, fWidth/2f, 0.0f, 1f, 2f,
		// EDrawAbleNodeDetailLevel.VeryHigh);
		// ArrayList<Vec3f> b =tree.getRoot().drawAtPostion(gl, fHigh/4f, fWidth/4f, 0.0f, 1.5f, 3f,
		// EDrawAbleNodeDetailLevel.High);

		// gl.glColor4f(1, 0, 0, 1);
		// gl.glBegin(GL.GL_LINE);
		//		
		// for(int i = 0; i < HyperbolicRenderStyle.DA_OBJ_NUM_CONTACT_POINTS; i++)
		// {
		// gl.glVertex3f(a.get(i).get(0), a.get(i).get(1), a.get(i).get(2));
		// gl.glVertex3f(b.get(i).get(0), b.get(i).get(1), b.get(i).get(2));
		// }
		// gl.glVertex3f(fXCoord + fDrawWidth, fYCoord - fDrawHeight, fZCoord);
		// gl.glVertex3f(fXCoord - fDrawWidth, fYCoord - fDrawHeight, fZCoord);
		// gl.glVertex3f(fXCoord - fDrawWidth, fYCoord + fDrawHeight, fZCoord);
		gl.glEnd();
		gl.glFlush();

		// LinearLineFactory lLFactory = new LinearLineFactory(a,b);
		// DrawAbleLinearLine line1 = new DrawAbleLinearLine();
		// line1.drawLineFromStartToEnd(gl, lLFactory.getStartPoint(), lLFactory.getEndPoint(), 5.0f);

		return;
	}

	private ArrayList<Vec3f> positionAndDrawNode(GL gl, IDrawAbleNode node, int iLayer, float fLayerSpacing,
		int iNodeNrOnLayer, float fNodeSpacing, EDrawAbleNodeDetailLevel eDetailLevel) {
		float fXCoord = fViewSpaceX[0] + iNodeNrOnLayer * fNodeSpacing;
		float fYCoord = fViewSpaceY[1] - iLayer * fLayerSpacing + fLayerSpacing / 2f;
		float fZCoord = 0;
		return node.drawAtPostion(gl, fXCoord, fYCoord, fZCoord, fLayerSpacing - fLayerSpacing * 0.1f,
			fNodeSpacing - fNodeSpacing * 0.1f, eDetailLevel);

	}

}
