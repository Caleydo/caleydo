package org.caleydo.core.view.opengl.canvas.radial;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.view.opengl.canvas.radial.DrawingController;
import org.caleydo.core.view.opengl.canvas.radial.DrawingState;
import org.caleydo.core.view.opengl.canvas.radial.DrawingStrategyManager;
import org.caleydo.core.view.opengl.canvas.radial.GLRadialHierarchy;
import org.caleydo.core.view.opengl.canvas.radial.PartialDisc;

public class DrawingStateFullHierarchy
	extends DrawingState {

	public DrawingStateFullHierarchy(DrawingController drawingController, GLRadialHierarchy radialHierarchy) {
		super(drawingController, radialHierarchy);
	}

	@Override
	public void draw(float fXCenter, float fYCenter, GL gl, GLU glu) {

		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();
		PartialDisc pdCurrentMouseOverElement = radialHierarchy.getCurrentMouseOverElement();

		gl.glLoadIdentity();
		gl.glTranslatef(fXCenter, fYCenter, 0);

		int iDisplayedHierarchyDepth =
			Math.min(radialHierarchy.getMaxDisplayedHierarchyDepth(), pdCurrentRootElement
				.getHierarchyDepth(radialHierarchy.getMaxDisplayedHierarchyDepth()));

		float fHierarchyOuterRadius = Math.min(fXCenter * 0.9f, fYCenter * 0.9f);
		float fDiscWidth = fHierarchyOuterRadius / iDisplayedHierarchyDepth;
		
		if(pdCurrentMouseOverElement != null) {
			PDDrawingStrategyDecorator dsLabelDecorator = new PDDrawingStrategyLabelDecorator();
			dsLabelDecorator.setDrawingStrategy(DrawingStrategyManager.get().getDrawingStrategy(
				DrawingStrategyManager.PD_DRAWING_STRATEGY_RAINBOW));
			pdCurrentMouseOverElement.setPDDrawingStrategyChildren(dsLabelDecorator, 3);

			dsLabelDecorator = new PDDrawingStrategyLabelDecorator();
			dsLabelDecorator.setDrawingStrategy(DrawingStrategyManager.get().getDrawingStrategy(
				DrawingStrategyManager.PD_DRAWING_STRATEGY_SELECTED));
			pdCurrentMouseOverElement.setPDDrawingStrategy(dsLabelDecorator);
		}

		pdCurrentRootElement.drawHierarchyFull(gl, glu, fDiscWidth, iDisplayedHierarchyDepth);

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
					.getDrawingStrategy(DrawingStrategyManager.PD_DRAWING_STRATEGY_RAINBOW), 3);
			}
			if (pdClicked == pdCurrentRootElement) {
				radialHierarchy.setCurrentRootElement(pdClicked.getParent());
				radialHierarchy.setCurrentMouseOverElement(pdClicked.getParent());
			}
			else {
				radialHierarchy.setCurrentSelectedElement(pdClicked);
				radialHierarchy.setCurrentRootElement(pdClicked);
				radialHierarchy.setCurrentMouseOverElement(pdClicked);
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
					.getDrawingStrategy(DrawingStrategyManager.PD_DRAWING_STRATEGY_RAINBOW), 3);
			}

			radialHierarchy.setCurrentMouseOverElement(pdMouseOver);
			radialHierarchy.setDisplayListDirty();
		}
	}

	@Override
	public void handleDoubleClick(PartialDisc pdClicked) {

		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();
		PartialDisc pdCurrentMouseOverElement = radialHierarchy.getCurrentMouseOverElement();

		if (pdClicked != pdCurrentRootElement && pdClicked.hasChildren() && pdClicked.getCurrentDepth() > 1) {
			if (pdCurrentMouseOverElement != null) {
				pdCurrentMouseOverElement.setPDDrawingStrategy(DrawingStrategyManager.get()
					.getDrawingStrategy(DrawingStrategyManager.PD_DRAWING_STRATEGY_RAINBOW));
			}

			radialHierarchy.setCurrentSelectedElement(pdClicked);
			radialHierarchy.setCurrentMouseOverElement(pdClicked);
			drawingController.setDrawingState(DrawingController.DRAWING_STATE_DETAIL_OUTSIDE);

			radialHierarchy.setDisplayListDirty();
		}
	}

}
