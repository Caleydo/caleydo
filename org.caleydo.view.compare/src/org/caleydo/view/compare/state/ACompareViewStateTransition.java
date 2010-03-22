package org.caleydo.view.compare.state;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Calendar;

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
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.compare.GLCompare;
import org.caleydo.view.compare.SetBar;
import org.caleydo.view.compare.rendercommand.RenderCommandFactory;

import com.sun.opengl.util.j2d.TextRenderer;

public abstract class ACompareViewStateTransition extends ACompareViewState {

	private double previousTimeStamp;
	/**
	 * Determines, whether the animation has been started or not.
	 */
	protected boolean animationStarted;
	/**
	 * Determines the duration of the animation.
	 */
	protected float animationDuration;

	public ACompareViewStateTransition(GLCompare view, int viewID,
			TextRenderer textRenderer, TextureManager textureManager,
			PickingManager pickingManager, GLMouseListener glMouseListener,
			SetBar setBar, RenderCommandFactory renderCommandFactory,
			EDataDomain dataDomain, IUseCase useCase,
			DragAndDropController dragAndDropController,
			CompareViewStateController compareViewStateController) {
		super(view, viewID, textRenderer, textureManager, pickingManager,
				glMouseListener, setBar, renderCommandFactory, dataDomain,
				useCase, dragAndDropController, compareViewStateController);
		previousTimeStamp = 0;
		animationStarted = false;
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
	public int getNumSetsInFocus() {
		return 0;
	}

	@Override
	public void handleContentGroupListUpdate(int setID,
			ContentGroupList contentGroupList) {
	}

	@Override
	public void handleMouseWheel(GL gl, int amount, Point wheelPoint) {
	}

	@Override
	public void handlePickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick,
			boolean isControlPressed) {
	}

	@Override
	public void handleReplaceContentVA(int setID, EIDCategory idCategory,
			ContentVAType vaType) {
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
	public void init(GL gl) {
		isInitialized = true;
	}

	@Override
	public boolean isInitialized() {
		return false;
	}

	@Override
	public void setSetsInFocus(ArrayList<ISet> setsInFocus) {

	}

	@Override
	public void setSetsToCompare(ArrayList<ISet> setsToCompare) {

	}

	@Override
	public void setupLayouts() {
		double currentTimeStamp = Calendar.getInstance().getTimeInMillis();

		if (!animationStarted)
			setupLayouts(0);
		else {
			double timePassed = (currentTimeStamp - previousTimeStamp) / 1000;
			setupLayouts(timePassed);
		}
		previousTimeStamp = currentTimeStamp;
	}
	
	@Override
	public void handleDragging(GL gl) {
		
	}

	// protected abstract void drawActiveElements(GL gl, double timePassed);
	//
	// protected abstract void drawDisplayListElements(GL gl, double
	// timePassed);

	protected abstract void setupLayouts(double timePassed);

}
