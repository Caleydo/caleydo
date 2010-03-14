package org.caleydo.view.compare.state;

import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
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
				dragAndDropController, glMouseListener, view, contextMenu);
		RenderCommandFactory renderCommandFactory = new RenderCommandFactory(
				viewID, pickingManager, textureManager, textRenderer);

		hashStates = new HashMap<ECompareViewStateType, ACompareViewState>();

		hashStates.put(ECompareViewStateType.DETAIL_VIEW, new DetailViewState(
				view, viewID, textRenderer, textureManager, pickingManager,
				glMouseListener, setBar, renderCommandFactory, dataDomain,
				useCase, dragAndDropController));
		hashStates.put(ECompareViewStateType.OVERVIEW, new OverviewState(view,
				viewID, textRenderer, textureManager, pickingManager,
				glMouseListener, setBar, renderCommandFactory, dataDomain,
				useCase, dragAndDropController));

		currentState = hashStates.get(ECompareViewStateType.DETAIL_VIEW);

	}

	public void init(GL gl) {
		currentState.init(gl);
	}

	public void executeDrawingPreprocessing(GL gl, boolean isDisplayListDirty) {
		currentState.executeDrawingPreprocessing(gl, isDisplayListDirty);
	}

	public void drawDisplayListElements(GL gl) {
		currentState.drawDisplayListElements(gl);
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

}
