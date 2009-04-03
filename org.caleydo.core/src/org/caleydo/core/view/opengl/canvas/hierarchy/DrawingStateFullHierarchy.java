package org.caleydo.core.view.opengl.canvas.hierarchy;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

public class DrawingStateFullHierarchy
	extends DrawingState {

	public DrawingStateFullHierarchy(DrawingController drawingController, GLRadialHierarchy radialHierarchy) {
		super(drawingController, radialHierarchy);
	}

	@Override
	public void handleClick(PartialDisc pdClicked) {

		PartialDisc pdRealRootElement = radialHierarchy.getRealRootElement();
		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();
		PartialDisc pdCurrentSelectedElement = radialHierarchy.getCurrentSelectedElement();

		if (pdClicked != pdRealRootElement && pdClicked.hasChildren()) {
			if (pdCurrentSelectedElement != null) {
				pdCurrentSelectedElement.setPDDrawingStrategy(DrawingStrategyManager.getInstance()
					.getDrawingStrategy(DrawingStrategyManager.PD_DRAWING_STRATEGY_NORMAL));
			}
			if (pdClicked == pdCurrentRootElement) {
				radialHierarchy.setCurrentRootElement(pdRealRootElement);
			}
			else {

				radialHierarchy.setCurrentSelectedElement(pdClicked);
				radialHierarchy.setCurrentRootElement(pdClicked);
				radialHierarchy.setAnimationActive(true);
				drawingController.setDrawingState(drawingController
					.getDrawingState(DrawingController.DRAWING_STATE_ANIM_NEW_ROOT_ELEMENT));

			}
			radialHierarchy.setDisplayListDirty();
		}

	}

	@Override
	public void handleMouseOver(PartialDisc pdMouseOver) {

		PartialDisc pdCurrentSelectedElement = radialHierarchy.getCurrentSelectedElement();

		if (pdMouseOver != pdCurrentSelectedElement) {
			if (pdCurrentSelectedElement != null) {
				pdCurrentSelectedElement.setPDDrawingStrategy(DrawingStrategyManager.getInstance()
					.getDrawingStrategy(DrawingStrategyManager.PD_DRAWING_STRATEGY_NORMAL));
			}

			pdMouseOver.setPDDrawingStrategy(DrawingStrategyManager.getInstance().getDrawingStrategy(
				DrawingStrategyManager.PD_DRAWING_STRATEGY_SELECTED));
			radialHierarchy.setCurrentSelectedElement(pdMouseOver);

			radialHierarchy.setDisplayListDirty();
		}
	}

	@Override
	public void draw(float fXCenter, float fYCenter, GL gl, GLU glu) {

		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();

		gl.glLoadIdentity();
		gl.glColor4f(1, 1, 1, 1);
		gl.glTranslatef(fXCenter, fYCenter, 0);

		int iDisplayedHierarchyDepth =
			Math.min(radialHierarchy.getMaxDisplayedHierarchyDepth(), pdCurrentRootElement
				.getHierarchyDepth());

		float fDiscWidth =
			Math.min(fXCenter - (fXCenter / 10), fYCenter - (fYCenter / 10)) / iDisplayedHierarchyDepth;

		pdCurrentRootElement.drawHierarchyFull(gl, glu, fDiscWidth, iDisplayedHierarchyDepth);

	}

}
