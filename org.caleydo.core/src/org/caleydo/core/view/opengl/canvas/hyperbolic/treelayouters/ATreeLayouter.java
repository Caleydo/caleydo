package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters;

import gleem.linalg.Vec3f;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.media.opengl.GL;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.IDrawAbleNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines.DrawAbleHyperbolicLayoutConnector;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines.IDrawAbleConnection;
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

	// TODO: Maybe replace by maps!
	private List<IDrawAbleNode> nodeLayout;
	private List<IDrawAbleConnection> connectionLayout;

	private PickingManager pickingManager;
	private int iViewID;
	private int iGLDisplayListNode;
	private int iGLDisplayListConnection;

	private ITreeProjection treeProjector = null;

	protected Tree<IDrawAbleNode> tree = null;

	protected Map<Integer, Vec3f> mLayoutAnimationStart = null;
	protected Map<Integer, Vec3f> mLayoutAnimationEnd = null;

	public ATreeLayouter(IViewFrustum frustum, PickingManager pickingManager, int iViewID,
		ITreeProjection treeProjector) {
		this.viewFrustum = frustum;
		this.pickingManager = pickingManager;
		this.iViewID = iViewID;
		this.nodeLayout = new ArrayList<IDrawAbleNode>();
		this.connectionLayout = new ArrayList<IDrawAbleConnection>();
		this.treeProjector = treeProjector;
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
		fViewRadius = Math.min(fViewSpaceXAbs / 2, fViewSpaceYAbs / 2);
		if (treeProjector != null)
			treeProjector.updateFrustumInfos(fHeight, fWidth, 0.0f, fViewSpaceX, fViewSpaceXAbs, fViewSpaceY,
				fViewSpaceYAbs);
	}

	@Override
	public final void setLayoutDirty() {
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
			if (node.isPickAble()) {
				gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.HYPERBOLIC_NODE_SELECTION,
					node.getID()));
				if (bIsNodeHighlighted && node.getID() == iHighlightedNode)
					node.draw(gl, true);
				else
					node.draw(gl, false);
				gl.glPopName();
			}
			else {
				node.draw(gl, false);
			}
		}
		gl.glEndList();
	}

	private final void buildDisplayListConnections(GL gl) {
		// TODO: check usage of DL with Bezier Splines and glevalcoord1f function!
		// gl.glNewList(iGLDisplayListConnection, GL.GL_COMPILE);
		for (IDrawAbleConnection conn : connectionLayout) {
			if (conn.isPickAble()) {
				gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.HYPERBOLIC_LINE_SELECTION,
					conn
					.getID()));
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
		// TODO: Really bad!
		// gl.glCallList(iGLDisplayListConnection);
		buildDisplayListConnections(gl);
		gl.glCallList(iGLDisplayListNode);
		if (treeProjector != null)
			treeProjector.drawCanvas(gl);
	}

	private final void clearDisplay() {
		nodeLayout.clear();
		connectionLayout.clear();
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

	public final void animateToNewTree(Tree<IDrawAbleNode> tree) {
		// storeAnimationInfosIntoMap()
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
