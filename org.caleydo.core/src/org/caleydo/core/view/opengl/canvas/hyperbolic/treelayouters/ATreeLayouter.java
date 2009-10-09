package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

import javax.media.opengl.GL;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;
import org.caleydo.core.view.opengl.canvas.hyperbolic.TextLabel;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.EDrawAbleNodeDetailLevel;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.IDrawAbleNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines.DrawAbleHyperbolicLayoutConnector;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines.IDrawAbleConnection;
import org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.animation.AnimationConnectionHandler;
import org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.animation.AnimationVec3f;
import org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.htcalculation.HTModel;
import org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.projections.ITreeProjection;

public abstract class ATreeLayouter
	implements ITreeLayouter {
	protected int iComparableValue = 0;
	protected float fHeight = 0;
	protected float fWidth = 0;
	protected float fViewSpaceX[] = { 0f, 0f };
	protected float fViewSpaceY[] = { 0f, 0f };
	protected float fViewSpaceXAbs = 0;
	protected float fViewSpaceYAbs = 0;
	protected float fViewRadius = 0;
	protected Vec3f fvViewCenterPoint = null;
	protected IViewFrustum viewFrustum;

	private boolean bIsLayoutDirty = true;
	private boolean bIsNodeListDirty = false;
	private boolean bIsConnectionListDirty = false;

	private boolean bIsNodeHighlighted = false;
	private int iHighlightedNode = 0;

	private boolean bIsConnectionHighlighted = false;
	private int iHighlightedConnection = 0;

	private boolean bIsAnimating = false;

	protected int iNumLayers;

	protected Semaphore lockAnimation = null;

	// TODO: Maybe replace by maps!
	protected List<IDrawAbleNode> nodeLayout;
	private List<IDrawAbleConnection> connectionLayout;

	private PickingManager pickingManager;
	private int iViewID;
	private int iGLDisplayListNode;
	private int iGLDisplayListConnection;
	protected HyperbolicRenderStyle renderStyle = null;

	protected ITreeProjection treeProjector = null;

	protected Tree<IDrawAbleNode> tree = null;
	protected ArrayList<Integer> alMaxSiblingsInLayer = null;

	private Map<IDrawAbleNode, AnimationVec3f> mAnimationNodes = null;
	private AnimationConnectionHandler animationConnectionHandler = null;

	private List<IDrawAbleNode> lAnimationNodesLeave = null;

	TextLabel nodeLabel = null;
	IDrawAbleNode currentSelectedNode = null;
	private boolean bIsBusy = false;
	private String strInformation = null;

	// protected CaleydoTextRenderer textRenderer = null;
	protected TextLabel informationLabel = null;
	private Semaphore lockAnimateToNewTree = null;

	public ATreeLayouter(IViewFrustum frustum, PickingManager pickingManager, int iViewID,
		HyperbolicRenderStyle renderStyle, ITreeProjection treeProjector, String strInformation) {
		this.viewFrustum = frustum;
		this.pickingManager = pickingManager;
		this.iViewID = iViewID;
		this.nodeLayout = new ArrayList<IDrawAbleNode>();
		this.connectionLayout = new ArrayList<IDrawAbleConnection>();
		this.alMaxSiblingsInLayer = new ArrayList<Integer>();
		this.treeProjector = treeProjector;
		this.strInformation = strInformation;
		this.renderStyle = renderStyle;
		this.informationLabel =
			new TextLabel(new Font(renderStyle.LABEL_FONT_NAME, renderStyle.LABEL_FONT_STYLE,
				renderStyle.LABEL_FONT_SIZE));
		this.nodeLabel =
			new TextLabel(new Font(renderStyle.LABEL_FONT_NAME, renderStyle.LABEL_FONT_STYLE,
				renderStyle.LABEL_FONT_SIZE));
		this.lockAnimation = new Semaphore(1);
		this.lockAnimateToNewTree = new Semaphore(1);
		// this.textRenderer =
		// new CaleydoTextRenderer(new Font(HyperbolicRenderStyle.LABEL_FONT_NAME,
		// HyperbolicRenderStyle.LABEL_FONT_STYLE, HyperbolicRenderStyle.LABEL_FONT_SIZE), false);
		updateSizeInfo();
	}

	// public ATreeLayouter(IViewFrustum frustum, PickingManager pickingManager, int iViewID, ITreeProjection
	// treeProjector) {
	// this.viewFrustum = frustum;
	// this.pickingManager = pickingManager;
	// this.iViewID = iViewID;
	// this.nodeLayout = new ArrayList<IDrawAbleNode>();
	// this.connectionLayout = new ArrayList<IDrawAbleConnection>();
	// updateSizeInfo();
	// }

	@Override
	public final int compareTo(ITreeLayouter layouter) {
		return this.iComparableValue - layouter.getID();
	}

	protected abstract void renderTreeLayout();

	protected final void updateSizeInfo() {
		fHeight = viewFrustum.getHeight();
		fWidth = viewFrustum.getWidth();
		fViewSpaceX[0] = fWidth * HyperbolicRenderStyle.X_BORDER_SPACING;
		fViewSpaceX[1] = fWidth - fWidth * HyperbolicRenderStyle.X_BORDER_SPACING;
		fViewSpaceXAbs = Math.abs(fViewSpaceX[0] - fViewSpaceX[1]);
		fViewSpaceY[0] = fHeight * HyperbolicRenderStyle.Y_BORDER_SPACING;
		fViewSpaceY[1] = fHeight - fHeight * HyperbolicRenderStyle.Y_BORDER_SPACING;
		fViewSpaceYAbs = Math.abs(fViewSpaceY[0] - fViewSpaceY[1]);
		fvViewCenterPoint = new Vec3f(fWidth / 2.0f, fHeight / 2.0f, 0.0f);
		fViewRadius = Math.min(fViewSpaceXAbs / 2.0f, fViewSpaceYAbs / 2.0f);
		if (treeProjector != null)
			treeProjector.updateFrustumInfos(fHeight, fWidth, 0.0f, fViewSpaceX, fViewSpaceXAbs, fViewSpaceY,
				fViewSpaceYAbs);
	}

	@Override
	public final void setLayoutDirty() {
		finishAnimation();
		bIsLayoutDirty = true;
		bIsNodeListDirty = true;
		bIsConnectionListDirty = true;
	}

	public final void setLayoutClean() {
		bIsLayoutDirty = false;
	}

	@Override
	public final void setHighlightedNode(int iNodeID) {
		if (bIsNodeHighlighted && iHighlightedNode == iNodeID)
			return;
		bIsNodeHighlighted = true;
		iHighlightedNode = iNodeID;
		bIsNodeListDirty = true;
		bIsConnectionListDirty = bIsConnectionHighlighted;
		bIsConnectionHighlighted = false;
	}

	public final void setHiglightedLine(int iLineID) {
		if (bIsConnectionHighlighted && iHighlightedConnection == iLineID)
			return;
		bIsConnectionHighlighted = true;
		iHighlightedConnection = iLineID;
		bIsConnectionListDirty = true;
		bIsNodeListDirty = bIsNodeHighlighted;
		bIsNodeHighlighted = false;
	}

	private final void buildDisplayListNodes(GL gl) {
		gl.glNewList(iGLDisplayListNode, GL.GL_COMPILE);
		for (IDrawAbleNode node : nodeLayout) {
			// if (node.isPickAble()) {
			gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.HYPERBOLIC_NODE_SELECTION, node
				.getID()));
			if (bIsNodeHighlighted && node.getID() == iHighlightedNode) {
				currentSelectedNode = node;
				node.draw(gl, true);
			}
			else
				node.draw(gl, false);
			node.placeNodeName(0);
			gl.glPopName();
			// }
			// else {
			// node.draw(gl, false);
			// }
		}
		gl.glEndList();
	}

	private void drawTextBox(GL gl) {
		// TODO: check usage of LABELS, from RADIAL View
		// nodeLabel.setText("Elements: " + currentSelectedNode.getDependingClusterNode().getNrElements() +
		// "\n" + "HirarchyDepth: " + currentSelectedNode.getDependingClusterNode().getDepth());
		// float x =

		// nodeLabel.place(currentSelectedNode., fYCoord, fZCoord, fHeight, fWidth)
	}

	private final void buildDisplayListConnections(GL gl) {
		// TODO: check usage of DL with Bezier Splines and glevalcoord1f function!
		// gl.glNewList(iGLDisplayListConnection, GL.GL_COMPILE);
		for (IDrawAbleConnection conn : connectionLayout) {
			if (conn.isPickAble()) {
				gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.HYPERBOLIC_LINE_SELECTION,
					conn.getID()));
				if (bIsConnectionHighlighted && conn.getID() == iHighlightedConnection)
					conn.draw(gl, true);
				else
					conn.draw(gl, false);
				gl.glPopName();
			}
			else
				conn.draw(gl, false);

		}
		// gl.glEndList();

	}

	@Override
	public final void init(int iGLDisplayListNode, int iGLDisplayListConnection) {
		this.iGLDisplayListNode = iGLDisplayListNode;
		this.iGLDisplayListConnection = iGLDisplayListConnection;
	}

	@Override
	public final void buildDisplayLists(GL gl) {
		if (tree == null)
			return;
		if (bIsLayoutDirty) {
			setLayoutDirty();
			clearDisplay();
			renderTreeLayout();
		}
		if (bIsNodeListDirty) {
			buildDisplayListNodes(gl);
			bIsNodeListDirty = false;
		}
		if (bIsConnectionListDirty) {
			buildDisplayListConnections(gl);
			bIsConnectionListDirty = false;
		}
		setLayoutClean();
	}

	@Override
	public final void display(GL gl) {
		if (treeProjector != null)
			treeProjector.drawCanvas(gl);
		if (bIsAnimating) {
			try {
				lockAnimation.acquire();
			}
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!bIsAnimating) {
				lockAnimation.release();
				return;
			}
			bIsAnimating = false;
			List<IDrawAbleNode> dummy = new ArrayList<IDrawAbleNode>();
			if (!mAnimationNodes.isEmpty())
				for (IDrawAbleNode node : mAnimationNodes.keySet()) {
					AnimationVec3f vec = mAnimationNodes.get(node);
					if (!vec.nextStep()) {
						bIsAnimating = true;
						node.place(vec.getCurrentPos().x(), vec.getCurrentPos().y(), vec.getCurrentPos().z(),
							node.getDimension().x(), node.getDimension().y(), treeProjector);
					}
					else {
						node.place(vec.getFinalPos().x(), vec.getFinalPos().y(), vec.getFinalPos().z(), node
							.getDimension().x(), node.getDimension().y(), treeProjector);
						dummy.add(node);

						if (lAnimationNodesLeave.contains(node)) {
							animationConnectionHandler.clearAllOccurencesOfNode(node);
							lAnimationNodesLeave.remove(node);
						}
						else
							nodeLayout.add(node);

					}
					node.draw(gl, false);
				}
			if (!dummy.isEmpty())
				for (IDrawAbleNode node : dummy) {
					mAnimationNodes.remove(node);
				}
			for (IDrawAbleConnection conn : animationConnectionHandler.getAllConnections())
				conn.draw(gl, false);
			if (!nodeLayout.isEmpty())
				for (IDrawAbleNode node : nodeLayout)
					node.draw(gl, false);
			if (!bIsAnimating) {
				connectionLayout = animationConnectionHandler.getAllConnections();
				mAnimationNodes = null;
				animationConnectionHandler = null;
				bIsNodeListDirty = true;
				bIsConnectionListDirty = true;
//				bIsConnectionListDirty = false;
//				clearDisplay();
//				renderTreeLayout();
				lockAnimateToNewTree.release();
			}
			lockAnimation.release();

		}
		else {
			// TODO: Really bad!
			// gl.glCallList(iGLDisplayListConnection);
			buildDisplayListConnections(gl);
			gl.glCallList(iGLDisplayListNode);

			if (bIsNodeHighlighted && currentSelectedNode != null)
				drawTextBox(gl);
			// IDrawAbleConnection conn = new DrawAbleTextBoxConnector(textNode, currentSelectedNode);
			// conn.draw(gl, true);
			// }
		}

		bIsBusy = false;
		bIsConnectionListDirty = true;
		// textRenderer.renderText(gl, strInformation, fViewSpaceX[0], fViewSpaceY[1], 0.1f, 5, 32);
		informationLabel.setText(strInformation);
		informationLabel
			.place(fViewSpaceX[0], fViewSpaceY[1], 1.0f, fHeight - fViewSpaceY[1], fViewSpaceXAbs);
		informationLabel.draw3d(gl, false);
	}

	private final void clearDisplay() {
		nodeLayout.clear();
//		connectionLayout.clear();
	}

	@Override
	public final void setTree(Tree<IDrawAbleNode> tree) {
		this.tree = tree;
		setLayoutDirty();
	}

	protected final void placeNode(IDrawAbleNode node, float fXCoord, float fYCoord, float fZCoord,
		float fHeight, float fWidth) {
		node.place(fXCoord, fYCoord, fZCoord, fHeight, fWidth, treeProjector);
		nodeLayout.add(node);
	}

	protected final void placeNode(IDrawAbleNode node) {
		nodeLayout.add(node);
	}

	// protected final void placeNodeAndProject(IDrawAbleNode node, float fXCoord, float fYCoord, float
	// fZCoord,
	// float fHeight, float fWidth, ITreeProjection projection) {
	// node.placeAndProject(fXCoord, fYCoord, fZCoord, fHeight, fWidth, projection);
	// nodeLayout.add(node);
	// }

	protected final void placeConnection(IDrawAbleNode root, IDrawAbleNode child) {
		connectionLayout.add(new DrawAbleHyperbolicLayoutConnector(root, child, treeProjector));
	}

	// protected final void placeConnection(IDrawAbleConnection conn) {
	// connectionLayout.add(conn);
	// }

	@Override
	public final int getID() {
		return iComparableValue;
	}

	@Override
	public final boolean isAnimating() {
		return bIsAnimating;
	}

//	public final void animateToNewTree(Tree<IDrawAbleNode> tree) {
//
//		// TODO: Maybe send an event to all controls!
//		// nothing to do
//		try {
//			lockAnimateToNewTree.acquire();
//			lockAnimation.acquire();
//		}
//		catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		bIsAnimating = true;
//
//		this.mAnimationNodes = new HashMap<IDrawAbleNode, AnimationVec3f>();
//		this.animationConnectionHandler = new AnimationConnectionHandler();
//		this.lAnimationNodesLeave = new ArrayList<IDrawAbleNode>();
//
//		Map<IDrawAbleNode, Vec3f> mLayoutAnimationStart = new HashMap<IDrawAbleNode, Vec3f>();
//		Map<IDrawAbleNode, Vec3f> mLayoutAnimationEnd = new HashMap<IDrawAbleNode, Vec3f>();
//
//		for (IDrawAbleNode node : nodeLayout)
//			mLayoutAnimationStart.put(node, node.getRealCoordinates());
//		for (IDrawAbleConnection conn : connectionLayout)
//			animationConnectionHandler.addConnectionInformation(conn);
//		Tree<IDrawAbleNode> oldTree = this.tree;
//		this.tree = tree;
//		clearDisplay();
//		renderTreeLayout();
//		for (IDrawAbleNode node : nodeLayout)
//			mLayoutAnimationEnd.put(node, node.getRealCoordinates());
//		for (IDrawAbleConnection conn : connectionLayout)
//			animationConnectionHandler.addConnectionInformation(conn);
//
//		// TODO: find nicer placing!#
//		//raus
//		for (IDrawAbleNode i : mLayoutAnimationStart.keySet())
//			if (!mLayoutAnimationEnd.containsKey(i)) {
//				IDrawAbleNode parent = oldTree.getParent(i);
//				while(!mLayoutAnimationEnd.containsKey(parent))
//					parent = oldTree.getParent(parent);
//				mLayoutAnimationEnd.put(i, treeProjector.getNearestPointOnEuclidianBorder(mLayoutAnimationEnd.get(parent)));
//				lAnimationNodesLeave.add(i);
//			}
//		
//		//rein
//		for (IDrawAbleNode i : mLayoutAnimationEnd.keySet())
//			if (!mLayoutAnimationStart.containsKey(i)) {
//
//				IDrawAbleNode parent = tree.getParent(i);
//				while(!mLayoutAnimationStart.containsKey(parent))
//					parent = tree.getParent(parent);
//				mLayoutAnimationStart.put(i, treeProjector.getNearestPointOnEuclidianBorder(mLayoutAnimationStart.get(parent)));
//				
//		//		if (mLayoutAnimationStart.containsKey(tree.getParent(i)))
//		//			mLayoutAnimationStart.put(i, treeProjector
//		//				.getNearestPointOnEuclidianBorder(mLayoutAnimationStart.get(tree.getParent(i))));
//		//		else
//		//			toadd.add(i);
//
//			}
////		while (toadd.size() > 0) {
////
////			for (IDrawAbleNode node : toadd) {
////				if (mLayoutAnimationStart.containsKey(tree.getParent(node))) {
////					mLayoutAnimationStart.put(node, treeProjector
////						.getNearestPointOnEuclidianBorder(mLayoutAnimationStart.get(tree.getParent(node))));
////					remove.add(node);
////				}
////				toadd.removeAll(remove);
////				remove.clear();
////			}
////		}
//
//		for (IDrawAbleNode i : mLayoutAnimationStart.keySet()) {
//			mAnimationNodes.put(i, new AnimationVec3f(mLayoutAnimationStart.get(i), mLayoutAnimationEnd
//				.get(i), 100f));
//		}
//		clearDisplay();
//		bIsNodeListDirty = true;
//		bIsConnectionListDirty = true;
//		bIsNodeHighlighted = false;
//		bIsConnectionHighlighted = false;
//		lockAnimation.release();
//	}
	
	public final void animateToNewTree(int iExternalID) {
	
		//
		// TODO: Maybe send an event to all controls!
		// nothing to do
		try {
			lockAnimateToNewTree.acquire();
			lockAnimation.acquire();
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bIsAnimating = true;

		this.mAnimationNodes = new HashMap<IDrawAbleNode, AnimationVec3f>();
		this.animationConnectionHandler = new AnimationConnectionHandler();
		this.lAnimationNodesLeave = new ArrayList<IDrawAbleNode>();

		Map<IDrawAbleNode, Vec3f> mLayoutAnimationStart = new HashMap<IDrawAbleNode, Vec3f>();
		Map<IDrawAbleNode, Vec3f> mLayoutAnimationEnd = new HashMap<IDrawAbleNode, Vec3f>();

		for (IDrawAbleNode node : nodeLayout)
			mLayoutAnimationStart.put(node, node.getRealCoordinates());
//		for (IDrawAbleConnection conn : connectionLayout)
//			animationConnectionHandler.addConnectionInformation(conn);
//		Tree<IDrawAbleNode> oldTree = this.tree;
//		this.tree = tree;
		clearDisplay();
//		renderTreeLayout();
		for (IDrawAbleNode node : nodeLayout)
			mLayoutAnimationEnd.put(node, node.getRealCoordinates());
		for (IDrawAbleConnection conn : connectionLayout)
			animationConnectionHandler.addConnectionInformation(conn);

		// TODO: find nicer placing!#
		//raus
//		for (IDrawAbleNode i : mLayoutAnimationStart.keySet())
//			if (!mLayoutAnimationEnd.containsKey(i)) {
//				IDrawAbleNode parent = oldTree.getParent(i);
//				while(!mLayoutAnimationEnd.containsKey(parent))
//					parent = oldTree.getParent(parent);
//				mLayoutAnimationEnd.put(i, treeProjector.getNearestPointOnEuclidianBorder(mLayoutAnimationEnd.get(parent)));
//				lAnimationNodesLeave.add(i);
//			}
//		
//		//rein
//		for (IDrawAbleNode i : mLayoutAnimationEnd.keySet())
//			if (!mLayoutAnimationStart.containsKey(i)) {
//
//				IDrawAbleNode parent = tree.getParent(i);
//				while(!mLayoutAnimationStart.containsKey(parent))
//					parent = tree.getParent(parent);
//				mLayoutAnimationStart.put(i, treeProjector.getNearestPointOnEuclidianBorder(mLayoutAnimationStart.get(parent)));
				
		//		if (mLayoutAnimationStart.containsKey(tree.getParent(i)))
		//			mLayoutAnimationStart.put(i, treeProjector
		//				.getNearestPointOnEuclidianBorder(mLayoutAnimationStart.get(tree.getParent(i))));
		//		else
		//			toadd.add(i);

//			}
//		while (toadd.size() > 0) {
//
//			for (IDrawAbleNode node : toadd) {
//				if (mLayoutAnimationStart.containsKey(tree.getParent(node))) {
//					mLayoutAnimationStart.put(node, treeProjector
//						.getNearestPointOnEuclidianBorder(mLayoutAnimationStart.get(tree.getParent(node))));
//					remove.add(node);
//				}
//				toadd.removeAll(remove);
//				remove.clear();
//			}
//		}

//		for (IDrawAbleNode i : mLayoutAnimationStart.keySet()) {
//			mAnimationNodes.put(i, new AnimationVec3f(mLayoutAnimationStart.get(i), mLayoutAnimationEnd
//				.get(i), 100f));
//		}
		
		Vec3f vec = translateTree(iExternalID);
		for (IDrawAbleNode i : mLayoutAnimationStart.keySet()) {
			Vec3f start = new Vec3f(mLayoutAnimationStart.get(i));
			Vec3f end = new Vec3f(mLayoutAnimationStart.get(i));
			end.add(vec);
			mLayoutAnimationEnd.put(i, end);
			
			mAnimationNodes.put(i, new AnimationVec3f(start, end, 50.0f));
		}
//		clearDisplay();
		bIsNodeListDirty = true;
		bIsConnectionListDirty = true;
		bIsNodeHighlighted = false;
		bIsConnectionHighlighted = false;
		lockAnimation.release();
	}

	protected int getMaxNumberOfSiblingsInLayer(IDrawAbleNode node, int iTargetLayer, int iCurrentLayer) {
		// IDrawAbleNode rootNode = tree.getRoot();
		// iCurrentLayer = 1;
		if (tree.hasChildren(node)) {
			int iSiblings = 0;
			for (IDrawAbleNode child : tree.getChildren(node)) {
				alMaxSiblingsInLayer.set(1, 1);
				// alMaxSiblingsInLayer.add(1, 1);
				if (tree.hasChildren(child) || iCurrentLayer != iTargetLayer) {
					iSiblings = getMaxNumberOfSiblingsInLayer(child, iTargetLayer, iCurrentLayer + 1);
				}
				else {
					iSiblings = tree.getChildren(tree.getParent(child)).size();
					if (iSiblings > alMaxSiblingsInLayer.get(iCurrentLayer + 1)) {
						alMaxSiblingsInLayer.add(iCurrentLayer + 1, iSiblings);
					}
					return iSiblings;
				}
				tree.getNumberOfElementsInLayer(iCurrentLayer);
			}
		}
		else
			return 1;
		return alMaxSiblingsInLayer.get(iCurrentLayer);

	}

	public void setInformationText(String strInformation) {
		this.strInformation = strInformation;
	}

	protected final void finishAnimation() {
		try {
			lockAnimation.acquire();
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bIsAnimating = false;
		if (mAnimationNodes != null)
			mAnimationNodes = null;
		if (animationConnectionHandler != null)
			animationConnectionHandler = null;
		lockAnimation.release();
		lockAnimateToNewTree.release();
	}
	
	public Vec3f getTranslationVector(Vec3f source, Vec3f dest){
		Vec3f vec = source.minus(dest);
		return vec;
	}
	
	public Vec3f translateTree(int iExternalID){
		
		IDrawAbleNode targetNode = tree.getNodeByNumber(iExternalID);
		Vec3f targetVec = new Vec3f(targetNode.getXCoord(), targetNode.getYCoord(), 0.0f);
		Vec3f translateVec = getTranslationVector(new Vec3f(fWidth/2, fHeight/2, 0.0f), targetVec);
		
//		if (this.tree == null)
//			return;
		IDrawAbleNode root = this.tree.getRoot();

//		runThroughTreeAndPlaceNew(translateVec , root, 1);
//
//		bIsNodeListDirty = true;
//		bIsConnectionListDirty = true;
		//clearDisplay();
		//renderTreeLayout();
		return translateVec;

	}
	private void runThroughTreeAndPlaceNew(Vec3f vec, IDrawAbleNode node, int layer){
		if(tree.hasChildren(node)){
			for(IDrawAbleNode child : tree.getChildren(node)){
				runThroughTreeAndPlaceNew(vec, child, layer++);
			}
		}

//		for(IDrawAbleNode node: nodeLayout){
		Vec3f newNode = new Vec3f(node.getXCoord(), node.getYCoord(), 0.0f);
		newNode.add(vec);
		node.setXCoord(newNode.x());
		node.setYCoord(newNode.y());
		node.setDetailLevel(EDrawAbleNodeDetailLevel.High);
		node.place(node.getRealCoordinates().x(), node.getRealCoordinates().y(), 0.0f, 0.1f, 0.1f, treeProjector);
		
	//	placeConnection(tree.getParent(node), node);
//	}
	}
		
	

	// protected final Vec3f[] findClosestCorrespondendingPoints(List<Vec3f> pointsA, List<Vec3f> pointsB){
	// float fMin = Float.MAX_VALUE;
	// Vec3f foundA = null;
	// Vec3f foundB = null;
	// float ft;
	// for(Vec3f pointA : pointsA)
	// for(Vec3f pointB : pointsB)
	// if((ft = (float) Math.sqrt(Math.pow(pointA.x()-pointB.x(), 2)+Math.pow(pointA.y()-pointB.y(), 2))) <
	// fMin)
	// {
	// foundA = pointA;
	// foundB = pointB;
	// fMin = ft;
	// }
	// Vec3f[] vaPoints = {foundA, foundB};
	// return vaPoints;
	// }

	// @Override
	// public abstract void animateToNewTree(Tree<IDrawAbleNode> tree);

	// public final void initLocal(GL gl) {
	// iGLDisplayListNode = gl.glGenLists(1);
	// iGLDisplayListConnection = gl.glGenLists(1);
	// init(gl);
	// }

	// public final void initRemote(GL gl) {
	// iGLDisplayListConnectionRemote = gl.glGenLists(1);
	// iGLDisplayListNodeRemote = gl.glGenLists(1);
	// init(gl);

	/*
	 * protected final void drawLayout(GL gl) { if (bIsNodeHighlighted) for (IDrawAbleNode node : layout) { if
	 * (node.getNodeNr() == iHighlightedNode) node.setHighlight(true); else node.setHighlight(false);
	 * gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.HYPERBOLIC_NODE_SELECTION,
	 * node.getNodeNr())); node.draw(gl); gl.glPopName(); } else for (IDrawAbleNode node : layout) {
	 * node.setHighlight(false); gl.glPushName(pickingManager.getPickingID(iViewID,
	 * EPickingType.HYPERBOLIC_NODE_SELECTION, node.getNodeNr())); node.draw(gl); gl.glPopName(); }
	 * resetHighlight(); }
	 */
	// protected boolean bIsRemoteLayoutDirty = true;
	// protected boolean bIsRemoteNodeListDirty = false;
	// protected boolean bIsRemoteConnectionListDirty = false;
	// int iGLDisplayListNodeRemote;
	// int iGLDisplayListConnectionRemote;
}
