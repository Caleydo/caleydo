package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.ADrawAbleNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.IDrawAbleNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines.IDrawAbleConnection;

public abstract class ATreeLayouter
	implements ITreeLayouter, Comparable<ATreeLayouter> {
	protected int iComparableValue = 0;
	protected float fHeight = 0;
	protected float fWidth = 0;
	protected float fViewSpaceX[] = { 0f, 0f };
	protected float fViewSpaceY[] = { 0f, 0f };
	protected float fViewSpaceXAbs = 0;
	protected float fViewSpaceYAbs = 0;
	protected IViewFrustum viewFrustum;

	// protected boolean bIsRemoteLayoutDirty = true;
	// protected boolean bIsRemoteNodeListDirty = false;
	// protected boolean bIsRemoteConnectionListDirty = false;

	protected boolean bIsLayoutDirty = true;
	protected boolean bIsNodeListDirty = false;
	protected boolean bIsConnectionListDirty = false;

	protected boolean bIsNodeHighlighted = false;
	protected int iHighlightedNode = 0;

	protected boolean bIsConnectionHighlighted = false;
	protected int iHighlightedConnection = 0;

	protected boolean bIsAnimating = false;

	protected List<IDrawAbleNode> nodeLayout;
	protected List<IDrawAbleConnection> connectionLayout;

	PickingManager pickingManager;

	int iViewID;

	int iGLDisplayListNode;
	int iGLDisplayListConnection;
	// int iGLDisplayListNodeRemote;
	// int iGLDisplayListConnectionRemote;
	protected Tree<IDrawAbleNode> tree = null;

	public ATreeLayouter(IViewFrustum frustum, PickingManager pickingManager, int iViewID) {
		this.viewFrustum = frustum;
		this.pickingManager = pickingManager;
		this.iViewID = iViewID;
		this.nodeLayout = new ArrayList<IDrawAbleNode>();
		this.connectionLayout = new ArrayList<IDrawAbleConnection>();
		updateSizeInfo();
	}

	@Override
	public final int compareTo(ATreeLayouter layouter) {
		return this.iComparableValue - layouter.iComparableValue;
	}

	protected abstract void renderTreeLayout(GL gl);

	protected final void updateSizeInfo() {
		fHeight = viewFrustum.getHeight();
		fWidth = viewFrustum.getWidth();
		fViewSpaceX[0] = fWidth * HyperbolicRenderStyle.X_BORDER_SPACING;
		fViewSpaceX[1] = fWidth - fWidth * HyperbolicRenderStyle.X_BORDER_SPACING;
		fViewSpaceXAbs = Math.abs(fViewSpaceX[0] - fViewSpaceX[1]);
		fViewSpaceY[0] = fHeight * HyperbolicRenderStyle.Y_BORDER_SPACING;
		fViewSpaceY[1] = fHeight - fHeight * HyperbolicRenderStyle.Y_BORDER_SPACING;
		fViewSpaceYAbs = Math.abs(fViewSpaceY[0] - fViewSpaceY[1]);
	}

	/*
	 * protected final void drawLayout(GL gl) { if (bIsNodeHighlighted) for (IDrawAbleNode node : layout) { if
	 * (node.getNodeNr() == iHighlightedNode) node.setHighlight(true); else node.setHighlight(false);
	 * gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.HYPERBOLIC_NODE_SELECTION,
	 * node.getNodeNr())); node.draw(gl); gl.glPopName(); } else for (IDrawAbleNode node : layout) {
	 * node.setHighlight(false); gl.glPushName(pickingManager.getPickingID(iViewID,
	 * EPickingType.HYPERBOLIC_NODE_SELECTION, node.getNodeNr())); node.draw(gl); gl.glPopName(); }
	 * resetHighlight(); }
	 */

	@Override
	public final void setLayoutDirty() {
		bIsConnectionHighlighted = false;
		bIsNodeHighlighted = false;
		bIsLayoutDirty = true;
		bIsNodeListDirty = true;
		bIsConnectionListDirty = true;
	}

	public final void setLayoutClean() {
		bIsLayoutDirty = false;
	}

	@Override
	public final void setHighlightedNode(int iNodeID) {
		resetHighlight();
		bIsNodeHighlighted = true;
		iHighlightedNode = iNodeID;
		bIsNodeListDirty = true;
	}

	public final void setHiglightedLine(int iLineID) {
		resetHighlight();
		bIsConnectionHighlighted = true;
		iHighlightedConnection = iLineID;
		bIsConnectionListDirty = true;
	}

	@Override
	public final void resetHighlight() {
		if (bIsConnectionHighlighted) {
			bIsConnectionHighlighted = false;
			bIsConnectionListDirty = true;
		}
		if (bIsNodeHighlighted) {
			bIsNodeHighlighted = false;
			bIsNodeListDirty = true;
		}
	}

	private final void buildDisplayListNodes(GL gl, int iGLListToBuild) {
		gl.glNewList(iGLListToBuild, GL.GL_COMPILE);
		for (IDrawAbleNode node : nodeLayout) {
			if (bIsNodeHighlighted && node.getNodeNr() == iHighlightedNode)
				node.setHighlight(true);
			else
				node.setHighlight(false);
			gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.HYPERBOLIC_NODE_SELECTION, node
				.getNodeNr()));
			node.draw(gl);
			gl.glPopName();
		}
		gl.glEndList();
	}

	private final void buildDisplayListConnections(GL gl, int iGLListToBuild) {
		gl.glNewList(iGLListToBuild, GL.GL_COMPILE);
		for (IDrawAbleConnection conn : connectionLayout) {
			if (bIsConnectionHighlighted && conn.getConnNr() == iHighlightedConnection)
				conn.setHighlight(true);
			else
				conn.setHighlight(false);
			gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.HYPERBOLIC_LINE_SELECTION, conn
				.getConnNr()));
			conn.draw(gl);
			gl.glPopName();
		}
		gl.glEndList();

	}

	// public final void initLocal(GL gl) {
	// iGLDisplayListNode = gl.glGenLists(1);
	// iGLDisplayListConnection = gl.glGenLists(1);
	// init(gl);
	// }

	// public final void initRemote(GL gl) {
	// iGLDisplayListConnectionRemote = gl.glGenLists(1);
	// iGLDisplayListNodeRemote = gl.glGenLists(1);
	// init(gl);

	@Override
	public void init(GL gl) {
		iGLDisplayListNode = gl.glGenLists(1);
		iGLDisplayListConnection = gl.glGenLists(1);
	}

	@Override
	public final void buildDisplayLists(GL gl) {
		if (bIsLayoutDirty) {
			setLayoutDirty();
			clearDisplay();
			renderTreeLayout(gl);

		}
		if (bIsNodeListDirty) {
			buildDisplayListNodes(gl, iGLDisplayListNode);
			bIsNodeListDirty = false;
		}
		if (bIsConnectionListDirty) {
			buildDisplayListConnections(gl, iGLDisplayListConnection);
			bIsConnectionListDirty = false;
		}
		// resetHighlight();
		setLayoutClean();
	}

	@Override
	public final void display(GL gl) {
		gl.glCallList(iGLDisplayListConnection);
		gl.glCallList(iGLDisplayListNode);
		
	}

	private final void clearDisplay() {
		nodeLayout.clear();
		connectionLayout.clear();
	}

	@Override
	public final void setTree(Tree<IDrawAbleNode> tree) {
		setLayoutDirty();
		this.tree = tree;
	}

	@Override
	public abstract void animateToNewTree(Tree<IDrawAbleNode> tree);
}
