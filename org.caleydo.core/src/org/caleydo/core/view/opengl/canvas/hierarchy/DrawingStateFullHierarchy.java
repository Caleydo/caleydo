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
		PartialDisc pdCurrentMouseOverElement = radialHierarchy.getCurrentMouseOverElement();

		if (pdClicked != pdRealRootElement && pdClicked.hasChildren()) {
			if (pdCurrentMouseOverElement != null) {
				pdCurrentMouseOverElement.setPDDrawingStrategy(DrawingStrategyManager.get()
					.getDrawingStrategy(DrawingStrategyManager.PD_DRAWING_STRATEGY_RAINBOW));
				radialHierarchy.setCurrentMouseOverElement(null);
			}
			if (pdClicked == pdCurrentRootElement) {
				radialHierarchy.setCurrentRootElement(pdRealRootElement);
			}
			else {

				radialHierarchy.setCurrentSelectedElement(pdClicked);
				radialHierarchy.setCurrentRootElement(pdClicked);
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
				pdCurrentMouseOverElement.setPDDrawingStrategy(DrawingStrategyManager.get()
					.getDrawingStrategy(DrawingStrategyManager.PD_DRAWING_STRATEGY_RAINBOW));
			}

			pdMouseOver.setPDDrawingStrategy(DrawingStrategyManager.get().getDrawingStrategy(
				DrawingStrategyManager.PD_DRAWING_STRATEGY_SELECTED));
			radialHierarchy.setCurrentMouseOverElement(pdMouseOver);

			radialHierarchy.setDisplayListDirty();
		}
	}

	@Override
	public void draw(float fXCenter, float fYCenter, GL gl, GLU glu) {

		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();

		gl.glLoadIdentity();
		gl.glTranslatef(fXCenter, fYCenter, 0);

		int iDisplayedHierarchyDepth =
			Math.min(radialHierarchy.getMaxDisplayedHierarchyDepth(), pdCurrentRootElement
				.getHierarchyDepth());

		float fDiscWidth =
			Math.min(fXCenter * 0.9f, fYCenter * 0.9f) / iDisplayedHierarchyDepth;

		pdCurrentRootElement.drawHierarchyFull(gl, glu, fDiscWidth, iDisplayedHierarchyDepth);

	}

	@Override
	public void handleDoubleClick(PartialDisc pdClicked) {
		
		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();
		PartialDisc pdCurrentMouseOverElement = radialHierarchy.getCurrentMouseOverElement();

		if (pdClicked != pdCurrentRootElement && pdClicked.hasChildren() && pdClicked.getCurrentDepth() > 1) {
			if (pdCurrentMouseOverElement != null) {
				pdCurrentMouseOverElement.setPDDrawingStrategy(DrawingStrategyManager.get()
					.getDrawingStrategy(DrawingStrategyManager.PD_DRAWING_STRATEGY_RAINBOW));
				radialHierarchy.setCurrentMouseOverElement(null);
			}

			radialHierarchy.setCurrentSelectedElement(pdClicked);
			drawingController.setDrawingState(DrawingController.DRAWING_STATE_DETAIL_OUTSIDE);

			radialHierarchy.setDisplayListDirty();
		}
	}

}
