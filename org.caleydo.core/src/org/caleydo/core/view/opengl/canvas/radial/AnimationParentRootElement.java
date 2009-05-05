package org.caleydo.core.view.opengl.canvas.radial;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;

public class AnimationParentRootElement
	extends DrawingStateAnimation {
	
	public static final float DEFAULT_ANIMATION_DURATION = 0.35f;
	
	private MovementValue mvCurrentStartAngle;
	private MovementValue mvCurrentAngle;
	private MovementValue mvCurrentWidth;
	private MovementValue mvCurrentInnerRadius;
	private MovementValue mvCurrentSelectedColorR;
	private MovementValue mvCurrentSelectedColorG;
	private MovementValue mvCurrentSelectedColorB;
	private int iTargetDepth;
	
	PDDrawingStrategyFixedColor pddsFixedColor;
	
	public AnimationParentRootElement(DrawingController drawingController, GLRadialHierarchy radialHierarchy,
		NavigationHistory navigationHistory) {
		
		super(drawingController, radialHierarchy, navigationHistory);
		fAnimationDuration = DEFAULT_ANIMATION_DURATION;
	}

	@Override
	public void draw(float fXCenter, float fYCenter, GL gl, GLU glu, double dTimePassed) {
		PartialDisc pdCurrentSelectedElement = radialHierarchy.getCurrentSelectedElement();

		if (!bAnimationStarted) {
			initAnimation(gl, glu, fXCenter, fYCenter, pdCurrentSelectedElement);
			bAnimationStarted = true;
		}

		moveValues(dTimePassed);

		if (haveMovementValuesReachedTargets()) {
			bAnimationStarted = false;
		}

		gl.glLoadIdentity();
		gl.glTranslatef(fXCenter, fYCenter, 0);

		pddsFixedColor.setFillColor(mvCurrentSelectedColorR.getMovementValue(), mvCurrentSelectedColorG
			.getMovementValue(), mvCurrentSelectedColorB.getMovementValue(), 1);

		pdCurrentSelectedElement.drawHierarchyAngular(gl, glu, mvCurrentWidth.getMovementValue(),
			iTargetDepth, mvCurrentStartAngle.getMovementValue(), mvCurrentAngle.getMovementValue(),
			mvCurrentInnerRadius.getMovementValue());

		if (!bAnimationStarted) {
			DrawingState dsNext =
				drawingController.getDrawingState(DrawingController.DRAWING_STATE_FULL_HIERARCHY);

			drawingController.setDrawingState(dsNext);
			radialHierarchy.setAnimationActive(false);
			PartialDisc pdNewRootElement = pdCurrentSelectedElement.getParent();
			radialHierarchy.setCurrentRootElement(pdNewRootElement);
			radialHierarchy.setCurrentMouseOverElement(pdNewRootElement);
			radialHierarchy.setCurrentSelectedElement(pdNewRootElement);

			navigationHistory.addNewHistoryEntry(dsNext, pdNewRootElement, pdNewRootElement,
				radialHierarchy.getMaxDisplayedHierarchyDepth());

			// pdCurrentSelectedElement.setPDDrawingStrategy(DrawingStrategyManager.get().getDrawingStrategy(
			// DrawingStrategyManager.PD_DRAWING_STRATEGY_RAINBOW));
		}

	}
	
//	private void initAnimation(GL gl, GLU glu, float fXCenter, float fYCenter, PartialDisc pdCurrentSelectedElement) {
//
//		float fCurrentAngle = pdCurrentSelectedElement.getCurrentAngle();
//		float fCurrentInnerRadius = pdCurrentSelectedElement.getCurrentInnerRadius();
//		float fCurrentStartAngle = pdCurrentSelectedElement.getCurrentStartAngle();
//		float fCurrentWidth = pdCurrentSelectedElement.getCurrentWidth();
//		
//		PartialDisc pdNewRootElement = pdCurrentSelectedElement.getParent();
//
//		int iDisplayedHierarchyDepth =
//			Math.min(radialHierarchy.getMaxDisplayedHierarchyDepth(), pdNewRootElement
//				.getHierarchyDepth(radialHierarchy.getMaxDisplayedHierarchyDepth()));
//		
//		float fTargetWidth = Math.min(fXCenter * 0.9f, fYCenter * 0.9f) / (float)iDisplayedHierarchyDepth;
//		
//		pdNewRootElement.setCurrentStartAngle(0);
//		pdNewRootElement.simulateDrawHierarchyFull(gl, glu, fTargetWidth, iDisplayedHierarchyDepth);
//		
//		iTargetDepth = pdCurrentSelectedElement.getCurrentDepth();
//		float fTargetAngle = pdCurrentSelectedElement.getCurrentAngle();
//		float fTargetStartAngle = pdCurrentSelectedElement.getCurrentStartAngle();
//		float fTargetInnerRadius = pdCurrentSelectedElement.getCurrentInnerRadius();
//		
//		float fTargetMidAngle = fTargetStartAngle + (fTargetAngle / 2.0f);
//		while (fTargetMidAngle > 360) {
//			fTargetMidAngle -= 360;
//		}
//		while (fTargetMidAngle < 0) {
//			fTargetMidAngle += 360;
//		}
//		
//		if (fTargetStartAngle < fCurrentStartAngle) {
//			fTargetStartAngle += 360;
//		}
////		float fRootTargetStartAngle = fMidAngle + fAngleToAdd;
//
//		// TODO: if new colormode is introduced, use correct colormapping, also use target color from renderstyle
//
//		ColorMapping cmRainbow = ColorMappingManager.get().getColorMapping(EColorMappingType.RAINBOW);
//		float fArRGB[] = cmRainbow.getColor(fTargetMidAngle / 360);
//
//		alMovementValues.clear();
//
//		mvCurrentAngle = createNewMovementValue(fCurrentAngle, fTargetAngle, fAnimationDuration);
//		mvCurrentStartAngle =
//			createNewMovementValue(fCurrentStartAngle, fTargetStartAngle, fAnimationDuration);
//		mvCurrentInnerRadius =
//			createNewMovementValue(fCurrentInnerRadius, fTargetInnerRadius, fAnimationDuration);
//		mvCurrentWidth = createNewMovementValue(fCurrentWidth, fTargetWidth, fAnimationDuration);
//		mvCurrentSelectedColorR = createNewMovementValue(1, fArRGB[0], fAnimationDuration);
//		mvCurrentSelectedColorG = createNewMovementValue(1, fArRGB[1], fAnimationDuration);
//		mvCurrentSelectedColorB = createNewMovementValue(1, fArRGB[2], fAnimationDuration);
//
//		pddsFixedColor =
//			(PDDrawingStrategyFixedColor) DrawingStrategyManager.get().getDrawingStrategy(
//				DrawingStrategyManager.PD_DRAWING_STRATEGY_FIXED_COLOR);
//
//		pdCurrentSelectedElement.setPDDrawingStrategy(pddsFixedColor);
//	}
	
	
	private void initAnimation(GL gl, GLU glu, float fXCenter, float fYCenter, PartialDisc pdCurrentSelectedElement) {

		float fCurrentAngle = pdCurrentSelectedElement.getCurrentAngle();
		float fCurrentInnerRadius = pdCurrentSelectedElement.getCurrentInnerRadius();
		float fCurrentStartAngle = pdCurrentSelectedElement.getCurrentStartAngle();
		float fCurrentWidth = pdCurrentSelectedElement.getCurrentWidth();
		
		PartialDisc pdNewRootElement = pdCurrentSelectedElement.getParent();

		int iDisplayedHierarchyDepth =
			Math.min(radialHierarchy.getMaxDisplayedHierarchyDepth(), pdNewRootElement
				.getHierarchyDepth(radialHierarchy.getMaxDisplayedHierarchyDepth()));
		
		float fTargetWidth = Math.min(fXCenter * 0.9f, fYCenter * 0.9f) / (float)iDisplayedHierarchyDepth;
		
		pdNewRootElement.simulateDrawHierarchyFull(gl, glu, fTargetWidth, iDisplayedHierarchyDepth);
		
		iTargetDepth = pdCurrentSelectedElement.getCurrentDepth();
		float fTargetAngle = pdCurrentSelectedElement.getCurrentAngle();
		float fSimulatedStartAngle = pdCurrentSelectedElement.getCurrentStartAngle();
		float fTargetInnerRadius = pdCurrentSelectedElement.getCurrentInnerRadius();
		
		float fCurrentMidAngle = fCurrentStartAngle + (fCurrentAngle / 2.0f);
		while (fCurrentMidAngle > 360) {
			fCurrentMidAngle -= 360;
		}
		while (fCurrentMidAngle < 0) {
			fCurrentMidAngle += 360;
		}
		
		float fSimulatedMidAngle = fSimulatedStartAngle + (fTargetAngle / 2.0f);
		while (fSimulatedMidAngle > 360) {
			fSimulatedMidAngle -= 360;
		}
		while (fSimulatedMidAngle < 0) {
			fSimulatedMidAngle += 360;
		}
		float fDeltaStartAngle = fCurrentMidAngle - fSimulatedMidAngle;
		
		pdNewRootElement.setCurrentStartAngle(pdNewRootElement.getCurrentStartAngle() + fDeltaStartAngle);
		float fTargetStartAngle = fCurrentMidAngle - (fTargetAngle / 2.0f);
		

		while(fTargetStartAngle < fCurrentStartAngle) {
			fTargetStartAngle += 360;
		}
//		float fRootTargetStartAngle = fMidAngle + fAngleToAdd;

		// TODO: if new colormode is introduced, use correct colormapping, also use target color from renderstyle
		
		float fArRGB[];
		if(DrawingStrategyManager.get().isRainbowStrategyDefault()) {
			ColorMapping cmRainbow = ColorMappingManager.get().getColorMapping(EColorMappingType.RAINBOW);
			fArRGB = cmRainbow.getColor(fCurrentMidAngle / 360);
		}
		else {
			ColorMapping cmExpression = ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);
			fArRGB = cmExpression.getColor(pdCurrentSelectedElement.getAverageExpressionValue());
		}
		
		alMovementValues.clear();

		mvCurrentAngle = createNewMovementValue(fCurrentAngle, fTargetAngle, fAnimationDuration);
		mvCurrentStartAngle =
			createNewMovementValue(fCurrentStartAngle, fTargetStartAngle, fAnimationDuration);
		mvCurrentInnerRadius =
			createNewMovementValue(fCurrentInnerRadius, fTargetInnerRadius, fAnimationDuration);
		mvCurrentWidth = createNewMovementValue(fCurrentWidth, fTargetWidth, fAnimationDuration);
		mvCurrentSelectedColorR = createNewMovementValue(1, fArRGB[0], fAnimationDuration);
		mvCurrentSelectedColorG = createNewMovementValue(1, fArRGB[1], fAnimationDuration);
		mvCurrentSelectedColorB = createNewMovementValue(1, fArRGB[2], fAnimationDuration);

		pddsFixedColor =
			(PDDrawingStrategyFixedColor) DrawingStrategyManager.get().getDrawingStrategy(
				DrawingStrategyManager.PD_DRAWING_STRATEGY_FIXED_COLOR);

		pdCurrentSelectedElement.setPDDrawingStrategy(pddsFixedColor);
	}
	
	

}
