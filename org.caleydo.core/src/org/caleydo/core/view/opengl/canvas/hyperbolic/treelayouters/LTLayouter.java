package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.EDrawAbleNodeDetailLevel;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.IDrawAbleNode;

public final class LTLayouter
	extends ATreeLayouter {

	int[] iNodesPlacedInLayer;
	Map<IDrawAbleNode, Integer> mNodeSpaceRec;
	float fCalcMaxNodeSize;
	float fCurNodeRealSize;

	public LTLayouter(IViewFrustum frustum, PickingManager pickingManager, int iViewID) {
		super(frustum, pickingManager, iViewID, null);
	}

	@Override
	public void renderTreeLayout() {
		updateSizeInfo();
		if (tree == null)
			return;

		mNodeSpaceRec = new HashMap<IDrawAbleNode, Integer>();
		IDrawAbleNode rootNode = tree.getRoot();
		int overall = 0;
		if (tree.hasChildren(rootNode))
			overall = updateNodespace(tree.getChildren(rootNode), 2);
		mNodeSpaceRec.put(rootNode, overall);

		float fLayerH = fViewSpaceYAbs / HyperbolicRenderStyle.MAX_DEPTH;
		fCalcMaxNodeSize = fViewSpaceXAbs / overall;
		float fCurNodeSize =
			(HyperbolicRenderStyle.MAX_NODE_SIZE < fCalcMaxNodeSize ? HyperbolicRenderStyle.MAX_NODE_SIZE
				: fCalcMaxNodeSize);
		fCurNodeRealSize = fCurNodeSize - 0.05f;
		rootNode.setDetailLevel(EDrawAbleNodeDetailLevel.VeryHigh);
		iNodesPlacedInLayer = new int[HyperbolicRenderStyle.MAX_DEPTH];
		placeNode(rootNode, fViewSpaceX[1] / 2, fViewSpaceY[1] - fLayerH / 2.0f, 0.1f, fCurNodeSize,
			fCurNodeSize);
		placeRecursive(rootNode, 2, fLayerH, fViewSpaceX[0]);
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

	private void placeRecursive(IDrawAbleNode rootNode, int iCurrentLayer, float fLayerHigh, float viewSpace) {
		if (iCurrentLayer > HyperbolicRenderStyle.MAX_DEPTH)
			return;
		if (!tree.hasChildren(rootNode))
			return;
		int iNodesInThisLayer = tree.getNumberOfElementsInLayer(iCurrentLayer);

		ArrayList<IDrawAbleNode> children = tree.getChildren(rootNode);
		float placelast = 0;
		for (int i = 0; i < children.size(); ++i) {
			IDrawAbleNode node = children.get(i);
			float fNumberNodesbefore = (i == 0 ? 0 : (float) mNodeSpaceRec.get(children.get(i - 1)));
			if (iCurrentLayer < HyperbolicRenderStyle.DETAIL_LEVEL_GRADING.length)
				node.setDetailLevel(HyperbolicRenderStyle.DETAIL_LEVEL_GRADING[iCurrentLayer - 1]);
			else
				node.setDetailLevel(EDrawAbleNodeDetailLevel.VeryLow);
			if (i == 0)
				placelast = viewSpace + (float) mNodeSpaceRec.get(node) * fCalcMaxNodeSize / 4.0f;
			else
				placelast +=
					fCalcMaxNodeSize / 2.0f + (float) mNodeSpaceRec.get(node) * fCalcMaxNodeSize / 4.0f
						+ (float) mNodeSpaceRec.get(children.get(i - 1)) / 4.0f * fCalcMaxNodeSize;
			placeNode(node, placelast, fViewSpaceY[1] - fLayerHigh * iCurrentLayer + fLayerHigh / 2.0f, 0.1f,
				fCurNodeRealSize, fCurNodeRealSize);
			placeConnection(rootNode, node);
			placeRecursive(node, iCurrentLayer + 1, fLayerHigh, placelast - (float) mNodeSpaceRec.get(node)
				/ 4.0f * fCalcMaxNodeSize);
		}
		return;

		// float fCurNodeSize = HyperbolicRenderStyle.MAX_NODE_SIZE * (float)
		// Math.pow(HyperbolicRenderStyle.NODE_SCALING_PER_LAYER, iCurrentLayer);
		// for(IDrawAbleNode node : tree.getChildren(rootNode)){
		// if(iCurrentLayer < HyperbolicRenderStyle.DETAIL_LEVEL_GRADING.length)
		// node.setDetailLevel(HyperbolicRenderStyle.DETAIL_LEVEL_GRADING[iCurrentLayer - 1]);
		// else
		// node.setDetailLevel(EDrawAbleNodeDetailLevel.VeryLow);
		// placeNode(node, fViewSpaceX[0] + fViewSpaceXAbs / (iNodesInThisLayer + 1) *
		// ++iNodesPlacedInLayer[iCurrentLayer - 1], fViewSpaceY[1] - fLayerHigh * iCurrentLayer + fLayerHigh
		// / 2, 0.1f,
		// fCurNodeSize, fCurNodeSize);
		// placeRecursive(node, iCurrentLayer + 1, fLayerHigh);
		// placeConnection(new DrawAbleLinearConnection(rootNode, node));
		// }
	}

	// float fLayerH = fViewSpaceYAbs / HyperbolicRenderStyle.MAX_DEPTH;
	// float fCurNodeSize = HyperbolicRenderStyle.MAX_NODE_SIZE;
	// IDrawAbleNode rootNode = tree.getRoot();
	// rootNode.setDetailLevel(EDrawAbleNodeDetailLevel.VeryHigh);
	// iNodesPlacedInLayer = new int[HyperbolicRenderStyle.MAX_DEPTH];
	// placeNode(rootNode, fViewSpaceX[1] / 2, fViewSpaceY[1] - fLayerH / 2.0f, 0.1f, fCurNodeSize,
	// fCurNodeSize);
	// placeRecursive(rootNode, 2, fLayerH);
	// }
	//	
	// private void placeRecursive(IDrawAbleNode rootNode, int iCurrentLayer, float fLayerHigh){
	// if(iCurrentLayer > HyperbolicRenderStyle.MAX_DEPTH)
	// return;
	// if(!tree.hasChildren(rootNode))
	// return;
	// int iNodesInThisLayer = tree.getNumberOfElementsInLayer(iCurrentLayer);
	// float fCurNodeSize = HyperbolicRenderStyle.MAX_NODE_SIZE * (float)
	// Math.pow(HyperbolicRenderStyle.NODE_SCALING_PER_LAYER, iCurrentLayer);
	// for(IDrawAbleNode node : tree.getChildren(rootNode)){
	// if(iCurrentLayer < HyperbolicRenderStyle.DETAIL_LEVEL_GRADING.length)
	// node.setDetailLevel(HyperbolicRenderStyle.DETAIL_LEVEL_GRADING[iCurrentLayer - 1]);
	// else
	// node.setDetailLevel(EDrawAbleNodeDetailLevel.VeryLow);
	// placeNode(node, fViewSpaceX[0] + fViewSpaceXAbs / (iNodesInThisLayer + 1) *
	// ++iNodesPlacedInLayer[iCurrentLayer - 1], fViewSpaceY[1] - fLayerHigh * iCurrentLayer + fLayerHigh / 2,
	// 0.1f,
	// fCurNodeSize, fCurNodeSize);
	// placeRecursive(node, iCurrentLayer + 1, fLayerHigh);
	// placeConnection(new DrawAbleLinearConnection(rootNode, node));
	// }
	// }

	// for(IDrawAbleNode node : tree.getChildren(rootNode)) {

	// }

	// int iMaxNodesPerLayer = 0;
	// for (int i = 1; i <= tree.getDepth(); ++i)
	// if (iMaxNodesPerLayer < tree.getNumberOfElementsInLayer(i))
	// iMaxNodesPerLayer = tree.getNumberOfElementsInLayer(i);
	//
	// float fNodeW = fViewSpaceYAbs / iMaxNodesPerLayer;
	// float fMaxNodeW = fNodeW * (1.0f - HyperbolicRenderStyle.X_NODE_SPACING);
	//
	// // first start with root node
	//		
	// placeNode(rootNode, fViewSpaceX[0] + fViewSpaceXAbs / 2.0f, fViewSpaceY[1] - fLayerH / 2.0f, 0.1f,
	// fMaxNodeH, fMaxNodeW);
	// // placeNode(rootNode, fWidth / 2, fHeight / 2, 0.1f , 0.5f, 0.5f);
	//		
	// IDrawAbleConnection conn =
	// DrawAbleConnectionsFactory.getDrawAbleConnection("Spline",1,0);//HyperbolicRenderStyle.LINEAR_TREE_LAYOUTER_CONNECTION_TYPE,
	// 1);
	// List<Vec3f> lP = new ArrayList<Vec3f>();
	// Vec3f vf = new Vec3f(fViewSpaceX[0], fViewSpaceY[0], 0.1f);
	// lP.add(vf);
	// vf = new Vec3f(fViewSpaceX[1]/3, fViewSpaceY[1]/3+1, 0.1f);
	// //lP.add(vf);
	// vf = new Vec3f(fViewSpaceX[1]*2/3, fViewSpaceY[1]*2/3-1, 0.1f);
	// //lP.add(vf);
	// vf = new Vec3f(fViewSpaceX[1], fViewSpaceY[1], 0.1f);
	// lP.add(vf);
	// placeConnection(conn, lP);
	//		
	// conn =
	// DrawAbleConnectionsFactory.getDrawAbleConnection("Spline",2,0);//HyperbolicRenderStyle.LINEAR_TREE_LAYOUTER_CONNECTION_TYPE,
	// 1);
	// placeConnection(conn, lP);

	//
	// updateSizeInfo();
	// if (tree == null)
	// return;
	//
	// int deph = tree.getDepth();
	// //int iNumNodesInLayer = 1;
	// IDrawAbleNode rootNode = tree.getRoot();
	//
	// float f = 0;
	// for (int i = 0; i < deph; i++)
	// f = f + (float) Math.pow((double) HyperbolicRenderStyle.LIN_TREE_Y_SCALING_PER_LAYER, i);
	// float fLayerSpacing = fViewSpaceYAbs / f;
	// // float fNodeSpacing = fViewSpaceXAbs / (iNumNodesInLayer + 1);
	// float fYOff = fViewSpaceY[1] - fLayerSpacing;
	//		
	// for (int i = 1; i <= deph; ++i) {
	// float fYCoord = fYOff + fLayerSpacing / 2f;
	// float fNodeSpacing = fViewSpaceXAbs / (i + 1);
	// for (int j = 1; j <= i; j++) {
	// float fXCoord = fViewSpaceX[0] + j * fNodeSpacing;
	// float fZCoord = 0;
	// rootNode.drawAtPostion(gl, fXCoord, fYCoord, fZCoord, fLayerSpacing * 0.8f, fNodeSpacing,
	// EDrawAbleNodeDetailLevel.Low);
	// // positionAndDrawNode(gl, rootNode, i, fLayerSpacing, j,
	// // fNodeSpacing,EDrawAbleNodeDetailLevel.VeryHigh);
	//
	// }
	// fLayerSpacing = fLayerSpacing * HyperbolicRenderStyle.LIN_TREE_Y_SCALING_PER_LAYER;
	// fYOff = fYOff - fLayerSpacing;
	// }
	//
	// // first place root node
	//
	// // ArrayList<Vec3f> a = tree.getRoot().drawAtPostion(gl, fHigh/2f, fWidth/2f, 0.0f, 1f, 2f,
	// // EDrawAbleNodeDetailLevel.VeryHigh);
	// // ArrayList<Vec3f> b =tree.getRoot().drawAtPostion(gl, fHigh/4f, fWidth/4f, 0.0f, 1.5f, 3f,
	// // EDrawAbleNodeDetailLevel.High);
	//
	// // gl.glColor4f(1, 0, 0, 1);
	// // gl.glBegin(GL.GL_LINE);
	// //
	// // for(int i = 0; i < HyperbolicRenderStyle.DA_OBJ_NUM_CONTACT_POINTS; i++)
	// // {
	// // gl.glVertex3f(a.get(i).get(0), a.get(i).get(1), a.get(i).get(2));
	// // gl.glVertex3f(b.get(i).get(0), b.get(i).get(1), b.get(i).get(2));
	// // }
	// // gl.glVertex3f(fXCoord + fDrawWidth, fYCoord - fDrawHeight, fZCoord);
	// // gl.glVertex3f(fXCoord - fDrawWidth, fYCoord - fDrawHeight, fZCoord);
	// // gl.glVertex3f(fXCoord - fDrawWidth, fYCoord + fDrawHeight, fZCoord);
	// //gl.glEnd();
	//
	// // LinearLineFactory lLFactory = new LinearLineFactory(a,b);
	// // DrawAbleLinearLine line1 = new DrawAbleLinearLine();
	// // line1.drawLineFromStartToEnd(gl, lLFactory.getStartPoint(), lLFactory.getEndPoint(), 5.0f);
	//
	// return;
	// }
	//
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
}
