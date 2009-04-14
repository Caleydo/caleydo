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

	public AnimationNewRootElement(DrawingController drawingController, GLRadialHierarchy radialHierarchy) {
		super(drawingController, radialHierarchy);
		fAnimationDuration = DEFAULT_ANIMATION_DURATION;
	}

	@Override
	public void draw(float fXCenter, float fYCenter, GL gl, GLU glu, double dTimePassed) {

		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();

		if (!bAnimationStarted) {
			initAnimation(fXCenter, fYCenter, pdCurrentRootElement);
			bAnimationStarted = true;
		}

		moveValues(dTimePassed);

		if (areStoppingCreteriaFulfilled()) {
			bAnimationStarted = false;
		}

		gl.glLoadIdentity();
		gl.glTranslatef(fXCenter, fYCenter, 0);

		dsFixedColor.setFillColor(mvCurrentRootColorR.getMovementValue(), mvCurrentRootColorG
			.getMovementValue(), mvCurrentRootColorB.getMovementValue(), 1);

		pdCurrentRootElement.drawHierarchyAngular(gl, glu, mvCurrentWidth.getMovementValue(), fTargetDepth,
			mvCurrentRootStartAngle.getMovementValue(), mvCurrentRootAngle.getMovementValue(),
			mvCurrentRootInnerRadius.getMovementValue());

		if (!bAnimationStarted) {
			drawingController.setDrawingState(DrawingController.DRAWING_STATE_FULL_HIERARCHY);
			radialHierarchy.setAnimationActive(false);

			pdCurrentRootElement.setPDDrawingStrategy(DrawingStrategyManager.get().getDrawingStrategy(
				DrawingStrategyManager.PD_DRAWING_STRATEGY_RAINBOW));
		}
	}

	private void initAnimation(float fXCenter, float fYCenter, PartialDisc pdCurrentRootElement) {

		float fCurrentRootAngle = pdCurrentRootElement.getCurrentAngle();
		float fCurrentDepth = pdCurrentRootElement.getCurrentDepth();
		float fCurrentRootInnerRadius = pdCurrentRootElement.getCurrentInnerRadius();
		float fCurrentRootStartAngle = pdCurrentRootElement.getCurrentStartAngle();
		float fCurrentWidth = pdCurrentRootElement.getCurrentWidth();

		fTargetDepth = 
			Math.min(radialHierarchy.getMaxDisplayedHierarchyDepth(), pdCurrentRootElement
				.getHierarchyDepth(radialHierarchy.getMaxDisplayedHierarchyDepth()));
		float fTargetWidth = Math.min(fXCenter * 0.9f, fYCenter * 0.9f) / fTargetDepth;

		float fMidAngle = fCurrentRootStartAngle + (fCurrentRootAngle / 2.0f);
		fTargetDepth = pdCurrentRootElement.getCurrentDepth();

		while (fMidAngle > 360) {
			fMidAngle -= 360;
		}
		while (fMidAngle < 0) {
			fMidAngle += 360;
		}
		float fAngleToAdd = (fMidAngle > fCurrentRootStartAngle) ? -180 : 180;
		float fRootTargetStartAngle = fMidAngle + fAngleToAdd;

		// TODO: if new colormode is introduced, use correct colormapping

		ColorMapping cmRainbow = ColorMappingManager.get().getColorMapping(EColorMappingType.RAINBOW);
		float fArRGB[] = cmRainbow.getColor(fMidAngle / 360);

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

		pdCurrentRootElement.setPDDrawingStrategy(dsFixedColor);
	}

}
