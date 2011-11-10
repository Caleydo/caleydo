package org.caleydo.view.matchmaker.state;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL2;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.core.event.view.SelectionCommandEvent;
import org.caleydo.core.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingManager;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.matchmaker.GLMatchmaker;
import org.caleydo.view.matchmaker.HeatMapWrapper;
import org.caleydo.view.matchmaker.SetBar;
import org.caleydo.view.matchmaker.rendercommand.RenderCommandFactory;

import com.jogamp.opengl.util.awt.TextRenderer;

public abstract class ACompareViewStateStatic extends ACompareViewState {

	public ACompareViewStateStatic(GLMatchmaker view, int viewID,
			TextRenderer textRenderer, TextureManager textureManager,
			PickingManager pickingManager, GLMouseListener glMouseListener,
			SetBar setBar, RenderCommandFactory renderCommandFactory,
			ATableBasedDataDomain dataDomain, DragAndDropController dragAndDropController,

			CompareViewStateController compareViewStateController) {
		super(view, viewID, textRenderer, textureManager, pickingManager,
				glMouseListener, setBar, renderCommandFactory, dataDomain,
				dragAndDropController, compareViewStateController);
	}

	@Override
	public void setTablesToCompare(ArrayList<DataContainer> setsToCompare) {
		setBar.setTables(setsToCompare);
	}

	@Override
	public void handlePickingEvents(PickingType ePickingType, PickingMode pickingMode,
			int externalID, Pick pick, boolean isControlPressed) {
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
				break;

			default:
				return;

			}

			// FIXME: This is not ok! Probably this view should use its own
			// selection manager
			RecordSelectionManager contentSelectionManager = dataDomain
					.getRecordSelectionManager();
			if (contentSelectionManager.checkStatus(selectionType, externalID)) {
				break;
			}

			contentSelectionManager.clearSelection(selectionType);
			contentSelectionManager.addToType(selectionType, externalID);

			SelectionDelta selectionDelta = contentSelectionManager.getDelta();
			SelectionUpdateEvent event = new SelectionUpdateEvent();
			event.setSender(this);
			event.setSelectionDelta((SelectionDelta) selectionDelta);
			// event.setInfo(getShortInfoLocal());
			eventPublisher.triggerEvent(event);

			// SelectionCommandEvent selectionCommandEvent = new
			// SelectionCommandEvent();
			// selectionCommandEvent.setCategory(EIDCategory.GENE);
			// selectionCommandEvent.setSelectionCommand(new
			// SelectionCommand(ESelectionCommandType.CLEAR, selectionType));
			// eventPublisher.triggerEvent(selectionCommandEvent);
			//
			// SelectionDelta selectionDelta = new
			// SelectionDelta(EIDType.EXPRESSION_INDEX);
			// selectionDelta.addSelection(externalID, selectionType);
			// SelectionUpdateEvent event = new SelectionUpdateEvent();
			// event.setSender(this);
			// event.setSelectionDelta((SelectionDelta) selectionDelta);
			// eventPublisher.triggerEvent(event);

			setHeatMapWrapperSelectionDisplayListDirty();
			break;

		case COMPARE_SET_BAR_ITEM_SELECTION:
			setBar.handleSetBarItemSelection(externalID, pickingMode, pick);
			break;

		case COMPARE_RIBBON_SELECTION:

			if (leftHeatMapWrapperToDetailBands != null) {

				DetailBand activeDetailBand = null;
				for (ArrayList<DetailBand> detailBands : leftHeatMapWrapperToDetailBands
						.values()) {
					for (DetailBand detailBand : detailBands) {
						if (externalID == detailBand.getBandID()) {
							activeDetailBand = detailBand;
							break;
						}
					}
				}

				if (activeDetailBand == null)
					break;

				SelectionCommandEvent selectionCommandEvent = new SelectionCommandEvent();
				selectionCommandEvent.tableIDCategory(dataDomain.getRecordIDCategory());
				selectionCommandEvent.setSelectionCommand(new SelectionCommand(
						ESelectionCommandType.CLEAR, ACTIVE_HEATMAP_SELECTION_TYPE));
				eventPublisher.triggerEvent(selectionCommandEvent);

				SelectionDelta bandSelectionDelta = new SelectionDelta(
						dataDomain.getRecordIDType());

				for (Integer recordID : activeDetailBand.getContentIDs())
					bandSelectionDelta.addSelection(recordID,
							ACTIVE_HEATMAP_SELECTION_TYPE);

				SelectionUpdateEvent selectionUpdateEvent = new SelectionUpdateEvent();
				selectionUpdateEvent.setSender(this);
				selectionUpdateEvent
						.setSelectionDelta((SelectionDelta) bandSelectionDelta);
				eventPublisher.triggerEvent(selectionUpdateEvent);
			}
			break;

		case COMPARE_SELECTION_WINDOW_SELECTION:
		case COMPARE_SELECTION_WINDOW_ARROW_LEFT_SELECTION:
		case COMPARE_SELECTION_WINDOW_ARROW_RIGHT_SELECTION:
			setBar.handleSetBarSelectionWindowSelection(externalID, ePickingType,
					pickingMode, pick);
			break;
		}

		handleStateSpecificPickingEvents(ePickingType, pickingMode, externalID, pick,
				isControlPressed);
	}

	@Override
	public int getNumSetsInFocus() {
		return numSetsInFocus;
	}

	@Override
	public boolean isInitialized() {
		return isInitialized;
	}

	@Override
	public void handleContentGroupListUpdate(int tableID, RecordGroupList contentGroupList) {
		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			if (heatMapWrapper.getTable().getID() == tableID) {
				heatMapWrapper.handleContentGroupListUpdate(contentGroupList);
				setHeatMapWrapperDisplayListDirty();
				return;
			}
		}
	}

	@Override
	public void handleReplaceRecordVA(int tableID, String dataDomain, String vaType) {

		// FIXME: we should not destroy all the heat map wrappers when a
		// recordVA is handled
		setTablesInFocus(setBar.getTablesInFocus());
	}

	protected void renderOverviewLineSelections(GL2 gl) {
		RecordSelectionManager contentSelectionManager = heatMapWrappers.get(0)
				.getContentSelectionManager();

		ArrayList<SelectionType> selectionTypes = new ArrayList<SelectionType>();

		selectionTypes.add(SelectionType.MOUSE_OVER);
		selectionTypes.add(SelectionType.SELECTION);
		selectionTypes.add(ACTIVE_HEATMAP_SELECTION_TYPE);

		// for (SelectionType selectionType :
		// contentSelectionManager.getSelectionTypes()) {
		// if (selectionType.isManaged()) {
		// selectionTypes.add(selectionType);
		// break;
		// }
		// }

		float z = 0;
		for (int i = 0; i < heatMapWrappers.size() - 1; i++) {

			HeatMapWrapper heatMapWrapper = heatMapWrappers.get(i);

			for (SelectionType selectionType : selectionTypes) {

				for (Integer recordID : contentSelectionManager
						.getElements(selectionType)) {

					gl.glPushAttrib(GL2.GL_LINE_BIT);

					gl.glPushName(pickingManager.getPickingID(viewID,
							PickingType.POLYLINE_SELECTION, recordID));

					z = setRelationColor(gl, heatMapWrappers.get(0), recordID, true);
					HashMap<Integer, ArrayList<Vec3f>> map = recordIDToIndividualLines
							.get(heatMapWrapper);
					if (map != null) {

						ArrayList<Vec3f> points = map.get(recordID);
						if (points == null)
							continue;

						for (Vec3f point : points)
							point.setZ(z);

						renderSingleCurve(gl, points, recordID,
								40 + (int) (20 * Math.random()));
					}

					gl.glPopName();

					gl.glPopAttrib();

				}
			}
		}

		// ContentSelectionManager contentSelectionManager =
		// heatMapWrappers.get(0)
		// .getContentSelectionManager();
		// ArrayList<SelectionType> selectionTypes = new
		// ArrayList<SelectionType>();
		// selectionTypes.add(ACTIVE_HEATMAP_SELECTION_TYPE);
		// selectionTypes.add(SelectionType.MOUSE_OVER);
		// selectionTypes.add(SelectionType.SELECTION);
		//
		// for (int i = 0; i < heatMapWrappers.size() - 1; i++) {
		//
		// HeatMapWrapper heatMapWrapper = heatMapWrappers.get(i);
		//
		// for (SelectionType selectionType : selectionTypes) {
		//
		// for (Integer recordID : contentSelectionManager
		// .getElements(selectionType)) {
		// gl.glPushAttrib(GL2.GL_LINE_BIT);
		// setRelationColor(gl, heatMapWrappers.get(0), recordID, true);
		// HashMap<Integer, ArrayList<Vec3f>> map = recordIDToIndividualLines
		// .get(heatMapWrapper);
		// if (map != null) {
		// renderSingleCurve(gl, map.get(recordID), recordID,
		// 40 + (int) (20 * Math.random()));
		// }
		//
		// gl.glPopAttrib();
		// }
		// }
		// }
	}

	protected abstract void renderSelections(GL2 gl);

	public abstract void handleStateSpecificPickingEvents(PickingType ePickingType,
			PickingMode pickingMode, int externalID, Pick pick, boolean isControlPressed);
}
