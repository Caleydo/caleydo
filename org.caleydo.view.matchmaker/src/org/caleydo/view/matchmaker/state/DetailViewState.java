package org.caleydo.view.matchmaker.state;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.picking.PickingMode;
import org.caleydo.core.manager.picking.PickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.matchmaker.GLMatchmaker;
import org.caleydo.view.matchmaker.HeatMapWrapper;
import org.caleydo.view.matchmaker.SetBar;
import org.caleydo.view.matchmaker.layout.AHeatMapLayout;
import org.caleydo.view.matchmaker.layout.HeatMapLayoutDetailViewLeft;
import org.caleydo.view.matchmaker.layout.HeatMapLayoutDetailViewMid;
import org.caleydo.view.matchmaker.layout.HeatMapLayoutDetailViewRight;
import org.caleydo.view.matchmaker.rendercommand.RenderCommandFactory;

import com.jogamp.opengl.util.awt.TextRenderer;

public class DetailViewState extends ACompareViewStateStatic {

	private int indexOfHeatMapWrapperWithDendrogram;

	public DetailViewState(GLMatchmaker view, int viewID, TextRenderer textRenderer,
			TextureManager textureManager, PickingManager pickingManager,
			GLMouseListener glMouseListener, SetBar setBar,
			RenderCommandFactory renderCommandFactory, ATableBasedDataDomain dataDomain,
			DragAndDropController dragAndDropController,
			CompareViewStateController compareViewStateController) {

		super(view, viewID, textRenderer, textureManager, pickingManager,
				glMouseListener, setBar, renderCommandFactory, dataDomain,
				dragAndDropController, compareViewStateController);
		numSetsInFocus = 2;
		indexOfHeatMapWrapperWithDendrogram = -1;
	}

	@Override
	public void init(GL2 gl) {

		setBarDisplayListIndex = gl.glGenLists(1);
		heatMapWrapperDisplayListIndex = gl.glGenLists(1);
		heatMapWrapperSelectionDisplayListIndex = gl.glGenLists(1);

		compareConnectionRenderer.init(gl);
		setsChanged = false;
	}

	@Override
	public void drawActiveElements(GL2 gl) {

		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			if (heatMapWrapper.handleDragging(gl, glMouseListener)) {
				setHeatMapWrapperDisplayListDirty();
			}
		}

		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			if (heatMapWrapper.isNewSelection()) {
				for (HeatMapWrapper wrapper : heatMapWrappers) {
					if (wrapper != heatMapWrapper) {
						wrapper.choosePassiveHeatMaps(
								heatMapWrapper.getRecordVAsOfHeatMaps(true), true, true,
								true);
					}
				}
				setHeatMapWrapperDisplayListDirty();
				break;
			}
		}

		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			heatMapWrapper.drawRemoteItems(gl, glMouseListener, pickingManager);
		}

	}

	@Override
	public void buildDisplayList(GL2 gl) {

		// The bands need to be created only once in the detail
		// if (detailBands == null)
		if (isHeatMapWrapperDisplayListDirty) {
			// if(true){
			isHeatMapWrapperDisplayListDirty = false;
			// isHeatMapWrapperSelectionDisplayListDirty = false;

			// FIXME: Why can't this be? Heatmap wrappers should be initialized
			// all the time?!
			if (heatMapWrappers.size() < 2)
				return;

			gl.glNewList(heatMapWrapperDisplayListIndex, GL2.GL_COMPILE);

			leftHeatMapWrapperToDetailBands = new HashMap<HeatMapWrapper, ArrayList<DetailBand>>();
			detailBandID = 0;
			calculateDetailBands(heatMapWrappers.get(0), heatMapWrappers.get(1), false);

			for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
				heatMapWrapper.drawLocalItems(gl, textureManager, pickingManager,
						glMouseListener, viewID);
			}

			gl.glEnable(GL2.GL_BLEND);
			gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

			if (heatMapWrappers.get(0).getSelectedGroups().isEmpty()) {

				try {
					renderIndiviudalLineRelations(gl, heatMapWrappers.get(0),
							heatMapWrappers.get(1));

					if (bandBundlingActive) {

						renderOverviewToDetailBandRelations(gl, heatMapWrappers.get(0),
								true);
						renderOverviewToDetailBandRelations(gl, heatMapWrappers.get(1),
								false);
						renderDetailBandRelations(gl, heatMapWrappers.get(0),
								heatMapWrappers.get(1));
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			} else {
				renderDetailRelations(gl);
				renderOverviewToDetailRelations(gl);
			}
			gl.glEndList();
		}

		if (isHeatMapWrapperSelectionDisplayListDirty) {
			isHeatMapWrapperSelectionDisplayListDirty = false;
			gl.glNewList(heatMapWrapperSelectionDisplayListIndex, GL2.GL_COMPILE);
			renderSelections(gl);
			gl.glEndList();
		}

		if (isSetBarDisplayListDirty) {
			isSetBarDisplayListDirty = false;
			gl.glNewList(setBarDisplayListIndex, GL2.GL_COMPILE);
			ViewFrustum viewFrustum = view.getViewFrustum();

			setBar.setWidth(viewFrustum.getWidth());
			setBar.render(gl);
			gl.glEndList();
		}

		gl.glCallList(heatMapWrapperDisplayListIndex);
		gl.glCallList(heatMapWrapperSelectionDisplayListIndex);
		gl.glCallList(setBarDisplayListIndex);
	}

	@Override
	protected void renderSelections(GL2 gl) {
		if (heatMapWrappers.get(0).getSelectedGroups().isEmpty()) {
			renderOverviewLineSelections(gl);
		}
		renderHeatMapOverviewSelections(gl);
	}

	private void renderOverviewToDetailRelations(GL2 gl) {
		HeatMapWrapper leftHeatMapWrapper = heatMapWrappers.get(0);
		HeatMapWrapper rightHeatMapWrapper = heatMapWrappers.get(1);

		renderOverviewToDetailRelations(gl, leftHeatMapWrapper);
		renderOverviewToDetailRelations(gl, rightHeatMapWrapper);
	}

	private void renderOverviewToDetailRelations(GL2 gl, HeatMapWrapper heatMapWrapper) {

		for (GLHeatMap heatMap : heatMapWrapper.getHeatMaps(true)) {

			boolean highlight = false;

			// // If at least one element in the band is in mouse_over state ->
			// // change
			// // band color

			for (Integer recordID : heatMap.getRecordVA()) {

				if (activeBand != null && activeBand.getContentIDs().contains(recordID)) {
					highlight = true;
					break;
				}
			}

			// This method needs also to be called if we don't use band
			// rendering
			// Initialization of xOffset must be calculated anyway
			renderOverviewToDetailBand(gl, heatMap, heatMapWrapper, highlight);

			// if (!bandBundlingActive || highlight)
			if (highlight)
				renderSingleOverviewToDetailRelation(gl, heatMap, heatMapWrapper);
		}
	}

	private void renderOverviewToDetailBand(GL2 gl, GLHeatMap heatMap,
			HeatMapWrapper heatMapWrapper, boolean highlight) {

		RecordVirtualArray va = heatMap.getRecordVA();
		Integer firstDetailContentID = va.get(0);
		Integer lastDetailContentID = va.get(va.size() - 1);

		GLHeatMap detailHeatMap = heatMapWrapper
				.getHeatMapByContentID(lastDetailContentID);

		int numberOfVisibleLines = detailHeatMap.getNumberOfVisibleElements() - 1;
		if (numberOfVisibleLines < 0)
			return;

		lastDetailContentID = detailHeatMap.getRecordVA().get(numberOfVisibleLines);

		float[] leftTopPos;

		Group group = heatMapWrapper.getSelectedGroupFromContentIndex(heatMapWrapper
				.getRecordVA().indexOf(firstDetailContentID));
		int overviewFirstContentIndex = group.getStartIndex();
		int overviewLastContentIndex = group.getEndIndex();

		if (heatMapWrapper == heatMapWrappers.get(0))
			leftTopPos = heatMapWrapper
					.getRightOverviewLinkPositionFromContentIndex(overviewFirstContentIndex);
		else
			leftTopPos = heatMapWrapper
					.getLeftOverviewLinkPositionFromContentIndex(overviewFirstContentIndex);

		float[] rightTopPos = null;

		if (heatMapWrapper == heatMapWrappers.get(0))
			rightTopPos = heatMapWrapper
					.getLeftDetailLinkPositionFromContentID(firstDetailContentID);
		else
			rightTopPos = heatMapWrapper
					.getRightDetailLinkPositionFromContentID(firstDetailContentID);

		float[] leftBottomPos;

		if (heatMapWrapper == heatMapWrappers.get(0))
			leftBottomPos = heatMapWrapper
					.getRightOverviewLinkPositionFromContentIndex(overviewLastContentIndex);
		else
			leftBottomPos = heatMapWrapper
					.getLeftOverviewLinkPositionFromContentIndex(overviewLastContentIndex);

		float[] rightBottomPos;

		if (heatMapWrapper == heatMapWrappers.get(0))
			rightBottomPos = heatMapWrapper
					.getLeftDetailLinkPositionFromContentID(lastDetailContentID);
		else
			rightBottomPos = heatMapWrapper
					.getRightDetailLinkPositionFromContentID(lastDetailContentID);

		float rightTopHeatMapElementOffset = heatMapWrapper.getHeatMapByContentID(
				firstDetailContentID).getFieldHeight(firstDetailContentID)
				/ 2f - bandPaddingY;
		float rightBottomHeatMapElementOffset = heatMapWrapper.getHeatMapByContentID(
				lastDetailContentID).getFieldHeight(lastDetailContentID)
				/ 2f - bandPaddingY;

		try {

			rightTopPos[1] = rightTopPos[1] + rightTopHeatMapElementOffset;
			rightBottomPos[1] = rightBottomPos[1] - rightBottomHeatMapElementOffset;

			float xOffset = (rightTopPos[0] - leftTopPos[0]) / 3f;

			renderSingleBand(gl, leftTopPos, leftBottomPos, rightTopPos, rightBottomPos,
					highlight, xOffset, -1, false);
		} catch (Exception e) {
			System.out.println("TODO: investigate NPE");
		}

	}

	private void renderSingleOverviewToDetailRelation(GL2 gl, GLHeatMap heatMap,
			HeatMapWrapper heatMapWrapper) {

		RecordVirtualArray va = heatMap.getRecordVA();

		for (Integer recordID : va) {

			float[] leftPos;
			if (heatMapWrapper == heatMapWrappers.get(0))
				leftPos = heatMapWrapper
						.getRightOverviewLinkPositionFromContentID(recordID);
			else
				leftPos = heatMapWrapper
						.getLeftOverviewLinkPositionFromContentID(recordID);

			float[] rightPos;
			if (heatMapWrapper == heatMapWrappers.get(0))
				rightPos = heatMapWrapper
						.getLeftDetailLinkPositionFromContentID(recordID);
			else
				rightPos = heatMapWrapper
						.getRightDetailLinkPositionFromContentID(recordID);

			if (leftPos == null || rightPos == null)
				return;

			float positionZ = setRelationColor(gl, heatMapWrapper, recordID, true);
			leftPos[2] = positionZ;
			rightPos[2] = positionZ;

			renderSingleDetailRelation(gl, recordID, leftPos, rightPos);
		}
	}

	// TODO: Refine crossing detection algorithm
	public boolean isConnectionCrossing(int recordID, RecordVirtualArray overviewVA,
			RecordVirtualArray detailVA, HeatMapWrapper heatMapWrapper) {

		int detailContentIndex = detailVA.indexOf(recordID);
		int overviewContentIndex = overviewVA.indexOf(recordID);
		Group group = heatMapWrapper.getSelectedGroupFromContentIndex(overviewVA
				.indexOf(recordID));
		overviewContentIndex = overviewContentIndex - group.getStartIndex();

		return (Math.abs(overviewContentIndex - detailContentIndex)) < 10 ? false : true;
	}

	private void renderDetailRelations(GL2 gl) {

		if (setsInFocus == null || setsInFocus.size() == 0)
			return;

		determineActiveBand();
		ArrayList<DetailBand> detailBands = leftHeatMapWrapperToDetailBands
				.get(heatMapWrappers.get(0));
		for (DetailBand detailBand : detailBands) {
			ArrayList<Integer> recordIDs = detailBand.getContentIDs();

			if (recordIDs.size() < 2 || detailBand == activeBand)
				continue;

			renderSingleDetailBand(gl, detailBand, false);
		}

		if (activeBand != null) {
			renderSingleDetailBand(gl, activeBand, true);
		}

		// Render single lines which have no bundling
		for (DetailBand detailBand : detailBands) {
			if (detailBand.getContentIDs().size() == 1) {
				renderSingleDetailToDetailRelation(gl, detailBand.getContentIDs().get(0));
				continue;
			}
		}

		RecordSelectionManager contentSelectionManager = heatMapWrappers.get(0)
				.getContentSelectionManager();

		for (Integer mouseOverConentID : contentSelectionManager
				.getElements(SelectionType.MOUSE_OVER))
			renderSingleDetailToDetailRelation(gl, mouseOverConentID);

		for (Integer selectionConentID : contentSelectionManager
				.getElements(SelectionType.SELECTION))
			renderSingleDetailToDetailRelation(gl, selectionConentID);
	}

	protected void renderSingleDetailToDetailRelation(GL2 gl, Integer recordID) {

		// float positionZ = setRelationColor(gl, heatMapWrappers.get(0),
		// recordID, true);

		float[] leftPos = heatMapWrappers.get(0).getRightDetailLinkPositionFromContentID(
				recordID);

		float[] rightPos = heatMapWrappers.get(1).getLeftDetailLinkPositionFromContentID(
				recordID);

		renderSingleDetailRelation(gl, recordID, leftPos, rightPos);

	}

	private void determineActiveBand() {

		activeBand = null;
		ArrayList<DetailBand> detailBands = leftHeatMapWrapperToDetailBands
				.get(heatMapWrappers.get(0));
		for (DetailBand detailBand : detailBands) {
			// If at least one element in the band is in mouse_over state ->
			// change
			// band color
			RecordSelectionManager contentSelectionManager = heatMapWrappers.get(0)
					.getContentSelectionManager();
			for (Integer recordID : detailBand.getContentIDs()) {

				boolean isActive = false;
				for (SelectionType type : contentSelectionManager
						.getSelectionTypes(recordID)) {

					if (type == SelectionType.MOUSE_OVER)
						isActive = true;

					if (type.equals(GLHeatMap.SELECTION_HIDDEN)) {
						isActive = false;
						break;
					}
				}

				if (isActive) {
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
	public void handleSelectionCommand(IDCategory category,
			SelectionCommand selectionCommand) {

		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			if (category == heatMapWrapper.getContentSelectionManager().getIDType()
					.getIDCategory())
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
	// ContentVirtualArray pValueFilteredVA = heatMapWrappers.get(0).getDataTable()
	// .getStatisticsResult().getVABasedOnTwoSidedTTestResult(
	// heatMapWrappers.get(1).getDataTable(), pValue);
	//
	// for (Integer recordID : heatMapWrappers.get(0).getRecordVA()) {
	//
	// if (pValueFilteredVA.containsElement(recordID) == 0)
	// heatMapWrappers.get(0).getContentSelectionManager().addToType(
	// SelectionType.DESELECTED, recordID);
	// else
	// heatMapWrappers.get(0).getContentSelectionManager()
	// .removeFromType(SelectionType.DESELECTED, recordID);
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
	public void handleStateSpecificPickingEvents(PickingType ePickingType,
			PickingMode pickingMode, int externalID, Pick pick, boolean isControlPressed) {

		SelectionType selectionType = null;

		HeatMapWrapper leftHeatMapWrapper = heatMapWrappers.get(0);
		HeatMapWrapper rightHeatMapWrapper = heatMapWrappers.get(1);

		switch (ePickingType) {
		case COMPARE_LEFT_EMBEDDED_VIEW_SELECTION:
			rightHeatMapWrapper.setHeatMapsInactive();
			leftHeatMapWrapper.setHeatMapActive(externalID, false);
			break;

		case COMPARE_RIGHT_EMBEDDED_VIEW_SELECTION:
			leftHeatMapWrapper.setHeatMapsInactive();
			rightHeatMapWrapper.setHeatMapActive(externalID, false);
			break;

		case COMPARE_OVERVIEW_SLIDER_ARROW_DOWN_SELECTION:
		case COMPARE_OVERVIEW_SLIDER_ARROW_UP_SELECTION:
		case COMPARE_OVERVIEW_SLIDER_BODY_SELECTION:
			HeatMapWrapper heatMapWrapper = heatMapWrappers.get(externalID);
			if (heatMapWrapper != null) {
				heatMapWrapper.handleOverviewSliderSelection(ePickingType, pickingMode);
			}
			break;

		case COMPARE_GROUP_1_SELECTION:
			switch (pickingMode) {
			case CLICKED:
				selectionType = SelectionType.SELECTION;
				break;
			case MOUSE_OVER:
				selectionType = SelectionType.MOUSE_OVER;
				break;
			}

			leftHeatMapWrapper.handleGroupSelection(selectionType, externalID,
					isControlPressed, createSelectionTypes);
			rightHeatMapWrapper.setHeatMapsInactive();
			break;

		case COMPARE_GROUP_2_SELECTION:
			switch (pickingMode) {
			case CLICKED:
				selectionType = SelectionType.SELECTION;
				break;
			case MOUSE_OVER:
				selectionType = SelectionType.MOUSE_OVER;
				break;
			}
			rightHeatMapWrapper.handleGroupSelection(selectionType, externalID,
					isControlPressed, createSelectionTypes);
			leftHeatMapWrapper.setHeatMapsInactive();
			break;

		case COMPARE_DENDROGRAM_BUTTON_SELECTION:
			if (pickingMode == PickingMode.CLICKED) {
				if (indexOfHeatMapWrapperWithDendrogram == externalID) {
					layouts.get(externalID).useDendrogram(false);
					indexOfHeatMapWrapperWithDendrogram = -1;
				} else {
					for (AHeatMapLayout layout : layouts) {
						layout.useDendrogram(false);
					}
					layouts.get(externalID).useDendrogram(true);
					indexOfHeatMapWrapperWithDendrogram = externalID;
				}
				setHeatMapWrapperDisplayListDirty();
			}
			break;
		}

	}

	@Override
	public void setDataTablesInFocus(ArrayList<DataTable> setsInFocus) {

		indexOfHeatMapWrapperWithDendrogram = -1;

		if (setsInFocus.size() >= getMinSetsInFocus()
				&& setsInFocus.size() <= getMaxSetsInFocus()) {

			this.setsInFocus = setsInFocus;

			if (layouts.isEmpty() || setsInFocus.size() != layouts.size()) {
				layouts.clear();
				heatMapWrappers.clear();

				int heatMapWrapperID = 0;
				for (@SuppressWarnings("unused")
				DataTable set : setsInFocus) {
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
							layout, view, null, dataDomain, view, this);
					heatMapWrappers.add(heatMapWrapper);
					heatMapWrapperID++;
				}
			}

			// FIXME: Use array of relations?
			// DataTable setLeft = setsInFocus.get(0);
			// DataTable setRight = setsInFocus.get(1);
			// relations = SetComparer.compareSets(setLeft, setRight);

			for (int i = 0; i < heatMapWrappers.size(); i++) {
				HeatMapWrapper heatMapWrapper = heatMapWrappers.get(i);
				heatMapWrapper.setDataTable(setsInFocus.get(i));
			}
			setsChanged = true;
			numSetsInFocus = setsInFocus.size();

			// Select all groups in detail per default
			// HeatMapWrapper heatMapWrapper = heatMapWrappers.get(0);
			// for (Group group : heatMapWrapper.getRecordVA().getGroupList())
			// {
			// heatMapWrapper.handleGroupSelection(SelectionType.SELECTION,
			// group
			// .getGroupIndex(), true);
			// }

			setHeatMapWrapperDisplayListDirty();
		}

	}

	@Override
	public void handleMouseWheel(GL2 gl, int amount, Point wheelPoint) {
		if (amount > 0) {

			for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
				heatMapWrapper.setHeatMapsInactive();
			}

			DetailToOverviewTransition transition = (DetailToOverviewTransition) compareViewStateController
					.getState(ECompareViewStateType.DETAIL_TO_OVERVIEW_TRANSITION);

			indexOfHeatMapWrapperWithDendrogram = -1;

			compareViewStateController
					.setCurrentState(ECompareViewStateType.DETAIL_TO_OVERVIEW_TRANSITION);
			transition.init(gl);
			// view.setDisplayListDirty();
		}

	}

	@Override
	protected void setupLayouts() {

		ViewFrustum viewFrustum = view.getViewFrustum();
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
			// heatMapWrapper.setDisplayListDirty();
			setHeatMapWrapperDisplayListDirty();
		}
	}

	@Override
	public void setUseZoom(boolean useZoom) {
		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			heatMapWrapper.setUseZoom(useZoom);
			// heatMapWrapper.setDisplayListDirty();
			setHeatMapWrapperDisplayListDirty();
		}
	}

	@Override
	public void setUseFishEye(boolean useFishEye) {
		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			heatMapWrapper.setUseFishEye(useFishEye);
			// heatMapWrapper.setDisplayListDirty();
			setHeatMapWrapperDisplayListDirty();
		}
	}

}
