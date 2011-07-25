package org.caleydo.view.matchmaker.state;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.picking.PickingMode;
import org.caleydo.core.manager.picking.PickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.matchmaker.GLMatchmaker;
import org.caleydo.view.matchmaker.HeatMapWrapper;
import org.caleydo.view.matchmaker.SetBar;
import org.caleydo.view.matchmaker.layout.AHeatMapLayout;
import org.caleydo.view.matchmaker.layout.HeatMapLayoutOverviewLeft;
import org.caleydo.view.matchmaker.layout.HeatMapLayoutOverviewMid;
import org.caleydo.view.matchmaker.layout.HeatMapLayoutOverviewRight;
import org.caleydo.view.matchmaker.rendercommand.RenderCommandFactory;

import com.jogamp.opengl.util.awt.TextRenderer;

public class OverviewState extends ACompareViewStateStatic {

	private static final float HEATMAP_WRAPPER_OVERVIEW_GAP_PORTION = 0.8f;
	private static final float HEATMAP_WRAPPER_SPACE_PORTION = 0.7f;

	public OverviewState(GLMatchmaker view, int viewID, TextRenderer textRenderer,
			TextureManager textureManager, PickingManager pickingManager,
			GLMouseListener glMouseListener, SetBar setBar,
			RenderCommandFactory renderCommandFactory, ATableBasedDataDomain dataDomain,
			DragAndDropController dragAndDropController,
			CompareViewStateController compareViewStateController) {

		super(view, viewID, textRenderer, textureManager, pickingManager,
				glMouseListener, setBar, renderCommandFactory, dataDomain,
				dragAndDropController, compareViewStateController);
		numSetsInFocus = 4;
	}

	@Override
	public void drawActiveElements(GL2 gl) {

		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			heatMapWrapper.drawRemoteItems(gl, glMouseListener, pickingManager);
		}
	}

	@Override
	public void buildDisplayList(GL2 gl) {

		if (heatMapWrappers.size() < 2)
			return;

		if (isHeatMapWrapperDisplayListDirty) {
			isHeatMapWrapperDisplayListDirty = false;
			// isHeatMapWrapperSelectionDisplayListDirty = false;

			gl.glNewList(heatMapWrapperDisplayListIndex, GL2.GL_COMPILE);

			gl.glEnable(GL2.GL_BLEND);
			gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

			for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
				heatMapWrapper.drawLocalItems(gl, textureManager, pickingManager,
						glMouseListener, viewID);
			}

			recordIDToIndividualLines.clear();
			leftHeatMapWrapperToDetailBands = new HashMap<HeatMapWrapper, ArrayList<DetailBand>>();
			detailBandID = 0;

			for (int i = 0; i < heatMapWrappers.size() - 1; i++) {

				renderIndiviudalLineRelations(gl, heatMapWrappers.get(i),
						heatMapWrappers.get(i + 1));

				if (bandBundlingActive) {

					// TODO: if we put the heatmapwarpper combination with the
					// calculated detail bands in
					// a hash map we have to calculate it only once!
					calculateDetailBands(heatMapWrappers.get(i),
							heatMapWrappers.get(i + 1), false);

					renderOverviewToDetailBandRelations(gl, heatMapWrappers.get(i), true);
					renderOverviewToDetailBandRelations(gl, heatMapWrappers.get(i + 1),
							false);
					renderDetailBandRelations(gl, heatMapWrappers.get(i),
							heatMapWrappers.get(i + 1));
				}

				// renderStraightLineRelation(gl, heatMapWrappers.get(i),
				// heatMapWrappers.get(i+1));
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
		renderOverviewLineSelections(gl);
		renderHeatMapOverviewSelections(gl);
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
	public ECompareViewStateType getStateType() {
		return ECompareViewStateType.OVERVIEW;
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
			heatMapWrapper.getOverview().updateHeatMapTextures(
					heatMapWrapper.getContentSelectionManager());
		}

		setAllDisplayListsDirty();
	}

	@Override
	public void adjustPValue() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getMaxSetsInFocus() {
		return 6;
	}

	@Override
	public int getMinSetsInFocus() {
		return 2;
	}

	@Override
	public void handleStateSpecificPickingEvents(PickingType ePickingType,
			PickingMode pickingMode, int externalID, Pick pick, boolean isControlPressed) {

		HeatMapWrapper selectedHeatMapWrapper = null;

		switch (pickingMode) {
		case CLICKED:
			switch (ePickingType) {
			// FIXME: That way only the first 2 heatmapwrappers can be used
			case COMPARE_GROUP_1_SELECTION:
				selectedHeatMapWrapper = heatMapWrappers.get(0);
				break;
			case COMPARE_GROUP_2_SELECTION:
				selectedHeatMapWrapper = heatMapWrappers.get(1);
				break;
			case COMPARE_GROUP_3_SELECTION:
				selectedHeatMapWrapper = heatMapWrappers.get(2);
				break;
			case COMPARE_GROUP_4_SELECTION:
				selectedHeatMapWrapper = heatMapWrappers.get(3);
				break;
			case COMPARE_GROUP_5_SELECTION:
				selectedHeatMapWrapper = heatMapWrappers.get(4);
				break;
			case COMPARE_GROUP_6_SELECTION:
				selectedHeatMapWrapper = heatMapWrappers.get(5);
				break;
			}
			break;
		}

		if (selectedHeatMapWrapper != null) {
			for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
				heatMapWrapper.setHeatMapsInactive();
			}

			selectedHeatMapWrapper.setHeatMapActive(externalID, createSelectionTypes);
			if (createSelectionTypes) {
				setHeatMapWrapperDisplayListDirty();
			} else {
				setHeatMapWrapperSelectionDisplayListDirty();
			}
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

		setAllDisplayListsDirty();
	}

	@Override
	public void setDataTablesInFocus(ArrayList<DataTable> setsInFocus) {
		// FIXME: Maybe we can put this in the base class.

		if (setsInFocus.size() >= getMinSetsInFocus()
				&& setsInFocus.size() <= getMaxSetsInFocus()) {

			this.setsInFocus = setsInFocus;

			if (layouts.isEmpty() || setsInFocus.size() != layouts.size()) {
				layouts.clear();
				heatMapWrappers.clear();

				int heatMapWrapperID = 0;
				for (@SuppressWarnings("unused") DataTable set : setsInFocus) {
					AHeatMapLayout layout = null;
					if (heatMapWrapperID == 0) {
						layout = new HeatMapLayoutOverviewLeft(renderCommandFactory);
					} else if (heatMapWrapperID == setsInFocus.size() - 1) {
						layout = new HeatMapLayoutOverviewRight(renderCommandFactory);
					} else {
						layout = new HeatMapLayoutOverviewMid(renderCommandFactory);
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

			setHeatMapWrapperDisplayListDirty();
		}
	}

	@Override
	public void handleMouseWheel(GL2 gl, int amount, Point wheelPoint) {
		if (amount < 0) {

			for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
				heatMapWrapper.setHeatMapsInactive();
			}

			OverviewToDetailTransition transition = (OverviewToDetailTransition) compareViewStateController
					.getState(ECompareViewStateType.OVERVIEW_TO_DETAIL_TRANSITION);

			float[] wheelPointWorldCoordinates = GLCoordinateUtils
					.convertWindowCoordinatesToWorldCoordinates(gl, wheelPoint.x,
							wheelPoint.y);

			int itemOffset = 0;
			for (int i = 0; i < layouts.size() - 1; i++) {

				if ((i == layouts.size() - 2)
						&& (wheelPointWorldCoordinates[0] >= layouts.get(i).getPosition()
								.x())) {
					itemOffset = i;
					break;
				}

				if ((wheelPointWorldCoordinates[0] >= layouts.get(i).getPosition().x())
						&& (wheelPointWorldCoordinates[0] <= layouts.get(i + 1)
								.getPosition().x()
								+ (layouts.get(i + 1).getWidth() / 2.0f))) {
					itemOffset = i;
					break;
				}
			}
			compareViewStateController
					.setCurrentState(ECompareViewStateType.OVERVIEW_TO_DETAIL_TRANSITION);

			transition.initTransition(gl, itemOffset);
			// view.setDisplayListDirty();
		}

	}

	@Override
	protected void setupLayouts() {

		ViewFrustum viewFrustum = view.getViewFrustum();
		float setBarHeight = setBar.getHeight();
		float heatMapWrapperPosY = setBar.getPosition().y() + setBarHeight;

		float heatMapWrapperPosX = 0.0f;

		float spaceForHeatMapWrapperOverviews = (1.0f - HEATMAP_WRAPPER_OVERVIEW_GAP_PORTION)
				* viewFrustum.getWidth();
		float heatMapWrapperWidth = HEATMAP_WRAPPER_SPACE_PORTION
				* viewFrustum.getWidth() / (float) heatMapWrappers.size();
		int numTotalExperiments = 0;
		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			numTotalExperiments += heatMapWrapper.getDataTable()
					.getDimensionData(DataTable.DIMENSION).getDimensionVA().size();
		}
		float heatMapWrapperGapWidth = (1 - HEATMAP_WRAPPER_SPACE_PORTION)
				* viewFrustum.getWidth() / (float) (heatMapWrappers.size() - 1);

		for (int i = 0; i < heatMapWrappers.size(); i++) {
			HeatMapWrapper heatMapWrapper = heatMapWrappers.get(i);
			AHeatMapLayout layout = layouts.get(i);
			int numExperiments = heatMapWrapper.getDataTable()
					.getDimensionData(DataTable.DIMENSION).getDimensionVA().size();
			// TODO: Maybe get info in layout from heatmapwrapper
			layout.setTotalSpaceForAllHeatMapWrappers(spaceForHeatMapWrapperOverviews);
			layout.setNumExperiments(numExperiments);
			layout.setNumTotalExperiments(numTotalExperiments);

			layout.setLayoutParameters(heatMapWrapperPosX, heatMapWrapperPosY,
					viewFrustum.getHeight() - setBarHeight, heatMapWrapperWidth);
			layout.setHeatMapWrapper(heatMapWrapper);

			heatMapWrapperPosX += heatMapWrapperWidth + heatMapWrapperGapWidth;
		}

	}
}
