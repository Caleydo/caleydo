package org.caleydo.core.view.opengl.canvas.radial;

import java.util.ArrayList;

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
				.getHierarchyDepth());

		pdCurrentRootElement.setPDDrawingStrategyChildren(DrawingStrategyManager.get()
			.getDefaultDrawingStrategy(), iDisplayedHierarchyDepth);

		float fHierarchyOuterRadius = Math.min(fXCenter * 0.9f, fYCenter * 0.9f);
		float fDiscWidth = fHierarchyOuterRadius / iDisplayedHierarchyDepth;
		boolean bIsMouseOverElementDisplayed = true;
		boolean bIsMouseOverElementParentOfCurrentRoot = false;

		if (pdCurrentMouseOverElement != null) {

			if (pdCurrentMouseOverElement != pdCurrentRootElement) {
				ArrayList<PartialDisc> alParentPath =
					pdCurrentMouseOverElement.getParentPath(pdCurrentRootElement);
				if (alParentPath != null) {
					if (alParentPath.size() >= iDisplayedHierarchyDepth) {
						DrawingStrategyManager drawingStategyManager = DrawingStrategyManager.get();
						PDDrawingStrategyChildIndicator dsDefault =
							(PDDrawingStrategyChildIndicator) drawingStategyManager
								.createDrawingStrategy(drawingStategyManager.getDefaultStrategyType());

						dsDefault.setChildIndicatorColor(RadialHierarchyRenderStyle.MOUSE_OVER_COLOR);
						alParentPath.get(alParentPath.size() - iDisplayedHierarchyDepth)
							.setPDDrawingStrategy(dsDefault);
						bIsMouseOverElementDisplayed = false;
					}
				}
				else {
					bIsMouseOverElementParentOfCurrentRoot = true;
					bIsMouseOverElementDisplayed = false;
				}
			}

			if (bIsMouseOverElementDisplayed) {
				PDDrawingStrategyDecorator dsLabelDecorator = new PDDrawingStrategyLabelDecorator();
				dsLabelDecorator.setDrawingStrategy(DrawingStrategyManager.get().getDefaultDrawingStrategy());
				pdCurrentMouseOverElement.setPDDrawingStrategyChildren(dsLabelDecorator, 3);
			}
		}

		pdCurrentRootElement.drawHierarchyFull(gl, glu, fDiscWidth, iDisplayedHierarchyDepth);

		// The mouse over element has to be drawn (again in using different drawing strategy) at last for
		// correct antialiasing
		if (bIsMouseOverElementDisplayed) {
			PDDrawingStrategy dsSelected =
				DrawingStrategyManager.get().getDrawingStrategy(
					DrawingStrategyManager.PD_DRAWING_STRATEGY_SELECTED);
			dsSelected.drawPartialDisc(gl, glu, pdCurrentMouseOverElement);
		}

		if (bIsMouseOverElementParentOfCurrentRoot) {
			gl.glPushClientAttrib(GL.GL_COLOR_BUFFER_BIT);
			gl.glColor3fv(RadialHierarchyRenderStyle.MOUSE_OVER_COLOR, 0);
			GLPrimitives.renderCircle(gl, glu, fDiscWidth / 2.0f, 100);
			GLPrimitives.renderCircleBorder(gl, glu, fDiscWidth / 2.0f, 100, 2);
			gl.glPopAttrib();
		}

		LabelManager.get().drawAllLabels(gl, glu, fXCenter * 2.0f, fYCenter * 2.0f, fHierarchyOuterRadius);
		LabelManager.get().clearLabels();

	}

	@Override
	public void handleSelection(PartialDisc pdClicked) {

		PartialDisc pdRealRootElement = radialHierarchy.getRealRootElement();
		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();
		PartialDisc pdCurrentMouseOverElement = radialHierarchy.getCurrentMouseOverElement();

		if (pdClicked != pdRealRootElement && pdClicked.hasChildren()) {
			
			if (pdCurrentMouseOverElement != null) {
				pdCurrentMouseOverElement.setPDDrawingStrategyChildren(DrawingStrategyManager.get()
					.getDefaultDrawingStrategy(), 3);
			}
			if (pdClicked == pdCurrentRootElement) {
				radialHierarchy.setCurrentSelectedElement(pdClicked);
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
	public void handleFocus(PartialDisc pdMouseOver) {

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
		event.setSender(this);
		event.setClusterNumber(pdMouseOver.getElementID());

		eventPublisher.triggerEvent(event);
	}

	@Override
	public void handleAlternativeSelection(PartialDisc pdClicked) {

		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();
		PartialDisc pdCurrentMouseOverElement = radialHierarchy.getCurrentMouseOverElement();

		if (pdClicked != pdCurrentRootElement && pdClicked.hasChildren() && pdClicked.getCurrentDepth() > 1) {
			
			if (pdCurrentMouseOverElement != null) {
				pdCurrentMouseOverElement.setPDDrawingStrategy(DrawingStrategyManager.get()
					.getDefaultDrawingStrategy());
			}

			radialHierarchy.setCurrentSelectedElement(pdClicked);
			radialHierarchy.setCurrentMouseOverElement(pdClicked);
			drawingController.setDrawingState(DrawingController.DRAWING_STATE_ANIM_POP_OUT_DETAIL_OUTSIDE);
			radialHierarchy.setAnimationActive(true);
			radialHierarchy.setDisplayListDirty();
		}
	}

}
