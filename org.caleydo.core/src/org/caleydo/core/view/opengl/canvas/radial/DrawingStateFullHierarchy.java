package org.caleydo.core.view.opengl.canvas.radial;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.radial.event.ClusterNodeMouseOverEvent;

public class DrawingStateFullHierarchy
	extends DrawingState {

	public DrawingStateFullHierarchy(DrawingController drawingController, GLRadialHierarchy radialHierarchy,
		NavigationHistory navigationHistory) {

		super(drawingController, radialHierarchy, navigationHistory);
	}

	@Override
	public void draw(float fXCenter, float fYCenter, GL gl, GLU glu) {

		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();
		PartialDisc pdCurrentMouseOverElement = radialHierarchy.getCurrentMouseOverElement();
		int iMaxDisplayedHierarchyDepth = radialHierarchy.getMaxDisplayedHierarchyDepth();

		gl.glLoadIdentity();
		gl.glTranslatef(fXCenter, fYCenter, 0);

		int iDisplayedHierarchyDepth =
			Math.min(iMaxDisplayedHierarchyDepth, pdCurrentRootElement
				.getHierarchyDepth(iMaxDisplayedHierarchyDepth));

		pdCurrentRootElement.setPDDrawingStrategyChildren(DrawingStrategyManager.get()
			.getDefaultDrawingStrategy(), iDisplayedHierarchyDepth);

		float fHierarchyOuterRadius = Math.min(fXCenter * 0.9f, fYCenter * 0.9f);
		float fDiscWidth = fHierarchyOuterRadius / iDisplayedHierarchyDepth;

		if (pdCurrentMouseOverElement != null) {
			PDDrawingStrategyDecorator dsLabelDecorator = new PDDrawingStrategyLabelDecorator();
			dsLabelDecorator.setDrawingStrategy(DrawingStrategyManager.get().getDefaultDrawingStrategy());
			pdCurrentMouseOverElement.setPDDrawingStrategyChildren(dsLabelDecorator, 3);

			// dsLabelDecorator = new PDDrawingStrategyLabelDecorator();
			// dsLabelDecorator.setDrawingStrategy(DrawingStrategyManager.get().getDrawingStrategy(
			// DrawingStrategyManager.PD_DRAWING_STRATEGY_SELECTED));
			// pdCurrentMouseOverElement.setPDDrawingStrategy(dsLabelDecorator);
		}

		pdCurrentRootElement.drawHierarchyFull(gl, glu, fDiscWidth, iDisplayedHierarchyDepth);

		// The mouse over element has to be drawn (again in using different drawing strategy) at last for
		// correct antialiasing
		PDDrawingStrategy dsSelected =
			DrawingStrategyManager.get().getDrawingStrategy(
				DrawingStrategyManager.PD_DRAWING_STRATEGY_SELECTED);
		dsSelected.drawPartialDisc(gl, glu, pdCurrentMouseOverElement);

		LabelManager.get().drawAllLabels(gl, glu, fXCenter * 2.0f, fYCenter * 2.0f, fHierarchyOuterRadius);
		LabelManager.get().clearLabels();

	}

	@Override
	public void handleClick(PartialDisc pdClicked) {

		PartialDisc pdRealRootElement = radialHierarchy.getRealRootElement();
		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();
		PartialDisc pdCurrentMouseOverElement = radialHierarchy.getCurrentMouseOverElement();

		if (pdClicked != pdRealRootElement && pdClicked.hasChildren()) {
			if (pdCurrentMouseOverElement != null) {
				pdCurrentMouseOverElement.setPDDrawingStrategyChildren(DrawingStrategyManager.get()
					.getDefaultDrawingStrategy(), 3);
			}
			if (pdClicked == pdCurrentRootElement) {
				radialHierarchy.setCurrentRootElement(pdClicked.getParent());
				radialHierarchy.setCurrentMouseOverElement(pdClicked.getParent());
				radialHierarchy.setAnimationActive(true);
				drawingController.setDrawingState(DrawingController.DRAWING_STATE_ANIM_PARENT_ROOT_ELEMENT);

			}
			else {
				radialHierarchy.setCurrentSelectedElement(pdClicked);
				radialHierarchy.setAnimationActive(true);
				drawingController.setDrawingState(DrawingController.DRAWING_STATE_ANIM_NEW_ROOT_ELEMENT);
			}
			radialHierarchy.setDisplayListDirty();
		}

	}

	@Override
	public void handleMouseOver(PartialDisc pdMouseOver) {

		PartialDisc pdCurrentMouseOverElement = radialHierarchy.getCurrentMouseOverElement();

		if (pdMouseOver != pdCurrentMouseOverElement) {
			if (pdCurrentMouseOverElement != null) {
				pdCurrentMouseOverElement.setPDDrawingStrategyChildren(DrawingStrategyManager.get()
					.getDefaultDrawingStrategy(), 3);
			}

			radialHierarchy.setCurrentMouseOverElement(pdMouseOver);
			radialHierarchy.setDisplayListDirty();
		}
		IEventPublisher eventPublisher = GeneralManager.get().getEventPublisher();
		ClusterNodeMouseOverEvent event = new ClusterNodeMouseOverEvent();
		event.setClusterNodeName(pdMouseOver.getName());

		eventPublisher.triggerEvent(event);
	}

	@Override
	public void handleDoubleClick(PartialDisc pdClicked) {

		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();
		PartialDisc pdCurrentMouseOverElement = radialHierarchy.getCurrentMouseOverElement();

		if (pdClicked != pdCurrentRootElement && pdClicked.hasChildren() && pdClicked.getCurrentDepth() > 1) {
			if (pdCurrentMouseOverElement != null) {
				pdCurrentMouseOverElement.setPDDrawingStrategy(DrawingStrategyManager.get()
					.getDefaultDrawingStrategy());
			}

			radialHierarchy.setCurrentSelectedElement(pdClicked);
			radialHierarchy.setCurrentMouseOverElement(pdClicked);
			DrawingState dsNext =
				drawingController.getDrawingState(DrawingController.DRAWING_STATE_DETAIL_OUTSIDE);
			drawingController.setDrawingState(dsNext);

			navigationHistory.addNewHistoryEntry(dsNext, pdCurrentRootElement, pdClicked, radialHierarchy
				.getMaxDisplayedHierarchyDepth());
			radialHierarchy.setDisplayListDirty();
		}
	}

}
