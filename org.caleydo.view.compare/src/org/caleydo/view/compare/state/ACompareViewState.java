package org.caleydo.view.compare.state;

import gleem.linalg.Vec3f;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.ContentGroupList;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.Group;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.SelectionTypeEvent;
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
import org.caleydo.view.compare.renderer.CompareConnectionBandRenderer;
import org.caleydo.view.compare.renderer.ICompareConnectionRenderer;

import com.sun.opengl.util.j2d.TextRenderer;

public abstract class ACompareViewState {

	protected final static int NUMBER_OF_SPLINE_POINTS = 30;
	protected final static int NUMBER_OF_SPLINE_POINTS_SHORT = 10;

	protected final static float SET_BAR_HEIGHT_PORTION = 0.07f;

	protected static SelectionType activeHeatMapSelectionType = new SelectionType(
			"ActiveHeatmap", new float[] { 0.0f, 0.0f, 0.0f, 1.0f }, true, false, 1f);;

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

	float yPosInitLeft = 0;
	float xPosInitLeft = 0;
	float yPosInitRight = 0;
	float xPosInitRight = 0;

	Tree<ClusterNode> leftTree;
	Tree<ClusterNode> rightTree;

	boolean renderPseudoHierarchy = false;

	boolean bandBundlingActive = false;

	protected ICompareConnectionRenderer compareConnectionRenderer;

	// HashMap<Integer, Vec3f> contentIDToLeftOverviewPoints;
	HashMap<Integer, float[]> contentIDToLeftDetailPoints;
	// HashMap<Integer, Vec3f> contentIDToRightOverviewPoints;
	HashMap<Integer, float[]> contentIDToRightDetailPoints;

	float firstLevelOffset = 0;

	ArrayList<DetailBand> detailBands;

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

		setsChanged = false;

		eventPublisher = GeneralManager.get().getEventPublisher();

		compareConnectionRenderer = new CompareConnectionBandRenderer();

		contentIDToLeftDetailPoints = new HashMap<Integer, float[]>();
		contentIDToRightDetailPoints = new HashMap<Integer, float[]>();

		// bandBundlingActive = true;

		SelectionTypeEvent selectionTypeEvent = new SelectionTypeEvent(
				activeHeatMapSelectionType);
		eventPublisher.triggerEvent(selectionTypeEvent);
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

	protected void renderSingleDetailBand(GL gl, DetailBand detailBand, boolean highlight) {

		ArrayList<Integer> contentIDs = detailBand.getContentIDs();

		int startContentID = contentIDs.get(0);
		int endContentID = contentIDs.get(contentIDs.size() - 1);

		float[] leftTopPos = contentIDToLeftDetailPoints.get(startContentID);
		float[] rightTopPos = contentIDToRightDetailPoints.get(startContentID);

		if (leftTopPos == null || rightTopPos == null)
			return;

		float[] leftBottomPos = contentIDToLeftDetailPoints.get(endContentID);
		float[] rightBottomPos = contentIDToRightDetailPoints.get(endContentID);

		if (leftBottomPos == null || rightBottomPos == null)
			return;

		float bundlingOffsetX = 0.1f;
		rightTopPos[0] = rightTopPos[0] + bundlingOffsetX;
		rightBottomPos[0] = rightBottomPos[0] + bundlingOffsetX;
		leftTopPos[0] = leftTopPos[0] - bundlingOffsetX;
		leftBottomPos[0] = leftBottomPos[0] - bundlingOffsetX;

		float offsetY = 0.0025f;
		leftTopPos[1] = leftTopPos[1] + offsetY;
		rightTopPos[1] = rightTopPos[1] + offsetY;
		leftBottomPos[1] = leftBottomPos[1] - offsetY;
		rightBottomPos[1] = rightBottomPos[1] - offsetY;

		// TODO integrate offset for band in Y

		renderSingleBand(gl, leftTopPos, leftBottomPos, rightTopPos, rightBottomPos,
				highlight);
	}

	protected void renderSingleBand(GL gl, float[] leftTopPos, float[] leftBottomPos,
			float[] rightTopPos, float[] rightBottomPos, boolean highlight) {

		if (leftTopPos == null || leftBottomPos == null || rightTopPos == null
				|| rightBottomPos == null)
			return;

		float xOffset = -(rightTopPos[0] - leftTopPos[0]) / 1.5f;

		// if (heatMapWrapper == heatMapWrappers.get(0))
		// xOffset = -Math.abs((rightPos.x() - leftPos.x()) / 1.3f);
		// else
		// xOffset = Math.abs((rightPos.x() - leftPos.x()) / 1.3f);

		ArrayList<Vec3f> inputPoints = new ArrayList<Vec3f>();
		inputPoints.add(new Vec3f(leftTopPos[0], leftTopPos[1], leftTopPos[2]));
		inputPoints.add(new Vec3f(rightTopPos[0] + xOffset, leftTopPos[1], 0));
		inputPoints.add(new Vec3f(rightTopPos[0] + xOffset / 3f, rightTopPos[1], 0));
		inputPoints.add(new Vec3f(rightTopPos[0], rightTopPos[1], rightTopPos[2]));

		NURBSCurve curve = new NURBSCurve(inputPoints, NUMBER_OF_SPLINE_POINTS);
		ArrayList<Vec3f> outputPoints = curve.getCurvePoints();

		// Band border
		gl.glLineWidth(1);
		gl.glColor4f(0, 0, 0, 0.6f);
		gl.glBegin(GL.GL_LINE_STRIP);
		for (int i = 0; i < outputPoints.size(); i++) {
			gl.glVertex3f(outputPoints.get(i).x(), outputPoints.get(i).y(), 0f);
		}
		gl.glEnd();

		inputPoints = new ArrayList<Vec3f>();
		inputPoints.add(new Vec3f(leftBottomPos[0], leftBottomPos[1], leftBottomPos[2]));
		inputPoints.add(new Vec3f(rightBottomPos[0] + xOffset, leftBottomPos[1], 0));
		inputPoints
				.add(new Vec3f(rightBottomPos[0] + xOffset / 3f, rightBottomPos[1], 0));
		inputPoints
				.add(new Vec3f(rightBottomPos[0], rightBottomPos[1], rightBottomPos[2]));

		curve = new NURBSCurve(inputPoints, NUMBER_OF_SPLINE_POINTS);
		ArrayList<Vec3f> points = curve.getCurvePoints();

		// Reverse point order
		for (int i = points.size() - 1; i >= 0; i--) {
			outputPoints.add(points.get(i));
		}

		// Band border
		gl.glLineWidth(1);
		gl.glColor4f(0, 0, 0, 0.6f);
		gl.glBegin(GL.GL_LINE_STRIP);
		for (int i = 0; i < points.size(); i++) {
			gl.glVertex3f(points.get(i).x(), points.get(i).y(), 0f);
		}
		gl.glEnd();

		// if (highlight) {
		// gl.glColor4f(0.3f, 0.3f, 0.3f, 0.95f);
		//
		// for (Vec3f point : outputPoints) {
		// point.setZ(0.2f);
		// }
		// }
		// // } else if (activeGroup)
		// // gl.glColor4f(0f, 0f, 0f, 0.4f);
		// else
		// gl.glColor4f(0f, 0f, 0f, 0.4f);

		if (!highlight)
			gl.glColor4f(0f, 0f, 0f, 0.4f);
		else
			gl.glColor4f(0f, 0f, 0f, 0.7f);

		compareConnectionRenderer.render(gl, outputPoints);
	}

	protected void calculateDetailBands(HeatMapWrapper leftHeatMapWrapper,
			HeatMapWrapper rightHeatMapWrapper, boolean considerSelectedGroups) {

		detailBands = new ArrayList<DetailBand>();
		ArrayList<Integer> bandContentIDs = null;
		DetailBand detailBand = null;

		// Iterate over all detail content VAs on the left
		for (ContentVirtualArray leftContentVA : leftHeatMapWrapper
				.getContentVAsOfHeatMaps(considerSelectedGroups)) {

			for (ContentVirtualArray rightContentVA : rightHeatMapWrapper
					.getContentVAsOfHeatMaps(considerSelectedGroups)) {

				bandContentIDs = new ArrayList<Integer>();
				detailBand = new DetailBand();
				detailBand.setContentIDs(bandContentIDs);
				detailBands.add(detailBand);

				for (int leftContentIndex = 0; leftContentIndex < leftContentVA.size() - 1; leftContentIndex++) {

					int contentID = leftContentVA.get(leftContentIndex);

					int nextContentID = leftContentVA.get(leftContentIndex + 1);

					if (rightContentVA.containsElement(contentID) == 0)
						continue;

					if ((rightContentVA.indexOf(contentID)) == (rightContentVA
							.indexOf(nextContentID) - 1)) {
						bandContentIDs.add(contentID);
						bandContentIDs.add(nextContentID);
					} else
						bandContentIDs.add(contentID);
				}
			}
		}

		for (Integer contentID : leftHeatMapWrapper.getContentVA()) {
			boolean isInBand = false;
			ArrayList<DetailBand> newBands = new ArrayList<DetailBand>();
			for (DetailBand band : detailBands) {
				if (band.getContentIDs().contains(contentID)) {
					isInBand = true;
					continue;
				}
			}

			if (!isInBand) {
				bandContentIDs = new ArrayList<Integer>();
				bandContentIDs.add(contentID);
				detailBand = new DetailBand();
				detailBand.setContentIDs(bandContentIDs);
				newBands.add(detailBand);
			}

			detailBands.addAll(newBands);
		}
	}

	public void renderIndiviudalLineRelations(GL gl, HeatMapWrapper leftHeatMapWrapper,
			HeatMapWrapper rightHeatMapWrapper) {

		contentIDToLeftDetailPoints.clear();
		contentIDToRightDetailPoints.clear();

		ArrayList<Vec3f> points = new ArrayList<Vec3f>();

		ContentVirtualArray overview = leftHeatMapWrapper.getContentVA().clone();
		ContentVirtualArray overviewRight = rightHeatMapWrapper.getContentVA().clone();

		float overviewDistance = rightHeatMapWrapper
				.getLeftOverviewLinkPositionFromContentIndex(0)[0]
				- leftHeatMapWrapper.getLeftOverviewLinkPositionFromContentIndex(0)[0];
		firstLevelOffset = overviewDistance / 7;

		float leftElementHeight = leftHeatMapWrapper.getLayout()
				.getOverviewHeatMapSampleHeight();

		float rightElementHeight = leftHeatMapWrapper.getLayout()
				.getOverviewHeatMapSampleHeight();

		float leftElementHeightIncludingSpacing = leftElementHeight * 0.8f;
		float rightElementHeightIncludingSpacing = rightElementHeight * 0.8f;
		float groupPadding = 0;
		float bundlingCorrectionOffsetX = overviewDistance / 18f;

		float top = leftHeatMapWrapper.getLayout().getOverviewHeatMapPosition().y()
				+ leftHeatMapWrapper.getLayout().getOverviewHeight();

		rightHeatMapWrapper.choosePassiveHeatMaps(leftHeatMapWrapper
				.getContentVAsOfHeatMaps(false), false, false);
		leftHeatMapWrapper.choosePassiveHeatMaps(rightHeatMapWrapper
				.getContentVAsOfHeatMaps(false), false, false);

		for (ContentVirtualArray groupVA : leftHeatMapWrapper
				.getContentVAsOfHeatMaps(false)) {

			// Is there a better way to find the y pos of the cluster beginning
			float groupTopY = 0;
			for (Integer overviewContentID : overview) {
				if (groupVA.containsElement(overviewContentID) == 0)
					continue;

				groupPadding = groupVA.size() * leftElementHeight * 0.1f;
				groupTopY = overview.indexOf(overviewContentID) * leftElementHeight
						+ groupPadding;
				break;
			}

			float overviewX = leftHeatMapWrapper
					.getRightOverviewLinkPositionFromContentID(groupVA.get(0))[0];

			for (Integer contentID : groupVA) {

				points.clear();

				float overviewY = leftHeatMapWrapper
						.getRightOverviewLinkPositionFromContentID(contentID)[1];

				float sortedY = top - groupTopY - groupVA.indexOf(contentID)
						* leftElementHeightIncludingSpacing;

				setRelationColor(gl, leftHeatMapWrapper, contentID);

				float[] leftOverviewPos = new float[] { overviewX, overviewY, 0 };
				float[] leftDetailPos = new float[] { overviewX + firstLevelOffset,
						sortedY, 0 };

				contentIDToLeftDetailPoints.put(contentID, leftDetailPos);

				points.add(new Vec3f(leftOverviewPos[0], leftOverviewPos[1],
						leftOverviewPos[2]));
				points
						.add(new Vec3f(overviewX + bundlingCorrectionOffsetX, overviewY,
								0));
				points.add(new Vec3f(leftDetailPos[0] - bundlingCorrectionOffsetX,
						sortedY, 0));
				points
						.add(new Vec3f(leftDetailPos[0], leftDetailPos[1],
								leftDetailPos[2]));

				ArrayList<ContentVirtualArray> rightContentVAs = rightHeatMapWrapper
						.getContentVAsOfHeatMaps(false);

				float overviewRightX = rightHeatMapWrapper
						.getLeftOverviewLinkPositionFromContentID(contentID)[0];

				for (ContentVirtualArray rightGroupVA : rightContentVAs) {

					if (rightGroupVA.containsElement(contentID) == 0)
						continue;

					// Is there a better way to find the y pos of the cluster
					// beginning
					float rightGroupTopY = 0;
					for (Integer overviewRightContentID : overviewRight) {

						if (rightGroupVA.containsElement(overviewRightContentID) == 0)
							continue;

						groupPadding = rightGroupVA.size() * rightElementHeight * 0.1f;
						rightGroupTopY = overviewRight.indexOf(overviewRightContentID)
								* rightElementHeight + groupPadding;
						break;
					}

					float overviewRightY = rightHeatMapWrapper
							.getLeftOverviewLinkPositionFromContentID(contentID)[1];

					float sortedRightY = top - rightGroupTopY
							- rightGroupVA.indexOf(contentID)
							* rightElementHeightIncludingSpacing;

					float[] rightDetailPos = new float[] {
							overviewRightX - firstLevelOffset, sortedRightY, 0 };
					float[] rightOverviewPos = new float[] { overviewRightX,
							overviewRightY, 0 };

					contentIDToRightDetailPoints.put(contentID, rightDetailPos);

					float xOffset = -(rightDetailPos[0] - leftDetailPos[0]) / 1.5f;
					points
							.add(new Vec3f(rightDetailPos[0] + xOffset, leftDetailPos[1],
									0));
					points.add(new Vec3f(rightDetailPos[0] + xOffset / 3,
							rightDetailPos[1], 0));
					points.add(new Vec3f(rightDetailPos[0], rightDetailPos[1],
							rightDetailPos[2]));
					points.add(new Vec3f(rightDetailPos[0] + bundlingCorrectionOffsetX,
							sortedRightY, 0));
					points.add(new Vec3f(overviewRightX - bundlingCorrectionOffsetX,
							overviewRightY, 0));
					points.add(new Vec3f(rightOverviewPos[0], rightOverviewPos[1],
							rightOverviewPos[2]));
				}

				if (!bandBundlingActive)
					renderSingleCurve(gl, points, contentID);
			}
		}
	}

	protected void renderDetailBandRelations(GL gl, HeatMapWrapper leftHeatMapWrapper,
			HeatMapWrapper rightHeatMapWrapper) {

		for (DetailBand detailBand : detailBands) {
			ArrayList<Integer> contentIDs = detailBand.getContentIDs();

			if (contentIDs.size() < 2)
				continue;

			// if (contentIDs.size() < 2 || detailBand == activeBand)
			// continue;

			renderSingleDetailBand(gl, detailBand, false);
		}

		// if (activeBand != null)
		// renderSingleDetailBand(gl, activeBand, true);
		// // }
		//
		// // Iterate over all detail content VAs on the left
		// HeatMapWrapper leftHeatMapWrapper = heatMapWrappers.get(0);
		// for (ContentVirtualArray contentVA : leftHeatMapWrapper
		// .getContentVAsOfHeatMaps(true)) {
		//
		// for (Integer contentID : contentVA) {
		//
		// // if (!bandBundlingActive ||
		// if (activeBand != null &&
		// activeBand.getContentIDs().contains(contentID))
		// renderSingleDetailRelation(gl, contentID);
		// }
		// }

		for (DetailBand detailBand : detailBands) {
			ArrayList<Integer> contentIDs = detailBand.getContentIDs();

			if (contentIDs.size() == 1) {

				int contentID = contentIDs.get(0);

				float[] leftTopPos = contentIDToLeftDetailPoints.get(contentID);
				float[] rightTopPos = contentIDToRightDetailPoints.get(contentID);

				if (leftTopPos == null || rightTopPos == null)
					return;

				float bundlingOffsetX = 0.1f;
				rightTopPos[0] = rightTopPos[0] + bundlingOffsetX;
				leftTopPos[0] = leftTopPos[0] - bundlingOffsetX;

				renderSingleDetailRelation(gl, contentIDs.get(0), leftTopPos, rightTopPos);
			}
		}
	}

	protected void renderSingleDetailRelation(GL gl, int contentID, float[] leftPos,
			float[] rightPos) {

		if (leftPos == null || rightPos == null)
			return;

		float xOffset = -(rightPos[0] - leftPos[0]) / 1.5f;

		ArrayList<Vec3f> points = new ArrayList<Vec3f>();
		points.add(new Vec3f(leftPos[0], leftPos[1], leftPos[2]));
		points.add(new Vec3f(rightPos[0] + xOffset, leftPos[1], leftPos[2]));
		points.add(new Vec3f(rightPos[0] + xOffset / 3f, rightPos[1], rightPos[2]));
		points.add(new Vec3f(rightPos[0], rightPos[1], rightPos[2]));

		renderSingleCurve(gl, points, contentID);
	}

	protected void renderOverviewToDetailBandRelations(GL gl,
			HeatMapWrapper heatMapWrapper, boolean isLeft) {

		float overviewX = 0;
		if (isLeft)
			overviewX = heatMapWrapper.getRightOverviewLinkPositionFromContentIndex(0)[0];
		else
			overviewX = heatMapWrapper.getLeftOverviewLinkPositionFromContentIndex(0)[0];

		ContentVirtualArray va = heatMapWrapper.getContentVA();
		for (Group group : va.getGroupList()) {
			float overviewFirstPosY = heatMapWrapper.getLayout()
					.getOverviewHeatMapSamplePositionY(group.getStartIndex());
			float overviewLastPosY = heatMapWrapper.getLayout()
					.getOverviewHeatMapSamplePositionY(group.getEndIndex());

			float[] leftTopPos = new float[] { overviewX, overviewFirstPosY, 0 };
			float[] leftBottomPos = new float[] { overviewX, overviewLastPosY, 0 };

			ContentVirtualArray detailVA = heatMapWrapper.getHeatMapByContentID(
					va.get(group.getStartIndex())).getContentVA();
			float[] rightTopPos = null;
			float[] rightBottomPos = null;
			float bundlingOffsetX = 0;
			if (isLeft) {
				rightTopPos = contentIDToLeftDetailPoints.get(detailVA.get(0)).clone();
				rightBottomPos = contentIDToLeftDetailPoints.get(
						detailVA.get(detailVA.size() - 1)).clone();
				bundlingOffsetX = 0.1f;
			} else {
				rightTopPos = contentIDToRightDetailPoints.get(detailVA.get(0)).clone();
				rightBottomPos = contentIDToRightDetailPoints.get(
						detailVA.get(detailVA.size() - 1)).clone();
				bundlingOffsetX = -0.1f;
			}
			rightTopPos[0] = rightTopPos[0] - bundlingOffsetX;
			rightBottomPos[0] = rightBottomPos[0] - bundlingOffsetX;

			renderSingleBand(gl, leftTopPos, leftBottomPos, rightTopPos, rightBottomPos,
					false);
		}
	}

	public void renderSingleCurve(GL gl, ArrayList<Vec3f> points, Integer contentID) {

		NURBSCurve curve = new NURBSCurve(points, 40);
		points = curve.getCurvePoints();

		gl.glPushName(pickingManager.getPickingID(viewID,
				EPickingType.POLYLINE_SELECTION, contentID));

		gl.glBegin(GL.GL_LINE_STRIP);
		for (int i = 0; i < points.size(); i++) {
			Vec3f point = points.get(i);
			gl.glVertex3f(point.x(), point.y(), point.z());
		}
		gl.glEnd();

		gl.glPopName();
	}

	public void renderSingleCurve(GL gl, ArrayList<Vec3f> points) {

		NURBSCurve curve = new NURBSCurve(points, 40);
		points = curve.getCurvePoints();

		gl.glBegin(GL.GL_LINE_STRIP);
		for (int i = 0; i < points.size(); i++) {
			Vec3f point = points.get(i);
			gl.glVertex3f(point.x(), point.y(), point.z());
		}
		gl.glEnd();
	}

	protected float setRelationColor(GL gl, HeatMapWrapper heatMapWrapper, int contentID) {

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
			z = 0.3f;
		} else if (type == SelectionType.SELECTION) {
			gl.glLineWidth(2);
			alpha = 1f;
			z = 0.3f;
		} else {
			gl.glLineWidth(1);
			alpha = 0.4f;
			z = 0.2f;
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
	 * @param gl
	 *            GL context.
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

	public void setBandBundling(boolean bandBundlingActive) {
		this.bandBundlingActive = bandBundlingActive;
	}

	// private void renderStraightLineRelation(GL gl, HeatMapWrapper
	// leftHeatMapWrapper,
	// HeatMapWrapper rightHeatMapWrapper, int contentID) {
	//
	// float positionZ = setRelationColor(gl, leftHeatMapWrapper, contentID);
	//
	// Vec2f leftPos = leftHeatMapWrapper
	// .getRightOverviewLinkPositionFromContentID(contentID);
	//
	// if (leftPos == null)
	// return;
	//
	// Vec2f rightPos = rightHeatMapWrapper
	// .getLeftOverviewLinkPositionFromContentID(contentID);
	//
	// if (rightPos == null)
	// return;
	//
	// ArrayList<Vec3f> points = new ArrayList<Vec3f>();
	// points.add(new Vec3f(leftPos.x(), leftPos.y(), positionZ));
	// points.add(new Vec3f(rightPos.x(), rightPos.y(), 0));
	//
	// if (points.size() == 0)
	// return;
	//
	// gl.glPushName(pickingManager.getPickingID(viewID,
	// EPickingType.POLYLINE_SELECTION, contentID));
	//
	// gl.glBegin(GL.GL_LINE_STRIP);
	// for (int i = 0; i < points.size(); i++)
	// gl.glVertex3f(points.get(i).x(), points.get(i).y(), positionZ);
	// gl.glEnd();
	//
	// gl.glPopName();
	//
	// }

	// private void renderDendrogram(final GL gl, ClusterNode currentNode, float
	// fOpacity,
	// Tree<ClusterNode> tree, float xPosInit, boolean isLeft) {
	//
	// // float fLookupValue = currentNode.getAverageExpressionValue();
	// // float[] fArMappingColor = colorMapper.getColor(fLookupValue);
	//
	// // if (bUseBlackColoring)
	// gl.glColor4f(0, 0, 0, 1);
	// // else
	// // gl.glColor4f(fArMappingColor[0], fArMappingColor[1],
	// // fArMappingColor[2], fOpacity);
	// float fTemp = 0;
	// float fDiff = 0;
	// try {
	// fTemp = currentNode.getPos().x();
	//
	// } catch (Exception e) {
	// return;
	// }
	//
	// List<ClusterNode> listGraph = null;
	//
	// if (tree.hasChildren(currentNode)) {
	// listGraph = tree.getChildren(currentNode);
	//
	// int iNrChildsNode = listGraph.size();
	//
	// float xmin = Float.MAX_VALUE;
	// float xmax = Float.MIN_VALUE;
	// float ymax = Float.MIN_VALUE;
	// float ymin = Float.MAX_VALUE;
	//
	// Vec3f[] tempPositions = new Vec3f[iNrChildsNode];
	// for (int i = 0; i < iNrChildsNode; i++) {
	//
	// ClusterNode current = listGraph.get(i);
	//
	// tempPositions[i] = new Vec3f();
	// tempPositions[i].setX(current.getPos().x());
	// tempPositions[i].setY(current.getPos().y());
	// tempPositions[i].setZ(current.getPos().z());
	//
	// xmin = Math.min(xmin, current.getPos().x());
	// xmax = Math.max(xmax, current.getPos().x());
	// ymax = Math.max(ymax, current.getPos().y());
	// ymin = Math.min(ymin, current.getPos().y());
	//
	// renderDendrogram(gl, current, 1, tree, xPosInit, isLeft);
	// }
	//
	// float x = 0;
	// if (isLeft) {
	// fDiff = fTemp - xmax;
	// x = xmax;
	// } else {
	// fDiff = fTemp - xmin;
	// x = xmin;
	// }
	//
	// gl.glPushName(pickingManager.getPickingID(this.viewID,
	// EPickingType.DENDROGRAM_GENE_NODE_SELECTION, currentNode.getID()));
	//
	// // vertical line connecting all child nodes
	// gl.glBegin(GL.GL_LINES);
	// gl.glVertex3f(x, ymin, currentNode.getPos().z());
	// gl.glVertex3f(x, ymax, currentNode.getPos().z());
	// gl.glEnd();
	//
	// // horizontal lines connecting all children with their parent
	// for (int i = 0; i < iNrChildsNode; i++) {
	// gl.glBegin(GL.GL_LINES);
	// gl.glVertex3f(x, tempPositions[i].y(), tempPositions[i].z());
	// gl.glVertex3f(tempPositions[i].x(), tempPositions[i].y(),
	// tempPositions[i].z());
	// gl.glEnd();
	// }
	//
	// gl.glPopName();
	//
	// } else {
	// gl.glPushName(pickingManager.getPickingID(this.viewID,
	// EPickingType.DENDROGRAM_GENE_LEAF_SELECTION, currentNode.getID()));
	//
	// // horizontal line visualizing leaf nodes
	// gl.glBegin(GL.GL_LINES);
	// gl.glVertex3f(currentNode.getPos().x(), currentNode.getPos().y(),
	// currentNode
	// .getPos().z());
	// gl.glVertex3f(xPosInit, currentNode.getPos().y(),
	// currentNode.getPos().z());
	// gl.glEnd();
	//
	// gl.glPopName();
	// }
	//
	// gl.glBegin(GL.GL_LINES);
	// gl.glVertex3f(currentNode.getPos().x() - fDiff, currentNode.getPos().y(),
	// currentNode.getPos().z());
	// gl.glVertex3f(currentNode.getPos().x(), currentNode.getPos().y(),
	// currentNode
	// .getPos().z());
	// gl.glEnd();
	//
	// }

	// private void renderSingleHierarchyRelation(GL gl, HeatMapWrapper
	// leftHeatMapWrapper,
	// HeatMapWrapper rightHeatMapWrapper, int contentID) {
	//
	// boolean useDendrogramCutOff = false;
	// float dendrogramCutOff = 7;
	//
	// float positionZ = setRelationColor(gl, leftHeatMapWrapper, contentID);
	//
	// Vec2f leftPos = leftHeatMapWrapper
	// .getRightOverviewLinkPositionFromContentID(contentID);
	//
	// if (leftPos == null)
	// return;
	//
	// Vec2f rightPos = rightHeatMapWrapper
	// .getLeftOverviewLinkPositionFromContentID(contentID);
	//
	// if (rightPos == null)
	// return;
	//
	// ArrayList<Vec3f> points = new ArrayList<Vec3f>();
	// points.add(new Vec3f(leftPos.x(), leftPos.y(), positionZ));
	//
	// int nodeID;
	// ClusterNode node;
	// ArrayList<ClusterNode> pathToRoot;
	//
	// // Add spline points for left hierarchy
	// try {
	// nodeID = leftTree.getNodeIDsFromLeafID(contentID).get(0);
	// } catch (Exception e) {
	// // TODO: handle exception
	// return;
	// }
	// node = leftTree.getNodeByNumber(nodeID);
	// pathToRoot = node.getParentPath(leftTree.getRoot());
	//
	// // Remove last because it is root bundling
	// // pathToRoot.remove(pathToRoot.size() - 1);
	//
	// if (renderPseudoHierarchy)
	// pathToRoot.remove(1);
	//
	// for (int i = 0; i < pathToRoot.size() - 1; i++) {
	//
	// if (useDendrogramCutOff && i > dendrogramCutOff)
	// continue;
	//
	// // Vec3f nodePos = pathNode.getPos();
	// Vec3f nodePos = pathToRoot.get(i).getPos();
	// points.add(nodePos);
	// }
	//
	// // Add spline points for right hierarchy
	// nodeID = rightTree.getNodeIDsFromLeafID(contentID).get(0);
	// node = rightTree.getNodeByNumber(nodeID);
	// pathToRoot = node.getParentPath(rightTree.getRoot());
	//
	// // Remove last because it is root bundling
	// pathToRoot.remove(pathToRoot.size() - 1);
	//
	// if (renderPseudoHierarchy)
	// pathToRoot.remove(1);
	//
	// for (int i = pathToRoot.size() - 1; i >= 0; i--) {
	//
	// if (useDendrogramCutOff && i > dendrogramCutOff)
	// continue;
	//
	// // Vec3f nodePos = pathNode.getPos();
	// ClusterNode pathNode = pathToRoot.get(i);
	// Vec3f nodePos = pathNode.getPos();
	// points.add(nodePos);
	// }
	//
	// // Center point
	// // points.add(new Vec3f(viewFrustum.getWidth() / 2f, viewFrustum
	// // .getHeight() / 2f, 0));
	// // points.add(new Vec3f(2, 4, 0));
	// // points.add(new Vec3f(1,5,0));
	//
	// points.add(new Vec3f(rightPos.x(), rightPos.y(), 0));
	//
	// if (points.size() == 0)
	// return;
	//
	// NURBSCurve curve = new NURBSCurve(points, 80);
	// points = curve.getCurvePoints();
	//
	// gl.glPushName(pickingManager.getPickingID(viewID,
	// EPickingType.POLYLINE_SELECTION, contentID));
	//
	// gl.glBegin(GL.GL_LINE_STRIP);
	// for (int i = 0; i < points.size(); i++)
	// gl.glVertex3f(points.get(i).x(), points.get(i).y(), positionZ);
	// gl.glEnd();
	//
	// gl.glPopName();
	//
	// }

	// protected void renderTree(GL gl, HeatMapWrapper heatMapWrapperLeft,
	// HeatMapWrapper heatMapWrapperRight) {
	//
	// renderPseudoHierarchy = false;
	//
	// if (setsInFocus == null || setsInFocus.size() == 0)
	// return;
	//
	// AHeatMapLayout heatMapLayoutLeft = heatMapWrapperLeft.getLayout();
	// AHeatMapLayout heatMapLayoutRight = heatMapWrapperRight.getLayout();
	//
	// xPosInitLeft = heatMapLayoutLeft.getOverviewHeatMapPosition().x()
	// + heatMapLayoutLeft.getOverviewHeatMapWidth();
	// yPosInitLeft = heatMapLayoutLeft.getOverviewHeatMapPosition().y()
	// + heatMapLayoutLeft.getOverviewHeight();
	// xPosInitRight = heatMapLayoutRight.getOverviewHeatMapPosition().x();
	// yPosInitRight = heatMapLayoutRight.getOverviewHeatMapPosition().y()
	// + heatMapLayoutRight.getOverviewHeight();
	//
	// float overviewDistance = xPosInitRight - xPosInitLeft;
	//
	// if (!renderPseudoHierarchy) {
	// // Left hierarchy
	// leftTree = heatMapWrapperLeft.getSet().getContentTree();
	// determineTreePositions(leftTree.getRoot(), leftTree, heatMapWrapperLeft,
	// overviewDistance, true);
	// // renderDendrogram(gl, leftTree.getRoot(), 1, leftTree,
	// // xPosInitLeft, true);
	//
	// // Right hierarchy
	// rightTree = heatMapWrapperRight.getSet().getContentTree();
	// determineTreePositions(rightTree.getRoot(), rightTree,
	// heatMapWrapperRight,
	// overviewDistance, false);
	// // renderDendrogram(gl, rightTree.getRoot(), 1, rightTree,
	// // xPosInitRight, false);
	//
	// } else {
	// leftTree = new Tree<ClusterNode>();
	// determinePseudoTreePositions(leftTree, heatMapWrapperLeft,
	// heatMapWrapperRight, overviewDistance, true);
	//
	// // renderDendrogram(gl, leftTree.getRoot(), 1, leftTree,
	// // xPosInitRight, false);
	//
	// rightTree = new Tree<ClusterNode>();
	// determinePseudoTreePositions(rightTree, heatMapWrapperRight,
	// heatMapWrapperLeft, overviewDistance, false);
	// }
	//
	// }

	// /**
	// * Function calculates for each node (gene or entity) in the dendrogram
	// * recursive the corresponding position inside the view frustum
	// *
	// * @param currentNode
	// * current node for calculation
	// * @return Vec3f position of the current node
	// */
	// protected Vec3f determineTreePositions(ClusterNode currentNode,
	// Tree<ClusterNode> tree, HeatMapWrapper heatMapWrapper,
	// float overviewGapWidth, boolean isLeft) {
	//
	// Vec3f pos = new Vec3f();
	//
	// AHeatMapLayout heatMapLayoutLeft = heatMapWrapper.getLayout();
	//
	// float levelWidth = 0;
	//
	// float depthCorrection = 0;
	// if (tree.getDepth() > 2) {
	// depthCorrection = 0;
	// levelWidth = (overviewGapWidth / 2.0f)
	// / (tree.getRoot().getDepth() - depthCorrection);
	//
	// levelWidth = 0.3f;
	// } else {
	// // subtract -1 instead of -2 for full dendrograms including root
	// depthCorrection = 1;
	// levelWidth = 0.3f;
	// }
	//
	// float sampleHeight = heatMapLayoutLeft.getOverviewHeight()
	// / tree.getRoot().getNrLeaves();
	//
	// if (tree.hasChildren(currentNode)) {
	//
	// ArrayList<ClusterNode> alChilds = tree.getChildren(currentNode);
	//
	// int iNrChildsNode = alChilds.size();
	//
	// Vec3f[] positions = new Vec3f[iNrChildsNode];
	//
	// for (int i = 0; i < iNrChildsNode; i++) {
	//
	// ClusterNode node = alChilds.get(i);
	// positions[i] = determineTreePositions(node, tree, heatMapWrapper,
	// overviewGapWidth, isLeft);
	// }
	//
	// if (currentNode != tree.getRoot()) {
	// float fXmin = Float.MAX_VALUE;
	// float fXmax = Float.MIN_VALUE;
	// float fYmax = Float.MIN_VALUE;
	// float fYmin = Float.MAX_VALUE;
	//
	// for (Vec3f vec : positions) {
	// fXmin = Math.min(fXmin, vec.x());
	// fXmax = Math.max(fXmax, vec.x());
	// fYmax = Math.max(fYmax, vec.y());
	// fYmin = Math.min(fYmin, vec.y());
	// }
	//
	// if (isLeft) {
	// pos.setX(fXmax + levelWidth);
	// } else {
	// pos.setX(fXmin - levelWidth);
	// }
	//
	// pos.setY(fYmin + (fYmax - fYmin) / 2);
	// pos.setZ(0);
	// }
	// } else {
	//
	// if (isLeft) {
	// pos.setX(xPosInitLeft + levelWidth);
	// pos.setY(yPosInitLeft);
	// yPosInitLeft -= sampleHeight;
	// } else {
	// pos.setX(xPosInitRight - levelWidth);
	// pos.setY(yPosInitRight);
	// yPosInitRight -= sampleHeight;
	// }
	//
	// pos.setZ(0);
	// }
	//
	// currentNode.setPos(pos);
	//
	// return pos;
	//
	// }

	// protected void determinePseudoTreePositions(Tree<ClusterNode> tree,
	// HeatMapWrapper leftHeatMapWrapper, HeatMapWrapper rightHeatMapWrapper,
	// float overviewGapWidth, boolean isLeft) {
	//
	// float xPosInit = xPosInitRight;
	// if (isLeft)
	// xPosInit = xPosInitLeft;
	//
	// ClusterNode rootNode = new ClusterNode(tree, "", 0, true, -1);
	// tree.setRootNode(rootNode);
	//
	// float levelWidth = 0.5f;// (overviewGapWidth / 2.0f) / 2;
	// int nodeID = 0;
	// for (ContentVirtualArray leftVA : leftHeatMapWrapper
	// .getContentVAsOfHeatMaps(false)) {
	//
	// ClusterNode clusterNode = new ClusterNode(tree, "", nodeID++, false, -1);
	//
	// // Calculate position of cluster
	// float[] clusterStartPos = leftHeatMapWrapper
	// .getLeftOverviewLinkPositionFromContentID(leftVA.get(0));
	// float[] clusterEndPos = leftHeatMapWrapper
	// .getLeftOverviewLinkPositionFromContentID(leftVA
	// .get(leftVA.size() - 1));
	//
	// float yPos = clusterStartPos[1]
	// + ((clusterEndPos[1] - clusterStartPos[1]) / 2f);
	//
	// float xPos = xPosInit + levelWidth;
	// if (!isLeft)
	// xPos = xPosInit - levelWidth;
	//
	// Vec3f clusterPos = new Vec3f(xPos, yPos, 0);
	// clusterNode.setPos(clusterPos);
	//
	// tree.addChild(rootNode, clusterNode);
	//
	// ArrayList<ArrayList<ClusterNode>> passiveBundlePoints = new
	// ArrayList<ArrayList<ClusterNode>>();
	//
	// for (ContentVirtualArray rightVA : rightHeatMapWrapper
	// .getContentVAsOfHeatMaps(false)) {
	//
	// ArrayList<ClusterNode> passiveBundlePointLeaves = new
	// ArrayList<ClusterNode>();
	// for (Integer contentID : leftVA) {
	//
	// if (rightVA.containsElement(contentID) == 0)
	// continue;
	//
	// ClusterNode leaf = new ClusterNode(tree, "", nodeID++, false,
	// contentID);
	// leaf.setPos(new Vec3f(xPosInit, leftHeatMapWrapper
	// .getLeftOverviewLinkPositionFromContentID(contentID)[1], 0));
	//
	// passiveBundlePointLeaves.add(leaf);
	// }
	//
	// if (passiveBundlePointLeaves.size() > 0) {
	// passiveBundlePoints.add(passiveBundlePointLeaves);
	// }
	// }
	//
	// for (ArrayList<ClusterNode> passiveBundlePoint : passiveBundlePoints) {
	//
	// ClusterNode passiveBundlePointNode = new ClusterNode(tree, "", nodeID++,
	// false, -1);
	//
	// xPos = xPosInit + levelWidth;
	// if (!isLeft)
	// xPos = xPosInit - levelWidth;
	//
	// passiveBundlePointNode.setPos(new Vec3f(xPos, clusterStartPos.y()
	// + passiveBundlePoints.indexOf(passiveBundlePoint)
	// * (clusterEndPos.y() - clusterStartPos.y())
	// / (passiveBundlePoints.size()), 0));
	//
	// // passiveBundlePointNode.setPos(new Vec3f(xPos, yPos
	// // - ((clusterEndPos.y() - clusterStartPos.y()) / 2)
	// // + passiveBundlePoints.indexOf(passiveBundlePoint)
	// // * (clusterEndPos.y() - clusterStartPos.y())
	// // / (passiveBundlePoints.size() - 1), 0));
	// //
	// tree.addChild(clusterNode, passiveBundlePointNode);
	// tree.addChildren(passiveBundlePointNode, passiveBundlePoint);
	// }
	// }
	// }

}
