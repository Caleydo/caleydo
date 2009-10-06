package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters;

import java.awt.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.EDrawAbleNodeDetailLevel;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.IDrawAbleNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.projections.DefaultProjection;
import org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.projections.HyperbolicGlobeProjection;

// import gleem.linalg.Vec3f;

// import java.util.ArrayList;

// import gleem.linalg.Vec3f;

public final class HTGlobeLayouter
	extends ATreeLayouter
	implements ITreeLayouter {

	float fXCoord = 0.0f;
	float fYCoord = 0.0f;
	float fCenterX = 0.0f;
	float fCenterY = 0.0f;
	float fCenterZ = 0.0f;
	int iLineIDDummy = 0;
	int iDepth = 0;
	float fRadius = 0;
	float fViewAbleSpaceRadius = 0.0f;

	Map<IDrawAbleNode, Integer> mNodeSpaceRec;

	public HTGlobeLayouter(IViewFrustum frustum, PickingManager pickingManager, int iViewID,HyperbolicRenderStyle renderStyle, String strInformation) {
		super(frustum, pickingManager, iViewID, renderStyle, new HyperbolicGlobeProjection(1), strInformation);//new HyperbolicGlobeProjection(1));

	}

	@Override
	public void renderTreeLayout() {
		updateSizeInfo();
		// fRadius = (fViewSpaceYAbs/2)/HyperbolicRenderStyle.MAX_DEPTH;
		// fRadius = 0.7f;
//		iDepth = tree.getDepth();
		iDepth = Math.min(tree.getDepth(), HyperbolicRenderStyle.MAX_DEPTH);
		fViewAbleSpaceRadius = treeProjector.getProjectedLineFromCenterToBorder();
		// fRadius = fViewAbleSpaceRadius/fDepth;
		fRadius = fViewAbleSpaceRadius / (iDepth - 1);
		mNodeSpaceRec = new HashMap<IDrawAbleNode, Integer>();

		setFCenterX(fWidth / 2);
		setFCenterY(fHeight / 2);
		setFCenterZ(0.0f);

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

		// int overall = 0;
		// if(tree.hasChildren(rootNode))
		// overall = updateNodespace(tree.getChildren(rootNode), 2);
		// mNodeSpaceRec.put(rootNode, overall);

		if (tree.hasChildren(rootNode)) {
			ArrayList<IDrawAbleNode> childs = new ArrayList<IDrawAbleNode>();
			childs = tree.getChildren(rootNode);
			childs = sortChildList(childs);
			childs = findOptimalAdjustmentOfChildList(childs);

			// childs.
			// getMaxNumberOfSiblingsInLayer(rootNode, fLayer+1, fLayer);
			for (IDrawAbleNode tmpChild : childs) {

				fCurrentNodeCount++;
				float fFirstLayerAngle = calculateCircle(fRadius, fCurrentNodeCount, fNumberOfNodesInLayer);
				// float fFirstLayerAngle =
				// calculateCircle((calculateNewRadius(tree.getNumberOfElementsInLayer(fLayer))),
				// fCurrentNodeCount, fNumberOfNodesInLayer);
				// float space = calculateChildSpace(fRadius + 0.2f, fNumberOfNodesInLayer);

				fNodeSize =
					HyperbolicRenderStyle.MAX_NODE_SIZE
						* (float) Math.pow(HyperbolicRenderStyle.NODE_SCALING_PER_LAYER, fLayer);
				tmpChild.setDetailLevel(EDrawAbleNodeDetailLevel.Low);

				placeNode(tmpChild, fXCoord, fYCoord, 0.0f, fNodeSize, fNodeSize);
				placeConnection(rootNode, tmpChild);

				calculateRecursiveLayout(tmpChild, fRadius, fFirstLayerAngle * fCurrentNodeCount, fLayer + 1,
					fNodeSize, fXCoord, fYCoord);
				// bIsOptimzed = true;
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

	public float calculateRecursiveLayout(IDrawAbleNode node, float fRadius, float fParentAngle, int fLayer,
		float fNodeSize, float fXCoordOfParent, float fYCoordOfParent) {
		// if (fLayer <= fDepth) {
		// float fNumberOfNodesInNewLayer = fNumberOfNodesInLayer;// +
		// layer;//node.getNumberOfNodesInLayer(layer);
		// float fNumberOfNodesInNewLayer = tree.getNumberOfElementsInLayer((int) fLayer);

		// float fChildSpace = calculateChildSpace(fRadius, fNumberOfNodesInNewLayer);

		float fRadiusUpdate = 0;
		if (tree.hasChildren(node) && fLayer <= iDepth) {

			// float fDeltaRadius = 5.0f/(fLayer*6);
			// float childs = 3.0f; // node.getNumberOfChildren()

			ArrayList<IDrawAbleNode> childsOfCurrentNode = new ArrayList<IDrawAbleNode>();
			childsOfCurrentNode = tree.getChildren(node);
			childsOfCurrentNode = sortChildList(childsOfCurrentNode);
			childsOfCurrentNode = findOptimalAdjustmentOfChildList(childsOfCurrentNode);
			float fDeltaRadius = fRadius;
			// float fDeltaRadius = calculateNewRadius(tree.getNumberOfElementsInLayer(fLayer));
			float fNumberOfChildsOfNode = childsOfCurrentNode.size();
			float fChildSpace = (fDeltaRadius * (float) Math.PI) / (fNumberOfChildsOfNode + 1);
			// float fChildSpace = (fDeltaRadius * (float) Math.PI) / ((fNumberOfChildsOfNode + 1));
			// float fChildSpace = (fDeltaRadius * (float) Math.PI) /
			// ((tree.getNumberOfElementsInLayer(fLayer+1)/100)*fNumberOfChildsOfNode + 1);
			// float fChildSpace = (fRadius* (float) Math.PI *
			// ((tree.getNumberOfElementsInLayer(fLayer+1)/2)/fNumberOfChildsOfNode));

			float fChildCount = 0.0f;
			float fChildAngle = 0.0f;

			for (IDrawAbleNode tmpChild : childsOfCurrentNode) {
				fChildCount++;

				// float parentAngle = (angle*currentStep);// - (angle);
				// float parentAngle = angle;
				// float alphaHalfOffset = (angle/ 2);

				// childAngle =
				// calculateChildAngle(parentAngle , space/fNumberOfNodesInNewLayer , radius, numChilds)
				// + alphaHalfOffset;

				fChildAngle =
					(calculateChildAngle(fParentAngle, fChildSpace, fDeltaRadius, fLayer - 1)) / (fLayer - 1);
				// fChildAngle = calculateChildAngle(fParentAngle, fChildSpace, fDeltaRadius, fLayer-1);// +
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

				// float fLineToCenter = calculateLineLengthFromCenterToPoint(fXCoord, fYCoord);
				// if(fLayer == fDepth)
				// while(fLineToCenter > fViewAbleSpaceRadius)
				// {
				// fRadius --;
				// }

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
							* (float) Math.pow(HyperbolicRenderStyle.NODE_SCALING_PER_LAYER, fLayer + 1);
					tmpChild.setDetailLevel(EDrawAbleNodeDetailLevel.Low);

					placeNode(tmpChild, fXCoord1, fYCoord2, 0.0f, fNodeSize, fNodeSize);
					placeConnection(node, tmpChild);

					calculateRecursiveLayout(tmpChild, fDeltaRadius, fRealChildAngle, fLayer + 1, fNodeSize,
						fXCoord, fYCoord);

				}

			}
		}
		else {
			fRadiusUpdate =
				fRadius * (float) Math.PI * (tree.getNumberOfElementsInLayer(fLayer) / 2)
					/ tree.getChildren(tree.getParent(node)).size();
		}

		// }

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

	private float calculateNewRadius(int fNumberOfNodesInLayer) {

		// float fNewRadius = this.fRadius * fViewSpaceXAbs/100 * fDepth;
		// float fNewRadius = fViewSpaceXAbs/100 * fDepth;
		// float fNewRadius = this.fRadius * fNumberOfNodesInLayer * 0.1f;
		float fNewRadius = this.fRadius * fNumberOfNodesInLayer * 0.1f;
		if (fNewRadius < 0.3)
			return (float) Math.min(fViewSpaceXAbs, fViewSpaceYAbs) / 100 * fRadius * 10.0f;
		else
			return fNewRadius;
	}

	private int updateNodespace(ArrayList<IDrawAbleNode> children, int iLayer) {
		if (iLayer > HyperbolicRenderStyle.MAX_DEPTH)
			return 0;
		int overall = children.size();
		for (IDrawAbleNode node : children) {
			int temp = 0;
			if (tree.hasChildren(node))
				temp = updateNodespace(tree.getChildren(node), iLayer + 1);
			mNodeSpaceRec.put(node, temp);
			overall = (temp > overall ? temp : overall);
		}
		return overall;
	}

	private float calculateLineLengthFromCenterToPoint(float fXCoord, float fYCoord) {
		float fDx = fCenterX - fXCoord;
		float fDy = fCenterY - fYCoord;
		float fLine = (float) Math.sqrt(Math.pow(fDx, 2) + Math.pow(fDy, 2));
		return fLine;
	}

	private ArrayList<IDrawAbleNode> sortChildList(ArrayList<IDrawAbleNode> alChildList) {

		ArrayList<Pair<Integer, IDrawAbleNode>> alDummyPairArray =
			new ArrayList<Pair<Integer, IDrawAbleNode>>();
		
		for (IDrawAbleNode tmpNode : alChildList) {
			if (tree.hasChildren(tmpNode)) {
				int iSize = tree.getChildren(tmpNode).size();
				Pair<Integer, IDrawAbleNode> tmpPair = new Pair<Integer, IDrawAbleNode>(iSize, tmpNode);
				alDummyPairArray.add(tmpPair);
			}
			else {
				int iSize = 0;
				Pair<Integer, IDrawAbleNode> tmpPair = new Pair<Integer, IDrawAbleNode>(iSize, tmpNode);
				alDummyPairArray.add(tmpPair);
			}
		}
		ArrayList<Integer> alDummyKeyArray = new ArrayList<Integer>();
		for (int j = 0; j < alDummyPairArray.size(); j++) {
			alDummyKeyArray.add(alDummyPairArray.get(j).getFirst());
		}

		Collections.sort(alDummyKeyArray);
		// Collections.shuffle(dummyKeyArray);
		ArrayList<IDrawAbleNode> alSortedChildNumberArray = new ArrayList<IDrawAbleNode>();

		for (int i = 0; i < alDummyKeyArray.size(); i++) {
			int iValue = alDummyKeyArray.get(i);
			for (int k = 0; k < alDummyPairArray.size(); k++) {
				if (iValue == alDummyPairArray.get(k).getFirst()) {
					alSortedChildNumberArray.add(alDummyPairArray.get(k).getSecond());
					alDummyPairArray.remove(k);
				}
			}
		}

		return alSortedChildNumberArray;
	}

	private ArrayList<IDrawAbleNode> findOptimalAdjustmentOfChildList(ArrayList<IDrawAbleNode> alChildList) {
		ArrayList<IDrawAbleNode> alDummyList = new ArrayList<IDrawAbleNode>();
		for (int i = 0; i < (alChildList.size() + 1) / 2; i++) {
			if (i != (alChildList.size() - 1) - i) {
				alDummyList.add(alChildList.get(i));
				alDummyList.add(alChildList.get((alChildList.size() - 1) - i));
			}
			else
				alDummyList.add(alChildList.get(i));
		}
		return alDummyList;
	}

}
