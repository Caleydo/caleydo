package org.caleydo.core.view.opengl.canvas.radial;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

public class AnimationPopOutDetailOutside
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

	public AnimationPopOutDetailOutside(DrawingController drawingController,
		GLRadialHierarchy radialHierarchy, NavigationHistory navigationHistory) {
		super(drawingController, radialHierarchy, navigationHistory);
		fAnimationDuration = DEFAULT_ANIMATION_DURATION;
	}

	@Override
	public void draw(float fXCenter, float fYCenter, GL gl, GLU glu, double dTimePassed) {

		PartialDisc pdCurrentSelectedElement = radialHierarchy.getCurrentSelectedElement();
		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();

		if (!bAnimationStarted) {
			initAnimationFirstPart(fXCenter, fYCenter, pdCurrentSelectedElement, pdCurrentRootElement);
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

		if (iAnimationPart == 1) {
			pdCurrentSelectedElement.drawHierarchyAngular(gl, glu, mvDetailViewWidth.getMovementValue(),
				iDisplayedDetailViewDepth, pdCurrentSelectedElement.getCurrentStartAngle(),
				pdCurrentSelectedElement.getCurrentAngle(), mvDetailViewInnerRadius.getMovementValue());
		}
		else {
			pdCurrentSelectedElement.drawHierarchyAngular(gl, glu, mvDetailViewWidth.getMovementValue(),
				iDisplayedDetailViewDepth, mvDetailViewStartAngle.getMovementValue(), mvDetailViewAngle
					.getMovementValue(), mvDetailViewInnerRadius.getMovementValue());
		}
		
		if (haveMovementValuesReachedTargets()) {
			iAnimationPart++;
			if (iAnimationPart == 2) {
				initAnimationSecondPart(fXCenter, fYCenter, pdCurrentSelectedElement, pdCurrentRootElement);
			}
			if (iAnimationPart > 2) {
				bAnimationStarted = false;
			}
		}

		if (!bAnimationStarted) {
			DrawingState dsNext =
				drawingController.getDrawingState(DrawingController.DRAWING_STATE_DETAIL_OUTSIDE);

			drawingController.setDrawingState(dsNext);
			radialHierarchy.setAnimationActive(false);
			radialHierarchy.setCurrentMouseOverElement(pdCurrentSelectedElement);

			navigationHistory.addNewHistoryEntry(dsNext, pdCurrentRootElement, pdCurrentSelectedElement,
				radialHierarchy.getMaxDisplayedHierarchyDepth());

			// pdCurrentSelectedElement.setPDDrawingStrategy(DrawingStrategyManager.get().getDrawingStrategy(
			// DrawingStrategyManager.PD_DRAWING_STRATEGY_RAINBOW));
		}
	}

	private void initAnimationFirstPart(float fXCenter, float fYCenter, PartialDisc pdCurrentSelectedElement,
		PartialDisc pdCurrentRootElement) {

		float fCurrentRootWidth = pdCurrentRootElement.getCurrentWidth();

		int iCurrentSelectedElementDepth = pdCurrentSelectedElement.getCurrentDepth();
		float fCurrentSelectedElementWidth = pdCurrentSelectedElement.getCurrentWidth();
		float fCurrentSelecedElementInnderRadius = pdCurrentSelectedElement.getCurrentInnerRadius();

		int iMaxDisplayedHierarchyDepth = radialHierarchy.getMaxDisplayedHierarchyDepth();
		float fDetailViewScreenPercentage;

		if (iMaxDisplayedHierarchyDepth <= RadialHierarchyRenderStyle.MIN_DISPLAYED_DETAIL_DEPTH + 1) {
			fDetailViewScreenPercentage = RadialHierarchyRenderStyle.MIN_DETAIL_SCREEN_PERCENTAGE;
		}
		else {
			float fPercentageStep =
				(RadialHierarchyRenderStyle.MAX_DETAIL_SCREEN_PERCENTAGE - RadialHierarchyRenderStyle.MIN_DETAIL_SCREEN_PERCENTAGE)
					/ ((float) (iMaxDisplayedHierarchyDepth
						- RadialHierarchyRenderStyle.MIN_DISPLAYED_DETAIL_DEPTH - 1));

			fDetailViewScreenPercentage =
				RadialHierarchyRenderStyle.MIN_DETAIL_SCREEN_PERCENTAGE
					+ (iCurrentSelectedElementDepth - RadialHierarchyRenderStyle.MIN_DISPLAYED_DETAIL_DEPTH)
					* fPercentageStep;
		}

		iDisplayedDetailViewDepth = Math.min(iMaxDisplayedHierarchyDepth, iCurrentSelectedElementDepth);
		float fDetailViewTargetWidth =
			Math.min(fXCenter * fDetailViewScreenPercentage, fYCenter * fDetailViewScreenPercentage)
				/ iDisplayedDetailViewDepth;

		float fOverviewScreenPercentage =
			100.0f - (fDetailViewScreenPercentage + (100.0f - RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE));
		iDisplayedOverviewDepth =
			Math.min(iMaxDisplayedHierarchyDepth, pdCurrentRootElement
				.getHierarchyDepth(iMaxDisplayedHierarchyDepth));

		float fTotalOverviewWidth =
			Math.min(fXCenter * fOverviewScreenPercentage, fYCenter * fOverviewScreenPercentage);
		float fOverviewTargetWidth = fTotalOverviewWidth / iDisplayedOverviewDepth;

		float fDetailViewTargetInnerRadius = fTotalOverviewWidth + Math.min(fXCenter * 0.1f, fYCenter * 0.1f);

		alMovementValues.clear();

		mvOverviewWidth = createNewMovementValue(fCurrentRootWidth, fOverviewTargetWidth, fAnimationDuration);
		mvDetailViewWidth =
			createNewMovementValue(fCurrentSelectedElementWidth, fDetailViewTargetWidth, fAnimationDuration);
		mvDetailViewInnerRadius =
			createNewMovementValue(fCurrentSelecedElementInnderRadius, fDetailViewTargetInnerRadius,
				fAnimationDuration);
	}

	private void initAnimationSecondPart(float fXCenter, float fYCenter,
		PartialDisc pdCurrentSelectedElement, PartialDisc pdCurrentRootElement) {

		float fCurrentSelectedElementStartAngle = pdCurrentSelectedElement.getCurrentStartAngle();
		float fCurrentSelectedElementAngle = pdCurrentSelectedElement.getCurrentAngle();

		float fMidAngle = fCurrentSelectedElementStartAngle + (fCurrentSelectedElementAngle / 2.0f);

		while (fMidAngle > 360) {
			fMidAngle -= 360;
		}
		while (fMidAngle < 0) {
			fMidAngle += 360;
		}
		float fAngleToAdd = (fMidAngle > fCurrentSelectedElementStartAngle) ? -180 : 180;
		float fCurrentSelectedElementTargetStartAngle = fMidAngle + fAngleToAdd;

		mvDetailViewAngle = createNewMovementValue(fCurrentSelectedElementAngle, 360, fAnimationDuration);
		mvDetailViewStartAngle =
			createNewMovementValue(fCurrentSelectedElementStartAngle,
				fCurrentSelectedElementTargetStartAngle, fAnimationDuration);
	}
}
