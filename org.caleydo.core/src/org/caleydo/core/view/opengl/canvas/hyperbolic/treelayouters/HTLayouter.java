package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.EDrawAbleNodeDetailLevel;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.IDrawAbleNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.projections.HyperbolicGlobeProjection;

// import gleem.linalg.Vec3f;

// import java.util.ArrayList;

// import gleem.linalg.Vec3f;

public final class HTLayouter
	extends ATreeLayouter
	implements ITreeLayouter {

	float fXCoord = 0.0f;
	float fYCoord = 0.0f;
	float fCenterX = 0.0f;
	float fCenterY = 0.0f;
	float fCenterZ = 0.0f;
	int iLineIDDummy = 0;
	float fDepth = 6.0f;
	float fRadius = 0;
	
	Map<IDrawAbleNode, Integer> mNodeSpaceRec;

	public HTLayouter(IViewFrustum frustum, PickingManager pickingManager, int iViewID) {
		super(frustum, pickingManager, iViewID, new HyperbolicGlobeProjection(1));

	}

	@Override
	public void renderTreeLayout() {
//		fRadius = (fViewSpaceYAbs/2)/HyperbolicRenderStyle.MAX_DEPTH;
		fRadius = 0.7f;
		updateSizeInfo();
		mNodeSpaceRec = new HashMap<IDrawAbleNode, Integer>();
		fDepth = tree.getDepth();
		// TODO: put it into abstract class
		setFCenterX(fWidth / 2);
		setFCenterY(fHeight / 2);
		setFCenterZ(0.0f);
		// if (bHyperbolicFlag) {
		// treeProjector =
		// new HyperbolicGlobeProjection(1, fHeight, fWidth, fCenterZ, fViewSpaceX, fViewSpaceXAbs,
		// fViewSpaceY, fViewSpaceYAbs);
		// }
		if (tree == null)
			return;

		float fNodeSize = HyperbolicRenderStyle.MAX_NODE_SIZE;

		IDrawAbleNode rootNode = tree.getRoot();

		rootNode.setDetailLevel(EDrawAbleNodeDetailLevel.Low);
		placeNode(rootNode, fCenterX, fCenterY, fCenterZ, fNodeSize, fNodeSize);

		// RECURSIVE TREE LAYOUTER
		int fLayer = 2;
		// float fRadius = 0.7f;
		// float fRadius = 3.0f;

		// float fNumberOfNodesInLayer = 3.0f;// + layer;//node.getNumberOfNodesInLayer(layer);
		float fNumberOfNodesInLayer = tree.getNumberOfElementsInLayer((int) fLayer);
		float fCurrentNodeCount = 0.0f;
		
		int overall = 0;
		if(tree.hasChildren(rootNode))
			overall = updateNodespace(tree.getChildren(rootNode), 2);
		mNodeSpaceRec.put(rootNode, overall);
		
		if (tree.hasChildren(rootNode)) {
			ArrayList<IDrawAbleNode> childs = new ArrayList<IDrawAbleNode>();
			childs = tree.getChildren(rootNode);
//			getMaxNumberOfSiblingsInLayer(rootNode, fLayer+1, fLayer);
			for (IDrawAbleNode tmpChild : childs) {

				fCurrentNodeCount++;
				float fFirstLayerAngle = calculateCircle((calculateNewRadius(tree.getNumberOfElementsInLayer(fLayer))), fCurrentNodeCount, fNumberOfNodesInLayer);
				// float space = calculateChildSpace(fRadius + 0.2f, fNumberOfNodesInLayer);

				fNodeSize =
					HyperbolicRenderStyle.MAX_NODE_SIZE
						* (float) Math.pow(HyperbolicRenderStyle.NODE_SCALING_PER_LAYER, fLayer);
				tmpChild.setDetailLevel(EDrawAbleNodeDetailLevel.Low);

				// placeNode(tmpChild, getFXCoord(), getFYCoord(), 0.0f, fNodeSize, fNodeSize);
				// DrawAbleHyperbolicGeometryConnection conn = new
				// DrawAbleHyperbolicGeometryConnection(rootNode, tmpChild, fvViewCenterPoint, fViewRadius);
				// Vec3f splinePoint = new Vec3f();
				// splinePoint = conn.findSplinePoint();
				//				
				// if (bHyperbolicFlag) {
				// // Vec3f tmpPoint = new Vec3f();
				// // tmpPoint = treeProjector.projectCoordinates(new Vec3f(getFXCoord(), getFYCoord(),
				// // 0.02f));
				// // splinePoint = treeProjector.projectCoordinates(splinePoint);
				// // placeNodeAndProject(tmpChild, tmpPoint.x(), tmpPoint.y(), tmpPoint.z(), fNodeSize,
				// // fNodeSize);
				// placeNodeAndProject(tmpChild, fXCoord, fYCoord, 0.0f, fNodeSize, fNodeSize,
				// treeProjector);
				//
				// }
				// else
				placeNode(tmpChild, fXCoord, fYCoord, 0.0f, fNodeSize, fNodeSize);

				// placeConnection(new DrawAbleHyperbolicGeometryConnection(rootNode, tmpChild, treeProjector,
				// fvViewCenterPoint, fViewRadius));

				// placeConnection(new DrawAbleHyperbolicLayoutConnector(rootNode, tmpChild, treeProjector));
				placeConnection(rootNode, tmpChild);
				calculateRecursiveLayout(tmpChild, fRadius, fFirstLayerAngle * fCurrentNodeCount, fLayer + 1,
					fNodeSize, fXCoord, fYCoord);

			}
		}

		return;
	}

	public float getFCenterZ() {
		return fCenterZ;
	}

	public void setFCenterZ(float centerZ) {
		fCenterZ = centerZ;
	}

	public float calculateRecursiveLayout(IDrawAbleNode node, float fRadius, float fParentAngle,
		int fLayer, float fNodeSize, float fXCoordOfParent, float fYCoordOfParent) {
		if (fLayer <= fDepth) {
			// float fNumberOfNodesInNewLayer = fNumberOfNodesInLayer;// +
			// layer;//node.getNumberOfNodesInLayer(layer);
			// float fNumberOfNodesInNewLayer = tree.getNumberOfElementsInLayer((int) fLayer);

			// float fChildSpace = calculateChildSpace(fRadius, fNumberOfNodesInNewLayer);

			float fRadiusUpdate = 0;
			if (tree.hasChildren(node)) {

				// float fDeltaRadius = 5.0f/(fLayer*6);
				// float childs = 3.0f; // node.getNumberOfChildren()

				ArrayList<IDrawAbleNode> childsOfCurrentNode = new ArrayList<IDrawAbleNode>();
				childsOfCurrentNode = tree.getChildren(node);
				float fDeltaRadius = fRadius;
//				float fDeltaRadius = calculateNewRadius(tree.getNumberOfElementsInLayer(fLayer));
				float fNumberOfChildsOfNode = childsOfCurrentNode.size();
				float fChildSpace = (fDeltaRadius * (float) Math.PI) / (fNumberOfChildsOfNode + 1);
//				float fChildSpace = (fDeltaRadius * (float) Math.PI) / ((fNumberOfChildsOfNode + 1));
//				float fChildSpace = (fDeltaRadius * (float) Math.PI) / ((tree.getNumberOfElementsInLayer(fLayer+1)/100)*fNumberOfChildsOfNode + 1);
//				float fChildSpace = (fRadius* (float) Math.PI * ((tree.getNumberOfElementsInLayer(fLayer+1)/2)/fNumberOfChildsOfNode));
				
				float fChildCount = 0.0f;
				float fChildAngle = 0.0f;
				// for (float numChilds = 1; numChilds <= childs; numChilds++) {
				// use it in future
				for (IDrawAbleNode tmpChild : childsOfCurrentNode) {
					fChildCount++;

					// float parentAngle = (angle*currentStep);// - (angle);
					// float parentAngle = angle;
					// float alphaHalfOffset = (angle/ 2);

					// childAngle =
					// calculateChildAngle(parentAngle , space/fNumberOfNodesInNewLayer , radius, numChilds)
					// + alphaHalfOffset;

					fChildAngle = (calculateChildAngle(fParentAngle, fChildSpace, fDeltaRadius, fLayer-1))/(fLayer-1);
//					fChildAngle = calculateChildAngle(fParentAngle, fChildSpace, fDeltaRadius, fLayer-1);// +
					// alphaHalfOffset;
					// calcualteChildPosition(radius, parentAngle + childAngle*numChilds, numChilds);
					// float realChildAngle = parentAngle + (childAngle*(numChilds - 1));
					float fRealChildAngle =
						fParentAngle - ((fChildAngle * (fNumberOfChildsOfNode - 1)) / 2) + fChildAngle
							* (fChildCount - 1);
					// calcualteChildPosition(fRadius, fRealChildAngle, fChildCount, fXCoordOfParent,
					// fYCoordOfParent);
					calcualteChildPosition(fDeltaRadius, fRealChildAngle, fChildCount, fXCoordOfParent,
						fYCoordOfParent);

					float fXCoord1 = fXCoord;
					float fYCoord2 = fYCoord;
					// if(tree.hasChildren(tmpChild))
					{
						// float fLayerOfBranch =
						// calculateRecursiveLayout(tmpChild, fDeltaRadius, fRealChildAngle, fLayer + 1,
						// fNodeSize, fXCoord, fYCoord);
						// drawLine(fXCoordOfParent, fYCoordOfParent, fXCoord, fYCoord);
						// childAngle =
						// calculateChildAngle(alpha * fCurrentNode, space / childs, childRadius) * numChilds
						// + (alpha / 2);
						// calcualteChildPosition(childRadius, childAngle, numChilds);
						fNodeSize =
							HyperbolicRenderStyle.MAX_NODE_SIZE
								* (float) Math.pow(HyperbolicRenderStyle.NODE_SCALING_PER_LAYER, fLayer+1);
						tmpChild.setDetailLevel(EDrawAbleNodeDetailLevel.Low);
						// DrawAbleHyperbolicGeometryGlobeProjection p = new
						// DrawAbleHyperbolicGeometryGlobeProjection(node, tmpChild, fvViewCenterPoint,
						// fHeight/2);
						// ITreeProjection p = new HyperbolicGlobeProjection(1, fHeight, fWidth, 1.0f,
						// fViewSpaceX, fViewSpaceXAbs, fViewSpaceY, fViewSpaceYAbs);
						// {
						// Vec2f fvFirstPoint = new Vec2f();
						// Vec2f fvSecondPoint = new Vec2f();
						//
						// fvFirstPoint.set(fXCoordOfParent, fYCoordOfParent);
						// fvSecondPoint.set(fXCoord, fYCoord);
						//					
						// float line = 0;
						// line = p.projectLineOnGlobe( fvFirstPoint, fvSecondPoint);

						// Vec3f pSpP1 =
						// new Vec3f( + vSE.x() / 2.0f, pStartP.y() + vSE.y() / 2.0f, pStartP.z() + vSE.z()
						// / 2.0f);
						//				
						// placeNode(tmpChild, getFXCoord(), getFYCoord(), 0.0f, fNodeSize, fNodeSize);
						// DrawAbleHyperbolicGeometryConnection conn = new
						// DrawAbleHyperbolicGeometryConnection(node, tmpChild, fvViewCenterPoint,
						// fViewRadius);
						// Vec3f splinePoint = new Vec3f();
						// splinePoint = conn.findSplinePoint();
						// if (bHyperbolicFlag) {
						// Vec3f tmpLinPointCoord = new Vec3f();
						// Vec3f projectedPointCoord = new Vec3f();
						// tmpLinPointCoord.set(fXCoord, fYCoord, 0.0f);
						// projectedPointCoord = treeProjector.projectCoordinates(tmpLinPointCoord);
						// splinePoint = treeProjector.projectCoordinates(splinePoint);
						// placeNodeAndProject(tmpChild, projectedPointCoord.x(), projectedPointCoord.y(),
						// projectedPointCoord.z(), fNodeSize, fNodeSize, treeProjector);
						// placeNodeAndProject(tmpChild, fXCoord1, fYCoord2, 0.0f, fNodeSize, fNodeSize,
						// treeProjector);
						// }
						// else
						placeNode(tmpChild, fXCoord1, fYCoord2, 0.0f, fNodeSize, fNodeSize);
						// placeConnection(new DrawAbleHyperbolicLayoutConnector(node, tmpChild,
						// treeProjector));
						placeConnection(node, tmpChild);
						calculateRecursiveLayout(tmpChild, fDeltaRadius, fRealChildAngle, fLayer + 1,
							fNodeSize, fXCoord, fYCoord);

						// node.drawAtPostion(gl, getFXCoord(), getFYCoord(), 0, fNodeSize, 0.2f,
						// EDrawAbleNodeDetailLevel.Low);
						// drawLine(getFXCoord(), getFYCoord(), x, y);
					}

				}
			}
			else
			{
				fRadiusUpdate = fRadius * (float) Math.PI * (tree.getNumberOfElementsInLayer(fLayer)/2)/tree.getChildren(tree.getParent(node)).size();
			}

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

	// public float () {
	// return fXCoord;
	// }

	public void setFXCoord(float coord) {
		fXCoord = coord;
	}

	// public float fYCoord() {
	// return fYCoord;
	// }

	public void setFYCoord(float coord) {
		fYCoord = coord;
	}

	private float calculateCircle(float fRadius, float fCurrent_step, float fNumberOfElements) {
		float fPhi = (float) (2 * Math.PI) / fNumberOfElements;

		setFXCoord((float) (fCenterX + fRadius * (Math.cos(fPhi * fCurrent_step))));
		setFYCoord((float) (fCenterY + fRadius * (Math.sin(fPhi * fCurrent_step))));
		// setFXCoord((float) (fCenterX + radius * Math.cos(phi2 * current_step)));
		// setFYCoord((float) (fCenterY + radius * Math.sin(phi2 * current_step)));
		return fPhi;
	}

	private float calculateChildSpace(float fRadius, float fNumberOfNodesInLayer) {

		float fAmount = (float) (2 * Math.PI) * fRadius;
		float fSegment = fAmount / fNumberOfNodesInLayer;

		return fSegment;
	}

	private float calculateChildAngle(float fParentAngle, float fChildSpace, float fRadius, float fCurrentStep) {

		float fChildAngle = ((fChildSpace / fRadius));
		// float angle = ((space / radius)*currentStep);
		// float angle = parentAngle - space / radius;

		return fChildAngle;
	}

	private void calcualteChildPosition(float fRadius, float fAngle, float fCurrentStep, float fParentXCoord,
		float fParentYCoord) {

		setFXCoord((float) (fParentXCoord + fRadius * Math.cos(fAngle)));
		setFYCoord((float) (fParentYCoord + fRadius * Math.sin(fAngle)));

	}
	private float calculateNewRadius(int fNumberOfNodesInLayer){
		
//		float fNewRadius = this.fRadius * fViewSpaceXAbs/100 * fDepth;
//		float fNewRadius = fViewSpaceXAbs/100 * fDepth;
//		float fNewRadius = this.fRadius * fNumberOfNodesInLayer * 0.1f;
		float fNewRadius = this.fRadius * fNumberOfNodesInLayer * 0.1f;
		if(fNewRadius < 0.3)
			return (float)Math.min(fViewSpaceXAbs, fViewSpaceYAbs)/100 * fRadius * 10.0f;
		else
			return fNewRadius;
	}
	
	
	private int updateNodespace(ArrayList<IDrawAbleNode> children, int iLayer) {
		if(iLayer > HyperbolicRenderStyle.MAX_DEPTH)
			return 0;
		int overall = children.size();
		for(IDrawAbleNode node : children){
			int temp = 0;
			if(tree.hasChildren(node))
				temp = updateNodespace(tree.getChildren(node), iLayer + 1);
			mNodeSpaceRec.put(node, temp);
			overall = (temp > overall ? temp : overall);
		}
		return overall;
	}


	// private void drawLine(float fFirstXCoord, float fFirstYCoord, float fSecondXCoord, float
	// fSecondYCoord){
	// IDrawAbleConnection line =
	// DrawAbleConnectionsFactory.getDrawAbleConnection(HyperbolicRenderStyle.HYPERBOLIC_TREE_LAYOUTER_CONNECTION_TYPE,
	// iLineIDDummy++,iLineIDDummy++);
	// ArrayList<Vec3f> points = new ArrayList<Vec3f>();
	// Vec3f p1 = new Vec3f();
	// Vec3f p2 = new Vec3f();
	// p1.setX(fFirstXCoord);
	// p1.setY(fFirstYCoord);
	// p1.setZ(1.0f);
	// p2.setX(fSecondXCoord);
	// p2.setY(fSecondYCoord);
	// p2.setZ(1.0f);
	//		
	//		
	// points.add(p1);
	// points.add(p2);
	// //line.setConnectionColor3f(1.0f, 1.0f, 0.0f);
	// placeConnection(line, points);
	// // line.drawConnectionFromStartToEnd(gl, points, 2.0f);
	// }

}
