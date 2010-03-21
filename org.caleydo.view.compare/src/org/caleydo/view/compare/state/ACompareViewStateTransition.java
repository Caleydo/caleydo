package org.caleydo.view.compare.state;

import java.awt.Point;
import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.EIDCategory;
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
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.compare.GLCompare;
import org.caleydo.view.compare.SetBar;
import org.caleydo.view.compare.rendercommand.RenderCommandFactory;

import com.sun.opengl.util.j2d.TextRenderer;

public abstract class ACompareViewStateTransition extends ACompareViewState {

	public ACompareViewStateTransition(GLCompare view, int viewID,
			TextRenderer textRenderer, TextureManager textureManager,
			PickingManager pickingManager, GLMouseListener glMouseListener,
			SetBar setBar, RenderCommandFactory renderCommandFactory,
			EDataDomain dataDomain, IUseCase useCase,
			DragAndDropController dragAndDropController,
			CompareViewStateController compareViewStateController) {

	}

	@Override
	public void adjustPValue() {
	}


	@Override
	public void duplicateSetBarItem(int itemID) {

	}

	@Override
	public int getMaxSetsInFocus() {
		return 0;
	}

	@Override
	public int getMinSetsInFocus() {
		return 0;
	}

	@Override
	public void handleMouseWheel(GL gl, int amount, Point wheelPoint) {
		
	}

	@Override
	public void handleSelectionCommand(EIDCategory category,
			SelectionCommand selectionCommand) {
	}

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {
	}

	@Override
	public void handleStateSpecificPickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick,
			boolean isControlPressed) {
	}

	@Override
	public void init(GL gl) {

	}

	@Override
	public void setSetsInFocus(ArrayList<ISet> setsInFocus) {

	}

}
