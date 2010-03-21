package org.caleydo.view.compare.state;

import javax.media.opengl.GL;

import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.compare.GLCompare;
import org.caleydo.view.compare.HeatMapWrapper;
import org.caleydo.view.compare.SetBar;
import org.caleydo.view.compare.rendercommand.RenderCommandFactory;

import com.sun.opengl.util.j2d.TextRenderer;

public class OverviewToDetailTransition extends ACompareViewStateTransition {

	public OverviewToDetailTransition(GLCompare view, int viewID,
			TextRenderer textRenderer, TextureManager textureManager,
			PickingManager pickingManager, GLMouseListener glMouseListener,
			SetBar setBar, RenderCommandFactory renderCommandFactory,
			EDataDomain dataDomain, IUseCase useCase,
			DragAndDropController dragAndDropController,
			CompareViewStateController compareViewStateController) {
		super(view, viewID, textRenderer, textureManager, pickingManager,
				glMouseListener, setBar, renderCommandFactory, dataDomain,
				useCase, dragAndDropController, compareViewStateController);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void drawActiveElements(GL gl) {
		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			heatMapWrapper.drawRemoteItems(gl, glMouseListener, pickingManager);
		}

	}

	@Override
	public void drawDisplayListElements(GL gl) {
		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			heatMapWrapper.drawLocalItems(gl, textureManager, pickingManager,
					glMouseListener, viewID);
		}

		IViewFrustum viewFrustum = view.getViewFrustum();

		setBar.setWidth(viewFrustum.getWidth());
		setBar.render(gl);

		// for (int i = 0; i < heatMapWrappers.size() - 1; i++) {
		// renderTree(gl, heatMapWrappers.get(i), heatMapWrappers.get(i + 1));
		// renderOverviewRelations(gl, heatMapWrappers.get(i), heatMapWrappers
		// .get(i + 1));
		// }

	}

	@Override
	protected void setupLayouts(double timePassed) {
		// TODO Auto-generated method stub

	}

	// FIXME: Use set later on instead of itemOffset
	public void initTransition(GL gl, int itemOffset) {
		if (!isInitialized)
			init(gl);

		ACompareViewState detailViewState = compareViewStateController
				.getState(ECompareViewStateType.DETAIL_VIEW);

		setBar.setViewState(detailViewState);
		setBar.setMaxSelectedItems(detailViewState.getMaxSetsInFocus());
		setBar.setMinSelectedItems(detailViewState.getMinSetsInFocus());
		setBar.setWindowSize(detailViewState.getNumSetsInFocus());
		setBar.increaseLowestItemIndex(itemOffset);
		if (!detailViewState.isInitialized()) {
			detailViewState.init(gl);
		}
		detailViewState.setSetsInFocus(setBar.getSetsInFocus());

		compareViewStateController
				.setCurrentState(ECompareViewStateType.DETAIL_VIEW);
		view.setDisplayListDirty();

	}

	@Override
	public ECompareViewStateType getStateType() {
		return ECompareViewStateType.OVERVIEW_TO_DETAIL_TRANSITION;
	}

}
