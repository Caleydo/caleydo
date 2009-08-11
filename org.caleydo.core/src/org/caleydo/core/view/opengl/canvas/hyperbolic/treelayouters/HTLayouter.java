package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters;

//import gleem.linalg.Vec3f;

//import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.ADrawAbleNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.EDrawAbleNodeDetailLevel;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.IDrawAbleNode;
import org.caleydo.core.data.graph.tree.Tree;

public final class HTLayouter
	extends ATreeLayouter
	implements ITreeLayouter {

	float fXCoord;
	float fYCoord;
	float fCenterX;
	float fCenterY;

	public HTLayouter(IViewFrustum frustum) {
		super(frustum);


	}



	@Override
	public void renderTreeLayout(GL gl, Tree<ADrawAbleNode> tree) {
		updateSizeInfo();
		if (tree == null)
			return;
		
		//TODO: put it into abstract class
		setFCenterX(fViewSpaceXAbs / 2);	//it brings in constructor strange results????
		setFCenterY(fViewSpaceYAbs / 2);


		IDrawAbleNode rootNode = tree.getRoot();
		//rootNode.drawAtPostion(gl, fCenterX, fCenterY, 0, 1 * 0.8f, 2,
		// EDrawAbleNodeDetailLevel.Low);

		float fDepth = 10.0f;		//tree.getDepth();
		float fNumberOfNodesInLayer = 5.0f;	//tree.getNumberOfElementsInLayer(layer)
		float fNodeSize = 0.2f;
//		for (float fCurrentRadius = 1; fCurrentRadius < fDepth; fCurrentRadius++) {
//			
//			//for testing the tree will add the radius number to the nodes number 
//			for (float fCurrentNode = 1; fCurrentNode <= fNumberOfNodesInLayer+fCurrentRadius; fCurrentNode++) {
//				calculateCircle((fCurrentRadius / 3.5f), fCurrentNode, fNumberOfNodesInLayer+fCurrentRadius);
//				rootNode.drawAtPostion(gl, getFXCoord(), getFYCoord(), 0, fNodeSize, 0.2f, EDrawAbleNodeDetailLevel.Low);
//			}
//			fNodeSize = fNodeSize * HyperbolicRenderStyle.LIN_TREE_Y_SCALING_PER_LAYER;//TODO: generate own scaling
//		}
		
		//for (float fCurrentRadius = 1; fCurrentRadius < fDepth; fCurrentRadius++) {
			
			//for testing the tree will add the radius number to the nodes number 
			for (float fCurrentNode = 1; fCurrentNode <= fNumberOfNodesInLayer; fCurrentNode++) {
				calculateCircle((0.5f), fCurrentNode, fNumberOfNodesInLayer);
				rootNode.drawAtPostion(gl, getFXCoord(), getFYCoord(), 0, fNodeSize, 0.2f, EDrawAbleNodeDetailLevel.Low);
//				TransformationTest test = new TransformationTest(getFCenterX(),getFCenterY(),getFXCoord(),getFYCoord());
//				test.generateNewView();
			}
			fNodeSize = fNodeSize * HyperbolicRenderStyle.LIN_TREE_Y_SCALING_PER_LAYER;//TODO: generate own scaling
		//}
		// for (int k = 1 ; k <= 10; k++)
		// {
		// circle(1.0f, k, 10);
		// rootNode.drawAtPostion(gl, x, y, 0, 0.2f, 2,
		// EDrawAbleNodeDetailLevel.Low);
		// }


		// float fNodeSpacing = fViewSpaceXAbs / (iNumNodesInLayer + 1);
		// float fYOff = fViewSpaceY[1] - fLayerSpacing;
//		float fYOff = 1;
//		float radius = 0.1f;

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

		gl.glEnd();
		gl.glFlush();

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

	private void calculateCircle(float radius, float current_step, float numberOfElements) {
		float phix = (float) ((float) 2 * Math.PI / numberOfElements);
		float phiy = (float) ((float) 2 * Math.PI / numberOfElements);
		// float phi = 1;
		setFXCoord((float) (fCenterX + radius * Math.cos(phix * current_step)));
		setFYCoord( (float) (fCenterY + radius * Math.sin(phiy * current_step)));
	}


}
