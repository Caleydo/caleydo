package org.caleydo.view.compare.state;

import static org.caleydo.view.heatmap.dendrogram.DendrogramRenderStyle.DENDROGRAM_Z;
import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.awt.Point;
import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.SetRelations;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.general.GeneralManager;
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
import org.caleydo.view.compare.rendercommand.RenderCommandFactory;

import com.sun.opengl.util.j2d.TextRenderer;

public abstract class ACompareViewState {

	private final static float SET_BAR_HEIGHT_PORTION = 0.1f;

	protected TextRenderer textRenderer;
	protected TextureManager textureManager;
	protected PickingManager pickingManager;
	protected GLMouseListener glMouseListener;
	protected GLCompare view;
	protected int viewID;
	protected RenderCommandFactory renderCommandFactory;
	protected IEventPublisher eventPublisher;
	protected EDataDomain dataDomain;
	protected IUseCase useCase;
	protected DragAndDropController dragAndDropController;
	protected CompareViewStateController compareViewStateController;

	protected ArrayList<ISet> setsInFocus;
	protected SetBar setBar;
	protected ArrayList<HeatMapWrapper> heatMapWrappers;
	protected ArrayList<AHeatMapLayout> layouts;
	protected int numSetsInFocus;

	protected boolean setsChanged;
	protected boolean isInitialized;
	protected SetRelations relations;

	float yPosInitLeft = 0;
	float xPosInitLeft = 0;
	float yPosInitRight = 0;
	float xPosInitRight = 0;

	public ACompareViewState(GLCompare view, int viewID,
			TextRenderer textRenderer, TextureManager textureManager,
			PickingManager pickingManager, GLMouseListener glMouseListener,
			SetBar setBar, RenderCommandFactory renderCommandFactory,
			EDataDomain dataDomain, IUseCase useCase,
			DragAndDropController dragAndDropController,
			CompareViewStateController compareViewStateController) {
		this.view = view;
		this.viewID = viewID;
		this.textRenderer = textRenderer;
		this.textureManager = textureManager;
		this.pickingManager = pickingManager;
		this.glMouseListener = glMouseListener;
		this.setBar = setBar;
		this.renderCommandFactory = renderCommandFactory;
		this.dataDomain = dataDomain;
		this.useCase = useCase;
		this.dragAndDropController = dragAndDropController;
		this.compareViewStateController = compareViewStateController;

		setsInFocus = new ArrayList<ISet>();
		heatMapWrappers = new ArrayList<HeatMapWrapper>();
		layouts = new ArrayList<AHeatMapLayout>();

		setsChanged = false;

		eventPublisher = GeneralManager.get().getEventPublisher();
	}

	protected void renderTree(GL gl, HeatMapWrapper heatMapWrapperLeft,
			HeatMapWrapper heatMapWrapperRight, float overviewDistance) {

		if (setsInFocus == null || setsInFocus.size() == 0)
			return;

		AHeatMapLayout heatMapLayoutLeft = heatMapWrapperLeft.getLayout();
		AHeatMapLayout heatMapLayoutRight = heatMapWrapperRight.getLayout();

		xPosInitLeft = heatMapLayoutLeft.getOverviewHeatMapPosition().x()
				+ heatMapLayoutLeft.getOverviewHeatmapWidth();
		yPosInitLeft = heatMapLayoutLeft.getOverviewHeatMapPosition().y()
				+ heatMapLayoutLeft.getOverviewHeight();
		Tree<ClusterNode> tree = heatMapWrapperLeft.getSet().getContentTree();
		ClusterNode rootNode = tree.getRoot();

		determineTreePositions(rootNode, tree, heatMapWrapperLeft,
				overviewDistance);
		// renderDendrogram(gl, rootNode, 1, tree, xPosInitLeft);

		// Right hierarchy
		xPosInitRight = heatMapLayoutRight.getOverviewHeatMapPosition().x();
		yPosInitRight = heatMapLayoutRight.getOverviewHeatMapPosition().y()
				+ heatMapLayoutRight.getOverviewHeight();
		tree = heatMapWrapperRight.getSet().getContentTree();
		rootNode = tree.getRoot();

		determineTreePositions(rootNode, tree, heatMapWrapperLeft,
				overviewDistance);
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
	protected Vec3f determineTreePositions(ClusterNode currentNode,
			Tree<ClusterNode> tree, HeatMapWrapper heatMapWrapperLeft,
			float overviewGapWidth) {

		Vec3f pos = new Vec3f();

		AHeatMapLayout heatMapLayoutLeft = heatMapWrapperLeft.getLayout();

		// float levelWidth = (viewFrustum.getWidth() / 2f
		// - heatMapLayoutLeft.getOverviewSliderWidth()
		// - heatMapLayoutLeft.getOverviewSliderWidth() - heatMapLayoutLeft
		// .getOverviewHeatmapWidth())
		// / tree.getRoot().getDepth();
		float levelWidth = (overviewGapWidth / 2.0f)
				/ tree.getRoot().getDepth();

		float sampleHeight = heatMapLayoutLeft.getOverviewHeight()
				/ tree.getRoot().getNrLeaves();

		if (tree.hasChildren(currentNode)) {

			ArrayList<ClusterNode> alChilds = tree.getChildren(currentNode);

			int iNrChildsNode = alChilds.size();

			Vec3f[] positions = new Vec3f[iNrChildsNode];

			for (int i = 0; i < iNrChildsNode; i++) {

				ClusterNode node = alChilds.get(i);
				positions[i] = determineTreePositions(node, tree,
						heatMapWrapperLeft, overviewGapWidth);
			}

			float fXmin = Float.MAX_VALUE;
			float fYmax = Float.MIN_VALUE;
			float fYmin = Float.MAX_VALUE;

			for (Vec3f vec : positions) {
				fXmin = Math.min(fXmin, vec.x());
				fYmax = Math.max(fYmax, vec.y());
				fYmin = Math.min(fYmin, vec.y());
			}

			if (tree == heatMapWrapperLeft.getSet().getContentTree()) {
				pos.setX(fXmin + levelWidth);
			} else {
				pos.setX(fXmin - levelWidth);
			}

			pos.setY(fYmin + (fYmax - fYmin) / 2);
			pos.setZ(DENDROGRAM_Z);

		} else {

			if (tree == heatMapWrapperLeft.getSet().getContentTree()) {
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

	protected void renderOverviewRelations(GL gl,
			HeatMapWrapper leftHeatMapWrapper,
			HeatMapWrapper rightHeatMapWrapper) {

		if (setsInFocus == null || setsInFocus.size() == 0)
			return;

		// FIXME: Just for testing.
		// leftHeatMapWrapper.useDetailView(false);
		// rightHeatMapWrapper.useDetailView(false);

		float alpha = 0.6f;

		ContentSelectionManager contentSelectionManager = useCase
				.getContentSelectionManager();
		ContentVirtualArray contentVALeft = leftHeatMapWrapper.getSet()
				.getContentVA(ContentVAType.CONTENT);

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

			Vec2f leftPos = leftHeatMapWrapper
					.getRightOverviewLinkPositionFromContentID(contentID);

			if (leftPos == null)
				continue;

			Vec2f rightPos = rightHeatMapWrapper
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
			tree = leftHeatMapWrapper.getSet().getContentTree();
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
			tree = rightHeatMapWrapper.getSet().getContentTree();
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
			
			gl.glPushName(pickingManager.getPickingID(viewID,
					EPickingType.POLYLINE_SELECTION, contentID));

			gl.glBegin(GL.GL_LINE_STRIP);
			for (int i = 0; i < points.size(); i++)
				gl.glVertex3f(points.get(i).x(), points.get(i).y(), positionZ);
			gl.glEnd();

			gl.glPopName();
		}
	}

	public void executeDrawingPreprocessing(GL gl, boolean isDisplayListDirty) {
		IViewFrustum viewFrustum = view.getViewFrustum();
		if (isDisplayListDirty)
			setBar.setHeight(gl, SET_BAR_HEIGHT_PORTION
					* viewFrustum.getHeight());
		setupLayouts();
		
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

		case COMPARE_SET_BAR_SELECTION_WINDOW_SELECTION:
			setBar.handleSetBarSelectionWindowSelection(iExternalID,
					pickingMode, pick);
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

	public abstract void init(GL gl);

	public abstract void drawDisplayListElements(GL gl);

	public abstract void drawActiveElements(GL gl);

	public abstract void handleStateSpecificPickingEvents(
			EPickingType ePickingType, EPickingMode pickingMode,
			int iExternalID, Pick pick, boolean isControlPressed);

	public abstract ECompareViewStateType getStateType();

	public abstract void duplicateSetBarItem(int itemID);

	public abstract void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info);

	public abstract void handleSelectionCommand(EIDCategory category,
			SelectionCommand selectionCommand);
	
	public abstract void setSetsInFocus(ArrayList<ISet> setsInFocus);

	public abstract void adjustPValue();

	public abstract int getMaxSetsInFocus();

	public abstract int getMinSetsInFocus();

	public abstract void handleMouseWheel(GL gl, int amount, Point wheelPoint);
	
	protected abstract void setupLayouts();

	public void handleReplaceContentVA(int setID, EIDCategory idCategory,
			ContentVAType vaType) {
		
		// FIXME: we should not destroy all the heat map wrappers when a contentVA is handled
		setSetsInFocus(setBar.getSetsInFocus());
	}
}
