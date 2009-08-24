package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters;

// import gleem.linalg.Vec3f;

// import java.util.ArrayList;

import java.util.ArrayList;
import java.util.List;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.ADrawAbleNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.EDrawAbleNodeDetailLevel;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.IDrawAbleNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines.DrawAbleSplineConnection;
import org.caleydo.core.view.opengl.util.spline.Spline3D;

public final class HTLayouter
	extends ATreeLayouter
	implements ITreeLayouter {

	float fXCoord;
	float fYCoord;
	float fCenterX;
	float fCenterY;
	float childAngle;
	float fDepth = 10.0f; // tree.getDepth();

	ArrayList<Vec3f> vec = new ArrayList();

	public HTLayouter(IViewFrustum frustum) {
		super(frustum);

	}

	@Override
	public void renderTreeLayout(GL gl, Tree<ADrawAbleNode> tree) {
		updateSizeInfo();
		if (tree == null)
			return;

		// TODO: put it into abstract class
		setFCenterX(fViewSpaceXAbs / 2); // it brings in constructor strange results????
		setFCenterY(fViewSpaceYAbs / 2);

		IDrawAbleNode rootNode = tree.getRoot();
		// rootNode.drawAtPostion(gl, fCenterX, fCenterY, 0, 1 * 0.8f, 2,
		// EDrawAbleNodeDetailLevel.Low);

		float fNumberOfNodesInLayer = 5.0f; // tree.getNumberOfElementsInLayer(layer)
		float fNodeSize = 0.2f;

		// TEST RADIAL LAYOUT
		// for (float fCurrentRadius = 1; fCurrentRadius < fDepth; fCurrentRadius++) {
		//			
		// //for testing the tree will add the radius number to the nodes number
		// for (float fCurrentNode = 1; fCurrentNode <= fNumberOfNodesInLayer+fCurrentRadius; fCurrentNode++)
		// {
		// calculateCircle((fCurrentRadius / 3.5f), fCurrentNode, fNumberOfNodesInLayer+fCurrentRadius);
		// rootNode.drawAtPostion(gl, getFXCoord(), getFYCoord(), 0, fNodeSize, 0.2f,
		// EDrawAbleNodeDetailLevel.Low);
		// }
		// fNodeSize = fNodeSize * HyperbolicRenderStyle.LIN_TREE_Y_SCALING_PER_LAYER;//TODO: generate own
		// scaling
		// }

		// TEST FIRST AND SECOND LAYER
//		for (float fCurrentNode = 1; fCurrentNode <= fNumberOfNodesInLayer; fCurrentNode++) {
//
//			float childRadius = 1.0f;
//			float alpha = calculateCircle((0.5f), fCurrentNode, fNumberOfNodesInLayer);
//			float space = calculateChildSpace(childRadius, fNumberOfNodesInLayer);
//			rootNode.drawAtPostion(gl, getFXCoord(), getFYCoord(), 0, fNodeSize, 0.2f,
//				EDrawAbleNodeDetailLevel.Low);
//			childAngle = 0.0f;
//			float childs = 5.0f; // node.getNumberOfChildren()
//			for (float numChilds = 1; numChilds < childs; numChilds++) {
//
//				childAngle =
//					calculateChildAngle(alpha * fCurrentNode, space / childs, childRadius) * numChilds
//						+ (alpha / 2);
//				calcualteChildPosition(childRadius, childAngle, numChilds);
//				rootNode.drawAtPostion(gl, getFXCoord(), getFYCoord(), 0, fNodeSize, 0.2f,
//					EDrawAbleNodeDetailLevel.Low);
//			}
//
//		}
//		fNodeSize = fNodeSize * HyperbolicRenderStyle.LIN_TREE_Y_SCALING_PER_LAYER;// TODO: generate own
//		// scaling
//		gl.glFlush();

		// RECURSIVE TREE LAYOUTER
		float layer = 1.0f;
		float firstRadius = 0.5f;
		calculateRecursiveLayout(gl, rootNode,  firstRadius , 0, layer, fNodeSize);
		
		fNodeSize = fNodeSize * HyperbolicRenderStyle.LIN_TREE_Y_SCALING_PER_LAYER;// TODO: generate own
		

		// TEST SPLINE

		// gl.glVertex3f(3.0f, 3.0f, 0);

		Vec3f p1 = new Vec3f(3.0f, 3.0f, 0);
		Vec3f p2 = new Vec3f(1.1f, 2.8f, 0);
		Vec3f p3 = new Vec3f(1.0f, 1.0f, 0);

		vec.add(p1);
		vec.add(p2);
		vec.add(p3);

		// spline = new Spline3D((Vec3f[])vec, 0.5f, 0.5f);
		// DrawAbleSplineConnection spline = new DrawAbleSplineConnection();
		// spline.drawConnectionFromStartToEnd(gl, vec, 0.4f);

		// }
		// for (int k = 1 ; k <= 10; k++)
		// {
		// circle(1.0f, k, 10);
		// rootNode.drawAtPostion(gl, x, y, 0, 0.2f, 2,
		// EDrawAbleNodeDetailLevel.Low);
		// }

		// float fNodeSpacing = fViewSpaceXAbs / (iNumNodesInLayer + 1);
		// float fYOff = fViewSpaceY[1] - fLayerSpacing;
		// float fYOff = 1;
		// float radius = 0.1f;

		// for (int i = 0; i < deph; i++) {
		// for (int j = 0; j < 10; j++) {
		//				
		//				
		// //float fYCoord = fYOff + fLayerSpacing / (float)Math.sin(i);
		// float fNodeSpacing = fViewSpaceXAbs / (i + 1);
		// //float fXCoord = fViewSpaceX[0] + j * fNodeSpacing;
		// float fZCoord = 0;
		// //circle(fCenterY+fLayerSpacing, j, i);
		// circle(radius, 5, 5);
		// //circle(radius, j, i);
		//				
		// rootNode.drawAtPostion(gl, x, y, fZCoord, 0.1f, 0.2f,
		// EDrawAbleNodeDetailLevel.Low);
		//				
		// // rootNode.drawAtPostion(gl, x, y, fZCoord, fLayerSpacing * 0.8f, fNodeSpacing,
		// // EDrawAbleNodeDetailLevel.Low);
		// // positionAndDrawNode(gl, rootNode, i, fLayerSpacing, j,
		// // fNodeSpacing,EDrawAbleNodeDetailLevel.VeryHigh);
		//
		// }
		// fLayerSpacing = fLayerSpacing * HyperbolicRenderStyle.LIN_TREE_Y_SCALING_PER_LAYER;
		// fYOff = fYOff - fLayerSpacing;
		// radius = radius + 0.1f;
		// }

		// gl.glEnd();

		return;
	}

	// private ArrayList<Vec3f> positionAndDrawNode(GL gl, IDrawAbleNode node, int iLayer, float
	// fLayerSpacing,
	// int iNodeNrOnLayer, float fNodeSpacing, EDrawAbleNodeDetailLevel eDetailLevel) {
	// float fXCoord = fViewSpaceX[0] + iNodeNrOnLayer * fNodeSpacing;
	// float fYCoord = fViewSpaceY[1] - iLayer * fLayerSpacing + fLayerSpacing / 2f;
	// float fZCoord = 0;
	// return node.drawAtPostion(gl, fXCoord, fYCoord, fZCoord, fLayerSpacing - fLayerSpacing * 0.1f,
	// fNodeSpacing - fNodeSpacing * 0.1f, eDetailLevel);
	//
	// }

	public float calculateRecursiveLayout(GL gl, IDrawAbleNode node, float radius, float angle, float layer, float fNodeSize) {
		float fNumberOfNodesInLayer = 5.0f + layer;//node.getNumberOfNodesInLayer(layer);
		for (float fCurrentNode = 1; fCurrentNode <= fNumberOfNodesInLayer; fCurrentNode++) {

			float childRadius = radius + 0.5f;
			float alpha = calculateCircle((radius), fCurrentNode, fNumberOfNodesInLayer);
			float space = calculateChildSpace(childRadius, fNumberOfNodesInLayer);
			if(layer < 5){
			calculateRecursiveLayout(gl, node, childRadius, angle, layer+1, fNodeSize);
			}
			else
			{
			node.drawAtPostion(gl, getFXCoord(), getFYCoord(), 0, fNodeSize, 0.2f,
				EDrawAbleNodeDetailLevel.Low);
			childAngle = 0.0f;
			float childs = 5.0f; // node.getNumberOfChildren()
//			for (float numChilds = 1; numChilds < childs; numChilds++) {
//
//				childAngle =
//					calculateChildAngle(alpha * fCurrentNode, space / childs, childRadius) * numChilds
//						+ (alpha / 2);
//				calcualteChildPosition(childRadius, childAngle, numChilds);
//				node.drawAtPostion(gl, getFXCoord(), getFYCoord(), 0, fNodeSize, 0.2f,
//					EDrawAbleNodeDetailLevel.Low);
//			}
			}
		}
		return angle;
	}

	public float getFCenterX() {
		return fCenterX;
	}

	public void setFCenterX(float centerX) {
		fCenterX = centerX;
	}

	public float getFCenterY() {
		return fCenterY;
	}

	public void setFCenterY(float centerY) {
		fCenterY = centerY;
	}

	public float getFXCoord() {
		return fXCoord;
	}

	public void setFXCoord(float coord) {
		fXCoord = coord;
	}

	public float getFYCoord() {
		return fYCoord;
	}

	public void setFYCoord(float coord) {
		fYCoord = coord;
	}

	private float calculateCircle(float radius, float current_step, float numberOfElements) {
		float phi = (float) ((float) 2 * Math.PI / numberOfElements);
		// float phi = 1;
		setFXCoord((float) (fCenterX + radius * Math.cos(phi * current_step)));
		setFYCoord((float) (fCenterY + radius * Math.sin(phi * current_step)));
		return phi;
	}

	private float calculateChildSpace(float radius, float numberOfNodesInLayer) {

		float amount = (float) (2 * Math.PI) * radius;
		float b = amount / numberOfNodesInLayer;

		return b;
	}

	private float calculateChildAngle(float parentAngle, float space, float radius) {

		float angle = parentAngle - space / radius;

		return angle;
	}

	private void calcualteChildPosition(float radius, float phi, float currentStep) {
		// setFXCoord((float) (fCenterX + radius * Math.cos(phi * currentStep)));
		// setFYCoord( (float) (fCenterY + radius * Math.sin(phi * currentStep)));
		setFXCoord((float) (fCenterX + radius * Math.cos(phi)));
		setFYCoord((float) (fCenterY + radius * Math.sin(phi)));

	}

}
