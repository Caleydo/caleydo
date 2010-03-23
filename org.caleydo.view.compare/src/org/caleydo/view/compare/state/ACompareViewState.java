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
import org.caleydo.core.data.selection.ContentGroupList;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IUseCase;
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

	protected final static float SET_BAR_HEIGHT_PORTION = 0.07f;

	protected TextRenderer textRenderer;
	protected TextureManager textureManager;
	protected PickingManager pickingManager;
	protected GLMouseListener glMouseListener;
	protected GLCompare view;
	protected int viewID;
	protected SetBar setBar;
	protected ArrayList<HeatMapWrapper> heatMapWrappers;
	protected ArrayList<AHeatMapLayout> layouts;

	protected RenderCommandFactory renderCommandFactory;
	protected IEventPublisher eventPublisher;
	protected EDataDomain dataDomain;
	protected IUseCase useCase;
	protected DragAndDropController dragAndDropController;
	protected CompareViewStateController compareViewStateController;
	// protected HashMap<ClusterNode, Vec3f> hashNodePositions;

	protected ArrayList<ISet> setsInFocus;
	protected int numSetsInFocus;

	protected boolean setsChanged;
	protected boolean isInitialized;
	
	protected SetRelations relations;

	float yPosInitLeft = 0;
	float xPosInitLeft = 0;
	float yPosInitRight = 0;
	float xPosInitRight = 0;

	public ACompareViewState(GLCompare view, int viewID, TextRenderer textRenderer,
			TextureManager textureManager, PickingManager pickingManager,
			GLMouseListener glMouseListener, SetBar setBar,
			RenderCommandFactory renderCommandFactory, EDataDomain dataDomain,
			IUseCase useCase, DragAndDropController dragAndDropController,
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

		heatMapWrappers = new ArrayList<HeatMapWrapper>();
		layouts = new ArrayList<AHeatMapLayout>();
		setsInFocus = new ArrayList<ISet>();

		// hashNodePositions = new HashMap<ClusterNode, Vec3f>();

		setsChanged = false;

		eventPublisher = GeneralManager.get().getEventPublisher();
	}

	public void executeDrawingPreprocessing(GL gl, boolean isDisplayListDirty) {
		
		handleDragging(gl);

		IViewFrustum viewFrustum = view.getViewFrustum();
		if (isDisplayListDirty)
			setBar.setHeight(gl, SET_BAR_HEIGHT_PORTION * viewFrustum.getHeight());
		setupLayouts();

		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			if (!heatMapWrapper.isInitialized()) {
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

	protected float setRelationColor(GL gl, HeatMapWrapper heatMapWrapper,
			int contentID) {
		
		SelectionType type = heatMapWrapper.getContentSelectionManager()
				.getSelectionTypes(contentID).get(0);

		float z = type.getPriority();
		float[] typeColor = type.getColor();
		float alpha = 0;
		// if (type == activeHeatMapSelectionType) {
		// gl.glLineWidth(1);
		// alpha = 0.4f;
		// z = 0.4f;
		// } else
		if (type == SelectionType.MOUSE_OVER) {
			gl.glLineWidth(2);
			alpha = 1f;
			z = 0.6f;
		} else if (type == SelectionType.SELECTION) {
			gl.glLineWidth(2);
			alpha = 1f;
			z = 0.6f;
		} else {
			gl.glLineWidth(1);
			alpha = 0.4f;
			z = 0.4f;
			// if (isConnectionCrossing(contentID,
			// heatMapWrapper.getContentVA(),
			// heatMapWrapper.getContentVA(), heatMapWrapper))
			// alpha = 0.5f;
			// else
			// alpha = 0.3f;
		}

		typeColor[3] = alpha;
		gl.glColor4fv(typeColor, 0);

		return z;
	}

	public abstract void setSetsToCompare(ArrayList<ISet> setsToCompare);

	public abstract void handlePickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick, boolean isControlPressed);

	public abstract int getNumSetsInFocus();

	public abstract boolean isInitialized();

	public abstract void handleContentGroupListUpdate(int setID,
			ContentGroupList contentGroupList);

	public abstract void handleReplaceContentVA(int setID, EIDCategory idCategory,
			ContentVAType vaType);

	public abstract void init(GL gl);

	public abstract void buildDisplayList(GL gl);

	public abstract void drawActiveElements(GL gl);

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

	public void setUseSorting(boolean useSorting) {

	}

	public void setUseZoom(boolean useZoom) {

	}

	public void setUseFishEye(boolean useFishEye) {

	}

	/**
	 * Handles the dragging of the current state. Call this after all rendering
	 * of the state has finished.
	 * 
	 * @param gl GL context.
	 */
	public void handleDragging(GL gl) {
		dragAndDropController.handleDragging(gl, glMouseListener);
	}

	protected ArrayList<HeatMapWrapper> getHeatMapWrappers() {
		return heatMapWrappers;
	}

	protected ArrayList<AHeatMapLayout> getLayouts() {
		return layouts;
	}
}
