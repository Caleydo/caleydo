package org.caleydo.core.view.opengl.canvas.radial;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;

public class AnimationNewRootElement
	extends DrawingStateAnimation {

	public static final float DEFAULT_ANIMATION_DURATION = 0.35f;

	private static final float TARGET_ROOT_ANGLE = 360.0f;
	private static final float TARGET_ROOT_INNER_RADIUS = 0.0f;

	MovementValue mvCurrentRootAngle;
	MovementValue mvCurrentRootStartAngle;
	MovementValue mvCurrentRootInnerRadius;
	MovementValue mvCurrentWidth;
	MovementValue mvCurrentDepth;
	MovementValue mvCurrentRootColorR;
	MovementValue mvCurrentRootColorG;
	MovementValue mvCurrentRootColorB;

	int fTargetDepth;

	PDDrawingStrategyFixedColor dsFixedColor;

	public AnimationNewRootElement(DrawingController drawingController, GLRadialHierarchy radialHierarchy,
		NavigationHistory navigationHistory) {

		super(drawingController, radialHierarchy, navigationHistory);
		fAnimationDuration = DEFAULT_ANIMATION_DURATION;
	}

	@Override
	public void draw(float fXCenter, float fYCenter, GL gl, GLU glu, double dTimePassed) {

		PartialDisc pdCurrentSelectedElement = radialHierarchy.getCurrentSelectedElement();

		if (!bAnimationStarted) {
			initAnimation(fXCenter, fYCenter, pdCurrentSelectedElement);
			bAnimationStarted = true;
		}

		moveValues(dTimePassed);

		dsFixedColor.setFillColor(mvCurrentRootColorR.getMovementValue(), mvCurrentRootColorG
			.getMovementValue(), mvCurrentRootColorB.getMovementValue(), 1);

		gl.glLoadIdentity();
		gl.glTranslatef(fXCenter, fYCenter, 0);

		pdCurrentSelectedElement.drawHierarchyAngular(gl, glu, mvCurrentWidth.getMovementValue(),
			fTargetDepth, mvCurrentRootStartAngle.getMovementValue(), mvCurrentRootAngle.getMovementValue(),
			mvCurrentRootInnerRadius.getMovementValue());

		if (haveMovementValuesReachedTargets()) {
			bAnimationStarted = false;
		}

		if (!bAnimationStarted) {
			DrawingState dsNext =
				drawingController.getDrawingState(DrawingController.DRAWING_STATE_FULL_HIERARCHY);

			drawingController.setDrawingState(dsNext);
			radialHierarchy.setAnimationActive(false);
			radialHierarchy.setCurrentRootElement(pdCurrentSelectedElement);
			radialHierarchy.setCurrentMouseOverElement(pdCurrentSelectedElement);

			navigationHistory.addNewHistoryEntry(dsNext, pdCurrentSelectedElement, pdCurrentSelectedElement,
				radialHierarchy.getMaxDisplayedHierarchyDepth());

			// pdCurrentSelectedElement.setPDDrawingStrategy(DrawingStrategyManager.get().getDrawingStrategy(
			// DrawingStrategyManager.PD_DRAWING_STRATEGY_RAINBOW));
		}
	}

	// public void draw(float fXCenter, float fYCenter, GL gl, GLU glu, double dTimePassed) {
	//
	// PartialDisc pdCurrentSelectedElement = radialHierarchy.getCurrentSelectedElement();
	//
	// if (!bAnimationStarted) {
	// initAnimation(fXCenter, fYCenter, pdCurrentSelectedElement);
	// bAnimationStarted = true;
	// }
	//
	// moveValues(dTimePassed);
	//
	// if (haveMovementValuesReachedTargets()) {
	// bAnimationStarted = false;
	// }
	//
	// gl.glLoadIdentity();
	// gl.glTranslatef(fXCenter, fYCenter, 0);
	//
	// dsFixedColor.setFillColor(mvCurrentRootColorR.getMovementValue(), mvCurrentRootColorG
	// .getMovementValue(), mvCurrentRootColorB.getMovementValue(), 1);
	//
	// pdCurrentSelectedElement.drawHierarchyAngular(gl, glu, mvCurrentWidth.getMovementValue(),
	// fTargetDepth, mvCurrentRootStartAngle.getMovementValue(), mvCurrentRootAngle.getMovementValue(),
	// mvCurrentRootInnerRadius.getMovementValue());
	//
	// if (!bAnimationStarted) {
	// DrawingState dsNext =
	// drawingController.getDrawingState(DrawingController.DRAWING_STATE_FULL_HIERARCHY);
	//
	// drawingController.setDrawingState(dsNext);
	// radialHierarchy.setAnimationActive(false);
	// radialHierarchy.setCurrentRootElement(pdCurrentSelectedElement);
	// radialHierarchy.setCurrentMouseOverElement(pdCurrentSelectedElement);
	//
	// navigationHistory.addNewHistoryEntry(dsNext, pdCurrentSelectedElement, pdCurrentSelectedElement,
	// radialHierarchy.getMaxDisplayedHierarchyDepth());
	//
	// // pdCurrentSelectedElement.setPDDrawingStrategy(DrawingStrategyManager.get().getDrawingStrategy(
	// // DrawingStrategyManager.PD_DRAWING_STRATEGY_RAINBOW));
	// }
	// }

	// private void initAnimation(float fXCenter, float fYCenter, PartialDisc pdCurrentSelectedElement) {
	//
	// //TODO: rename to selected
	// float fCurrentRootAngle = pdCurrentSelectedElement.getCurrentAngle();
	// float fCurrentDepth = pdCurrentSelectedElement.getCurrentDepth();
	// float fCurrentRootInnerRadius = pdCurrentSelectedElement.getCurrentInnerRadius();
	// float fCurrentRootStartAngle = pdCurrentSelectedElement.getCurrentStartAngle();
	// float fCurrentWidth = pdCurrentSelectedElement.getCurrentWidth();
	//
	// fTargetDepth =
	// Math.min(radialHierarchy.getMaxDisplayedHierarchyDepth(), pdCurrentSelectedElement
	// .getHierarchyDepth(radialHierarchy.getMaxDisplayedHierarchyDepth()));
	// float fTargetWidth = Math.min(fXCenter * 0.9f, fYCenter * 0.9f) / fTargetDepth;
	//
	// float fMidAngle = fCurrentRootStartAngle + (fCurrentRootAngle / 2.0f);
	// fTargetDepth = pdCurrentSelectedElement.getCurrentDepth();
	//
	// while (fMidAngle > 360) {
	// fMidAngle -= 360;
	// }
	// while (fMidAngle < 0) {
	// fMidAngle += 360;
	// }
	// float fAngleToAdd = (fMidAngle > fCurrentRootStartAngle) ? -180 : 180;
	// float fRootTargetStartAngle = 0;
	//
	// // TODO: if new colormode is introduced, use correct colormapping, also use target color from
	// renderstyle
	//
	// ColorMapping cmRainbow = ColorMappingManager.get().getColorMapping(EColorMappingType.RAINBOW);
	// float fArRGB[] = cmRainbow.getColor(fMidAngle / 360);
	//
	// alMovementValues.clear();
	//
	// mvCurrentRootAngle = createNewMovementValue(fCurrentRootAngle, TARGET_ROOT_ANGLE, fAnimationDuration);
	// mvCurrentRootStartAngle =
	// createNewMovementValue(fCurrentRootStartAngle, fRootTargetStartAngle, fAnimationDuration);
	// mvCurrentRootInnerRadius =
	// createNewMovementValue(fCurrentRootInnerRadius, TARGET_ROOT_INNER_RADIUS, fAnimationDuration);
	// mvCurrentDepth = createNewMovementValue(fCurrentDepth, fTargetDepth, fAnimationDuration);
	// mvCurrentWidth = createNewMovementValue(fCurrentWidth, fTargetWidth, fAnimationDuration);
	// mvCurrentRootColorR = createNewMovementValue(fArRGB[0], 1, fAnimationDuration);
	// mvCurrentRootColorG = createNewMovementValue(fArRGB[1], 1, fAnimationDuration);
	// mvCurrentRootColorB = createNewMovementValue(fArRGB[2], 1, fAnimationDuration);
	//
	// dsFixedColor =
	// (PDDrawingStrategyFixedColor) DrawingStrategyManager.get().getDrawingStrategy(
	// DrawingStrategyManager.PD_DRAWING_STRATEGY_FIXED_COLOR);
	//
	// pdCurrentSelectedElement.setPDDrawingStrategy(dsFixedColor);
	// }

	private void initAnimation(float fXCenter, float fYCenter, PartialDisc pdCurrentSelectedElement) {

		// TODO: rename to selected
		float fCurrentRootAngle = pdCurrentSelectedElement.getCurrentAngle();
		float fCurrentDepth = pdCurrentSelectedElement.getCurrentDepth();
		float fCurrentRootInnerRadius = pdCurrentSelectedElement.getCurrentInnerRadius();
		float fCurrentRootStartAngle = pdCurrentSelectedElement.getCurrentStartAngle();
		float fCurrentWidth = pdCurrentSelectedElement.getCurrentWidth();

		fTargetDepth =
			Math.min(radialHierarchy.getMaxDisplayedHierarchyDepth(), pdCurrentSelectedElement
				.getHierarchyDepth());
		float fTargetWidth = Math.min(fXCenter * 0.9f, fYCenter * 0.9f) / fTargetDepth;

		float fMidAngle = fCurrentRootStartAngle + (fCurrentRootAngle / 2.0f);
		fTargetDepth = pdCurrentSelectedElement.getCurrentDepth();

		while (fMidAngle > 360) {
			fMidAngle -= 360;
		}
		while (fMidAngle < 0) {
			fMidAngle += 360;
		}
		float fAngleToAdd = (fMidAngle > fCurrentRootStartAngle) ? -180 : 180;
		float fRootTargetStartAngle = fMidAngle + fAngleToAdd;

		float fArRGB[];
		if (DrawingStrategyManager.get().getDefaultStrategyType() == DrawingStrategyManager.PD_DRAWING_STRATEGY_RAINBOW) {
			ColorMapping cmRainbow = ColorMappingManager.get().getColorMapping(EColorMappingType.RAINBOW);
			fArRGB = cmRainbow.getColor(fMidAngle / 360);
		}
		else {
			ColorMapping cmExpression =
				ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);
			fArRGB = cmExpression.getColor(pdCurrentSelectedElement.getAverageExpressionValue());
		}

		alMovementValues.clear();

		mvCurrentRootAngle = createNewMovementValue(fCurrentRootAngle, TARGET_ROOT_ANGLE, fAnimationDuration);
		mvCurrentRootStartAngle =
			createNewMovementValue(fCurrentRootStartAngle, fRootTargetStartAngle, fAnimationDuration);
		mvCurrentRootInnerRadius =
			createNewMovementValue(fCurrentRootInnerRadius, TARGET_ROOT_INNER_RADIUS, fAnimationDuration);
		mvCurrentDepth = createNewMovementValue(fCurrentDepth, fTargetDepth, fAnimationDuration);
		mvCurrentWidth = createNewMovementValue(fCurrentWidth, fTargetWidth, fAnimationDuration);
		mvCurrentRootColorR = createNewMovementValue(fArRGB[0], 1, fAnimationDuration);
		mvCurrentRootColorG = createNewMovementValue(fArRGB[1], 1, fAnimationDuration);
		mvCurrentRootColorB = createNewMovementValue(fArRGB[2], 1, fAnimationDuration);

		dsFixedColor =
			(PDDrawingStrategyFixedColor) DrawingStrategyManager.get().getDrawingStrategy(
				DrawingStrategyManager.PD_DRAWING_STRATEGY_FIXED_COLOR);

		pdCurrentSelectedElement.setPDDrawingStrategy(dsFixedColor);
	}

	//	
	// private void initFirstPart(GL gl, GLU glu, float fXCenter, float fYCenter, PartialDisc
	// pdCurrentSelectedElement) {
	//
	// float fCurrentAngle = pdCurrentSelectedElement.getCurrentAngle();
	// float fCurrentInnerRadius = pdCurrentSelectedElement.getCurrentInnerRadius();
	// float fCurrentStartAngle = pdCurrentSelectedElement.getCurrentStartAngle();
	// float fCurrentWidth = pdCurrentSelectedElement.getCurrentWidth();
	//
	// fTargetDepth = pdCurrentSelectedElement.getCurrentDepth();
	//		
	// float fTargetStartAngle = 180 - (fCurrentAngle / 2.0f);
	//		
	// // if (fTargetStartAngle < fCurrentStartAngle) {
	// // fTargetStartAngle += 360;
	// // }
	//
	// alMovementValues.clear();
	//
	// mvCurrentRootAngle = createNewMovementValue(fCurrentAngle, fCurrentAngle, fAnimationDuration);
	// mvCurrentRootStartAngle =
	// createNewMovementValue(fCurrentStartAngle, fTargetStartAngle, fAnimationDuration);
	// mvCurrentRootInnerRadius =
	// createNewMovementValue(fCurrentInnerRadius, fCurrentInnerRadius, fAnimationDuration);
	// mvCurrentWidth = createNewMovementValue(fCurrentWidth, fCurrentWidth, fAnimationDuration);
	// }
	//	
	// private void initSecondPart(float fXCenter, float fYCenter, PartialDisc pdCurrentSelectedElement) {
	//
	// //TODO: rename to selected
	// float fCurrentRootAngle = pdCurrentSelectedElement.getCurrentAngle();
	// float fCurrentDepth = pdCurrentSelectedElement.getCurrentDepth();
	// float fCurrentRootInnerRadius = pdCurrentSelectedElement.getCurrentInnerRadius();
	// float fCurrentRootStartAngle = pdCurrentSelectedElement.getCurrentStartAngle();
	// float fCurrentWidth = pdCurrentSelectedElement.getCurrentWidth();
	//
	// fTargetDepth =
	// Math.min(radialHierarchy.getMaxDisplayedHierarchyDepth(), pdCurrentSelectedElement
	// .getHierarchyDepth(radialHierarchy.getMaxDisplayedHierarchyDepth()));
	// float fTargetWidth = Math.min(fXCenter * 0.9f, fYCenter * 0.9f) / fTargetDepth;
	//
	// float fMidAngle = fCurrentRootStartAngle + (fCurrentRootAngle / 2.0f);
	// fTargetDepth = pdCurrentSelectedElement.getCurrentDepth();
	//
	// while (fMidAngle > 360) {
	// fMidAngle -= 360;
	// }
	// while (fMidAngle < 0) {
	// fMidAngle += 360;
	// }
	// float fAngleToAdd = (fMidAngle > fCurrentRootStartAngle) ? -180 : 180;
	// float fRootTargetStartAngle = 0;
	//
	// // TODO: if new colormode is introduced, use correct colormapping, also use target color from
	// renderstyle
	//
	// ColorMapping cmRainbow = ColorMappingManager.get().getColorMapping(EColorMappingType.RAINBOW);
	// float fArRGB[] = cmRainbow.getColor(fMidAngle / 360);
	//
	// alMovementValues.clear();
	//
	// mvCurrentRootAngle = createNewMovementValue(fCurrentRootAngle, TARGET_ROOT_ANGLE, fAnimationDuration);
	// mvCurrentRootStartAngle =
	// createNewMovementValue(fCurrentRootStartAngle, fRootTargetStartAngle, fAnimationDuration);
	// mvCurrentRootInnerRadius =
	// createNewMovementValue(fCurrentRootInnerRadius, TARGET_ROOT_INNER_RADIUS, fAnimationDuration);
	// mvCurrentDepth = createNewMovementValue(fCurrentDepth, fTargetDepth, fAnimationDuration);
	// mvCurrentWidth = createNewMovementValue(fCurrentWidth, fTargetWidth, fAnimationDuration);
	// mvCurrentRootColorR = createNewMovementValue(fArRGB[0], 1, fAnimationDuration);
	// mvCurrentRootColorG = createNewMovementValue(fArRGB[1], 1, fAnimationDuration);
	// mvCurrentRootColorB = createNewMovementValue(fArRGB[2], 1, fAnimationDuration);
	//
	// dsFixedColor =
	// (PDDrawingStrategyFixedColor) DrawingStrategyManager.get().getDrawingStrategy(
	// DrawingStrategyManager.PD_DRAWING_STRATEGY_FIXED_COLOR);
	//
	// pdCurrentSelectedElement.setPDDrawingStrategy(dsFixedColor);
	// }

}
