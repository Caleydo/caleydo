package org.caleydo.view.matchmaker.state;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.selection.ContentGroupList;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.matchmaker.GLMatchmaker;
import org.caleydo.view.matchmaker.HeatMapWrapper;
import org.caleydo.view.matchmaker.SetBar;
import org.caleydo.view.matchmaker.rendercommand.RenderCommandFactory;

import com.sun.opengl.util.j2d.TextRenderer;

public abstract class ACompareViewStateStatic extends ACompareViewState {

	public ACompareViewStateStatic(GLMatchmaker view, int viewID,
			TextRenderer textRenderer, TextureManager textureManager,
			PickingManager pickingManager, GLMouseListener glMouseListener,
			SetBar setBar, RenderCommandFactory renderCommandFactory,
			ASetBasedDataDomain dataDomain, DragAndDropController dragAndDropController,

			CompareViewStateController compareViewStateController) {
		super(view, viewID, textRenderer, textureManager, pickingManager,
				glMouseListener, setBar, renderCommandFactory, dataDomain,
				dragAndDropController, compareViewStateController);
	}

	public void setSetsToCompare(ArrayList<ISet> setsToCompare) {
		setBar.setSets(setsToCompare);
	}

	public void handlePickingEvents(EPickingType ePickingType, EPickingMode pickingMode,
			int iExternalID, Pick pick, boolean isControlPressed) {
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
			ContentSelectionManager contentSelectionManager = dataDomain
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

			// SelectionCommandEvent selectionCommandEvent = new
			// SelectionCommandEvent();
			// selectionCommandEvent.setCategory(EIDCategory.GENE);
			// selectionCommandEvent.setSelectionCommand(new
			// SelectionCommand(ESelectionCommandType.CLEAR, selectionType));
			// eventPublisher.triggerEvent(selectionCommandEvent);
			//
			// ISelectionDelta selectionDelta = new
			// SelectionDelta(EIDType.EXPRESSION_INDEX);
			// selectionDelta.addSelection(iExternalID, selectionType);
			// SelectionUpdateEvent event = new SelectionUpdateEvent();
			// event.setSender(this);
			// event.setSelectionDelta((SelectionDelta) selectionDelta);
			// eventPublisher.triggerEvent(event);

			setHeatMapWrapperSelectionDisplayListDirty();
			break;

		case COMPARE_SET_BAR_ITEM_SELECTION:
			setBar.handleSetBarItemSelection(iExternalID, pickingMode, pick);
			break;

		case COMPARE_RIBBON_SELECTION:

			if (leftHeatMapWrapperToDetailBands != null) {

				DetailBand activeDetailBand = null;
				for (ArrayList<DetailBand> detailBands : leftHeatMapWrapperToDetailBands
						.values()) {
					for (DetailBand detailBand : detailBands) {
						if (iExternalID == detailBand.getBandID()) {
							activeDetailBand = detailBand;
							break;
						}
					}
				}

				if (activeDetailBand == null)
					break;

				SelectionCommandEvent selectionCommandEvent = new SelectionCommandEvent();
				selectionCommandEvent.setCategory(EIDCategory.GENE);
				selectionCommandEvent.setSelectionCommand(new SelectionCommand(
						ESelectionCommandType.CLEAR, ACTIVE_HEATMAP_SELECTION_TYPE));
				eventPublisher.triggerEvent(selectionCommandEvent);

				ISelectionDelta bandSelectionDelta = new SelectionDelta(
						EIDType.EXPRESSION_INDEX);

				for (Integer contentID : activeDetailBand.getContentIDs())
					bandSelectionDelta.addSelection(contentID,
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
			setBar.handleSetBarSelectionWindowSelection(iExternalID, ePickingType,
					pickingMode, pick);
			break;
		}

		handleStateSpecificPickingEvents(ePickingType, pickingMode, iExternalID, pick,
				isControlPressed);
	}

	public int getNumSetsInFocus() {
		return numSetsInFocus;
	}

	public boolean isInitialized() {
		return isInitialized;
	}

	public void handleContentGroupListUpdate(int setID, ContentGroupList contentGroupList) {
		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			if (heatMapWrapper.getSet().getID() == setID) {
				heatMapWrapper.handleContentGroupListUpdate(contentGroupList);
				setHeatMapWrapperDisplayListDirty();
				return;
			}
		}
	}

	@Override
	public void handleReplaceContentVA(int setID, String dataDomain, ContentVAType vaType) {

		// FIXME: we should not destroy all the heat map wrappers when a
		// contentVA is handled
		setSetsInFocus(setBar.getSetsInFocus());
	}

	protected void renderOverviewLineSelections(GL gl) {
		ContentSelectionManager contentSelectionManager = heatMapWrappers.get(0)
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

				for (Integer contentID : contentSelectionManager
						.getElements(selectionType)) {

					gl.glPushAttrib(GL.GL_LINE_BIT);

					gl.glPushName(pickingManager.getPickingID(viewID,
							EPickingType.POLYLINE_SELECTION, contentID));

					z = setRelationColor(gl, heatMapWrappers.get(0), contentID, true);
					HashMap<Integer, ArrayList<Vec3f>> map = contentIDToIndividualLines
							.get(heatMapWrapper);
					if (map != null) {

						ArrayList<Vec3f> points = map.get(contentID);
						if (points == null)
							continue;

						for (Vec3f point : points)
							point.setZ(z);

						renderSingleCurve(gl, points, contentID,
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
		// for (Integer contentID : contentSelectionManager
		// .getElements(selectionType)) {
		// gl.glPushAttrib(GL.GL_LINE_BIT);
		// setRelationColor(gl, heatMapWrappers.get(0), contentID, true);
		// HashMap<Integer, ArrayList<Vec3f>> map = contentIDToIndividualLines
		// .get(heatMapWrapper);
		// if (map != null) {
		// renderSingleCurve(gl, map.get(contentID), contentID,
		// 40 + (int) (20 * Math.random()));
		// }
		//
		// gl.glPopAttrib();
		// }
		// }
		// }
	}

	protected abstract void renderSelections(GL gl);

	public abstract void handleStateSpecificPickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick, boolean isControlPressed);
}
