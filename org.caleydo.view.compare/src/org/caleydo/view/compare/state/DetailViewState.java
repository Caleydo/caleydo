package org.caleydo.view.compare.state;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.awt.Point;
import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.SetComparer;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.Group;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.SelectionTypeEvent;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.core.view.opengl.util.vislink.NURBSCurve;
import org.caleydo.view.compare.GLCompare;
import org.caleydo.view.compare.HeatMapWrapper;
import org.caleydo.view.compare.SetBar;
import org.caleydo.view.compare.layout.AHeatMapLayout;
import org.caleydo.view.compare.layout.HeatMapLayoutDetailViewLeft;
import org.caleydo.view.compare.layout.HeatMapLayoutDetailViewMid;
import org.caleydo.view.compare.layout.HeatMapLayoutDetailViewRight;
import org.caleydo.view.compare.rendercommand.RenderCommandFactory;
import org.caleydo.view.compare.renderer.CompareConnectionBandRenderer;
import org.caleydo.view.compare.renderer.ICompareConnectionRenderer;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

import com.sun.opengl.util.j2d.TextRenderer;

public class DetailViewState extends ACompareViewStateStatic {

	private SelectionType activeHeatMapSelectionType;

	private float xOffset = 0;
	private ICompareConnectionRenderer compareConnectionRenderer;

	private ArrayList<DetailBand> detailBands;
	private DetailBand activeBand;
	private int indexOfHeatMapWrapperWithDendrogram;

	public DetailViewState(GLCompare view, int viewID, TextRenderer textRenderer,
			TextureManager textureManager, PickingManager pickingManager,
			GLMouseListener glMouseListener, SetBar setBar,
			RenderCommandFactory renderCommandFactory, EDataDomain dataDomain,
			IUseCase useCase, DragAndDropController dragAndDropController,
			CompareViewStateController compareViewStateController) {

		super(view, viewID, textRenderer, textureManager, pickingManager,
				glMouseListener, setBar, renderCommandFactory, dataDomain, useCase,
				dragAndDropController, compareViewStateController);
		this.setBar.setPosition(new Vec3f(0.0f, 0.0f, 0.0f));
		compareConnectionRenderer = new CompareConnectionBandRenderer();
		numSetsInFocus = 2;
		indexOfHeatMapWrapperWithDendrogram = -1;
	}

	@Override
	public void init(GL gl) {

		activeHeatMapSelectionType = new SelectionType("ActiveHeatmap", new float[] {
				0.0f, 0.0f, 0.0f, 1.0f }, true, false, 1f);

		SelectionTypeEvent selectionTypeEvent = new SelectionTypeEvent(
				activeHeatMapSelectionType);
		eventPublisher.triggerEvent(selectionTypeEvent);

		compareConnectionRenderer.init(gl);
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
						wrapper.choosePassiveHeatMaps(heatMapWrapper
								.getContentVAsOfHeatMaps(true));
					}
				}
				view.setDisplayListDirty();
				break;
			}
		}

		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			heatMapWrapper.drawRemoteItems(gl, glMouseListener, pickingManager);
		}

	}

	@Override
	public void buildDisplayList(GL gl) {

		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			heatMapWrapper.drawLocalItems(gl, textureManager, pickingManager,
					glMouseListener, viewID);
		}

		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		IViewFrustum viewFrustum = view.getViewFrustum();

		setBar.setWidth(viewFrustum.getWidth());
		setBar.render(gl);

		renderOverviewToDetailRelations(gl);
		renderDetailRelations(gl);
	}

	private void renderOverviewToDetailRelations(GL gl) {
		HeatMapWrapper leftHeatMapWrapper = heatMapWrappers.get(0);
		HeatMapWrapper rightHeatMapWrapper = heatMapWrappers.get(1);

		renderOverviewToDetailRelations(gl, leftHeatMapWrapper);
		renderOverviewToDetailRelations(gl, rightHeatMapWrapper);
	}

	private void renderOverviewToDetailRelations(GL gl, HeatMapWrapper heatMapWrapper) {
		for (GLHeatMap heatMap : heatMapWrapper.getHeatMaps()) {

			boolean highlight = false;

			// This method needs also to be called if we don't use band
			// rendering
			// Initialization of xOffset must be calculated anyway
			renderOverviewToDetailBand(gl, heatMap, heatMapWrapper, highlight);

			if (bandBundlingActive) {

				// If at least one element in the band is in mouse_over state ->
				// change
				// band color
				ContentSelectionManager contentSelectionManager = heatMapWrapper
						.getContentSelectionManager();
				for (Integer contentID : heatMap.getContentVA()) {
					SelectionType type = contentSelectionManager.getSelectionTypes(
							contentID).get(0);

					if (type == SelectionType.MOUSE_OVER) {
						highlight = true;
						break;
					}
				}
			}

			if (!bandBundlingActive || highlight)
				renderSingleOverviewToDetailRelation(gl, heatMap, heatMapWrapper);
		}
	}

	private void renderDetailBand(GL gl, DetailBand detailBand, boolean highlight) {

		float spacing = 0.01f;

		ArrayList<Integer> contentIDs = detailBand.getContentIDs();

		int startContentID = contentIDs.get(0);
		int endContentID = contentIDs.get(contentIDs.size() - 1);

		HeatMapWrapper leftHeatMapWrapper = heatMapWrappers.get(0);
		HeatMapWrapper rightHeatMapWrapper = heatMapWrappers.get(1);

		float leftTopHeatMapElementOffset = detailBand.getLeftHeatMap().getFieldHeight(
				startContentID)
				/ 2f - spacing;
		float leftBottomHeatMapElementOffset = detailBand.getLeftHeatMap()
				.getFieldHeight(endContentID)
				/ 2f - spacing;
		float rightTopHeatMapElementOffset = detailBand.getRightHeatMap().getFieldHeight(
				startContentID)
				/ 2f - spacing;
		float rightBottomHeatMapElementOffset = detailBand.getRightHeatMap()
				.getFieldHeight(endContentID)
				/ 2f - spacing;

		Vec2f leftPos = leftHeatMapWrapper
				.getRightDetailLinkPositionFromContentID(startContentID);
		if (leftPos == null)
			return;

		Vec2f rightPos = rightHeatMapWrapper
				.getLeftDetailLinkPositionFromContentID(startContentID);
		if (rightPos == null)
			return;

		xOffset = -(rightPos.x() - leftPos.x()) / 1.5f;

		ArrayList<Vec3f> inputPoints = new ArrayList<Vec3f>();
		inputPoints.add(new Vec3f(leftPos.x(), leftPos.y() + leftTopHeatMapElementOffset,
				0));
		inputPoints.add(new Vec3f(rightPos.x() + xOffset, leftPos.y()
				+ leftTopHeatMapElementOffset, 0));
		inputPoints.add(new Vec3f(rightPos.x() + xOffset / 3f, rightPos.y()
				+ rightTopHeatMapElementOffset, 0));
		inputPoints.add(new Vec3f(rightPos.x(), rightPos.y()
				+ rightTopHeatMapElementOffset, 0));

		NURBSCurve curve = new NURBSCurve(inputPoints, NUMBER_OF_SPLINE_POINTS);
		ArrayList<Vec3f> outputPoints = curve.getCurvePoints();

		// Band border
		gl.glLineWidth(2);
		gl.glColor4f(0, 0, 0, 0.6f);
		gl.glBegin(GL.GL_LINE_STRIP);
		for (int i = 0; i < outputPoints.size(); i++)
			gl.glVertex3f(outputPoints.get(i).x(), outputPoints.get(i).y(), 0f);
		gl.glEnd();

		leftPos = leftHeatMapWrapper
				.getRightDetailLinkPositionFromContentID(endContentID);
		if (leftPos == null)
			return;

		rightPos = rightHeatMapWrapper
				.getLeftDetailLinkPositionFromContentID(endContentID);
		if (rightPos == null)
			return;

		inputPoints = new ArrayList<Vec3f>();
		inputPoints.add(new Vec3f(leftPos.x(), leftPos.y()
				- leftBottomHeatMapElementOffset, 0));
		inputPoints.add(new Vec3f(rightPos.x() + xOffset, leftPos.y()
				- leftBottomHeatMapElementOffset, 0));
		inputPoints.add(new Vec3f(rightPos.x() + xOffset / 3f, rightPos.y()
				- rightBottomHeatMapElementOffset, 0));
		inputPoints.add(new Vec3f(rightPos.x(), rightPos.y()
				- rightBottomHeatMapElementOffset, 0));

		curve = new NURBSCurve(inputPoints, NUMBER_OF_SPLINE_POINTS);
		ArrayList<Vec3f> points = curve.getCurvePoints();

		// Reverse point order
		for (int i = points.size() - 1; i >= 0; i--) {
			outputPoints.add(points.get(i));
		}

		// Band border
		gl.glLineWidth(2);
		gl.glColor4f(0, 0, 0, 0.6f);
		gl.glBegin(GL.GL_LINE_STRIP);
		for (int i = 0; i < points.size(); i++)
			gl.glVertex3f(points.get(i).x(), points.get(i).y(), 0f);
		gl.glEnd();

		if (highlight) {
			gl.glColor4f(0.3f, 0.3f, 0.3f, 0.95f);

			for (Vec3f point : outputPoints) {
				point.setZ(0.2f);
			}
		}
		// } else if (activeGroup)
		// gl.glColor4f(0f, 0f, 0f, 0.4f);
		else
			gl.glColor4f(0f, 0f, 0f, 0.4f);

		compareConnectionRenderer.render(gl, outputPoints);
	}

	private void renderOverviewToDetailBand(GL gl, GLHeatMap heatMap,
			HeatMapWrapper heatMapWrapper, boolean highlight) {

		ContentVirtualArray va = heatMap.getContentVA();
		Integer firstDetailContentID = va.get(0);
		Integer lastDetailContentID = va.get(va.size() - 1);

		GLHeatMap detailHeatMap = heatMapWrapper
				.getHeatMapByContentID(lastDetailContentID);

		float heatMapElementOffset = detailHeatMap.getFieldHeight(firstDetailContentID) / 2f - 0f;

		int numberOfVisibleLines = detailHeatMap.getNumberOfVisibleElements() - 1;
		if (numberOfVisibleLines < 0)
			return;

		lastDetailContentID = detailHeatMap.getContentVA().get(numberOfVisibleLines);

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

		Vec2f rightPos = null;

		if (heatMapWrapper == heatMapWrappers.get(0))
			rightPos = heatMapWrapper
					.getLeftDetailLinkPositionFromContentID(firstDetailContentID);
		else
			rightPos = heatMapWrapper
					.getRightDetailLinkPositionFromContentID(firstDetailContentID);

		if (rightPos == null)
			return;

		if (heatMapWrapper == heatMapWrappers.get(0))
			xOffset = -Math.abs((rightPos.x() - leftPos.x()) / 1.3f);
		else
			xOffset = Math.abs((rightPos.x() - leftPos.x()) / 1.3f);

		if (!bandBundlingActive)
			return;

		ArrayList<Vec3f> inputPoints = new ArrayList<Vec3f>();
		inputPoints.add(new Vec3f(leftPos.x(), leftPos.y(), 0));
		inputPoints.add(new Vec3f(rightPos.x() + xOffset, leftPos.y(), 0));
		inputPoints.add(new Vec3f(rightPos.x() + xOffset / 5f, rightPos.y()
				+ heatMapElementOffset, 0));
		inputPoints.add(new Vec3f(rightPos.x(), rightPos.y() + heatMapElementOffset, 0));

		NURBSCurve curve = new NURBSCurve(inputPoints, NUMBER_OF_SPLINE_POINTS);
		ArrayList<Vec3f> outputPoints = curve.getCurvePoints();

		// Band border
		gl.glLineWidth(2);
		gl.glColor4f(0, 0, 0, 0.6f);
		gl.glBegin(GL.GL_LINE_STRIP);
		for (int i = 0; i < outputPoints.size(); i++)
			gl.glVertex3f(outputPoints.get(i).x(), outputPoints.get(i).y(), 0f);
		gl.glEnd();

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

		inputPoints = new ArrayList<Vec3f>();
		inputPoints.add(new Vec3f(leftPos.x(), leftPos.y(), 0));
		inputPoints.add(new Vec3f(rightPos.x() + xOffset, leftPos.y(), 0));
		inputPoints.add(new Vec3f(rightPos.x() + xOffset / 5f, rightPos.y()
				- heatMapElementOffset, 0));
		inputPoints.add(new Vec3f(rightPos.x(), rightPos.y() - heatMapElementOffset, 0));

		curve = new NURBSCurve(inputPoints, NUMBER_OF_SPLINE_POINTS);
		ArrayList<Vec3f> points = curve.getCurvePoints();

		// Reverse point order
		for (int i = points.size() - 1; i >= 0; i--) {
			outputPoints.add(points.get(i));
		}

		// Band border
		gl.glLineWidth(2);
		gl.glColor4f(0, 0, 0, 0.6f);
		gl.glBegin(GL.GL_LINE_STRIP);
		for (int i = 0; i < points.size(); i++)
			gl.glVertex3f(points.get(i).x(), points.get(i).y(), 0f);
		gl.glEnd();

		if (!highlight)
			gl.glColor4f(0f, 0f, 0f, 0.4f);
		else
			gl.glColor4f(0f, 0f, 0f, 0.7f);

		compareConnectionRenderer.render(gl, outputPoints);
	}

	private void renderSingleOverviewToDetailRelation(GL gl, GLHeatMap heatMap,
			HeatMapWrapper heatMapWrapper) {

		ContentVirtualArray va = heatMap.getContentVA();

		for (Integer contentID : va) {

			float positionZ = setRelationColor(gl, heatMapWrapper, contentID);

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

			ArrayList<Vec3f> points = new ArrayList<Vec3f>();
			points.add(new Vec3f(leftPos.x(), leftPos.y(), 0));
			points.add(new Vec3f(rightPos.x() + xOffset, leftPos.y(), 0));
			points.add(new Vec3f(rightPos.x() + xOffset / 5f, rightPos.y(), 0));
			points.add(new Vec3f(rightPos.x(), rightPos.y(), 0));

			NURBSCurve curve = new NURBSCurve(points, NUMBER_OF_SPLINE_POINTS_SHORT);
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
	public boolean isConnectionCrossing(int contentID, ContentVirtualArray overviewVA,
			ContentVirtualArray detailVA, HeatMapWrapper heatMapWrapper) {

		int detailContentIndex = detailVA.indexOf(contentID);
		int overviewContentIndex = overviewVA.indexOf(contentID);
		Group group = heatMapWrapper.getGroupFromContentIndex(overviewVA
				.indexOf(contentID));
		overviewContentIndex = overviewContentIndex - group.getStartIndex();

		return (Math.abs(overviewContentIndex - detailContentIndex)) < 10 ? false : true;
	}

	private void renderDetailRelations(GL gl) {

		if (setsInFocus == null || setsInFocus.size() == 0)
			return;

		if (bandBundlingActive) {
			detailBands = new ArrayList<DetailBand>();
			calculateDetailBands();
			determineActiveBand();

			for (DetailBand detailBand : detailBands) {
				ArrayList<Integer> contentIDs = detailBand.getContentIDs();

				if (contentIDs.size() < 2 || detailBand == activeBand)
					continue;

				renderDetailBand(gl, detailBand, false);
			}

			if (activeBand != null)
				renderDetailBand(gl, activeBand, true);
		}

		// Iterate over all detail content VAs on the left
		HeatMapWrapper leftHeatMapWrapper = heatMapWrappers.get(0);
		for (ContentVirtualArray contentVA : leftHeatMapWrapper
				.getContentVAsOfHeatMaps(true)) {

			for (Integer contentID : contentVA) {

				if (!bandBundlingActive
						|| (activeBand != null && activeBand.getContentIDs().contains(
								contentID)))
					renderSingleDetailRelation(gl, contentID);
			}
		}
	}

	private void renderSingleDetailRelation(GL gl, Integer contentID) {

		float positionZ = setRelationColor(gl, heatMapWrappers.get(0), contentID);

		Vec2f leftPos = heatMapWrappers.get(0).getRightDetailLinkPositionFromContentID(
				contentID);

		if (leftPos == null)
			return;

		Vec2f rightPos = heatMapWrappers.get(1).getLeftDetailLinkPositionFromContentID(
				contentID);

		if (rightPos == null)
			return;

		xOffset = -(rightPos.x() - leftPos.x()) / 1.5f;

		gl.glPushName(pickingManager.getPickingID(viewID,
				EPickingType.POLYLINE_SELECTION, contentID));
		ArrayList<Vec3f> points = new ArrayList<Vec3f>();
		points.add(new Vec3f(leftPos.x(), leftPos.y(), positionZ));
		points.add(new Vec3f(rightPos.x() + xOffset, leftPos.y(), positionZ));
		points.add(new Vec3f(rightPos.x() + xOffset / 3f, rightPos.y(), positionZ));
		points.add(new Vec3f(rightPos.x(), rightPos.y(), positionZ));

		NURBSCurve curve = new NURBSCurve(points, NUMBER_OF_SPLINE_POINTS);
		points = curve.getCurvePoints();

		gl.glBegin(GL.GL_LINE_STRIP);
		for (int i = 0; i < points.size(); i++)
			gl.glVertex3f(points.get(i).x(), points.get(i).y(), positionZ);
		gl.glEnd();

		gl.glPopName();
	}

	private void calculateDetailBands() {

		HeatMapWrapper leftHeatMapWrapper = heatMapWrappers.get(0);
		HeatMapWrapper rightHeatMapWrapper = heatMapWrappers.get(1);

		ArrayList<Integer> bandContentIDs = null;
		DetailBand detailBand = null;

		// Iterate over all detail content VAs on the left
		for (GLHeatMap leftHeatMap : leftHeatMapWrapper.getHeatMaps()) {

			ContentVirtualArray leftContentVA = leftHeatMap.getContentVA();

			for (GLHeatMap rightHeatMap : rightHeatMapWrapper.getHeatMaps()) {

				ContentVirtualArray rightContentVA = rightHeatMap.getContentVA();

				bandContentIDs = new ArrayList<Integer>();
				detailBand = new DetailBand();
				detailBand.setContentIDs(bandContentIDs);
				detailBand.setLeftHeatMap(leftHeatMap);
				detailBand.setRightHeatMap(rightHeatMap);
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
				detailBand.setLeftHeatMap(leftHeatMapWrapper
						.getHeatMapByContentID(contentID));
				detailBand.setRightHeatMap(rightHeatMapWrapper
						.getHeatMapByContentID(contentID));
				newBands.add(detailBand);
			}

			detailBands.addAll(newBands);
		}
	}

	private void determineActiveBand() {

		for (DetailBand detailBand : detailBands) {
			// If at least one element in the band is in mouse_over state ->
			// change
			// band color
			ContentSelectionManager contentSelectionManager = heatMapWrappers.get(0)
					.getContentSelectionManager();
			for (Integer contentID : detailBand.getContentIDs()) {
				SelectionType type = contentSelectionManager.getSelectionTypes(contentID)
						.get(0);

				if (type == SelectionType.MOUSE_OVER) {
					// activeBand = true;
					this.activeBand = detailBand;
					return;
				}
			}
		}
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
			heatMapWrapper.handleSelectionUpdate(selectionDelta, scrollToSelection, info);

			// FIXME: Move to overview state when Christian has finished work on
			// states
			heatMapWrapper.getOverview().updateHeatMapTextures(
					heatMapWrapper.getContentSelectionManager());
		}
	}

	@Override
	public void handleSelectionCommand(EIDCategory category,
			SelectionCommand selectionCommand) {

		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			if (category == heatMapWrapper.getContentSelectionManager().getIDType()
					.getCategory())
				heatMapWrapper.getContentSelectionManager().executeSelectionCommand(
						selectionCommand);
			else
				return;
		}
	}

	@Override
	public void adjustPValue() {
		//
		// GeneralManager.get().getGUIBridge().getDisplay().asyncExec(
		// new Runnable() {
		//
		// @Override
		// public void run() {
		//
		// Shell shell = new Shell(GeneralManager.get()
		// .getGUIBridge().getDisplay());
		// shell.setLayout(new FillLayout());
		// shell.setSize(400, 50);
		// shell.setText("Adjust p-Value");
		//
		// final Slider slider = new Slider(shell, SWT.HORIZONTAL);
		// slider.setMinimum(0);
		// slider.setMaximum(110);
		// slider.setIncrement(1);
		// slider.setPageIncrement(10);
		// slider.setSelection(75);
		//
		// slider.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// performPValueAdjustment((float) slider
		// .getSelection() / 100f);
		// }
		// });
		// shell.open();
		// }
		// });
	}

	// private void performPValueAdjustment(float pValue) {
	//
	// ContentVirtualArray pValueFilteredVA = heatMapWrappers.get(0).getSet()
	// .getStatisticsResult().getVABasedOnTwoSidedTTestResult(
	// heatMapWrappers.get(1).getSet(), pValue);
	//
	// for (Integer contentID : heatMapWrappers.get(0).getContentVA()) {
	//
	// if (pValueFilteredVA.containsElement(contentID) == 0)
	// heatMapWrappers.get(0).getContentSelectionManager().addToType(
	// SelectionType.DESELECTED, contentID);
	// else
	// heatMapWrappers.get(0).getContentSelectionManager()
	// .removeFromType(SelectionType.DESELECTED, contentID);
	// }
	//
	// ISelectionDelta selectionDelta = heatMapWrappers.get(0)
	// .getContentSelectionManager().getDelta();
	// SelectionUpdateEvent event = new SelectionUpdateEvent();
	// event.setSender(this);
	// event.setSelectionDelta((SelectionDelta) selectionDelta);
	// eventPublisher.triggerEvent(event);
	// }

	@Override
	public int getMaxSetsInFocus() {
		return 2;
	}

	@Override
	public int getMinSetsInFocus() {
		return 2;
	}

	@Override
	public void handleStateSpecificPickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick, boolean isControlPressed) {

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

		case COMPARE_OVERVIEW_SLIDER_ARROW_DOWN_SELECTION:
		case COMPARE_OVERVIEW_SLIDER_ARROW_UP_SELECTION:
		case COMPARE_OVERVIEW_SLIDER_BODY_SELECTION:
			HeatMapWrapper heatMapWrapper = heatMapWrappers.get(iExternalID);
			if (heatMapWrapper != null) {
				heatMapWrapper.handleOverviewSliderSelection(ePickingType, pickingMode);
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
			rightHeatMapWrapper.handleGroupSelection(selectionType, iExternalID,
					isControlPressed);
			leftHeatMapWrapper.setHeatMapsInactive();
			break;

		case COMPARE_DENDROGRAM_BUTTON_SELECTION:
			if (pickingMode == EPickingMode.CLICKED) {
				if (indexOfHeatMapWrapperWithDendrogram == iExternalID) {
					layouts.get(iExternalID).useDendrogram(false);
					indexOfHeatMapWrapperWithDendrogram = -1;
				} else {
					for (AHeatMapLayout layout : layouts) {
						layout.useDendrogram(false);
					}
					layouts.get(iExternalID).useDendrogram(true);
					indexOfHeatMapWrapperWithDendrogram = iExternalID;
				}
				view.setDisplayListDirty();
			}
			break;
		}

	}

	@Override
	public void setSetsInFocus(ArrayList<ISet> setsInFocus) {

		indexOfHeatMapWrapperWithDendrogram = -1;

		if (setsInFocus.size() >= getMinSetsInFocus()
				&& setsInFocus.size() <= getMaxSetsInFocus()) {

			this.setsInFocus = setsInFocus;

			if (layouts.isEmpty() || setsInFocus.size() != layouts.size()) {
				layouts.clear();
				heatMapWrappers.clear();

				int heatMapWrapperID = 0;
				for (ISet set : setsInFocus) {
					AHeatMapLayout layout = null;
					if (heatMapWrapperID == 0) {
						layout = new HeatMapLayoutDetailViewLeft(renderCommandFactory);
					} else if (heatMapWrapperID == setsInFocus.size() - 1) {
						layout = new HeatMapLayoutDetailViewRight(renderCommandFactory);
					} else {
						layout = new HeatMapLayoutDetailViewMid(renderCommandFactory);
					}

					layouts.add(layout);

					HeatMapWrapper heatMapWrapper = new HeatMapWrapper(heatMapWrapperID,
							layout, view, null, useCase, view, dataDomain);
					heatMapWrapper
							.setActiveHeatMapSelectionType(activeHeatMapSelectionType);
					heatMapWrappers.add(heatMapWrapper);
					heatMapWrapperID++;
				}
			}

			// FIXME: Use array of relations?
			// ISet setLeft = setsInFocus.get(0);
			// ISet setRight = setsInFocus.get(1);
			// relations = SetComparer.compareSets(setLeft, setRight);

			for (int i = 0; i < heatMapWrappers.size(); i++) {
				HeatMapWrapper heatMapWrapper = heatMapWrappers.get(i);
				heatMapWrapper.setSet(setsInFocus.get(i));
			}
			setsChanged = true;
			numSetsInFocus = setsInFocus.size();

			// Select all groups in detail per default
			// HeatMapWrapper heatMapWrapper = heatMapWrappers.get(0);
			// for (Group group : heatMapWrapper.getContentVA().getGroupList())
			// {
			// heatMapWrapper.handleGroupSelection(SelectionType.SELECTION,
			// group
			// .getGroupIndex(), true);
			// }

			view.setDisplayListDirty();
		}

	}

	@Override
	public void handleMouseWheel(GL gl, int amount, Point wheelPoint) {
		if (amount > 0) {

			DetailToOverviewTransition transition = (DetailToOverviewTransition) compareViewStateController
					.getState(ECompareViewStateType.DETAIL_TO_OVERVIEW_TRANSITION);

			indexOfHeatMapWrapperWithDendrogram = -1;

			compareViewStateController
					.setCurrentState(ECompareViewStateType.DETAIL_TO_OVERVIEW_TRANSITION);
			transition.init(gl);
			view.setDisplayListDirty();
		}

	}

	@Override
	protected void setupLayouts() {

		IViewFrustum viewFrustum = view.getViewFrustum();
		float setBarHeight = setBar.getHeight();
		float heatMapWrapperPosY = setBar.getPosition().y() + setBarHeight;

		float heatMapWrapperPosX = 0.0f;
		float heatMapWrapperWidth = 0.0f;
		float dendrogramHeatMapWrapperWidth = 0.7f * viewFrustum.getWidth();
		if (indexOfHeatMapWrapperWithDendrogram != -1) {
			heatMapWrapperWidth = (viewFrustum.getWidth() - dendrogramHeatMapWrapperWidth)
					/ (2.0f * (float) heatMapWrappers.size() - 2.0f);
		} else {
			heatMapWrapperWidth = viewFrustum.getWidth()
					/ (2.0f * (float) heatMapWrappers.size() - 1.0f);
		}
		for (int i = 0; i < heatMapWrappers.size(); i++) {
			HeatMapWrapper heatMapWrapper = heatMapWrappers.get(i);
			AHeatMapLayout layout = layouts.get(i);
			if (i == indexOfHeatMapWrapperWithDendrogram) {
				layout.setLayoutParameters(heatMapWrapperPosX, heatMapWrapperPosY,
						viewFrustum.getHeight() - setBarHeight,
						dendrogramHeatMapWrapperWidth);
				heatMapWrapperPosX += dendrogramHeatMapWrapperWidth + heatMapWrapperWidth;
			} else {
				layout.setLayoutParameters(heatMapWrapperPosX, heatMapWrapperPosY,
						viewFrustum.getHeight() - setBarHeight, heatMapWrapperWidth);
				heatMapWrapperPosX += heatMapWrapperWidth * 2.0f;
			}
			layout.setHeatMapWrapper(heatMapWrapper);

		}
	}

	@Override
	public void setUseSorting(boolean useSorting) {
		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			heatMapWrapper.setUseSorting(useSorting);
			heatMapWrapper.setDisplayListDirty();
		}
	}

	@Override
	public void setUseZoom(boolean useZoom) {
		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			heatMapWrapper.setUseZoom(useZoom);
			heatMapWrapper.setDisplayListDirty();
			view.setDisplayListDirty();
		}
	}

	@Override
	public void setUseFishEye(boolean useFishEye) {
		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			heatMapWrapper.setUseFishEye(useFishEye);
			heatMapWrapper.setDisplayListDirty();
			view.setDisplayListDirty();
		}
	}

}
