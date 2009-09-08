package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters;

// import gleem.linalg.Vec3f;

// import java.util.ArrayList;

import java.util.ArrayList;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.IDrawAbleNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.EDrawAbleNodeDetailLevel;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines.DrawAbleConnectionsFactory;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines.IDrawAbleConnection;
import org.caleydo.core.view.opengl.util.spline.Spline3D;

public final class HTLayouter
	extends ATreeLayouter
	implements ITreeLayouter {

	float fXCoord;
	float fYCoord;
	float fCenterX;
	float fCenterY;
	//float childAngle;
	float fDepth = 3.0f; // tree.getDepth();

	ArrayList<Vec3f> vec = new ArrayList();

	public HTLayouter(IViewFrustum frustum, PickingManager pickingManager, int iViewID) {
		super(frustum,pickingManager, iViewID);

	}

	@Override
	public void renderTreeLayout() {
		updateSizeInfo();
		if (tree == null)
			return;

		// TODO: put it into abstract class
		setFCenterX(fWidth / 2); // it brings in constructor strange results????
		setFCenterY(fHeight / 2);

		IDrawAbleNode rootNode = tree.getRoot();
	
		
		// rootNode.drawAtPostion(gl, fCenterX, fCenterY, 0, 1 * 0.8f, 2,
		// EDrawAbleNodeDetailLevel.Low);
		float fNodeSize = 0.05f;

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
//			float childAngle = 0.0f;
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
		float fRadius = 0.1f;
		
		float fNumberOfNodesInLayer = 3.0f;// + layer;//node.getNumberOfNodesInLayer(layer);
		for (float fCurrentNode = 1; fCurrentNode <= fNumberOfNodesInLayer; fCurrentNode++) {

			float alpha = calculateCircle((fRadius), fCurrentNode, fNumberOfNodesInLayer);
		//	float space = calculateChildSpace(fRadius + 0.2f, fNumberOfNodesInLayer);
			
			rootNode.setDetailLevel(EDrawAbleNodeDetailLevel.Low);
			placeNode(rootNode, getFXCoord(), getFYCoord(), 0, fNodeSize, 0.2f);
			drawLine(fCenterX, fCenterY, getFXCoord(), getFYCoord());
			//rootNode.drawAtPostion(gl, getFXCoord(), getFYCoord(), 0, fNodeSize, 0.2f,EDrawAbleNodeDetailLevel.Low);
			//if (tree.hasChildren(node)){
			

		
		calculateRecursiveLayout(rootNode, fRadius + 0.2f , alpha, layer, fCurrentNode, fNumberOfNodesInLayer*fNumberOfNodesInLayer, fNodeSize, getFXCoord(), getFYCoord());
//		calculateRecursiveLayout(gl, node, tree, radius + 0.3f, childAngle, layer+1, numChilds+currentStep, fNumberOfNodesInNewLayer*childs, fNodeSize);
//		}
		}
		
		
//		drawLine(gl, 1.0f, 1.0f, 2.0f, 2.0f);
	//	fNodeSize = fNodeSize * HyperbolicRenderStyle.LIN_TREE_Y_SCALING_PER_LAYER;// TODO: generate own
		

		// TEST SPLINE

		// gl.glVertex3f(3.0f, 3.0f, 0);

//		Vec3f p1 = new Vec3f(3.0f, 3.0f, 0);
//		Vec3f p2 = new Vec3f(1.1f, 2.8f, 0);
//		Vec3f p3 = new Vec3f(1.0f, 1.0f, 0);
//
//		vec.add(p1);
//		vec.add(p2);
//		vec.add(p3);

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

	public float calculateRecursiveLayout(IDrawAbleNode node, float radius, float angle, 
		float layer, float currentStep, float fNumberOfNodesInLayer, float fNodeSize, float xCoord, float yCoord) {
		float fNumberOfNodesInNewLayer = fNumberOfNodesInLayer;// + layer;//node.getNumberOfNodesInLayer(layer);
//		for (float fCurrentNode = 1; fCurrentNode <= fNumberOfNodesInLayer; fCurrentNode++) {
//
//			float childRadius = radius + 0.5f;
//			float alpha = calculateCircle((radius), fCurrentNode, fNumberOfNodesInLayer);
		// float space = calculateChildSpace(radius, fNumberOfNodesInNewLayer);
			float space = calculateChildSpace(radius, fNumberOfNodesInNewLayer);
//			
//			node.drawAtPostion(gl, getFXCoord(), getFYCoord(), 0, fNodeSize, 0.2f,
//			EDrawAbleNodeDetailLevel.Low);
//			//if (tree.hasChildren(node)){

			if (layer < fDepth){
			float childs = 3.0f; // node.getNumberOfChildren()
			int childCount = 0;
			float childAngle = 0.0f;
			for (float numChilds = 1; numChilds <= childs; numChilds++) {
			//use it in future
			//for(ADrawAbleNode tmpNode : tree.getChildren(node)){
				childCount++;
				
				float parentAngle = (angle*currentStep) - (angle);
				//float parentAngle = (angle) - (angle/numChilds);
				float alphaHalfOffset = (angle/ 2);
				
//				childAngle =
//					calculateChildAngle(parentAngle , space/fNumberOfNodesInNewLayer , radius, numChilds)
//						+ alphaHalfOffset;
				
				childAngle =
				calculateChildAngle(parentAngle , space , radius, numChilds)
					;//+ alphaHalfOffset;
				calcualteChildPosition(radius, parentAngle + childAngle*numChilds, numChilds);
				
//				childAngle =
//				calculateChildAngle(alpha * fCurrentNode, space / childs, childRadius) * numChilds
//					+ (alpha / 2);
//			calcualteChildPosition(childRadius, childAngle, numChilds);
				node.setDetailLevel(EDrawAbleNodeDetailLevel.Low);
				placeNode(node, getFXCoord(), getFYCoord(), 0, fNodeSize, 0.2f);
//				node.drawAtPostion(gl, getFXCoord(), getFYCoord(), 0, fNodeSize, 0.2f,
//					EDrawAbleNodeDetailLevel.Low);
				drawLine(xCoord, yCoord, getFXCoord(), getFYCoord());
				calculateRecursiveLayout(node, radius + layer*0.3f, childAngle, layer+1, numChilds, fNumberOfNodesInNewLayer*childs, fNodeSize, getFXCoord(), getFYCoord());
			}
			}

			//float childs = 5.0f; // node.getNumberOfChildren()
//			for (float numChilds = 1; numChilds < childs; numChilds++) {
//
//				childAngle =
//					calculateChildAngle(alpha * fCurrentNode, space / childs, childRadius) * numChilds
//						+ (alpha / 2);
//				calcualteChildPosition(childRadius, childAngle, numChilds);
//				node.drawAtPostion(gl, getFXCoord(), getFYCoord(), 0, fNodeSize, 0.2f,
//					EDrawAbleNodeDetailLevel.Low);
//			}
			
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
		float phi1 = (float) ((float) 2 * Math.PI / numberOfElements);
		// float phi = 1;
		//float phi2 = (float)Math.toDegrees(phi1);

		float cos = (float)Math.cos(phi1 * current_step);
		float sin = (float)Math.sin(phi1 * current_step);
		setFXCoord((float) (fCenterX + radius * cos));
		setFYCoord((float) (fCenterY + radius * sin));
//		setFXCoord((float) (fCenterX + radius * Math.cos(phi2 * current_step)));
//		setFYCoord((float) (fCenterY + radius * Math.sin(phi2 * current_step)));
		return phi1;
	}

	private float calculateChildSpace(float radius, float numberOfNodesInLayer) {

		float amount = (float) (2 * Math.PI) * radius;
		float b = amount / numberOfNodesInLayer;

		return b;
	}

	private float calculateChildAngle(float parentAngle, float space, float radius, float currentStep) {

		float angle = ((space / radius));
		//float angle = ((space / radius)*currentStep);
		//float angle = parentAngle - space / radius;

		return angle;
	}

	private void calcualteChildPosition(float radius, float phi, float currentStep) {
		// setFXCoord((float) (fCenterX + radius * Math.cos(phi * currentStep)));
		// setFYCoord( (float) (fCenterY + radius * Math.sin(phi * currentStep)));
		setFXCoord((float) (fCenterX + radius * Math.cos(phi)));
		setFYCoord((float) (fCenterY + radius * Math.sin(phi)));

	}
	private void drawLine(float firstX, float firstY, float secondX, float secondY){
		IDrawAbleConnection line = DrawAbleConnectionsFactory.getDrawAbleConnection(HyperbolicRenderStyle.HYPERBOLIC_TREE_LAYOUTER_CONNECTION_TYPE, 0,1);
		ArrayList<Vec3f> points = new ArrayList<Vec3f>();
		Vec3f p1 = new Vec3f();
		Vec3f p2 = new Vec3f();
		p1.setX(firstX);
		p1.setY(firstY);
		p1.setZ(1.0f);
		p2.setX(secondX);
		p2.setY(secondY);
		p2.setZ(1.0f);
		
		
		points.add(p1);
		points.add(p2);
		//line.setConnectionColor3f(1.0f, 1.0f, 0.0f);
		placeConnection(line, points);
	//	line.drawConnectionFromStartToEnd(gl, points, 2.0f);
	}

	@Override
	public void animateToNewTree(Tree<IDrawAbleNode> tree) {
		// TODO Auto-generated method stub
		
	}

}
