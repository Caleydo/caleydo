package org.caleydo.view.compare;

import static org.caleydo.view.heatmap.DendrogramRenderStyle.DENDROGRAM_Z;
import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.SelectionTypeEvent;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.manager.event.view.grouper.CompareGroupsEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.util.clusterer.EClustererAlgo;
import org.caleydo.core.util.clusterer.EClustererType;
import org.caleydo.core.util.clusterer.EDistanceMeasure;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.container.ContentContextMenuItemContainer;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.vislink.NURBSCurve;
import org.caleydo.view.compare.listener.CompareGroupsEventListener;

/**
 * The group assignment interface
 * 
 * @author Christian Partl
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GLCompare extends AGLView implements IViewCommandHandler,
		IGLRemoteRenderingView {

	public final static String VIEW_ID = "org.caleydo.view.compare";

	private ArrayList<ISet> setsToCompare;
	private HashMap<Integer, HeatMapWrapper> hashHeatMapWrappers;

	// private TextRenderer textRenderer;
	private HeatMapLayoutLeft heatMapLayoutLeft;
	private HeatMapLayoutRight heatMapLayoutRight;
	private HeatMapWrapper leftHeatMapWrapper;
	private HeatMapWrapper rightHeatMapWrapper;
	private SelectionType activeHeatMapSelectionType;

	private CompareGroupsEventListener compareGroupsEventListener;

	float yPosInitLeft = 0;
	float xPosInitLeft = 0;
	float yPosInitRight = 0;
	float xPosInitRight = 0;

	private boolean areNewSetsAvailable;
	private boolean isControlPressed;

	private ArrayList<Pair<Float, Integer>> sortedClustersXOffsetUp;
	private ArrayList<Pair<Float, Integer>> sortedClustersXOffsetDown;

	private float xOffset = 0;

	// private SetRelations relations;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLCompare(GLCaleydoCanvas glCanvas, final String sLabel,
			final IViewFrustum viewFrustum) {
		super(glCanvas, sLabel, viewFrustum, true);

		viewType = VIEW_ID;
		setsToCompare = new ArrayList<ISet>();
		hashHeatMapWrappers = new HashMap<Integer, HeatMapWrapper>();
		glKeyListener = new GLCompareKeyListener(this);
		isControlPressed = false;

	}

	@Override
	public void init(GL gl) {
		// contentVA = useCase.getContentVA(ContentVAType.CONTENT);
		// storageVA = useCase.getStorageVA(StorageVAType.STORAGE);

		heatMapLayoutLeft = new HeatMapLayoutLeft();
		heatMapLayoutRight = new HeatMapLayoutRight();
		activeHeatMapSelectionType = new SelectionType("ActiveHeatmap",
				new float[] { 0.0f, 1.0f, 1.0f, 0.0f }, true, false, 0.9f);

		SelectionTypeEvent selectionTypeEvent = new SelectionTypeEvent(
				activeHeatMapSelectionType);
		eventPublisher.triggerEvent(selectionTypeEvent);

		leftHeatMapWrapper = new HeatMapWrapper(0, heatMapLayoutLeft, this,
				null, useCase, this, dataDomain, activeHeatMapSelectionType);
		hashHeatMapWrappers.put(0, leftHeatMapWrapper);
		rightHeatMapWrapper = new HeatMapWrapper(1, heatMapLayoutRight, this,
				null, useCase, this, dataDomain, activeHeatMapSelectionType);

		hashHeatMapWrappers.put(1, rightHeatMapWrapper);

		leftHeatMapWrapper.registerEventListeners();
		rightHeatMapWrapper.registerEventListeners();

		leftHeatMapWrapper.setSet(set);
		rightHeatMapWrapper.setSet(set);

		leftHeatMapWrapper.init(gl, this, glMouseListener, null, useCase, this,
				dataDomain);
		rightHeatMapWrapper.init(gl, this, glMouseListener, null, useCase,
				this, dataDomain);

		areNewSetsAvailable = false;
	}

	@Override
	public void initLocal(GL gl) {

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		// Register keyboard listener to GL canvas
		parentGLCanvas.getParentComposite().getDisplay().asyncExec(
				new Runnable() {
					public void run() {
						parentGLCanvas.getParentComposite().addKeyListener(
								glKeyListener);
					}
				});

		init(gl);
	}

	@Override
	public void initRemote(final GL gl, final AGLView glParentView,
			final GLMouseListener glMouseListener,
			GLInfoAreaManager infoAreaManager) {

		// Register keyboard listener to GL canvas
		glParentView.getParentGLCanvas().getParentComposite().getDisplay()
				.asyncExec(new Runnable() {
					public void run() {
						glParentView.getParentGLCanvas().getParentComposite()
								.addKeyListener(glKeyListener);
					}
				});

		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);

	}

	@Override
	public void initData() {
		super.initData();
	}

	@Override
	public void setDetailLevel(EDetailLevel detailLevel) {

	}

	@Override
	public void displayLocal(GL gl) {

		if (areNewSetsAvailable) {
			leftHeatMapWrapper.init(gl, this, glMouseListener, null, useCase,
					this, dataDomain);
			rightHeatMapWrapper.init(gl, this, glMouseListener, null, useCase,
					this, dataDomain);
			areNewSetsAvailable = false;
		}

		processEvents();
		leftHeatMapWrapper.processEvents();
		rightHeatMapWrapper.processEvents();
		if (!isVisible())
			return;
		pickingManager.handlePicking(this, gl);
		heatMapLayoutLeft.setLayoutParameters(0.0f, 0.0f, viewFrustum.getTop(),
				viewFrustum.getRight() / 3.0f);
		heatMapLayoutRight.setLayoutParameters(
				2.0f * viewFrustum.getRight() / 3.0f, 0.0f, viewFrustum
						.getTop(), viewFrustum.getRight() / 3.0f);

		leftHeatMapWrapper.calculateDrawingParameters();
		rightHeatMapWrapper.calculateDrawingParameters();

		if (bIsDisplayListDirtyLocal) {
			bIsDisplayListDirtyLocal = false;
			buildDisplayList(gl, iGLDisplayListIndexLocal);
			leftHeatMapWrapper.setDisplayListDirty();
			rightHeatMapWrapper.setDisplayListDirty();
		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		display(gl);
		checkForHits(gl);
	}

	@Override
	public void displayRemote(GL gl) {
		if (bIsDisplayListDirtyRemote) {
			bIsDisplayListDirtyRemote = false;
			buildDisplayList(gl, iGLDisplayListIndexRemote);
		}
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		display(gl);
		checkForHits(gl);
	}

	@Override
	public void display(GL gl) {
		// processEvents();
		if (leftHeatMapWrapper.handleDragging(gl, glMouseListener)
				|| rightHeatMapWrapper.handleDragging(gl, glMouseListener)) {
			setDisplayListDirty();
		}

		if (leftHeatMapWrapper.isNewSelection()) {
			rightHeatMapWrapper.selectGroupsFromContentVAList(gl,
					glMouseListener, leftHeatMapWrapper
							.getContentVAsOfHeatMaps());
			setDisplayListDirty();
		} else if (rightHeatMapWrapper.isNewSelection()) {
			leftHeatMapWrapper.selectGroupsFromContentVAList(gl,
					glMouseListener, rightHeatMapWrapper
							.getContentVAsOfHeatMaps());
			setDisplayListDirty();
		}

		gl.glCallList(iGLDisplayListToCall);

		leftHeatMapWrapper.drawRemoteItems(gl, glMouseListener, pickingManager);
		rightHeatMapWrapper
				.drawRemoteItems(gl, glMouseListener, pickingManager);
		// renderTree(gl);
		// renderOverviewRelations(gl);

		if (!isRenderedRemote())
			contextMenu.render(gl, this);
	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {
		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);
		leftHeatMapWrapper.drawLocalItems(gl, textureManager, pickingManager,
				glMouseListener, iUniqueID);
		rightHeatMapWrapper.drawLocalItems(gl, textureManager, pickingManager,
				glMouseListener, iUniqueID);

		renderTree(gl);

		renderOverviewToDetailRelations(gl);

		renderDetailRelations(gl);

		if (leftHeatMapWrapper.getContentVAsOfHeatMaps().size() == 0)
			renderOverviewRelations(gl);

		gl.glEndList();
	}

	@Override
	public String getDetailedInfo() {
		return new String("");
	}

	private void renderDetailRelations(GL gl) {

		if (setsToCompare == null || setsToCompare.size() == 0)
			return;

		float alpha = 0.6f;

		ContentSelectionManager contentSelectionManager = useCase
				.getContentSelectionManager();

		// Iterate over all detail content VAs on the left
		for (ContentVirtualArray contentVA : leftHeatMapWrapper
				.getContentVAsOfHeatMaps()) {

			for (Integer contentID : contentVA) {

				for (SelectionType type : contentSelectionManager
						.getSelectionTypes(contentID)) {

					float[] typeColor = type.getColor();
					typeColor[3] = alpha;
					gl.glColor4fv(typeColor, 0);

					if (type == SelectionType.MOUSE_OVER
							|| type == SelectionType.SELECTION
						|| type == activeHeatMapSelectionType) {
						gl.glLineWidth(3);
						break;
					} else {
						gl.glLineWidth(1);
					}
				}

				Vec2f leftPos = leftHeatMapWrapper
						.getRightDetailLinkPositionFromContentID(contentID);

				if (leftPos == null)
					continue;

				Vec2f rightPos = null;
				for (ContentVirtualArray rightVA : rightHeatMapWrapper
						.getContentVAsOfHeatMaps()) {

					rightPos = rightHeatMapWrapper
							.getLeftDetailLinkPositionFromContentID(contentID);

					if (rightPos != null)
						break;
				}

				if (rightPos == null)
					continue;

				gl.glPushName(pickingManager.getPickingID(iUniqueID,
						EPickingType.POLYLINE_SELECTION, contentID));
				ArrayList<Vec3f> points = new ArrayList<Vec3f>();
				points.add(new Vec3f(leftPos.x(), leftPos.y(), 0));
				points.add(new Vec3f(leftPos.x() + (rightPos.x() - leftPos.x())
						/ 2f, leftPos.y(), 0));
				points.add(new Vec3f(leftPos.x() + (rightPos.x() - leftPos.x())
						/ 2f, rightPos.y(), 0));
				points.add(new Vec3f(rightPos.x(), rightPos.y(), 0));

				NURBSCurve curve = new NURBSCurve(points, 30);
				points = curve.getCurvePoints();

				gl.glBegin(GL.GL_LINE_STRIP);
				for (int i = 0; i < points.size(); i++)
					gl.glVertex3f(points.get(i).x(), points.get(i).y(), 0);
				gl.glEnd();

				gl.glPopName();
			}
		}

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

			for (SelectionType type : contentSelectionManager
					.getSelectionTypes(contentID)) {

				float[] typeColor = type.getColor();
				typeColor[3] = alpha;
				gl.glColor4fv(typeColor, 0);

				if (type == SelectionType.MOUSE_OVER
						|| type == SelectionType.SELECTION
						|| type == activeHeatMapSelectionType)
				{
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

			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.POLYLINE_SELECTION, contentID));
			
			gl.glBegin(GL.GL_LINE_STRIP);
			for (int i = 0; i < points.size(); i++)
				gl.glVertex3f(points.get(i).x(), points.get(i).y(), 0);
			gl.glEnd();

			gl.glPopName();
		}
	}

	private void renderOverviewToDetailRelations(GL gl) {

		sortedClustersXOffsetUp = new ArrayList<Pair<Float, Integer>>();
		sortedClustersXOffsetDown = new ArrayList<Pair<Float, Integer>>();

		calculateClusterXOffset(leftHeatMapWrapper);

		for (ContentVirtualArray va : leftHeatMapWrapper
				.getContentVAsOfHeatMaps()) {

			xOffset = 0;
			// renderSplineCluster(gl, va, leftHeatMapWrapper);
			renderSplineRelation(gl, va, leftHeatMapWrapper);
		}

		calculateClusterXOffset(rightHeatMapWrapper);

		for (ContentVirtualArray va : rightHeatMapWrapper
				.getContentVAsOfHeatMaps()) {

			xOffset = 0;
			// renderSplineCluster(gl, va, rightHeatMapWrapper);
			renderSplineRelation(gl, va, rightHeatMapWrapper);
		}
	}

	private void calculateClusterXOffset(HeatMapWrapper heatMapWrapper) {

		sortedClustersXOffsetUp.clear();
		sortedClustersXOffsetDown.clear();

		for (ContentVirtualArray va : heatMapWrapper
				.getContentVAsOfHeatMaps()) {

			int contentID = va.get(0);

			// for (int contentID : va) {

			Vec2f leftPos;

			if (heatMapWrapper == leftHeatMapWrapper)
				leftPos = heatMapWrapper
						.getRightOverviewLinkPositionFromContentID(contentID);
			else
				leftPos = heatMapWrapper
						.getLeftOverviewLinkPositionFromContentID(contentID);

			if (leftPos == null)
				return;

			Vec2f rightPos;

			if (heatMapWrapper == leftHeatMapWrapper)
				rightPos = heatMapWrapper
						.getLeftDetailLinkPositionFromContentID(contentID);
			else
				rightPos = heatMapWrapper
						.getRightDetailLinkPositionFromContentID(contentID);

			if (rightPos == null)
				return;

			Pair<Float, Integer> xDiffToContentID = new Pair<Float, Integer>();
			float yDiff = rightPos.y() - leftPos.y();
			xDiffToContentID.set(yDiff, contentID);

			if (yDiff > 0)
				sortedClustersXOffsetUp.add(xDiffToContentID);
			else
				sortedClustersXOffsetDown.add(xDiffToContentID);

		}

		// }

		Collections.sort(sortedClustersXOffsetUp);
		Collections.sort(sortedClustersXOffsetDown);
		Collections.reverse(sortedClustersXOffsetDown);
	}

	private void renderSplineCluster(GL gl, ContentVirtualArray va,
			HeatMapWrapper heatMapWrapper) {

		gl.glColor3f(0.8f, 0.8f, 0.8f);

		int firstContentID = va.get(0);
		int lastContentID = va.get(va.size() - 1);

		Vec2f leftPos;

		if (heatMapWrapper == leftHeatMapWrapper)
			leftPos = heatMapWrapper
					.getRightOverviewLinkPositionFromContentID(firstContentID);
		else
			leftPos = heatMapWrapper
					.getLeftOverviewLinkPositionFromContentID(firstContentID);

		if (leftPos == null)
			return;

		Vec2f rightPos;

		if (heatMapWrapper == leftHeatMapWrapper)
			rightPos = heatMapWrapper
					.getLeftDetailLinkPositionFromContentID(firstContentID);
		else
			rightPos = heatMapWrapper
					.getRightDetailLinkPositionFromContentID(firstContentID);

		if (rightPos == null)
			return;

		ArrayList<Vec3f> inputPoints = new ArrayList<Vec3f>();
		inputPoints.add(new Vec3f(leftPos.x(), leftPos.y(), 0));
		inputPoints.add(new Vec3f(leftPos.x() + (rightPos.x() - leftPos.x())
				/ 2f, leftPos.y(), 0));
		inputPoints.add(new Vec3f(leftPos.x() + (rightPos.x() - leftPos.x())
				/ 2f, rightPos.y(), 0));
		inputPoints.add(new Vec3f(rightPos.x(), rightPos.y(), 0));

		NURBSCurve curve = new NURBSCurve(inputPoints, 30);
		ArrayList<Vec3f> outputPoints = curve.getCurvePoints();

		if (heatMapWrapper == leftHeatMapWrapper)
			leftPos = heatMapWrapper
					.getRightOverviewLinkPositionFromContentID(lastContentID);
		else
			leftPos = heatMapWrapper
					.getLeftOverviewLinkPositionFromContentID(lastContentID);

		if (leftPos == null)
			return;

		if (heatMapWrapper == leftHeatMapWrapper)
			rightPos = heatMapWrapper
					.getLeftDetailLinkPositionFromContentID(lastContentID);
		else
			rightPos = heatMapWrapper
					.getRightDetailLinkPositionFromContentID(lastContentID);

		if (rightPos == null)
			return;

		inputPoints = new ArrayList<Vec3f>();
		inputPoints.add(new Vec3f(leftPos.x(), leftPos.y(), 0));
		inputPoints.add(new Vec3f(leftPos.x() + (rightPos.x() - leftPos.x())
				/ 2f, leftPos.y(), 0));
		inputPoints.add(new Vec3f(leftPos.x() + (rightPos.x() - leftPos.x())
				/ 2f, rightPos.y(), 0));
		inputPoints.add(new Vec3f(rightPos.x(), rightPos.y(), 0));

		curve = new NURBSCurve(inputPoints, 30);
		ArrayList<Vec3f> points = curve.getCurvePoints();

		// Reverse point order
		for (int i = points.size() - 1; i >= 0; i--) {
			outputPoints.add(points.get(i));
		}

		gl.glBegin(GL.GL_POLYGON);
		for (int i = 0; i < outputPoints.size(); i++)
			gl.glVertex3f(outputPoints.get(i).x(), outputPoints.get(i).y(), 0);
		gl.glEnd();
	}

	private void renderSplineRelation(GL gl, ContentVirtualArray va,
			HeatMapWrapper heatMapWrapper) {

		float alpha = 0.6f;

		ContentSelectionManager contentSelectionManager = useCase
				.getContentSelectionManager();

		for (Integer contentID : va) {

			for (SelectionType type : contentSelectionManager
					.getSelectionTypes(contentID)) {

				float[] typeColor = type.getColor();
				typeColor[3] = alpha;
				gl.glColor4fv(typeColor, 0);

				if (type == SelectionType.MOUSE_OVER
						|| type == SelectionType.SELECTION
						|| type == activeHeatMapSelectionType) {
					gl.glLineWidth(3);
					break;
				} else {
					gl.glLineWidth(1);
					break;
				}
			}

			Vec2f leftPos;

			if (heatMapWrapper == leftHeatMapWrapper)
				leftPos = heatMapWrapper
						.getRightOverviewLinkPositionFromContentID(contentID);
			else
				leftPos = heatMapWrapper
						.getLeftOverviewLinkPositionFromContentID(contentID);

			if (leftPos == null)
				return;

			Vec2f rightPos;

			if (heatMapWrapper == leftHeatMapWrapper)
				rightPos = heatMapWrapper
						.getLeftDetailLinkPositionFromContentID(contentID);
			else
				rightPos = heatMapWrapper
						.getRightDetailLinkPositionFromContentID(contentID);

			if (rightPos == null)
				return;

			if (xOffset == 0) {
				for (Pair<Float, Integer> cluseterToXOffset : sortedClustersXOffsetUp) {
					if (contentID.equals(cluseterToXOffset.getSecond())) {
						xOffset = (rightPos.x() - leftPos.x())
								* -((float) sortedClustersXOffsetUp
										.indexOf(cluseterToXOffset) + 1)
								/ (sortedClustersXOffsetUp.size() + 1);
						break;
					}
				}

				for (Pair<Float, Integer> cluseterToXOffset : sortedClustersXOffsetDown) {
					if (contentID.equals(cluseterToXOffset.getSecond())) {
						xOffset = (rightPos.x() - leftPos.x())
								* -((float) sortedClustersXOffsetDown
										.indexOf(cluseterToXOffset) + 1)
								/ (sortedClustersXOffsetDown.size() + 1);
						break;
					}
				}
			}

			if (xOffset == 0)
				continue;

			ArrayList<Vec3f> points = new ArrayList<Vec3f>();
			points.add(new Vec3f(leftPos.x(), leftPos.y(), 0));
			points.add(new Vec3f(rightPos.x() + xOffset, leftPos.y(), 0));
			points.add(new Vec3f(rightPos.x() + xOffset/1.5f, rightPos.y(), 0));
			points.add(new Vec3f(rightPos.x(), rightPos.y(), 0));

			NURBSCurve curve = new NURBSCurve(points, 30);
			points = curve.getCurvePoints();

			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.POLYLINE_SELECTION, contentID));
			
			gl.glBegin(GL.GL_LINE_STRIP);
			for (int i = 0; i < points.size(); i++)
				gl.glVertex3f(points.get(i).x(), points.get(i).y(), 0.001f);
			gl.glEnd();
			
			gl.glPopName();
		}
	}

	private void renderTree(GL gl) {

		if (setsToCompare == null || setsToCompare.size() == 0)
			return;

		// Left hierarchy
		xPosInitLeft = heatMapLayoutLeft.getOverviewHeatmapWidth()
				+ heatMapLayoutLeft.getOverviewGroupWidth()
				+ heatMapLayoutLeft.getOverviewSliderWidth();
		yPosInitLeft = viewFrustum.getHeight();
		Tree<ClusterNode> tree = setsToCompare.get(0).getContentTree();
		ClusterNode rootNode = tree.getRoot();

		determineTreePositions(rootNode, tree);
		// renderDendrogram(gl, rootNode, 1, tree, xPosInitLeft);

		// Right hierarchy
		xPosInitRight = viewFrustum.getWidth()
				- heatMapLayoutLeft.getOverviewHeatmapWidth()
				- heatMapLayoutLeft.getOverviewGroupWidth()
				- heatMapLayoutLeft.getOverviewSliderWidth();
		yPosInitRight = viewFrustum.getHeight();
		tree = setsToCompare.get(1).getContentTree();
		rootNode = tree.getRoot();

		determineTreePositions(rootNode, tree);
		// renderDendrogram(gl, rootNode, 1, tree, xPosInitRight);
	}

	private void renderDendrogram(final GL gl, ClusterNode currentNode,
			float fOpacity, Tree<ClusterNode> tree, float xPosInit) {

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
				ymax = Math.max(ymax, current.getPos().y());
				ymin = Math.min(ymin, current.getPos().y());

				// if (bEnableDepthCheck && bRenderUntilCut == true) {
				// if (current.getPos().x() <= fPosCut) {
				// // if (current.getSelectionType() !=
				// // SelectionType.DESELECTED) {
				// renderDendrogramGenes(gl, current, 1);
				// // renderDendrogramGenes(gl, current, 0.3f);
				// // gl.glColor4f(fArMappingColor[0], fArMappingColor[1],
				// // fArMappingColor[2], fOpacity);
				// } else {
				// // renderDendrogramGenes(gl, current, 1);
				// bCutOffActive[i] = true;
				// }
				// } else

				renderDendrogram(gl, current, 1, tree, xPosInit);

			}

			fDiff = fTemp - xmin;

			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.DENDROGRAM_GENE_NODE_SELECTION, currentNode
							.getID()));

			// vertical line connecting all child nodes
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(xmin, ymin, currentNode.getPos().z());
			gl.glVertex3f(xmin, ymax, currentNode.getPos().z());
			gl.glEnd();

			// horizontal lines connecting all children with their parent
			for (int i = 0; i < iNrChildsNode; i++) {
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(xmin, tempPositions[i].y(), tempPositions[i].z());
				gl.glVertex3f(tempPositions[i].x(), tempPositions[i].y(),
						tempPositions[i].z());
				gl.glEnd();
			}

			gl.glPopName();

		} else {
			gl.glPushName(pickingManager.getPickingID(iUniqueID,
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

		float levelWidth = (viewFrustum.getWidth() / 2f
				- heatMapLayoutLeft.getOverviewSliderWidth()
				- heatMapLayoutLeft.getOverviewSliderWidth() - heatMapLayoutLeft
				.getOverviewHeatmapWidth())
				/ tree.getRoot().getDepth();

		float sampleHeight = viewFrustum.getHeight()
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

	@Override
	protected void handlePickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}

		SelectionType selectionType = null;

		switch (ePickingType) {
		case COMPARE_LEFT_EMBEDDED_VIEW_SELECTION:
			leftHeatMapWrapper.setHeatMapActive(iExternalID);
			rightHeatMapWrapper.setHeatMapsInactive();

			break;
		case COMPARE_RIGHT_EMBEDDED_VIEW_SELECTION:
			rightHeatMapWrapper.setHeatMapActive(iExternalID);
			leftHeatMapWrapper.setHeatMapsInactive();
			break;
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

				ContentContextMenuItemContainer contentContextMenuItemContainer = new ContentContextMenuItemContainer();
				contentContextMenuItemContainer.setID(EIDType.EXPRESSION_INDEX,
						iExternalID);
				contextMenu.addItemContanier(contentContextMenuItemContainer);
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
			event.setInfo(getShortInfoLocal());
			eventPublisher.triggerEvent(event);

			setDisplayListDirty();
			break;

		case COMPARE_OVERVIEW_SLIDER_ARROW_DOWN_SELECTION:
		case COMPARE_OVERVIEW_SLIDER_ARROW_UP_SELECTION:
		case COMPARE_OVERVIEW_SLIDER_BODY_SELECTION:
			HeatMapWrapper heatMapWrapper = hashHeatMapWrappers
					.get(iExternalID);
			if (heatMapWrapper != null) {
				heatMapWrapper.handleOverviewSliderSelection(ePickingType,
						pickingMode);
			}
			break;

		case COMPARE_LEFT_GROUP_SELECTION:
			switch (pickingMode) {
			case CLICKED:
				selectionType = SelectionType.SELECTION;
				break;
			case MOUSE_OVER:
				selectionType = SelectionType.MOUSE_OVER;
				break;
			}

			leftHeatMapWrapper.handleGroupSelection(selectionType, iExternalID,
					isControlPressed);
			rightHeatMapWrapper.setHeatMapsInactive();
			break;

		case COMPARE_RIGHT_GROUP_SELECTION:
			switch (pickingMode) {
			case CLICKED:
				selectionType = SelectionType.SELECTION;
				break;
			case MOUSE_OVER:
				selectionType = SelectionType.MOUSE_OVER;
				break;
			}

			rightHeatMapWrapper.handleGroupSelection(selectionType,
					iExternalID, isControlPressed);
			leftHeatMapWrapper.setHeatMapsInactive();
			break;
		}
	}

	@Override
	public void broadcastElements(EVAOperation type) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumberOfSelections(SelectionType selectionType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getShortInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearAllSelections() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRedrawView() {
		setDisplayListDirty();
	}

	@Override
	public void handleUpdateView() {
		setDisplayListDirty();
	}

	@Override
	public void handleClearSelections() {
		// nothing to do because histogram has no selections
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedCompareView serializedForm = new SerializedCompareView(
				dataDomain);
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		compareGroupsEventListener = new CompareGroupsEventListener();
		compareGroupsEventListener.setHandler(this);
		eventPublisher.addListener(CompareGroupsEvent.class,
				compareGroupsEventListener);

		if (leftHeatMapWrapper != null)
			leftHeatMapWrapper.registerEventListeners();
		if (rightHeatMapWrapper != null)
			rightHeatMapWrapper.registerEventListeners();
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		if (compareGroupsEventListener != null) {
			eventPublisher.removeListener(compareGroupsEventListener);
			compareGroupsEventListener = null;
		}
		// if (leftHeatMapWrapper != null)
		// leftHeatMapWrapper.unregisterEventListeners();
		// if (rightHeatMapWrapper != null)
		// rightHeatMapWrapper.unregisterEventListeners();
	}

	@Override
	public List<AGLView> getRemoteRenderedViews() {
		// TODO: Do it differently?
		return new ArrayList<AGLView>();
	}

	public void setGroupsToCompare(ArrayList<ISet> sets) {
		setsToCompare.clear();
		setsToCompare.addAll(sets);

		ClusterState clusterState = new ClusterState();
		clusterState.setClustererAlgo(EClustererAlgo.AFFINITY_PROPAGATION);
		clusterState.setClustererType(EClustererType.GENE_CLUSTERING);
		clusterState.setAffinityPropClusterFactorGenes(2);
		clusterState.setDistanceMeasure(EDistanceMeasure.EUCLIDEAN_DISTANCE);

		// clusterState.setClustererAlgo(EClustererAlgo.TREE_CLUSTERER);
		// clusterState.setClustererType(EClustererType.GENE_CLUSTERING);
		// clusterState.setDistanceMeasure(EDistanceMeasure.EUCLIDEAN_DISTANCE);

		for (ISet set : sets) {
			set.cluster(clusterState);
		}

		if (sets.size() >= 2) {
			ISet setLeft = setsToCompare.get(0);
			ISet setRight = setsToCompare.get(1);
			// relations = SetComparer.compareSets(setLeft, setRight);
			//
			leftHeatMapWrapper.setSet(setLeft);
			rightHeatMapWrapper.setSet(setRight);
			areNewSetsAvailable = true;
			setDisplayListDirty();
		}

	}

	public boolean isControlPressed() {
		return isControlPressed;
	}

	public void setControlPressed(boolean isControlPressed) {
		this.isControlPressed = isControlPressed;
	}
}
