package org.caleydo.view.matchmaker.state;

import gleem.linalg.Vec3f;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.IDCategory;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.virtualarray.group.ContentGroupList;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.ContextMenu;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.matchmaker.GLMatchmaker;
import org.caleydo.view.matchmaker.SetBar;
import org.caleydo.view.matchmaker.rendercommand.RenderCommandFactory;

import com.jogamp.opengl.util.awt.TextRenderer;

public class CompareViewStateController {

	private HashMap<ECompareViewStateType, ACompareViewState> hashStates;

	private ACompareViewState currentState;

	public CompareViewStateController(GLMatchmaker view, int viewID,
			TextRenderer textRenderer, TextureManager textureManager,
			PickingManager pickingManager, GLMouseListener glMouseListener,
			ContextMenu contextMenu, ASetBasedDataDomain dataDomain) {
		DragAndDropController dragAndDropController = new DragAndDropController(view);
		SetBar setBar = new SetBar(viewID, pickingManager, textRenderer,
				dragAndDropController, glMouseListener, view, contextMenu, textureManager);
		setBar.setPosition(new Vec3f(0.0f, 0.0f, 0.01f));
		RenderCommandFactory renderCommandFactory = new RenderCommandFactory(viewID,
				pickingManager, textureManager, textRenderer);

		hashStates = new HashMap<ECompareViewStateType, ACompareViewState>();

		hashStates.put(ECompareViewStateType.DETAIL_VIEW, new DetailViewState(view,
				viewID, textRenderer, textureManager, pickingManager, glMouseListener,
				setBar, renderCommandFactory, dataDomain, dragAndDropController, this));
		hashStates.put(ECompareViewStateType.OVERVIEW, new OverviewState(view, viewID,
				textRenderer, textureManager, pickingManager, glMouseListener, setBar,
				renderCommandFactory, dataDomain, dragAndDropController, this));
		hashStates.put(ECompareViewStateType.OVERVIEW_TO_DETAIL_TRANSITION,
				new OverviewToDetailTransition(view, viewID, textRenderer,
						textureManager, pickingManager, glMouseListener, setBar,
						renderCommandFactory, dataDomain, dragAndDropController, this));
		hashStates.put(ECompareViewStateType.DETAIL_TO_OVERVIEW_TRANSITION,
				new DetailToOverviewTransition(view, viewID, textRenderer,
						textureManager, pickingManager, glMouseListener, setBar,
						renderCommandFactory, dataDomain, dragAndDropController, this));

		currentState = hashStates.get(ECompareViewStateType.OVERVIEW);
		setBar.setViewState(currentState);
	}

	public ACompareViewState getState(ECompareViewStateType stateType) {
		return hashStates.get(stateType);
	}

	public void setCurrentState(ECompareViewStateType stateType) {
		currentState = hashStates.get(stateType);
	}

	public void init(GL2 gl) {
		currentState.init(gl);
	}

	public void executeDrawingPreprocessing(GL2 gl, boolean isDisplayListDirty) {
		currentState.executeDrawingPreprocessing(gl, isDisplayListDirty);
	}

	public void drawDisplayListElements(GL2 gl) {
		currentState.buildDisplayList(gl);
	}

	public void drawActiveElements(GL2 gl) {
		currentState.drawActiveElements(gl);
	}

	public void handlePickingEvents(EPickingType ePickingType, EPickingMode pickingMode,
			int iExternalID, Pick pick, boolean isControlPressed) {
		currentState.handlePickingEvents(ePickingType, pickingMode, iExternalID, pick,
				isControlPressed);
	}

	public void setSetsToCompare(ArrayList<ISet> setsToCompare) {
		currentState.setSetsToCompare(setsToCompare);
	}

	public void duplicateSetBarItem(int itemID) {
		currentState.duplicateSetBarItem(itemID);
	}

	public void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {
		currentState.handleSelectionUpdate(selectionDelta, scrollToSelection, info);
	}

	public void handleSelectionCommand(IDCategory category,
			SelectionCommand selectionCommand) {
		currentState.handleSelectionCommand(category, selectionCommand);
	}

	public void handleAdjustPValue() {

		currentState.adjustPValue();
	}

	public void handleMouseWheel(GL2 gl, int amount, Point wheelPoint) {
		currentState.handleMouseWheel(gl, amount, wheelPoint);
	}

	public void handleReplaceContentVA(int setID, String dataDomain, String vaType) {
		currentState.handleReplaceContentVA(setID, dataDomain, vaType);
	}

	public void handleClearSelections() {
		currentState.handleClearSelections();
	}

	public void setUseSorting(boolean useSorting) {
		currentState.setUseSorting(useSorting);
	}

	public void setUseZoom(boolean useZoom) {
		currentState.setUseZoom(useZoom);
	}

	public void setUseFishEye(boolean useFishEye) {
		currentState.setUseFishEye(useFishEye);
	}

	public void setBandBundling(boolean bandBundlingActive) {
		currentState.setBandBundling(bandBundlingActive);
	}

	public void handleContentGroupListUpdate(int setID, ContentGroupList contentGroupList) {
		currentState.handleContentGroupListUpdate(setID, contentGroupList);
	}

	public void handleDragging(GL2 gl) {
		currentState.handleDragging(gl);
	}

	public void setCreateSelectionTypes(boolean createSelectionTypes) {
		for (ACompareViewState state : hashStates.values()) {
			state.setCreateSelectionTypes(createSelectionTypes);
		}
	}

	public void setHideHeatMapElements(boolean hideElements) {
		currentState.setHeatMapWrapperDisplayListDirty();
	}
}
