package org.caleydo.view.compare.state;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Collections;

import javax.media.opengl.GL;

import static org.caleydo.view.heatmap.dendrogram.DendrogramRenderStyle.DENDROGRAM_Z;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.SetComparer;
import org.caleydo.core.data.collection.set.SetRelations;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.Group;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.SelectionTypeEvent;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.collection.Pair;
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
import org.caleydo.view.compare.renderer.CompareConnectionBandRenderer;
import org.caleydo.view.compare.renderer.ICompareConnectionRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;

import com.sun.opengl.util.j2d.TextRenderer;

public class DetailViewState extends ACompareViewState {

	private final static float SET_BAR_HEIGHT_PORTION = 0.05f;

	private SelectionType activeHeatMapSelectionType;

	float yPosInitLeft = 0;
	float xPosInitLeft = 0;
	float yPosInitRight = 0;
	float xPosInitRight = 0;

	private float xOffset = 0;
	private SetRelations relations;
	private ICompareConnectionRenderer compareConnectionRenderer;

	private ArrayList<Pair<Float, Integer>> sortedClustersXOffsetUp;
	private ArrayList<Pair<Float, Integer>> sortedClustersXOffsetDown;

	public DetailViewState(GLCompare view, int viewID,
			TextRenderer textRenderer, TextureManager textureManager,
			PickingManager pickingManager, GLMouseListener glMouseListener,
			SetBar setBar, RenderCommandFactory renderCommandFactory,
			EDataDomain dataDomain, IUseCase useCase,
			DragAndDropController dragAndDropController) {

		super(view, viewID, textRenderer, textureManager, pickingManager,
				glMouseListener, setBar, renderCommandFactory, dataDomain,
				useCase, dragAndDropController);
		this.setBar.setPosition(new Vec3f(0.0f, 0.0f, 0.0f));
		compareConnectionRenderer = new CompareConnectionBandRenderer();
		numSetsInFocus = 2;
	}

	@Override
	public void init(GL gl) {

		// gl.glEnable(GL.GL_BLEND);
		// gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		// gl.glEnable(GL.GL_LINE_SMOOTH);

		layouts.clear();
		layouts.add(new HeatMapLayoutLeft(renderCommandFactory));
		layouts.add(new HeatMapLayoutRight(renderCommandFactory));
		activeHeatMapSelectionType = new SelectionType("ActiveHeatmap",
				new float[] { 0.0f, 1.0f, 1.0f, 0.0f }, true, false, 0.9f);

		SelectionTypeEvent selectionTypeEvent = new SelectionTypeEvent(
				activeHeatMapSelectionType);
		eventPublisher.triggerEvent(selectionTypeEvent);

		int heatMapWrapperID = 0;
		for (AHeatMapLayout layout : layouts) {
			HeatMapWrapper heatMapWrapper = new HeatMapWrapper(
					heatMapWrapperID, layout, view, null, useCase, view,
					dataDomain, activeHeatMapSelectionType);
			heatMapWrappers.add(heatMapWrapper);
			// heatMapWrapper.registerEventListeners();
			heatMapWrapper.init(gl, glMouseListener, null, dataDomain);
			heatMapWrapperID++;
		}

		compareConnectionRenderer.init(gl);

		// leftHeatMapWrapper.setSet(set);
		// rightHeatMapWrapper.setSet(set);

		setsChanged = false;
	}

	@Override
	public void drawActiveElements(GL gl) {

		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			if (heatMapWrapper.handleDragging(gl, glMouseListener)) {
				view.setDisplayListDirty();
			}
		}

		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			if (heatMapWrapper.isNewSelection()) {
				for (HeatMapWrapper wrapper : heatMapWrappers) {
					if (wrapper != heatMapWrapper) {
						wrapper.selectGroupsFromContentVAList(relations
								.getMapping(heatMapWrapper.getSet()),
								heatMapWrapper.getContentVAsOfHeatMaps());
					}
				}
				view.setDisplayListDirty();
				break;
			}
		}

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

		renderOverviewToDetailRelations(gl);

		renderDetailRelations(gl);

		if (heatMapWrappers.get(0).getContentVAsOfHeatMaps().size() == 0)
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

	private void renderOverviewToDetailRelations(GL gl) {

		sortedClustersXOffsetUp = new ArrayList<Pair<Float, Integer>>();
		sortedClustersXOffsetDown = new ArrayList<Pair<Float, Integer>>();

		HeatMapWrapper leftHeatMapWrapper = heatMapWrappers.get(0);
		HeatMapWrapper rightHeatMapWrapper = heatMapWrappers.get(1);

		calculateClusterXOffset(leftHeatMapWrapper);

		for (ContentVirtualArray va : leftHeatMapWrapper
				.getContentVAsOfHeatMaps()) {

			xOffset = 0;
			renderSplineCluster(gl, va, leftHeatMapWrapper);
			renderSplineRelation(gl, va, leftHeatMapWrapper);
		}

		calculateClusterXOffset(rightHeatMapWrapper);

		for (ContentVirtualArray va : rightHeatMapWrapper
				.getContentVAsOfHeatMaps()) {

			xOffset = 0;
			renderSplineCluster(gl, va, rightHeatMapWrapper);
			renderSplineRelation(gl, va, rightHeatMapWrapper);
		}
	}

	private void renderSplineCluster(GL gl, ContentVirtualArray va,
			HeatMapWrapper heatMapWrapper) {

		gl.glColor3f(0.8f, 0.8f, 0.8f);

		Integer firstDetailContentID = va.get(0);
		Integer lastDetailContentID = va.get(va.size() - 1);

		Vec2f leftPos;

		Group group = heatMapWrapper.getGroupFromContentIndex(heatMapWrapper
				.getContentVA().indexOf(firstDetailContentID));
		int overviewFirstContentIndex = group.getStartIndex();
		int overviewLastContentIndex = group.getEndIndex();

		if (heatMapWrapper == heatMapWrappers.get(0))

			leftPos = heatMapWrapper
					.getRightOverviewLinkPositionFromContentIndex(overviewFirstContentIndex);
		else
			leftPos = heatMapWrapper
					.getLeftOverviewLinkPositionFromIndex(overviewFirstContentIndex);

		if (leftPos == null)
			return;

		Vec2f rightPos;

		if (heatMapWrapper == heatMapWrappers.get(0))
			rightPos = heatMapWrapper
					.getLeftDetailLinkPositionFromContentID(firstDetailContentID);
		else
			rightPos = heatMapWrapper
					.getRightDetailLinkPositionFromContentID(firstDetailContentID);

		if (rightPos == null)
			return;

		if (xOffset == 0) {
			for (Pair<Float, Integer> cluseterToXOffset : sortedClustersXOffsetUp) {
				if (firstDetailContentID.equals(cluseterToXOffset.getSecond())) {
					xOffset = (rightPos.x() - leftPos.x())
							* -((float) sortedClustersXOffsetUp
									.indexOf(cluseterToXOffset) + 1)
							/ (sortedClustersXOffsetUp.size() + 1);
					break;
				}
			}

			for (Pair<Float, Integer> cluseterToXOffset : sortedClustersXOffsetDown) {
				if (firstDetailContentID.equals(cluseterToXOffset.getSecond())) {
					xOffset = (rightPos.x() - leftPos.x())
							* -((float) sortedClustersXOffsetDown
									.indexOf(cluseterToXOffset) + 1)
							/ (sortedClustersXOffsetDown.size() + 1);
					break;
				}
			}
		}

		if (xOffset == 0)
			return;

		ArrayList<Vec3f> inputPoints = new ArrayList<Vec3f>();
		inputPoints.add(new Vec3f(leftPos.x(), leftPos.y(), 0));
		inputPoints.add(new Vec3f(rightPos.x() + xOffset, leftPos.y(), 0));
		inputPoints.add(new Vec3f(rightPos.x() + xOffset / 1.5f, rightPos.y(),
				0));
		inputPoints.add(new Vec3f(rightPos.x(), rightPos.y(), 0));

		// ArrayList<Vec3f> inputPoints = new ArrayList<Vec3f>();
		// inputPoints.add(new Vec3f(leftPos.x(), leftPos.y(), 0));
		// inputPoints.add(new Vec3f(leftPos.x() + (rightPos.x() - leftPos.x())
		// / 2f, leftPos.y(), 0));
		// inputPoints.add(new Vec3f(leftPos.x() + (rightPos.x() - leftPos.x())
		// / 2f, rightPos.y(), 0));
		// inputPoints.add(new Vec3f(rightPos.x(), rightPos.y(), 0));

		NURBSCurve curve = new NURBSCurve(inputPoints, 30);
		ArrayList<Vec3f> outputPoints = curve.getCurvePoints();

		if (heatMapWrapper == heatMapWrappers.get(0))
			leftPos = heatMapWrapper
					.getRightOverviewLinkPositionFromContentIndex(overviewLastContentIndex);
		else
			leftPos = heatMapWrapper
					.getLeftOverviewLinkPositionFromIndex(overviewLastContentIndex);

		if (leftPos == null)
			return;

		if (heatMapWrapper == heatMapWrappers.get(0))
			rightPos = heatMapWrapper
					.getLeftDetailLinkPositionFromContentID(lastDetailContentID);
		else
			rightPos = heatMapWrapper
					.getRightDetailLinkPositionFromContentID(lastDetailContentID);

		if (rightPos == null)
			return;

		if (xOffset == 0) {
			for (Pair<Float, Integer> cluseterToXOffset : sortedClustersXOffsetUp) {
				if (lastDetailContentID.equals(cluseterToXOffset.getSecond())) {
					xOffset = (rightPos.x() - leftPos.x())
							* -((float) sortedClustersXOffsetUp
									.indexOf(cluseterToXOffset) + 1)
							/ (sortedClustersXOffsetUp.size() + 1);
					break;
				}
			}

			for (Pair<Float, Integer> cluseterToXOffset : sortedClustersXOffsetDown) {
				if (lastDetailContentID.equals(cluseterToXOffset.getSecond())) {
					xOffset = (rightPos.x() - leftPos.x())
							* -((float) sortedClustersXOffsetDown
									.indexOf(cluseterToXOffset) + 1)
							/ (sortedClustersXOffsetDown.size() + 1);
					break;
				}
			}
		}

		if (xOffset == 0)
			return;

		inputPoints = new ArrayList<Vec3f>();
		inputPoints.add(new Vec3f(leftPos.x(), leftPos.y(), 0));
		inputPoints.add(new Vec3f(rightPos.x() + xOffset, leftPos.y(), 0));
		inputPoints.add(new Vec3f(rightPos.x() + xOffset / 1.5f, rightPos.y(),
				0));
		inputPoints.add(new Vec3f(rightPos.x(), rightPos.y(), 0));

		// inputPoints = new ArrayList<Vec3f>();
		// inputPoints.add(new Vec3f(leftPos.x(), leftPos.y(), 0));
		// inputPoints.add(new Vec3f(leftPos.x() + (rightPos.x() - leftPos.x())
		// / 2f, leftPos.y(), 0));
		// inputPoints.add(new Vec3f(leftPos.x() + (rightPos.x() - leftPos.x())
		// / 2f, rightPos.y(), 0));
		// inputPoints.add(new Vec3f(rightPos.x(), rightPos.y(), 0));

		curve = new NURBSCurve(inputPoints, 30);
		ArrayList<Vec3f> points = curve.getCurvePoints();

		// Reverse point order
		for (int i = points.size() - 1; i >= 0; i--) {
			outputPoints.add(points.get(i));
		}

		gl.glColor3f(1, 0, 0);
		compareConnectionRenderer.render(gl, outputPoints);

		// gl.glBegin(GL.GL_POLYGON);
		// for (int i = 0; i < outputPoints.size(); i++)
		// gl.glVertex3f(outputPoints.get(i).x(), outputPoints.get(i).y(), 0);
		// gl.glEnd();
	}

	private void calculateClusterXOffset(HeatMapWrapper heatMapWrapper) {

		sortedClustersXOffsetUp.clear();
		sortedClustersXOffsetDown.clear();

		for (ContentVirtualArray va : heatMapWrapper.getContentVAsOfHeatMaps()) {

			int contentID = va.get(0);

			// for (int contentID : va) {

			Vec2f leftPos;

			if (heatMapWrapper == heatMapWrappers.get(0))
				leftPos = heatMapWrapper
						.getRightOverviewLinkPositionFromContentID(contentID);
			else
				leftPos = heatMapWrapper
						.getLeftOverviewLinkPositionFromContentID(contentID);

			if (leftPos == null)
				return;

			Vec2f rightPos;

			if (heatMapWrapper == heatMapWrappers.get(0))
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

	private void renderSplineRelation(GL gl, ContentVirtualArray va,
			HeatMapWrapper heatMapWrapper) {

		float alpha = 0.2f;

		ContentSelectionManager contentSelectionManager = useCase
				.getContentSelectionManager();

		// Check if at least one element in the group is mouse over or selected
		boolean isActive = false;
		for (Integer contentID : contentSelectionManager
				.getElements(SelectionType.MOUSE_OVER)) {
			if (va.containsElement(contentID) > 0)
				isActive = true;
		}
		for (Integer contentID : contentSelectionManager
				.getElements(SelectionType.SELECTION)) {
			if (va.containsElement(contentID) > 0)
				isActive = true;
		}

		// if (!isActive)
		// return;

		// HashMap<Group, GroupInfo> selectedGroups =
		// heatMapWrapper.getSelectedGroups();

		for (Integer contentID : va) {

			// if (selectedGroups.containsKey(heatMapWrapper
			// .getGroupFromContentIndex(heatMapWrapper.getContentVA()
			// .indexOf(contentID))))
			// isActive = true;
			// else
			// System.out.println("do not render");

			if (!isActive)
				return;

			float positionZ = 0.0f;

			for (SelectionType type : contentSelectionManager
					.getSelectionTypes(contentID)) {

				float[] typeColor = type.getColor();
				positionZ = type.getPriority();

				if (type == SelectionType.MOUSE_OVER
						|| type == SelectionType.SELECTION
						|| type == activeHeatMapSelectionType) {
					gl.glLineWidth(3);
					alpha = 1;
					typeColor[3] = alpha;
					gl.glColor4fv(typeColor, 0);
					break;

				} else {
					gl.glLineWidth(1);

					if (isConnectionCrossing(contentID, heatMapWrapper
							.getContentVA(), va, heatMapWrapper))
						alpha = 0.6f;
					else
						alpha = 0.2f;

					typeColor[3] = alpha;
					gl.glColor4fv(typeColor, 0);
					break;
				}
			}

			Vec2f leftPos;

			if (heatMapWrapper == heatMapWrappers.get(0))
				leftPos = heatMapWrapper
						.getRightOverviewLinkPositionFromContentID(contentID);
			else
				leftPos = heatMapWrapper
						.getLeftOverviewLinkPositionFromContentID(contentID);

			if (leftPos == null)
				return;

			Vec2f rightPos;

			if (heatMapWrapper == heatMapWrappers.get(0))
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
			points
					.add(new Vec3f(rightPos.x() + xOffset / 1.5f, rightPos.y(),
							0));
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

	// TODO: Refine crossing detection algorithm
	public boolean isConnectionCrossing(int contentID,
			ContentVirtualArray overviewVA, ContentVirtualArray detailVA,
			HeatMapWrapper heatMapWrapper) {

		int detailContentIndex = detailVA.indexOf(contentID);
		int overviewContentIndex = overviewVA.indexOf(contentID);
		Group group = heatMapWrapper.getGroupFromContentIndex(overviewVA
				.indexOf(contentID));
		overviewContentIndex = overviewContentIndex - group.getStartIndex();

		return (Math.abs(overviewContentIndex - detailContentIndex)) < 10 ? false
				: true;
	}

	private void renderDetailRelations(GL gl) {

		if (setsToCompare == null || setsToCompare.size() == 0)
			return;

		float alpha = 0.6f;

		HeatMapWrapper leftHeatMapWrapper = heatMapWrappers.get(0);
		HeatMapWrapper rightHeatMapWrapper = heatMapWrappers.get(1);

		ContentSelectionManager contentSelectionManager = useCase
				.getContentSelectionManager();

		// Iterate over all detail content VAs on the left
		for (ContentVirtualArray contentVA : leftHeatMapWrapper
				.getContentVAsOfHeatMaps()) {

			for (Integer contentID : contentVA) {

				float positionZ = 0.0f;
				for (SelectionType type : contentSelectionManager
						.getSelectionTypes(contentID)) {

					float[] typeColor = type.getColor();
					typeColor[3] = alpha;
					gl.glColor4fv(typeColor, 0);
					positionZ = type.getPriority();

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

				gl.glPushName(pickingManager.getPickingID(viewID,
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
					gl.glVertex3f(points.get(i).x(), points.get(i).y(),
							positionZ);
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

			float positionZ = 0.0f;

			for (SelectionType type : contentSelectionManager
					.getSelectionTypes(contentID)) {

				float[] typeColor = type.getColor();
				typeColor[3] = alpha;
				gl.glColor4fv(typeColor, 0);
				positionZ = type.getPriority();

				if (type == SelectionType.MOUSE_OVER
						|| type == SelectionType.SELECTION
						|| type == activeHeatMapSelectionType) {
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
		case COMPARE_LEFT_EMBEDDED_VIEW_SELECTION:
			rightHeatMapWrapper.setHeatMapsInactive();
			leftHeatMapWrapper.setHeatMapActive(iExternalID);
			break;

		case COMPARE_RIGHT_EMBEDDED_VIEW_SELECTION:
			leftHeatMapWrapper.setHeatMapsInactive();
			rightHeatMapWrapper.setHeatMapActive(iExternalID);
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

		case COMPARE_OVERVIEW_SLIDER_ARROW_DOWN_SELECTION:
		case COMPARE_OVERVIEW_SLIDER_ARROW_UP_SELECTION:
		case COMPARE_OVERVIEW_SLIDER_BODY_SELECTION:
			HeatMapWrapper heatMapWrapper = heatMapWrappers.get(iExternalID);
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

		case COMPARE_SET_BAR_ITEM_SELECTION:
			setBar.handleSetBarItemSelection(iExternalID, pickingMode, pick);
			break;
		}

	}

	@Override
	public void setSetsToCompare(ArrayList<ISet> setsToCompare) {
		this.setsToCompare = setsToCompare;

		if (setsToCompare.size() >= 2) {

			ISet setLeft = setsToCompare.get(0);
			ISet setRight = setsToCompare.get(1);
			relations = SetComparer.compareSets(setLeft, setRight);

			heatMapWrappers.get(0).setSet(setLeft);
			heatMapWrappers.get(0).setRelations(relations);
			heatMapWrappers.get(1).setSet(setRight);
			heatMapWrappers.get(1).setRelations(relations);
			setsChanged = true;
			setBar.setSets(setsToCompare);

			view.setDisplayListDirty();
		}

		setBar.setSets(setsToCompare);
	}

	@Override
	public ECompareViewStateType getStateType() {
		return ECompareViewStateType.DETAIL_VIEW;
	}

	@Override
	public void duplicateSetBarItem(int itemID) {
		setBar.handleDuplicateSetBarItem(itemID);

	}

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {

		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			heatMapWrapper.handleSelectionUpdate(selectionDelta,
					scrollToSelection, info);
		}

	}

	@Override
	public void setSetsInFocus(ArrayList<ISet> setsInFocus) {

		setsToCompare = setsInFocus;

		if (setsInFocus.size() >= getMinSetsInFocus()
				&& setsInFocus.size() <= getMaxSetsInFocus()) {

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

		GeneralManager.get().getGUIBridge().getDisplay().asyncExec(
				new Runnable() {

					@Override
					public void run() {

						Shell shell = new Shell(GeneralManager.get()
								.getGUIBridge().getDisplay());
						shell.setLayout(new FillLayout());
						shell.setSize(200, 50);

						final Slider slider = new Slider(shell, SWT.HORIZONTAL);
						slider.setMinimum(0);
						slider.setMaximum(100);
						slider.setIncrement(5);
						slider.setPageIncrement(20);
						slider.setSelection(75);

						slider.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								performPValueAdjustment((float) slider
										.getSelection() / 100);
							}
						});
						shell.open();
					}
				});
	}

	private void performPValueAdjustment(float pValue) {

		ContentVirtualArray pValueFilteredVA = heatMapWrappers.get(0).getSet()
				.getStatisticsResult().getVABasedOnCompareResult(
						heatMapWrappers.get(1).getSet(), pValue);
		// ContentVirtualArray leftHeatMapContentVA =
		// heatMapWrappers.get(0).getContentVA();
		for (Integer contentID : heatMapWrappers.get(0).getContentVA()) {

			if (pValueFilteredVA.containsElement(contentID) == 0)
				heatMapWrappers.get(0).getContentSelectionManager().addToType(
						SelectionType.DESELECTED, contentID);
			else
				heatMapWrappers.get(0).getContentSelectionManager()
						.removeFromType(SelectionType.DESELECTED, contentID);
		}

		ISelectionDelta selectionDelta = heatMapWrappers.get(0)
				.getContentSelectionManager().getDelta();
		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setSender(this);
		event.setSelectionDelta((SelectionDelta) selectionDelta);
		eventPublisher.triggerEvent(event);
	}

	@Override
	public int getMaxSetsInFocus() {
		return 3;
	}

	@Override
	public int getMinSetsInFocus() {
		return 2;
	}
}
