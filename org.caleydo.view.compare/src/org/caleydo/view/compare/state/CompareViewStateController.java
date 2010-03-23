package org.caleydo.view.compare.state;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.ContentGroupList;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.ContextMenu;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.compare.GLCompare;
import org.caleydo.view.compare.SetBar;
import org.caleydo.view.compare.rendercommand.RenderCommandFactory;

import com.sun.opengl.util.j2d.TextRenderer;

public class CompareViewStateController {

	private HashMap<ECompareViewStateType, ACompareViewState> hashStates;

	private ACompareViewState currentState;

	public CompareViewStateController(GLCompare view, int viewID,
			TextRenderer textRenderer, TextureManager textureManager,
			PickingManager pickingManager, GLMouseListener glMouseListener,
			ContextMenu contextMenu, EDataDomain dataDomain, IUseCase useCase) {
		DragAndDropController dragAndDropController = new DragAndDropController(
				view);
		SetBar setBar = new SetBar(viewID, pickingManager, textRenderer,
				dragAndDropController, glMouseListener, view, contextMenu,
				textureManager);
		RenderCommandFactory renderCommandFactory = new RenderCommandFactory(
				viewID, pickingManager, textureManager, textRenderer);

		hashStates = new HashMap<ECompareViewStateType, ACompareViewState>();

		hashStates.put(ECompareViewStateType.DETAIL_VIEW, new DetailViewState(
				view, viewID, textRenderer, textureManager, pickingManager,
				glMouseListener, setBar, renderCommandFactory, dataDomain,
				useCase, dragAndDropController, this));
		hashStates.put(ECompareViewStateType.OVERVIEW, new OverviewState(view,
				viewID, textRenderer, textureManager, pickingManager,
				glMouseListener, setBar, renderCommandFactory, dataDomain,
				useCase, dragAndDropController, this));
		hashStates.put(ECompareViewStateType.OVERVIEW_TO_DETAIL_TRANSITION,
				new OverviewToDetailTransition(view, viewID, textRenderer,
						textureManager, pickingManager, glMouseListener,
						setBar, renderCommandFactory, dataDomain, useCase,
						dragAndDropController, this));
		hashStates.put(ECompareViewStateType.DETAIL_TO_OVERVIEW_TRANSITION,
				new DetailToOverviewTransition(view, viewID, textRenderer,
						textureManager, pickingManager, glMouseListener,
						setBar, renderCommandFactory, dataDomain, useCase,
						dragAndDropController, this));

		currentState = hashStates.get(ECompareViewStateType.OVERVIEW);
		setBar.setViewState(currentState);

	}

	public ACompareViewState getState(ECompareViewStateType stateType) {
		return hashStates.get(stateType);
	}

	public void setCurrentState(ECompareViewStateType stateType) {
		currentState = hashStates.get(stateType);
	}

	public void init(GL gl) {
		currentState.init(gl);
	}

	public void executeDrawingPreprocessing(GL gl, boolean isDisplayListDirty) {
		currentState.executeDrawingPreprocessing(gl, isDisplayListDirty);
	}

	public void drawDisplayListElements(GL gl) {
		currentState.buildDisplayList(gl);
	}

	public void drawActiveElements(GL gl) {
		currentState.drawActiveElements(gl);
	}

	public void handlePickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick,
			boolean isControlPressed) {
		currentState.handlePickingEvents(ePickingType, pickingMode,
				iExternalID, pick, isControlPressed);
	}

	public void setSetsToCompare(ArrayList<ISet> setsToCompare) {
		currentState.setSetsToCompare(setsToCompare);
	}

	public void duplicateSetBarItem(int itemID) {
		currentState.duplicateSetBarItem(itemID);
	}

	public void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {
		currentState.handleSelectionUpdate(selectionDelta, scrollToSelection,
				info);
	}

	public void handleSelectionCommand(EIDCategory category,
			SelectionCommand selectionCommand) {
		currentState.handleSelectionCommand(category, selectionCommand);
	}

	public void handleAdjustPValue() {

		currentState.adjustPValue();
	}

	public void handleMouseWheel(GL gl, int amount, Point wheelPoint) {
		currentState.handleMouseWheel(gl, amount, wheelPoint);
	}

	public void handleReplaceContentVA(int setID, EIDCategory idCategory,
			ContentVAType vaType) {
		currentState.handleReplaceContentVA(setID, idCategory, vaType);
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

	public void handleContentGroupListUpdate(int setID,
			ContentGroupList contentGroupList) {
		currentState.handleContentGroupListUpdate(setID, contentGroupList);
	}

}
