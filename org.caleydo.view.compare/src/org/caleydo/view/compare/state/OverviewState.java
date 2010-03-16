package org.caleydo.view.compare.state;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.SetComparer;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.compare.GLCompare;
import org.caleydo.view.compare.HeatMapWrapper;
import org.caleydo.view.compare.SetBar;
import org.caleydo.view.compare.layout.AHeatMapLayout;
import org.caleydo.view.compare.layout.HeatMapLayoutOverviewLeft;
import org.caleydo.view.compare.layout.HeatMapLayoutOverviewMid;
import org.caleydo.view.compare.layout.HeatMapLayoutOverviewRight;
import org.caleydo.view.compare.rendercommand.RenderCommandFactory;
import org.caleydo.view.compare.renderer.CompareConnectionBandRenderer;
import org.caleydo.view.compare.renderer.ICompareConnectionRenderer;

import com.sun.opengl.util.j2d.TextRenderer;

public class OverviewState extends ACompareViewState {

	private ICompareConnectionRenderer compareConnectionRenderer;

	public OverviewState(GLCompare view, int viewID, TextRenderer textRenderer,
			TextureManager textureManager, PickingManager pickingManager,
			GLMouseListener glMouseListener, SetBar setBar,
			RenderCommandFactory renderCommandFactory, EDataDomain dataDomain,
			IUseCase useCase, DragAndDropController dragAndDropController) {

		super(view, viewID, textRenderer, textureManager, pickingManager,
				glMouseListener, setBar, renderCommandFactory, dataDomain,
				useCase, dragAndDropController);
		this.setBar.setPosition(new Vec3f(0.0f, 0.0f, 0.0f));
		compareConnectionRenderer = new CompareConnectionBandRenderer();
		numSetsInFocus = 4;
	}

	@Override
	public void drawActiveElements(GL gl) {

		for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
			heatMapWrapper.drawRemoteItems(gl, glMouseListener, pickingManager);
		}

		dragAndDropController.handleDragging(gl, glMouseListener);

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

		for (int i = 0; i < heatMapWrappers.size() - 1; i++) {
			renderTree(gl, heatMapWrappers.get(i), heatMapWrappers.get(i + 1),
					heatMapWrappers.get(i).getLayout().getTotalOverviewWidth());
			renderOverviewRelations(gl, heatMapWrappers.get(i), heatMapWrappers
					.get(i + 1));
		}
	}

	@Override
	public void init(GL gl) {

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
			heatMapWrapper.handleSelectionUpdate(selectionDelta,
					scrollToSelection, info);
			heatMapWrapper.getOverview().updateHeatMapTextures(heatMapWrapper.getContentSelectionManager());
		}
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
	public void handleStateSpecificPickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick,
			boolean isControlPressed) {
		//Nothing to do yet

	}
	
	@Override
	public void setSetsInFocus(ArrayList<ISet> setsInFocus) {
		//FIXME: Maybe we can put this in the base class.

		if (setsInFocus.size() >= getMinSetsInFocus()
				&& setsInFocus.size() <= getMaxSetsInFocus()) {

			setsToCompare = setsInFocus;

			if (layouts.isEmpty() || setsInFocus.size() != layouts.size()) {
				layouts.clear();
				heatMapWrappers.clear();

				int heatMapWrapperID = 0;
				for (ISet set : setsInFocus) {
					AHeatMapLayout layout = null;
					if (heatMapWrapperID == 0) {
						layout = new HeatMapLayoutOverviewLeft(
								renderCommandFactory);
					} else if (heatMapWrapperID == setsInFocus.size() - 1) {
						layout = new HeatMapLayoutOverviewRight(
								renderCommandFactory);
					} else {
						layout = new HeatMapLayoutOverviewMid(
								renderCommandFactory);
					}

					layouts.add(layout);

					HeatMapWrapper heatMapWrapper = new HeatMapWrapper(
							heatMapWrapperID, layout, view, null, useCase,
							view, dataDomain);
					heatMapWrappers.add(heatMapWrapper);
					heatMapWrapperID++;
				}
			}

			// FIXME: Use array of relations?
			ISet setLeft = setsInFocus.get(0);
			ISet setRight = setsInFocus.get(1);
			relations = SetComparer.compareSets(setLeft, setRight);

			for (int i = 0; i < heatMapWrappers.size(); i++) {
				HeatMapWrapper heatMapWrapper = heatMapWrappers.get(i);
				heatMapWrapper.setSet(setsInFocus.get(i));
			}
			setsChanged = true;
			numSetsInFocus = setsInFocus.size();

			view.setDisplayListDirty();
		}
	}

}
