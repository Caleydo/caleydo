package org.caleydo.view.compare.state;

import static org.caleydo.view.heatmap.dendrogram.DendrogramRenderStyle.DENDROGRAM_Z;
import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.SetRelations;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.ContentGroupList;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.core.view.opengl.util.vislink.NURBSCurve;
import org.caleydo.view.compare.GLCompare;
import org.caleydo.view.compare.HeatMapWrapper;
import org.caleydo.view.compare.SetBar;
import org.caleydo.view.compare.layout.AHeatMapLayout;
import org.caleydo.view.compare.rendercommand.RenderCommandFactory;

import com.sun.opengl.util.j2d.TextRenderer;

public abstract class ACompareViewStateStatic extends ACompareViewState {

	protected SetRelations relations;

	float yPosInitLeft = 0;
	float xPosInitLeft = 0;
	float yPosInitRight = 0;
	float xPosInitRight = 0;

	public ACompareViewStateStatic(GLCompare view, int viewID,
			TextRenderer textRenderer, TextureManager textureManager,
			PickingManager pickingManager, GLMouseListener glMouseListener,
			SetBar setBar, RenderCommandFactory renderCommandFactory,
			EDataDomain dataDomain, IUseCase useCase,
			DragAndDropController dragAndDropController,
			CompareViewStateController compareViewStateController) {
		super(view, viewID, textRenderer, textureManager, pickingManager,
				glMouseListener, setBar, renderCommandFactory, dataDomain,
				useCase, dragAndDropController, compareViewStateController);
	}

	protected void renderTree(GL gl, HeatMapWrapper heatMapWrapperLeft,
			HeatMapWrapper heatMapWrapperRight) {

		if (setsInFocus == null || setsInFocus.size() == 0)
			return;

		AHeatMapLayout heatMapLayoutLeft = heatMapWrapperLeft.getLayout();
		AHeatMapLayout heatMapLayoutRight = heatMapWrapperRight.getLayout();

		xPosInitLeft = heatMapLayoutLeft.getOverviewHeatMapPosition().x()
				+ heatMapLayoutLeft.getOverviewHeatMapWidth();
		yPosInitLeft = heatMapLayoutLeft.getOverviewHeatMapPosition().y()
				+ heatMapLayoutLeft.getOverviewHeight();
		xPosInitRight = heatMapLayoutRight.getOverviewHeatMapPosition().x();
		yPosInitRight = heatMapLayoutRight.getOverviewHeatMapPosition().y()
				+ heatMapLayoutRight.getOverviewHeight();

		float overviewDistance = xPosInitRight - xPosInitLeft;

		// Left hierarchy
		Tree<ClusterNode> tree = heatMapWrapperLeft.getSet().getContentTree();
		ClusterNode rootNode = tree.getRoot();
		determineTreePositions(rootNode, tree, heatMapWrapperLeft,
				overviewDistance, true);
		// renderDendrogram(gl, rootNode, 1, tree, xPosInitLeft, true);

		// Right hierarchy
		tree = heatMapWrapperRight.getSet().getContentTree();
		rootNode = tree.getRoot();
		determineTreePositions(rootNode, tree, heatMapWrapperRight,
				overviewDistance, false);
		// renderDendrogram(gl, rootNode, 1, tree, xPosInitRight, false);
	}

	/**
	 * Function calculates for each node (gene or entity) in the dendrogram
	 * recursive the corresponding position inside the view frustum
	 * 
	 * @param currentNode
	 *            current node for calculation
	 * @return Vec3f position of the current node
	 */
	protected Vec3f determineTreePositions(ClusterNode currentNode,
			Tree<ClusterNode> tree, HeatMapWrapper heatMapWrapper,
			float overviewGapWidth, boolean isLeft) {

		Vec3f pos = new Vec3f();

		AHeatMapLayout heatMapLayoutLeft = heatMapWrapper.getLayout();

		float depthCorrection = 0;
		if (tree.getDepth() > 2)
			depthCorrection = 1;
		else {
			// subtract -1 instead of -2 for full dendrograms including root
			depthCorrection = 2;
		}

		float levelWidth = (overviewGapWidth / 2.0f)
				/ (tree.getRoot().getDepth() - depthCorrection);

		float sampleHeight = heatMapLayoutLeft.getOverviewHeight()
				/ tree.getRoot().getNrLeaves();

		if (tree.hasChildren(currentNode)) {

			ArrayList<ClusterNode> alChilds = tree.getChildren(currentNode);

			int iNrChildsNode = alChilds.size();

			Vec3f[] positions = new Vec3f[iNrChildsNode];

			for (int i = 0; i < iNrChildsNode; i++) {

				ClusterNode node = alChilds.get(i);
				positions[i] = determineTreePositions(node, tree,
						heatMapWrapper, overviewGapWidth, isLeft);
			}

			if (currentNode != tree.getRoot()) {
				float fXmin = Float.MAX_VALUE;
				float fXmax = Float.MIN_VALUE;
				float fYmax = Float.MIN_VALUE;
				float fYmin = Float.MAX_VALUE;

				for (Vec3f vec : positions) {
					fXmin = Math.min(fXmin, vec.x());
					fXmax = Math.max(fXmax, vec.x());
					fYmax = Math.max(fYmax, vec.y());
					fYmin = Math.min(fYmin, vec.y());
				}

				if (isLeft) {
					pos.setX(fXmax + levelWidth);
				} else {
					pos.setX(fXmin - levelWidth);
				}

				pos.setY(fYmin + (fYmax - fYmin) / 2);
				pos.setZ(DENDROGRAM_Z);
			}
		} else {

			if (isLeft) {
				pos.setX(xPosInitLeft);
				pos.setY(yPosInitLeft);
				yPosInitLeft -= sampleHeight;
			} else {
				pos.setX(xPosInitRight);
				pos.setY(yPosInitRight);
				yPosInitRight -= sampleHeight;
			}

			pos.setZ(DENDROGRAM_Z);
		}

		currentNode.setPos(pos);

		return pos;
	}

	protected void renderOverviewRelations(GL gl,
			HeatMapWrapper leftHeatMapWrapper,
			HeatMapWrapper rightHeatMapWrapper) {

		boolean useDendrogramCutOff = false;
		float dendrogramCutOff = 5;

		if (setsInFocus == null || setsInFocus.size() == 0)
			return;

		ContentVirtualArray contentVALeft = leftHeatMapWrapper.getSet()
				.getContentVA(ContentVAType.CONTENT);

		for (Integer contentID : contentVALeft) {

			float positionZ = setRelationColor(gl, leftHeatMapWrapper,
					contentID);

			Vec2f leftPos = leftHeatMapWrapper
					.getRightOverviewLinkPositionFromContentID(contentID);

			if (leftPos == null)
				continue;

			Vec2f rightPos = rightHeatMapWrapper
					.getLeftOverviewLinkPositionFromContentID(contentID);

			if (rightPos == null)
				continue;

			ArrayList<Vec3f> points = new ArrayList<Vec3f>();
			points.add(new Vec3f(leftPos.x(), leftPos.y(), positionZ));

			Tree<ClusterNode> tree;
			int nodeID;
			ClusterNode node;
			ArrayList<ClusterNode> pathToRoot;

			// Add spline points for left hierarchy
			tree = leftHeatMapWrapper.getSet().getContentTree();
			nodeID = tree.getNodeIDsFromLeafID(contentID).get(0);
			node = tree.getNodeByNumber(nodeID);
			pathToRoot = node.getParentPath(tree.getRoot());

			// Remove last because it is root bundling
			pathToRoot.remove(pathToRoot.size() - 1);

			for (int i = 0; i < pathToRoot.size() - 1; i++) {

				if (useDendrogramCutOff && i > dendrogramCutOff)
					continue;

				// Vec3f nodePos = pathNode.getPos();
				Vec3f nodePos = pathToRoot.get(i).getPos();
				points.add(nodePos);
			}

			// Add spline points for right hierarchy
			tree = rightHeatMapWrapper.getSet().getContentTree();
			nodeID = tree.getNodeIDsFromLeafID(contentID).get(0);
			node = tree.getNodeByNumber(nodeID);
			pathToRoot = node.getParentPath(tree.getRoot());

			// Remove last because it is root bundling
			pathToRoot.remove(pathToRoot.size() - 1);

			for (int i = pathToRoot.size() - 1; i >= 0; i--) {

				if (useDendrogramCutOff && i > dendrogramCutOff)
					continue;

				// Vec3f nodePos = pathNode.getPos();
				ClusterNode pathNode = pathToRoot.get(i);
				Vec3f nodePos = pathNode.getPos();
				points.add(nodePos);
			}

			// Center point
			// points.add(new Vec3f(viewFrustum.getWidth() / 2f, viewFrustum
			// .getHeight() / 2f, 0));
			// points.add(new Vec3f(2, 4, 0));
			// points.add(new Vec3f(1,5,0));

			points.add(new Vec3f(rightPos.x(), rightPos.y(), 0));

			if (points.size() == 0)
				continue;

			NURBSCurve curve = new NURBSCurve(points, 80);
			points = curve.getCurvePoints();

			gl.glPushName(pickingManager.getPickingID(viewID,
					EPickingType.POLYLINE_SELECTION, contentID));

			gl.glBegin(GL.GL_LINE_STRIP);
			for (int i = 0; i < points.size(); i++)
				gl.glVertex3f(points.get(i).x(), points.get(i).y(), positionZ);
			gl.glEnd();

			gl.glPopName();
		}
	}

	private void renderDendrogram(final GL gl, ClusterNode currentNode,
			float fOpacity, Tree<ClusterNode> tree, float xPosInit,
			boolean isLeft) {

		// float fLookupValue = currentNode.getAverageExpressionValue();
		// float[] fArMappingColor = colorMapper.getColor(fLookupValue);

		// if (bUseBlackColoring)
		gl.glColor4f(0, 0, 0, 1);
		// else
		// gl.glColor4f(fArMappingColor[0], fArMappingColor[1],
		// fArMappingColor[2], fOpacity);

		float fDiff = 0;

		float fTemp = currentNode.getPos().x();

		List<ClusterNode> listGraph = null;

		if (tree.hasChildren(currentNode)) {
			listGraph = tree.getChildren(currentNode);

			int iNrChildsNode = listGraph.size();

			float xmin = Float.MAX_VALUE;
			float xmax = Float.MIN_VALUE;
			float ymax = Float.MIN_VALUE;
			float ymin = Float.MAX_VALUE;

			Vec3f[] tempPositions = new Vec3f[iNrChildsNode];
			for (int i = 0; i < iNrChildsNode; i++) {

				ClusterNode current = listGraph.get(i);

				tempPositions[i] = new Vec3f();
				tempPositions[i].setX(current.getPos().x());
				tempPositions[i].setY(current.getPos().y());
				tempPositions[i].setZ(current.getPos().z());

				xmin = Math.min(xmin, current.getPos().x());
				xmax = Math.max(xmax, current.getPos().x());
				ymax = Math.max(ymax, current.getPos().y());
				ymin = Math.min(ymin, current.getPos().y());

				renderDendrogram(gl, current, 1, tree, xPosInit, isLeft);
			}

			float x = 0;
			if (isLeft) {
				fDiff = fTemp - xmax;
				x = xmax;
			} else {
				fDiff = fTemp - xmin;
				x = xmin;
			}

			gl.glPushName(pickingManager.getPickingID(this.viewID,
					EPickingType.DENDROGRAM_GENE_NODE_SELECTION, currentNode
							.getID()));

			// vertical line connecting all child nodes
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(x, ymin, currentNode.getPos().z());
			gl.glVertex3f(x, ymax, currentNode.getPos().z());
			gl.glEnd();

			// horizontal lines connecting all children with their parent
			for (int i = 0; i < iNrChildsNode; i++) {
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(x, tempPositions[i].y(), tempPositions[i].z());
				gl.glVertex3f(tempPositions[i].x(), tempPositions[i].y(),
						tempPositions[i].z());
				gl.glEnd();
			}

			gl.glPopName();

		} else {
			gl.glPushName(pickingManager.getPickingID(this.viewID,
					EPickingType.DENDROGRAM_GENE_LEAF_SELECTION, currentNode
							.getID()));

			// horizontal line visualizing leaf nodes
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(currentNode.getPos().x(), currentNode.getPos().y(),
					currentNode.getPos().z());
			gl.glVertex3f(xPosInit, currentNode.getPos().y(), currentNode
					.getPos().z());
			gl.glEnd();

			gl.glPopName();
		}

		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(currentNode.getPos().x() - fDiff, currentNode.getPos()
				.y(), currentNode.getPos().z());
		gl.glVertex3f(currentNode.getPos().x(), currentNode.getPos().y(),
				currentNode.getPos().z());
		gl.glEnd();

	}

	public void setSetsToCompare(ArrayList<ISet> setsToCompare) {
		setBar.setSets(setsToCompare);
	}

	public void handlePickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick,
			boolean isControlPressed) {
		SelectionType selectionType = null;

		switch (ePickingType) {

		case POLYLINE_SELECTION:

			switch (pickingMode) {
			case CLICKED:
				selectionType = SelectionType.SELECTION;
				break;
			case MOUSE_OVER:
				selectionType = SelectionType.MOUSE_OVER;
				break;
			case RIGHT_CLICKED:
				selectionType = SelectionType.SELECTION;

				// ContentContextMenuItemContainer
				// contentContextMenuItemContainer = new
				// ContentContextMenuItemContainer();
				// contentContextMenuItemContainer.setID(
				// EIDType.EXPRESSION_INDEX, iExternalID);
				// contextMenu
				// .addItemContanier(contentContextMenuItemContainer);
				break;

			default:
				return;

			}

			// FIXME: Check if is ok to share the content selection manager
			// of the use case
			ContentSelectionManager contentSelectionManager = useCase
					.getContentSelectionManager();
			if (contentSelectionManager.checkStatus(selectionType, iExternalID)) {
				break;
			}

			contentSelectionManager.clearSelection(selectionType);
			contentSelectionManager.addToType(selectionType, iExternalID);

			ISelectionDelta selectionDelta = contentSelectionManager.getDelta();
			SelectionUpdateEvent event = new SelectionUpdateEvent();
			event.setSender(this);
			event.setSelectionDelta((SelectionDelta) selectionDelta);
			// event.setInfo(getShortInfoLocal());
			eventPublisher.triggerEvent(event);

			view.setDisplayListDirty();
			break;

		case COMPARE_SET_BAR_ITEM_SELECTION:
			setBar.handleSetBarItemSelection(iExternalID, pickingMode, pick);
			break;

		case COMPARE_SELECTION_WINDOW_SELECTION:
		case COMPARE_SELECTION_WINDOW_ARROW_LEFT_SELECTION:
		case COMPARE_SELECTION_WINDOW_ARROW_RIGHT_SELECTION:
			setBar.handleSetBarSelectionWindowSelection(iExternalID,
					ePickingType, pickingMode, pick);
			break;
		}

		handleStateSpecificPickingEvents(ePickingType, pickingMode,
				iExternalID, pick, isControlPressed);
	}

	public int getNumSetsInFocus() {
		return numSetsInFocus;
	}

	public boolean isInitialized() {
		return isInitialized;
	}

	public void handleContentGroupListUpdate(int setID,
			ContentGroupList contentGroupList) {
		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			if (heatMapWrapper.getSet().getID() == setID) {
				heatMapWrapper.handleContentGroupListUpdate(contentGroupList);
				view.setDisplayListDirty();
				return;
			}
		}
	}

	public void handleReplaceContentVA(int setID, EIDCategory idCategory,
			ContentVAType vaType) {

		// FIXME: we should not destroy all the heat map wrappers when a
		// contentVA is handled
		setSetsInFocus(setBar.getSetsInFocus());
	}

	public abstract void handleStateSpecificPickingEvents(
			EPickingType ePickingType, EPickingMode pickingMode,
			int iExternalID, Pick pick, boolean isControlPressed);

	// public abstract void init(GL gl);
	//
	// public abstract void drawDisplayListElements(GL gl);
	//
	// public abstract void drawActiveElements(GL gl);
	//
	// public abstract void handleStateSpecificPickingEvents(
	// EPickingType ePickingType, EPickingMode pickingMode,
	// int iExternalID, Pick pick, boolean isControlPressed);
	//
	// public abstract ECompareViewStateType getStateType();
	//
	// public abstract void duplicateSetBarItem(int itemID);
	//
	// public abstract void handleSelectionUpdate(ISelectionDelta
	// selectionDelta,
	// boolean scrollToSelection, String info);
	//
	// public abstract void handleSelectionCommand(EIDCategory category,
	// SelectionCommand selectionCommand);
	//
	// public abstract void setSetsInFocus(ArrayList<ISet> setsInFocus);
	//
	// public abstract void adjustPValue();
	//
	// public abstract int getMaxSetsInFocus();
	//
	// public abstract int getMinSetsInFocus();
	//
	// public abstract void handleMouseWheel(GL gl, int amount, Point
	// wheelPoint);
	//
	// protected abstract void setupLayouts();

}
