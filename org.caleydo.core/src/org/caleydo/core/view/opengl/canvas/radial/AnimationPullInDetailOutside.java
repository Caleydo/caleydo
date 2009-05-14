package org.caleydo.core.view.opengl.canvas.radial;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

public class AnimationPullInDetailOutside
	extends DrawingStateAnimation {

	public static final float DEFAULT_ANIMATION_DURATION = 0.3f;

	private MovementValue mvDetailViewWidth;
	private MovementValue mvOverviewWidth;
	private MovementValue mvDetailViewInnerRadius;
	private MovementValue mvDetailViewStartAngle;
	private MovementValue mvDetailViewAngle;
	private int iDisplayedDetailViewDepth;
	private int iDisplayedOverviewDepth;
	private int iAnimationPart;

	public AnimationPullInDetailOutside(DrawingController drawingController,
		GLRadialHierarchy radialHierarchy, NavigationHistory navigationHistory) {
		super(drawingController, radialHierarchy, navigationHistory);
		fAnimationDuration = DEFAULT_ANIMATION_DURATION;
	}

	@Override
	public void draw(float fXCenter, float fYCenter, GL gl, GLU glu, double dTimePassed) {
		PartialDisc pdCurrentSelectedElement = radialHierarchy.getCurrentSelectedElement();
		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();

		if (!bAnimationStarted) {
			initAnimationFirstPart(gl, glu, fXCenter, fYCenter, pdCurrentSelectedElement,
				pdCurrentRootElement);
			iAnimationPart = 1;
			bAnimationStarted = true;
		}

		moveValues(dTimePassed);

		gl.glLoadIdentity();
		gl.glTranslatef(fXCenter, fYCenter, 0);

		pdCurrentRootElement.setPDDrawingStrategyChildren(DrawingStrategyManager.get()
			.getDefaultDrawingStrategy(), iDisplayedOverviewDepth);
		pdCurrentSelectedElement.setPDDrawingStrategyChildren(DrawingStrategyManager.get()
			.getDrawingStrategy(DrawingStrategyManager.PD_DRAWING_STRATEGY_INVISIBLE),
			iDisplayedDetailViewDepth);

		pdCurrentRootElement.drawHierarchyFull(gl, glu, mvOverviewWidth.getMovementValue(),
			iDisplayedOverviewDepth);

		pdCurrentSelectedElement.setPDDrawingStrategyChildren(DrawingStrategyManager.get()
			.getDefaultDrawingStrategy(), iDisplayedDetailViewDepth);

		pdCurrentSelectedElement.drawHierarchyAngular(gl, glu, mvDetailViewWidth.getMovementValue(),
			iDisplayedDetailViewDepth, mvDetailViewStartAngle.getMovementValue(), mvDetailViewAngle
				.getMovementValue(), mvDetailViewInnerRadius.getMovementValue());

		if (haveMovementValuesReachedTargets()) {
			iAnimationPart++;
			if (iAnimationPart == 2) {
				initAnimationSecondPart(gl, glu, fXCenter, fYCenter, pdCurrentSelectedElement, pdCurrentRootElement);
			}
			if (iAnimationPart > 2) {
				bAnimationStarted = false;
			}
		}
		
		if (!bAnimationStarted) {
			DrawingState dsNext =
				drawingController.getDrawingState(DrawingController.DRAWING_STATE_FULL_HIERARCHY);

			drawingController.setDrawingState(dsNext);
			radialHierarchy.setAnimationActive(false);
			radialHierarchy.setCurrentMouseOverElement(pdCurrentSelectedElement);

			navigationHistory.addNewHistoryEntry(dsNext, pdCurrentRootElement, pdCurrentSelectedElement,
				radialHierarchy.getMaxDisplayedHierarchyDepth());

			// pdCurrentSelectedElement.setPDDrawingStrategy(DrawingStrategyManager.get().getDrawingStrategy(
			// DrawingStrategyManager.PD_DRAWING_STRATEGY_RAINBOW));
		}
	}

	private void initAnimationFirstPart(GL gl, GLU glu, float fXCenter, float fYCenter,
		PartialDisc pdCurrentSelectedElement, PartialDisc pdCurrentRootElement) {

		iDisplayedDetailViewDepth = pdCurrentSelectedElement.getCurrentDepth();
		iDisplayedOverviewDepth = pdCurrentRootElement.getCurrentDepth();

		float fCurrentSelectedElementStartAngle = pdCurrentSelectedElement.getCurrentStartAngle();
		float fCurrentSelectedElementAngle = pdCurrentSelectedElement.getCurrentAngle();
		float fCurrentSelectedElementInnerRadius = pdCurrentSelectedElement.getCurrentInnerRadius();
		float fCurrentSelectedElementWidth = pdCurrentSelectedElement.getCurrentWidth();

		float fCurrentRootWidth = pdCurrentRootElement.getCurrentWidth();

		pdCurrentRootElement.simulateDrawHierarchyFull(gl, glu, pdCurrentRootElement.getCurrentWidth(),
			iDisplayedOverviewDepth);

		float fCurrentSelectedElementTargetAngle = pdCurrentSelectedElement.getCurrentAngle();
		float fCurrentSelectedElementTargetStartAngle = pdCurrentSelectedElement.getCurrentStartAngle();

		if(fCurrentSelectedElementTargetStartAngle < fCurrentSelectedElementStartAngle) {
			fCurrentSelectedElementTargetStartAngle += 360;
		}
			
			
			
		mvDetailViewAngle =
			createNewMovementValue(fCurrentSelectedElementAngle, fCurrentSelectedElementTargetAngle,
				fAnimationDuration);
		mvDetailViewStartAngle =
			createNewMovementValue(fCurrentSelectedElementStartAngle,
				fCurrentSelectedElementTargetStartAngle, fAnimationDuration);
		mvDetailViewInnerRadius =
			createNewMovementValue(fCurrentSelectedElementInnerRadius, fCurrentSelectedElementInnerRadius,
				fAnimationDuration);
		mvDetailViewWidth =
			createNewMovementValue(fCurrentSelectedElementWidth, fCurrentSelectedElementWidth,
				fAnimationDuration);
		mvOverviewWidth = createNewMovementValue(fCurrentRootWidth, fCurrentRootWidth, fAnimationDuration);
	}

	private void initAnimationSecondPart(GL gl, GLU glu, float fXCenter, float fYCenter,
		PartialDisc pdCurrentSelectedElement, PartialDisc pdCurrentRootElement) {

		float fCurrentRootWidth = pdCurrentRootElement.getCurrentWidth();
		float fCurrentSelectedElementWidth = pdCurrentSelectedElement.getCurrentWidth();
		float fCurrentSelecedElementInnderRadius = pdCurrentSelectedElement.getCurrentInnerRadius();
		float fCurrentSelectedElementStartAngle = pdCurrentSelectedElement.getCurrentStartAngle();
		float fCurrentSelectedElementAngle = pdCurrentSelectedElement.getCurrentAngle();

		float fHierarchyOuterRadius = Math.min(fXCenter * 0.9f, fYCenter * 0.9f);
		float fTargetWidth = fHierarchyOuterRadius / iDisplayedOverviewDepth;

		pdCurrentRootElement.simulateDrawHierarchyFull(gl, glu, fTargetWidth, iDisplayedOverviewDepth);

		float fCurrentSelectedElementTargetInnerRadius = pdCurrentSelectedElement.getCurrentInnerRadius();

		alMovementValues.clear();

		mvOverviewWidth = createNewMovementValue(fCurrentRootWidth, fTargetWidth, fAnimationDuration);
		mvDetailViewWidth =
			createNewMovementValue(fCurrentSelectedElementWidth, fTargetWidth, fAnimationDuration);
		mvDetailViewInnerRadius =
			createNewMovementValue(fCurrentSelecedElementInnderRadius,
				fCurrentSelectedElementTargetInnerRadius, fAnimationDuration);
		mvDetailViewAngle =
			createNewMovementValue(fCurrentSelectedElementAngle, fCurrentSelectedElementAngle,
				fAnimationDuration);
		mvDetailViewStartAngle =
			createNewMovementValue(fCurrentSelectedElementStartAngle, fCurrentSelectedElementStartAngle,
				fAnimationDuration);
	}

}
