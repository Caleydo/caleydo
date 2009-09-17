package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters;

// import gleem.linalg.Vec3f;

// import java.util.ArrayList;


import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.util.ArrayList;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.EDrawAbleNodeDetailLevel;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.IDrawAbleNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines.DrawAbleHyperboicGeometryGlobeProjection;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines.DrawAbleHyperbolicGeometryConnection;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines.IDrawAbleConnection;
import org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.projections.HyperbolicGlobeProjection;
import org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.projections.ITreeProjection;



public final class HTLayouter
	extends ATreeLayouter
	implements ITreeLayouter {

	float fXCoord;
	float fYCoord;
	float fCenterX;
	float fCenterY;
	int iLineIDDummy = 0;
	//float childAngle;
	float fDepth = 6.0f; // tree.getDepth();
	
	boolean bHyperbolicFlag = true;
	

	//ArrayList<Vec3f> vec = new ArrayList<Vec3f>();

	public HTLayouter(IViewFrustum frustum, PickingManager pickingManager, int iViewID) {
		super(frustum,pickingManager, iViewID);
		

	}

	@Override
	public void renderTreeLayout() {
		updateSizeInfo();
		if(bHyperbolicFlag)
		{
		treeProjector = new HyperbolicGlobeProjection(1, fHeight, fWidth, 0.01f, fViewSpaceX,
			fViewSpaceXAbs, fViewSpaceY, fViewSpaceYAbs);
		}
		if (tree == null)
			return;

		fDepth = tree.getDepth();
		// TODO: put it into abstract class
		setFCenterX(fWidth / 2); // it brings in constructor strange results????
		setFCenterY(fHeight / 2);
		float fNodeSize = HyperbolicRenderStyle.MAX_NODE_SIZE;

		IDrawAbleNode rootNode = tree.getRoot();
		
		rootNode.setDetailLevel(EDrawAbleNodeDetailLevel.Low);
		placeNode(rootNode, fCenterX, fCenterY, 0, fNodeSize, fNodeSize);
	
		// RECURSIVE TREE LAYOUTER
		float fLayer = 2.0f;
		float fRadius = 0.6f;
//		float fRadius = 3.0f;
		
		//float fNumberOfNodesInLayer = 3.0f;// + layer;//node.getNumberOfNodesInLayer(layer);
		float fNumberOfNodesInLayer = tree.getNumberOfElementsInLayer((int)fLayer);
		float fCurrentNodeCount = 0;
		if(tree.hasChildren(rootNode)){
		ArrayList<IDrawAbleNode> childs = new ArrayList<IDrawAbleNode>();
		childs = tree.getChildren(rootNode);
		for (IDrawAbleNode tmpChild : childs) {

			fCurrentNodeCount++;
			float fFirstLayerAngle = calculateCircle((fRadius), fCurrentNodeCount, fNumberOfNodesInLayer);
		//	float space = calculateChildSpace(fRadius + 0.2f, fNumberOfNodesInLayer);
			
			fNodeSize = HyperbolicRenderStyle.MAX_NODE_SIZE * (float) Math.pow(HyperbolicRenderStyle.NODE_SCALING_PER_LAYER, fLayer);
			tmpChild.setDetailLevel(EDrawAbleNodeDetailLevel.Low);
			if(bHyperbolicFlag){
			Vec3f tmpPoint = new Vec3f();
			tmpPoint = treeProjector.projectCoordinates(new Vec3f(getFXCoord(), getFYCoord(), 0.02f));
			placeNode(tmpChild, tmpPoint.x(), tmpPoint.y(), tmpPoint.z(), fNodeSize, fNodeSize);
			}
			else
				placeNode(tmpChild, getFXCoord(), getFYCoord(), 0.0f, fNodeSize, fNodeSize);
			float fFirstChildX = getFXCoord();
			float fFirstChildY = getFYCoord();
			placeConnection(new DrawAbleHyperbolicGeometryConnection(rootNode, tmpChild, fvViewCenterPoint, fViewRadius));
			
			calculateRecursiveLayout(tmpChild, fRadius , fFirstLayerAngle*fCurrentNodeCount, fLayer+1, fNodeSize, getFXCoord(), getFYCoord());
		
		}
		}
		
		return;
	}



	public float calculateRecursiveLayout(IDrawAbleNode node, float fRadius, float fParentAngle, 
		float fLayer, float fNodeSize, float fXCoordOfParent, float fYCoordOfParent) {
		if (fLayer <= fDepth)
		{
		//float fNumberOfNodesInNewLayer = fNumberOfNodesInLayer;// + layer;//node.getNumberOfNodesInLayer(layer);
			float fNumberOfNodesInNewLayer = tree.getNumberOfElementsInLayer((int)fLayer);

			//float fChildSpace = calculateChildSpace(fRadius, fNumberOfNodesInNewLayer);
			
			if (tree.hasChildren(node))
				{	

//				float fDeltaRadius = 5.0f/(fLayer*6);
			//float childs = 3.0f; // node.getNumberOfChildren()
			
			ArrayList<IDrawAbleNode> childsOfCurrentNode = new ArrayList<IDrawAbleNode>();
			childsOfCurrentNode = tree.getChildren(node);
//			float fDeltaRadius = fRadius/1.95f;
			float fDeltaRadius = fRadius;
			float fChildSpace = (fDeltaRadius * (float)Math.PI)/(childsOfCurrentNode.size()+1);
			float fNumberOfChildsOfNode = childsOfCurrentNode.size();
			float fChildCount = 0.0f;
			float fChildAngle = 0.0f;
			//for (float numChilds = 1; numChilds <= childs; numChilds++) {
			//use it in future
			for(IDrawAbleNode tmpChild : childsOfCurrentNode){
				fChildCount++;
				
				//float parentAngle = (angle*currentStep);// - (angle);
				//float parentAngle = angle;
				//float alphaHalfOffset = (angle/ 2);
				
//				childAngle =
//					calculateChildAngle(parentAngle , space/fNumberOfNodesInNewLayer , radius, numChilds)
//						+ alphaHalfOffset;
				
				fChildAngle =
				calculateChildAngle(fParentAngle , fChildSpace ,  fDeltaRadius, fChildCount)
				
					;//+ alphaHalfOffset;
				//calcualteChildPosition(radius, parentAngle + childAngle*numChilds, numChilds);
//				float realChildAngle = parentAngle + (childAngle*(numChilds - 1));
				float fRealChildAngle = fParentAngle - ((fChildAngle*(fNumberOfChildsOfNode-1))/2) + fChildAngle*(fChildCount-1);
//				calcualteChildPosition(fRadius, fRealChildAngle, fChildCount, fXCoordOfParent, fYCoordOfParent);
				calcualteChildPosition(fDeltaRadius, fRealChildAngle, fChildCount, fXCoordOfParent, fYCoordOfParent);
				
				float fXCoord = getFXCoord();
				float fYCoord = getFYCoord();
				//if(tree.hasChildren(tmpChild))
				{
					float fLayerOfBranch = calculateRecursiveLayout(tmpChild, fDeltaRadius, fRealChildAngle, fLayer+1, fNodeSize, fXCoord, fYCoord);
					//drawLine(fXCoordOfParent, fYCoordOfParent, fXCoord, fYCoord);
//				childAngle =
//				calculateChildAngle(alpha * fCurrentNode, space / childs, childRadius) * numChilds
//					+ (alpha / 2);
//			calcualteChildPosition(childRadius, childAngle, numChilds);
				fNodeSize = HyperbolicRenderStyle.MAX_NODE_SIZE * (float) Math.pow(HyperbolicRenderStyle.NODE_SCALING_PER_LAYER, fLayer);
				tmpChild.setDetailLevel(EDrawAbleNodeDetailLevel.Low);
				DrawAbleHyperboicGeometryGlobeProjection p = new DrawAbleHyperboicGeometryGlobeProjection(node, tmpChild, fvViewCenterPoint, fHeight/2);
//				{
//					Vec2f fvFirstPoint = new Vec2f();
//					Vec2f fvSecondPoint = new Vec2f();
//
//					fvFirstPoint.set(fXCoordOfParent, fYCoordOfParent);
//					fvSecondPoint.set(fXCoord, fYCoord);
//					
//				float line = 0; 
//				line = p.projectLineOnGlobe( fvFirstPoint, fvSecondPoint);
				
				if(bHyperbolicFlag){
				Vec3f tmpLinPointCoord = new Vec3f();
				Vec3f projectedPointCoord = new Vec3f();
				tmpLinPointCoord.set(fXCoord, fYCoord, 0.0f);
				projectedPointCoord = treeProjector.projectCoordinates(tmpLinPointCoord);
				placeNode(tmpChild, projectedPointCoord.x(), projectedPointCoord.y(), projectedPointCoord.z(), fNodeSize, fNodeSize);
				}
				else
					placeNode(tmpChild, fXCoord, fYCoord, 0.0f, fNodeSize, fNodeSize);
				placeConnection(new DrawAbleHyperbolicGeometryConnection(node, tmpChild, fvViewCenterPoint, fViewRadius));
				
				
//				node.drawAtPostion(gl, getFXCoord(), getFYCoord(), 0, fNodeSize, 0.2f,
//					EDrawAbleNodeDetailLevel.Low);
				//drawLine(getFXCoord(), getFYCoord(), x, y);
				}
				
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
		}
			
			return fLayer;
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

	private float calculateCircle(float fRadius, float fCurrent_step, float fNumberOfElements) {
		float fPhi = (float) (2 * Math.PI) / fNumberOfElements;

		setFXCoord((float) (fCenterX + fRadius * (Math.cos(fPhi * fCurrent_step))));
		setFYCoord((float) (fCenterY + fRadius * (Math.sin(fPhi * fCurrent_step))));
//		setFXCoord((float) (fCenterX + radius * Math.cos(phi2 * current_step)));
//		setFYCoord((float) (fCenterY + radius * Math.sin(phi2 * current_step)));
		return fPhi;
	}

	private float calculateChildSpace(float fRadius, float fNumberOfNodesInLayer) {

		float fAmount = (float) (2 * Math.PI) * fRadius;
		float fSegment = fAmount / fNumberOfNodesInLayer;

		return fSegment;
	}

	private float calculateChildAngle(float fParentAngle, float fChildSpace, float fRadius, float fCurrentStep) {

		float fChildAngle = ((fChildSpace / fRadius));
		//float angle = ((space / radius)*currentStep);
		//float angle = parentAngle - space / radius;

		return fChildAngle;
	}

	private void calcualteChildPosition(float fRadius, float fAngle, float fCurrentStep, float fParentXCoord, float fParentYCoord) {
		// setFXCoord((float) (fCenterX + radius * Math.cos(phi * currentStep)));
		// setFYCoord( (float) (fCenterY + radius * Math.sin(phi * currentStep)));
//		setFXCoord((float) (fCenterX + fRadius * Math.cos(fAngle)));
//		setFYCoord((float) (fCenterY + fRadius * Math.sin(fAngle)));
		setFXCoord((float) (fParentXCoord + fRadius * Math.cos(fAngle)));
		setFYCoord((float) (fParentYCoord + fRadius * Math.sin(fAngle)));

	}
//	private void drawLine(float fFirstXCoord, float fFirstYCoord, float fSecondXCoord, float fSecondYCoord){
//		IDrawAbleConnection line = DrawAbleConnectionsFactory.getDrawAbleConnection(HyperbolicRenderStyle.HYPERBOLIC_TREE_LAYOUTER_CONNECTION_TYPE, iLineIDDummy++,iLineIDDummy++);
//		ArrayList<Vec3f> points = new ArrayList<Vec3f>();
//		Vec3f p1 = new Vec3f();
//		Vec3f p2 = new Vec3f();
//		p1.setX(fFirstXCoord);
//		p1.setY(fFirstYCoord);
//		p1.setZ(1.0f);
//		p2.setX(fSecondXCoord);
//		p2.setY(fSecondYCoord);
//		p2.setZ(1.0f);
//		
//		
//		points.add(p1);
//		points.add(p2);
//		//line.setConnectionColor3f(1.0f, 1.0f, 0.0f);
//		placeConnection(line, points);
//	//	line.drawConnectionFromStartToEnd(gl, points, 2.0f);
//	}

	@Override
	public void animateToNewTree(Tree<IDrawAbleNode> tree) {
		// TODO Auto-generated method stub
		
	}

}
