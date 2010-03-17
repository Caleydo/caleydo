package org.caleydo.view.compare.state;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.SetComparer;
import org.caleydo.core.data.collection.set.SetRelations;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.Group;
import org.caleydo.core.data.selection.SelectionCommand;
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
import org.caleydo.view.compare.layout.HeatMapLayoutDetailViewLeft;
import org.caleydo.view.compare.layout.HeatMapLayoutDetailViewMid;
import org.caleydo.view.compare.layout.HeatMapLayoutDetailViewRight;
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

	private SelectionType activeHeatMapSelectionType;

	private float xOffset = 0;
	private SetRelations relations;
	private ICompareConnectionRenderer compareConnectionRenderer;

	private ArrayList<Pair<Float, Integer>> sortedClustersXOffsetUp;
	private ArrayList<Pair<Float, Integer>> sortedClustersXOffsetDown;

	private ArrayList<ArrayList<Integer>> detailBands;

	public DetailViewState(GLCompare view, int viewID,
			TextRenderer textRenderer, TextureManager textureManager,
			PickingManager pickingManager, GLMouseListener glMouseListener,
			SetBar setBar, RenderCommandFactory renderCommandFactory,
			EDataDomain dataDomain, IUseCase useCase,
			DragAndDropController dragAndDropController,
			CompareViewStateController compareViewStateController) {

		super(view, viewID, textRenderer, textureManager, pickingManager,
				glMouseListener, setBar, renderCommandFactory, dataDomain,
				useCase, dragAndDropController, compareViewStateController);
		this.setBar.setPosition(new Vec3f(0.0f, 0.0f, 0.0f));
		compareConnectionRenderer = new CompareConnectionBandRenderer();
		numSetsInFocus = 2;
	}

	@Override
	public void init(GL gl) {

		activeHeatMapSelectionType = new SelectionType("ActiveHeatmap",
				new float[]{0.0f, 0.0f, 0.0f, 1.0f}, true, false, 1f);

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

		float overviewDistance = layouts.get(1).getOverviewPosition().x()
				- (layouts.get(0).getOverviewPosition().x() + layouts.get(0)
						.getTotalOverviewWidth());
		renderTree(gl, heatMapWrappers.get(0), heatMapWrappers.get(1),
				overviewDistance);

		renderOverviewToDetailRelations(gl);

		renderDetailRelations(gl);

		if (heatMapWrappers.get(0).getContentVAsOfHeatMaps().size() == 0) {
			renderOverviewRelations(gl, heatMapWrappers.get(0), heatMapWrappers
					.get(1));
		}

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
			// renderSplineRelation(gl, va, leftHeatMapWrapper);
		}

		calculateClusterXOffset(rightHeatMapWrapper);

		for (ContentVirtualArray va : rightHeatMapWrapper
				.getContentVAsOfHeatMaps()) {

			xOffset = 0;
			renderSplineCluster(gl, va, rightHeatMapWrapper);
			// renderSplineRelation(gl, va, rightHeatMapWrapper);
		}
	}

	private void renderDetailBand(GL gl, int startContentID, int endContentID) {

		HeatMapWrapper leftHeatMapWrapper = heatMapWrappers.get(0);
		HeatMapWrapper rightHeatMapWrapper = heatMapWrappers.get(1);

		Vec2f leftPos = leftHeatMapWrapper
				.getRightDetailLinkPositionFromContentID(startContentID);
		if (leftPos == null)
			return;

		Vec2f rightPos = rightHeatMapWrapper
				.getLeftDetailLinkPositionFromContentID(startContentID);
		if (rightPos == null)
			return;

		xOffset = -1.8f;// (rightPos.x() - leftPos.x()) / 2f;

		ArrayList<Vec3f> inputPoints = new ArrayList<Vec3f>();
		inputPoints.add(new Vec3f(leftPos.x(), leftPos.y(), 0));
		inputPoints.add(new Vec3f(rightPos.x() + xOffset, leftPos.y(), 0));
		inputPoints
				.add(new Vec3f(rightPos.x() + xOffset / 3f, rightPos.y(), 0));
		inputPoints.add(new Vec3f(rightPos.x(), rightPos.y(), 0));

		NURBSCurve curve = new NURBSCurve(inputPoints, 30);
		ArrayList<Vec3f> outputPoints = curve.getCurvePoints();

		// Band border
		gl.glLineWidth(2);
		gl.glColor4f(0, 0, 0, 0.6f);
		gl.glBegin(GL.GL_LINE_STRIP);
		for (int i = 0; i < outputPoints.size(); i++)
			gl
					.glVertex3f(outputPoints.get(i).x(), outputPoints.get(i)
							.y(), .1f);
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
		inputPoints.add(new Vec3f(leftPos.x(), leftPos.y(), 0));
		inputPoints.add(new Vec3f(rightPos.x() + xOffset, leftPos.y(), 0));
		inputPoints
				.add(new Vec3f(rightPos.x() + xOffset / 3f, rightPos.y(), 0));
		inputPoints.add(new Vec3f(rightPos.x(), rightPos.y(), 0));

		curve = new NURBSCurve(inputPoints, 30);
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
			gl.glVertex3f(points.get(i).x(), points.get(i).y(), .1f);
		gl.glEnd();

		compareConnectionRenderer.render(gl, outputPoints);
	}

	private void renderSplineCluster(GL gl, ContentVirtualArray va,
			HeatMapWrapper heatMapWrapper) {

		Integer firstDetailContentID = va.get(0);
		Integer lastDetailContentID = va.get(va.size() - 1);

		int lastDetailContentIndex = va.size()-1;
		Vec2f testPos = null;
		while (testPos == null) {

			if (heatMapWrapper == heatMapWrappers.get(0))
				testPos = heatMapWrapper.getLeftDetailLinkPositionFromContentID(va
						.get(lastDetailContentIndex));
			else
				testPos = heatMapWrapper.getRightDetailLinkPositionFromContentID(va
						.get(lastDetailContentIndex));
			
			if (testPos == null)
				lastDetailContentIndex--;
		}

		lastDetailContentID = va.get(lastDetailContentIndex);

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

		// int detailLastIndex = 0;
		// if (heatMapWrapper == heatMapWrappers.get(0)) {
		// detailLastIndex = va.indexOf(va.get(0));
		// while (rightPos == null) {
		// rightPos = heatMapWrapper
		// .getLeftDetailLinkPositionFromContentID(va
		// .get(detailLastIndex--));
		// }
		// } else {
		// detailLastIndex = va.indexOf(va.get(0));
		// while (rightPos == null) {
		// rightPos = heatMapWrapper
		// .getRightDetailLinkPositionFromContentID(va
		// .get(detailLastIndex--));
		// }
		// }

		if (heatMapWrapper == heatMapWrappers.get(0))
			rightPos = heatMapWrapper
					.getLeftDetailLinkPositionFromContentID(firstDetailContentID);
		else
			rightPos = heatMapWrapper
					.getRightDetailLinkPositionFromContentID(firstDetailContentID);

		if (rightPos == null)
			return;

		if (heatMapWrapper == heatMapWrappers.get(0))
			xOffset = -0.5f;
		else
			xOffset = 0.5f;

		// if (xOffset == 0) {
		// for (Pair<Float, Integer> cluseterToXOffset :
		// sortedClustersXOffsetUp) {
		// if (firstDetailContentID.equals(cluseterToXOffset.getSecond())) {
		// xOffset = (rightPos.x() - leftPos.x())
		// * -((float) sortedClustersXOffsetUp
		// .indexOf(cluseterToXOffset) + 1)
		// / (sortedClustersXOffsetUp.size() + 1);
		// break;
		// }
		// }
		//
		// for (Pair<Float, Integer> cluseterToXOffset :
		// sortedClustersXOffsetDown) {
		// if (firstDetailContentID.equals(cluseterToXOffset.getSecond())) {
		// xOffset = (rightPos.x() - leftPos.x())
		// * -((float) sortedClustersXOffsetDown
		// .indexOf(cluseterToXOffset) + 1)
		// / (sortedClustersXOffsetDown.size() + 1);
		// break;
		// }
		// }
		// }

		if (xOffset == 0)
			return;

		ArrayList<Vec3f> inputPoints = new ArrayList<Vec3f>();
		inputPoints.add(new Vec3f(leftPos.x(), leftPos.y(), 0));
		inputPoints.add(new Vec3f(rightPos.x() + xOffset, leftPos.y(), 0));
		inputPoints
				.add(new Vec3f(rightPos.x() + xOffset / 5f, rightPos.y(), 0));
		inputPoints.add(new Vec3f(rightPos.x(), rightPos.y(), 0));

		NURBSCurve curve = new NURBSCurve(inputPoints, 30);
		ArrayList<Vec3f> outputPoints = curve.getCurvePoints();

		// Band border
		gl.glLineWidth(2);
		gl.glColor4f(0, 0, 0, 0.6f);
		gl.glBegin(GL.GL_LINE_STRIP);
		for (int i = 0; i < outputPoints.size(); i++)
			gl
					.glVertex3f(outputPoints.get(i).x(), outputPoints.get(i)
							.y(), .1f);
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

		// detailLastIndex = 0;
		// if (heatMapWrapper == heatMapWrappers.get(0)) {
		// detailLastIndex = va.indexOf(va.get(va.size() - 1));
		// while (rightPos == null) {
		// rightPos = heatMapWrapper
		// .getLeftDetailLinkPositionFromContentID(va
		// .get(detailLastIndex--));
		// }
		// } else {
		// detailLastIndex = va.indexOf(va.get(va.size() - 1));
		// while (rightPos == null) {
		// rightPos = heatMapWrapper
		// .getRightDetailLinkPositionFromContentID(va
		// .get(detailLastIndex--));
		// }
		// }

		if (rightPos == null)
			return;

		// if (xOffset == 0) {
		// for (Pair<Float, Integer> cluseterToXOffset :
		// sortedClustersXOffsetUp) {
		// if (lastDetailContentID.equals(cluseterToXOffset.getSecond())) {
		// xOffset = (rightPos.x() - leftPos.x())
		// * -((float) sortedClustersXOffsetUp
		// .indexOf(cluseterToXOffset) + 1)
		// / (sortedClustersXOffsetUp.size() + 1);
		// break;
		// }
		// }
		//
		// for (Pair<Float, Integer> cluseterToXOffset :
		// sortedClustersXOffsetDown) {
		// if (lastDetailContentID.equals(cluseterToXOffset.getSecond())) {
		// xOffset = (rightPos.x() - leftPos.x())
		// * -((float) sortedClustersXOffsetDown
		// .indexOf(cluseterToXOffset) + 1)
		// / (sortedClustersXOffsetDown.size() + 1);
		// break;
		// }
		// }
		// }

		if (xOffset == 0)
			return;

		inputPoints = new ArrayList<Vec3f>();
		inputPoints.add(new Vec3f(leftPos.x(), leftPos.y(), 0));
		inputPoints.add(new Vec3f(rightPos.x() + xOffset, leftPos.y(), 0));
		inputPoints
				.add(new Vec3f(rightPos.x() + xOffset / 5f, rightPos.y(), 0));
		inputPoints.add(new Vec3f(rightPos.x(), rightPos.y(), 0));

		curve = new NURBSCurve(inputPoints, 30);
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
			gl.glVertex3f(points.get(i).x(), points.get(i).y(), .1f);
		gl.glEnd();

		compareConnectionRenderer.render(gl, outputPoints);
	}
	private void calculateClusterXOffset(HeatMapWrapper heatMapWrapper) {

		sortedClustersXOffsetUp.clear();
		sortedClustersXOffsetDown.clear();

		for (ContentVirtualArray va : heatMapWrapper.getContentVAsOfHeatMaps()) {

			int contentID = va.get(0);

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

		Collections.sort(sortedClustersXOffsetUp);
		Collections.sort(sortedClustersXOffsetDown);
		Collections.reverse(sortedClustersXOffsetDown);
	}

	private void renderSplineRelation(GL gl, ContentVirtualArray va,
			HeatMapWrapper heatMapWrapper) {

		float alpha = 0.2f;

		ContentSelectionManager contentSelectionManager = heatMapWrapper
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

		for (Integer contentID : va) {

			if (!isActive)
				return;

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

		return (Math.abs(overviewContentIndex - detailContentIndex)) < 10
				? false
				: true;
	}

	private void renderDetailRelations(GL gl) {

		if (setsInFocus == null || setsInFocus.size() == 0)
			return;

		// HeatMapWrapper leftHeatMapWrapper = heatMapWrappers.get(0);
		// HeatMapWrapper rightHeatMapWrapper = heatMapWrappers.get(1);

		detailBands = new ArrayList<ArrayList<Integer>>();
		calculateDetailBands();

		for (ArrayList<Integer> detailBand : detailBands) {

			if (detailBand.size() == 1) {
				renderSingleDetailRelation(gl, detailBand.get(0));
			} else if (detailBand.size() >= 2) {

				renderDetailBand(gl, detailBand.get(0), detailBand
						.get(detailBand.size() - 1));
			}
		}

		// // Iterate over all detail content VAs on the left
		// for (ContentVirtualArray contentVA : leftHeatMapWrapper
		// .getContentVAsOfHeatMaps()) {
		//
		// for (Integer contentID : contentVA) {
		//
		// renderSingleDetailRelation(gl, contentID);
		// }
		// }
	}

	private void renderSingleDetailRelation(GL gl, Integer contentID) {

		float positionZ = setRelationColor(gl, heatMapWrappers.get(0),
				contentID);

		Vec2f leftPos = heatMapWrappers.get(0)
				.getRightDetailLinkPositionFromContentID(contentID);

		if (leftPos == null)
			return;

		Vec2f rightPos = heatMapWrappers.get(1)
				.getLeftDetailLinkPositionFromContentID(contentID);

		if (rightPos == null)
			return;

		gl.glPushName(pickingManager.getPickingID(viewID,
				EPickingType.POLYLINE_SELECTION, contentID));
		ArrayList<Vec3f> points = new ArrayList<Vec3f>();
		points.add(new Vec3f(leftPos.x(), leftPos.y(), 0));
		points.add(new Vec3f(leftPos.x() + (rightPos.x() - leftPos.x()) / 2f,
				leftPos.y(), 0));
		points.add(new Vec3f(leftPos.x() + (rightPos.x() - leftPos.x()) / 2f,
				rightPos.y(), 0));
		points.add(new Vec3f(rightPos.x(), rightPos.y(), 0));

		NURBSCurve curve = new NURBSCurve(points, 30);
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

		ArrayList<Integer> band = null;

		// Iterate over all detail content VAs on the left
		for (ContentVirtualArray leftContentVA : leftHeatMapWrapper
				.getContentVAsOfHeatMaps()) {

			for (ContentVirtualArray rightContentVA : rightHeatMapWrapper
					.getContentVAsOfHeatMaps()) {

				if (band == null || band.size() > 0) {
					band = new ArrayList<Integer>();
					detailBands.add(band);
				}

				for (int leftContentIndex = 0; leftContentIndex < leftContentVA
						.size()-1; leftContentIndex++) {

					int contentID = leftContentVA.get(leftContentIndex);
					int nextContentID = leftContentVA.get(leftContentIndex + 1);

					// if (band == null || band.size() > 0) {
					// band = new ArrayList<Integer>();
					// detailBands.add(band);
					// }

					if (rightContentVA.containsElement(contentID) == 0)
						continue;

					if ((rightContentVA.indexOf(contentID)) == (rightContentVA
							.indexOf(nextContentID) - 1)) {
						band.add(contentID);
						if (nextContentID != leftContentVA.get(leftContentVA.size()-1))
							band.add(nextContentID);
					}
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
			heatMapWrapper.handleSelectionUpdate(selectionDelta,
					scrollToSelection, info);

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
			if (category == heatMapWrapper.getContentSelectionManager()
					.getIDType().getCategory())
				heatMapWrapper.getContentSelectionManager()
						.executeSelectionCommand(selectionCommand);
			else
				return;
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
						shell.setSize(400, 50);
						shell.setText("Adjust p-Value");

						final Slider slider = new Slider(shell, SWT.HORIZONTAL);
						slider.setMinimum(0);
						slider.setMaximum(110);
						slider.setIncrement(1);
						slider.setPageIncrement(10);
						slider.setSelection(75);

						slider.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								performPValueAdjustment((float) slider
										.getSelection() / 100f);
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

	@Override
	public void handleStateSpecificPickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick,
			boolean isControlPressed) {

		SelectionType selectionType = null;

		HeatMapWrapper leftHeatMapWrapper = heatMapWrappers.get(0);
		HeatMapWrapper rightHeatMapWrapper = heatMapWrappers.get(1);

		switch (ePickingType) {
			case COMPARE_LEFT_EMBEDDED_VIEW_SELECTION :
				rightHeatMapWrapper.setHeatMapsInactive();
				leftHeatMapWrapper.setHeatMapActive(iExternalID);
				break;

			case COMPARE_RIGHT_EMBEDDED_VIEW_SELECTION :
				leftHeatMapWrapper.setHeatMapsInactive();
				rightHeatMapWrapper.setHeatMapActive(iExternalID);
				break;

			case COMPARE_OVERVIEW_SLIDER_ARROW_DOWN_SELECTION :
			case COMPARE_OVERVIEW_SLIDER_ARROW_UP_SELECTION :
			case COMPARE_OVERVIEW_SLIDER_BODY_SELECTION :
				HeatMapWrapper heatMapWrapper = heatMapWrappers
						.get(iExternalID);
				if (heatMapWrapper != null) {
					heatMapWrapper.handleOverviewSliderSelection(ePickingType,
							pickingMode);
				}
				break;

			case COMPARE_LEFT_GROUP_SELECTION :
				switch (pickingMode) {
					case CLICKED :
						selectionType = SelectionType.SELECTION;
						break;
					case MOUSE_OVER :
						selectionType = SelectionType.MOUSE_OVER;
						break;
				}

				leftHeatMapWrapper.handleGroupSelection(selectionType,
						iExternalID, isControlPressed);
				rightHeatMapWrapper.setHeatMapsInactive();
				break;

			case COMPARE_RIGHT_GROUP_SELECTION :
				switch (pickingMode) {
					case CLICKED :
						selectionType = SelectionType.SELECTION;
						break;
					case MOUSE_OVER :
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
	public void setSetsInFocus(ArrayList<ISet> setsInFocus) {

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
						layout = new HeatMapLayoutDetailViewLeft(
								renderCommandFactory);
					} else if (heatMapWrapperID == setsInFocus.size() - 1) {
						layout = new HeatMapLayoutDetailViewRight(
								renderCommandFactory);
					} else {
						layout = new HeatMapLayoutDetailViewMid(
								renderCommandFactory);
					}

					layouts.add(layout);

					HeatMapWrapper heatMapWrapper = new HeatMapWrapper(
							heatMapWrapperID, layout, view, null, useCase,
							view, dataDomain);
					heatMapWrapper
							.setActiveHeatMapSelectionType(activeHeatMapSelectionType);
					heatMapWrappers.add(heatMapWrapper);
					heatMapWrapperID++;
				}
			}

			// FIXME: Use array of relations?
			ISet setLeft = setsInFocus.get(0);
			ISet setRight = setsInFocus.get(1);
			relations = SetComparer.compareSets(setLeft, setRight);

			for (int i = 0; i < heatMapWrappers.size(); i++) {
				HeatMapWrapper heatMapWrapper = heatMapWrappers.get(i);
				heatMapWrapper.setSet(setsInFocus.get(i));
			}
			setsChanged = true;
			numSetsInFocus = setsInFocus.size();

			view.setDisplayListDirty();
		}

	}

	@Override
	public void handleMouseWheel(GL gl, int amount, Point wheelPoint) {
		if (amount > 0) {
			ACompareViewState overviewState = compareViewStateController
					.getState(ECompareViewStateType.OVERVIEW);
			setBar.setViewState(overviewState);
			setBar.adjustSelectionWindowSizeCentered(overviewState
					.getNumSetsInFocus());
			setBar.setMaxSelectedItems(overviewState.getMaxSetsInFocus());
			setBar.setMinSelectedItems(overviewState.getMinSetsInFocus());
			overviewState.setSetsInFocus(setBar.getSetsInFocus());
			if (!overviewState.isInitialized()) {
				overviewState.init(gl);
			}
			compareViewStateController
					.setCurrentState(ECompareViewStateType.OVERVIEW);
			view.setDisplayListDirty();
		}

	}

	private float setRelationColor(GL gl, HeatMapWrapper heatMapWrapper,
			int contentID) {

		SelectionType type = heatMapWrapper.getContentSelectionManager()
				.getSelectionTypes(contentID).get(0);

		float[] typeColor = type.getColor();
		float alpha = 0.2f;
		if (type == activeHeatMapSelectionType) {
			gl.glLineWidth(2);
			alpha = 0.5f;
		} else if (type == SelectionType.MOUSE_OVER
				|| type == SelectionType.SELECTION) {
			gl.glLineWidth(2);
			alpha = 1f;
		} else {
			gl.glLineWidth(1);

			if (isConnectionCrossing(contentID, heatMapWrapper.getContentVA(),
					heatMapWrapper.getContentVA(), heatMapWrapper))
				alpha = 0.5f;
			else
				alpha = 0.3f;
		}

		typeColor[3] = alpha;
		gl.glColor4fv(typeColor, 0);

		return type.getPriority();
	}

	@Override
	protected void setupLayouts() {

		IViewFrustum viewFrustum = view.getViewFrustum();
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
	}
}
