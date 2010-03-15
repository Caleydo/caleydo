package org.caleydo.view.compare.state;

import java.util.ArrayList;

import javax.media.opengl.GL;

import gleem.linalg.Vec3f;

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
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.compare.GLCompare;
import org.caleydo.view.compare.SetBar;
import org.caleydo.view.compare.rendercommand.RenderCommandFactory;

import com.sun.opengl.util.j2d.TextRenderer;

public class OverviewState extends ACompareViewState {

	public OverviewState(GLCompare view, int viewID,
			TextRenderer textRenderer, TextureManager textureManager,
			PickingManager pickingManager, GLMouseListener glMouseListener,
			SetBar setBar, RenderCommandFactory renderCommandFactory,
			EDataDomain dataDomain, IUseCase useCase,
			DragAndDropController dragAndDropController) {

		super(view, viewID, textRenderer, textureManager, pickingManager,
				glMouseListener, setBar, renderCommandFactory, dataDomain,
				useCase, dragAndDropController);
		this.setBar.setPosition(new Vec3f(0.0f, 0.0f, 0.0f));
	}

	@Override
	public void drawActiveElements(GL gl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawDisplayListElements(GL gl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void executeDrawingPreprocessing(GL gl, boolean isDisplayListDirty) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handlePickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick,
			boolean isControlPressed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GL gl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSetsToCompare(ArrayList<ISet> setsToCompare) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ECompareViewStateType getStateType() {
		return ECompareViewStateType.OVERVIEW;
	}

	@Override
	public void duplicateSetBarItem(int itemID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSetsInFocus(ArrayList<ISet> setsInFocus) {
		// TODO Auto-generated method stub
		
	}

}
