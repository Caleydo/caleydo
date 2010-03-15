package org.caleydo.view.compare.state;

import static org.caleydo.view.heatmap.dendrogram.DendrogramRenderStyle.DENDROGRAM_Z;
import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.SetComparer;
import org.caleydo.core.data.collection.set.SetRelations;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.SelectionTypeEvent;
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
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.core.view.opengl.util.vislink.NURBSCurve;
import org.caleydo.view.compare.GLCompare;
import org.caleydo.view.compare.HeatMapWrapper;
import org.caleydo.view.compare.SetBar;
import org.caleydo.view.compare.layout.AHeatMapLayout;
import org.caleydo.view.compare.layout.HeatMapLayoutLeft;
import org.caleydo.view.compare.layout.HeatMapLayoutRight;
import org.caleydo.view.compare.rendercommand.RenderCommandFactory;
import org.caleydo.view.compare.renderer.ICompareConnectionRenderer;

import com.sun.opengl.util.j2d.TextRenderer;

public class OverviewState extends ACompareViewState {

	private final static float SET_BAR_HEIGHT_PORTION = 0.1f;

	private SetRelations relations;
	float yPosInitLeft = 0;
	float xPosInitLeft = 0;
	float yPosInitRight = 0;
	float xPosInitRight = 0;

	private ICompareConnectionRenderer compareConnectionRenderer;

	public OverviewState(GLCompare view, int viewID, TextRenderer textRenderer,
			TextureManager textureManager, PickingManager pickingManager,
			GLMouseListener glMouseListener, SetBar setBar,
			RenderCommandFactory renderCommandFactory, EDataDomain dataDomain,
			IUseCase useCase, DragAndDropController dragAndDropController) {

		super(view, viewID, textRenderer, textureManager, pickingManager,
				glMouseListener, setBar, renderCommandFactory, dataDomain,
				useCase, dragAndDropController);
		this.setBar.setPosition(new Vec3f(0.0f, 0.0f, 0.0f));
		numSetsInFocus = 4;
	}

	@Override
	public void drawActiveElements(GL gl) {

		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			heatMapWrapper.drawRemoteItems(gl, glMouseListener, pickingManager);
		}

		dragAndDropController.handleDragging(gl, glMouseListener);

	}

	@Override
	public void drawDisplayListElements(GL gl) {

		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			heatMapWrapper.drawLocalItems(gl, textureManager, pickingManager,
					glMouseListener, viewID);
		}

		IViewFrustum viewFrustum = view.getViewFrustum();

		setBar.setWidth(viewFrustum.getWidth());
		setBar.render(gl);

		renderTree(gl);

		renderOverviewRelations(gl);

	}

	private void renderTree(GL gl) {

		if (setsToCompare == null || setsToCompare.size() == 0)
			return;

		// FIXME: This is not general enough for more than 2 heatmap wrappers
		// Left hierarchy
		AHeatMapLayout heatMapLayoutLeft = layouts.get(0);
		AHeatMapLayout heatMapLayoutRight = layouts.get(1);

		xPosInitLeft = heatMapLayoutLeft.getOverviewHeatmapWidth()
				+ heatMapLayoutLeft.getOverviewGroupWidth()
				+ heatMapLayoutLeft.getOverviewSliderWidth();
		yPosInitLeft = heatMapLayoutLeft.getOverviewPosition().y()
				+ heatMapLayoutLeft.getOverviewHeight();
		Tree<ClusterNode> tree = setsToCompare.get(0).getContentTree();
		ClusterNode rootNode = tree.getRoot();

		determineTreePositions(rootNode, tree);
		// renderDendrogram(gl, rootNode, 1, tree, xPosInitLeft);

		IViewFrustum viewFrustum = view.getViewFrustum();

		// Right hierarchy
		xPosInitRight = viewFrustum.getWidth()
				- heatMapLayoutRight.getOverviewHeatmapWidth()
				- heatMapLayoutRight.getOverviewGroupWidth()
				- heatMapLayoutRight.getOverviewSliderWidth();
		yPosInitRight = heatMapLayoutRight.getOverviewPosition().y()
				+ heatMapLayoutRight.getOverviewHeight();
		tree = setsToCompare.get(1).getContentTree();
		rootNode = tree.getRoot();

		determineTreePositions(rootNode, tree);
		// renderDendrogram(gl, rootNode, 1, tree, xPosInitRight);
	}

	/**
	 * Function calculates for each node (gene or entity) in the dendrogram
	 * recursive the corresponding position inside the view frustum
	 * 
	 * @param currentNode
	 *            current node for calculation
	 * @return Vec3f position of the current node
	 */
	private Vec3f determineTreePositions(ClusterNode currentNode,
			Tree<ClusterNode> tree) {

		Vec3f pos = new Vec3f();

		IViewFrustum viewFrustum = view.getViewFrustum();
		AHeatMapLayout heatMapLayoutLeft = layouts.get(0);

		float levelWidth = (viewFrustum.getWidth() / 2f
				- heatMapLayoutLeft.getOverviewSliderWidth()
				- heatMapLayoutLeft.getOverviewSliderWidth() - heatMapLayoutLeft
				.getOverviewHeatmapWidth())
				/ tree.getRoot().getDepth();

		float sampleHeight = heatMapLayoutLeft.getOverviewHeight()
				/ tree.getRoot().getNrLeaves();

		if (tree.hasChildren(currentNode)) {

			ArrayList<ClusterNode> alChilds = tree.getChildren(currentNode);

			int iNrChildsNode = alChilds.size();

			Vec3f[] positions = new Vec3f[iNrChildsNode];

			for (int i = 0; i < iNrChildsNode; i++) {

				ClusterNode node = alChilds.get(i);
				positions[i] = determineTreePositions(node, tree);
			}

			float fXmin = Float.MAX_VALUE;
			float fYmax = Float.MIN_VALUE;
			float fYmin = Float.MAX_VALUE;

			for (Vec3f vec : positions) {
				fXmin = Math.min(fXmin, vec.x());
				fYmax = Math.max(fYmax, vec.y());
				fYmin = Math.min(fYmin, vec.y());
			}

			if (tree == setsToCompare.get(0).getContentTree()) {
				pos.setX(fXmin + levelWidth);
			} else {
				pos.setX(fXmin - levelWidth);
			}

			pos.setY(fYmin + (fYmax - fYmin) / 2);
			pos.setZ(DENDROGRAM_Z);

		} else {

			if (tree == setsToCompare.get(0).getContentTree()) {
				pos.setX(xPosInitLeft + levelWidth);
				pos.setY(yPosInitLeft);
				yPosInitLeft -= sampleHeight;
			} else {
				pos.setX(xPosInitRight - levelWidth);
				pos.setY(yPosInitRight);
				yPosInitRight -= sampleHeight;
			}

			pos.setZ(DENDROGRAM_Z);
		}

		currentNode.setPos(pos);

		return pos;
	}

	private void renderOverviewRelations(GL gl) {

		if (setsToCompare == null || setsToCompare.size() == 0)
			return;

		// FIXME: Just for testing.
		// leftHeatMapWrapper.useDetailView(false);
		// rightHeatMapWrapper.useDetailView(false);

		float alpha = 0.6f;

		ContentSelectionManager contentSelectionManager = useCase
				.getContentSelectionManager();
		ContentVirtualArray contentVALeft = setsToCompare.get(0).getContentVA(
				ContentVAType.CONTENT);

		// gl.glColor3f(0, 0, 0);
		for (Integer contentID : contentVALeft) {

			float positionZ = 0.0f;

			for (SelectionType type : contentSelectionManager
					.getSelectionTypes(contentID)) {

				float[] typeColor = type.getColor();
				typeColor[3] = alpha;
				gl.glColor4fv(typeColor, 0);
				positionZ = type.getPriority();

				if (type == SelectionType.MOUSE_OVER
						|| type == SelectionType.SELECTION) {
					gl.glLineWidth(5);
					break;
				} else {
					gl.glLineWidth(1);
					break;
				}
			}

			Vec2f leftPos = heatMapWrappers.get(0)
					.getRightOverviewLinkPositionFromContentID(contentID);

			if (leftPos == null)
				continue;

			Vec2f rightPos = heatMapWrappers.get(1)
					.getLeftOverviewLinkPositionFromContentID(contentID);

			if (rightPos == null)
				continue;

			ArrayList<Vec3f> points = new ArrayList<Vec3f>();
			points.add(new Vec3f(leftPos.x(), leftPos.y(), 0));

			Tree<ClusterNode> tree;
			int nodeID;
			ClusterNode node;
			ArrayList<ClusterNode> pathToRoot;

			// Add spline points for left hierarchy
			tree = setsToCompare.get(0).getContentTree();
			nodeID = tree.getNodeIDsFromLeafID(contentID).get(0);
			node = tree.getNodeByNumber(nodeID);
			pathToRoot = node.getParentPath(tree.getRoot());

			// Remove last because it is root bundling
			pathToRoot.remove(pathToRoot.size() - 1);

			for (ClusterNode pathNode : pathToRoot) {
				Vec3f nodePos = pathNode.getPos();
				points.add(nodePos);
			}

			// Add spline points for right hierarchy
			tree = setsToCompare.get(1).getContentTree();
			nodeID = tree.getNodeIDsFromLeafID(contentID).get(0);
			node = tree.getNodeByNumber(nodeID);
			pathToRoot = node.getParentPath(tree.getRoot());

			// Remove last because it is root bundling
			pathToRoot.remove(pathToRoot.size() - 1);

			for (ClusterNode pathNode : pathToRoot) {
				Vec3f nodePos = pathNode.getPos();
				points.add(nodePos);
				break; // FIXME: REMOVE BREAK
			}

			// Center point
			// points.add(new Vec3f(viewFrustum.getWidth() / 2f, viewFrustum
			// .getHeight() / 2f, 0));
			// points.add(new Vec3f(2, 4, 0));
			// points.add(new Vec3f(1,5,0));

			points.add(new Vec3f(rightPos.x(), rightPos.y(), 0));

			NURBSCurve curve = new NURBSCurve(points, 30);
			points = curve.getCurvePoints();

			// VisLink.renderLine(gl, controlPoints, offset, numberOfSegments,
			// shadow)

			gl.glPushName(pickingManager.getPickingID(viewID,
					EPickingType.POLYLINE_SELECTION, contentID));

			gl.glBegin(GL.GL_LINE_STRIP);
			for (int i = 0; i < points.size(); i++)
				gl.glVertex3f(points.get(i).x(), points.get(i).y(), positionZ);
			gl.glEnd();

			gl.glPopName();
		}
	}

	@Override
	public void executeDrawingPreprocessing(GL gl, boolean isDisplayListDirty) {

		IViewFrustum viewFrustum = view.getViewFrustum();
		if (isDisplayListDirty)
			setBar.setHeight(gl, SET_BAR_HEIGHT_PORTION
					* viewFrustum.getHeight());
		// The setBar is an AGLGUIElement, therefore the above assignment is not
		// necessarily applied
		float setBarHeight = setBar.getHeight();
		float heatMapWrapperPosY = setBar.getPosition().y() + setBarHeight;

		float heatMapWrapperPosX = 0.0f;
		float heatMapWrapperWidth = viewFrustum.getRight()
				/ (2.0f * (float) heatMapWrappers.size() - 1.0f);
		for (AHeatMapLayout layout : layouts) {
			layout
					.setLayoutParameters(heatMapWrapperPosX,
							heatMapWrapperPosY, viewFrustum.getHeight()
									- setBarHeight, heatMapWrapperWidth);
			heatMapWrapperPosX += heatMapWrapperWidth * 2.0f;
		}

		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			if (setsChanged) {
				heatMapWrapper.init(gl, glMouseListener, null, dataDomain);
			}
			heatMapWrapper.processEvents();
			heatMapWrapper.calculateDrawingParameters();
			if (isDisplayListDirty) {
				heatMapWrapper.setDisplayListDirty();
			}
		}

		setsChanged = false;
	}

	@Override
	public void handlePickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick,
			boolean isControlPressed) {
		SelectionType selectionType = null;

		HeatMapWrapper leftHeatMapWrapper = heatMapWrappers.get(0);
		HeatMapWrapper rightHeatMapWrapper = heatMapWrappers.get(1);

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

		case COMPARE_SET_BAR_SELECTION_WINDOW_SELECTION:
			setBar.handleSetBarSelectionWindowSelection(iExternalID,
					pickingMode, pick);
			break;
		}
	}

	@Override
	public void init(GL gl) {

		compareConnectionRenderer.init(gl);

		// leftHeatMapWrapper.setSet(set);
		// rightHeatMapWrapper.setSet(set);

		setsChanged = false;

	}

	@Override
	public void setSetsToCompare(ArrayList<ISet> setsToCompare) {
		// TODO Auto-generated method stub

	}

	@Override
	public ECompareViewStateType getStateType() {
		return ECompareViewStateType.OVERVIEW;
	}

	@Override
	public void duplicateSetBarItem(int itemID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSetsInFocus(ArrayList<ISet> setsInFocus) {

		if (setsInFocus.size() >= getMinSetsInFocus()
				&& setsInFocus.size() <= getMaxSetsInFocus()) {

			setsToCompare = setsInFocus;

			if (layouts.isEmpty() || setsInFocus.size() != layouts.size()) {
				layouts.clear();
				layouts.add(new HeatMapLayoutLeft(renderCommandFactory));
				layouts.add(new HeatMapLayoutRight(renderCommandFactory));

				int heatMapWrapperID = 0;
				for (ISet set : setsInFocus) {
					AHeatMapLayout layout = null;
					if (heatMapWrapperID == 0) {
						layout = new HeatMapLayoutLeft(renderCommandFactory);
					}
					HeatMapWrapper heatMapWrapper = new HeatMapWrapper(
							heatMapWrapperID, layout, view, null, useCase,
							view, dataDomain, null);
					heatMapWrappers.add(heatMapWrapper);
					heatMapWrapperID++;
				}
			}

			ISet setLeft = setsInFocus.get(0);
			ISet setRight = setsInFocus.get(1);
			relations = SetComparer.compareSets(setLeft, setRight);

			heatMapWrappers.get(0).setSet(setLeft);
			heatMapWrappers.get(0).setRelations(relations);
			heatMapWrappers.get(1).setSet(setRight);
			heatMapWrappers.get(1).setRelations(relations);
			setsChanged = true;

			view.setDisplayListDirty();
		}

	}

	@Override
	public void adjustPValue() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getMaxSetsInFocus() {
		return 6;
	}

	@Override
	public int getMinSetsInFocus() {
		return 2;
	}

}
