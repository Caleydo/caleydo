package org.caleydo.core.view.opengl.canvas.radial;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;

/**
 * This class represents the animation where the parent of the current root element becomes the new root
 * element. When the animation is finished the follow up drawing state ({@link DrawingStateFullHierarchy})
 * will become active.
 * 
 * @author Christian Partl
 */
@XmlType
public class AnimationParentRootElement
	extends ADrawingStateAnimation {

	public static final float DEFAULT_ANIMATION_DURATION = 0.35f;

	private MovementValue mvCurrentStartAngle;
	private MovementValue mvCurrentAngle;
	private MovementValue mvCurrentWidth;
	private MovementValue mvCurrentInnerRadius;
	private MovementValue mvCurrentSelectedColorR;
	private MovementValue mvCurrentSelectedColorG;
	private MovementValue mvCurrentSelectedColorB;
	private int iTargetDepth;
	private int iAnimationPart;

	private PDDrawingStrategyFixedColor dsFixedColor;

	/**
	 * Constructor.
	 * 
	 * @param drawingController
	 *            DrawingController that holds the drawing states.
	 * @param radialHierarchy
	 *            GLRadialHierarchy instance that is used.
	 * @param navigationHistory
	 *            NavigationHistory instance that shall be used.
	 */
	public AnimationParentRootElement(DrawingController drawingController, GLRadialHierarchy radialHierarchy,
		NavigationHistory navigationHistory) {

		super(drawingController, radialHierarchy, navigationHistory);
		fAnimationDuration = DEFAULT_ANIMATION_DURATION;
	}

	// @Override
	// public void draw(float fXCenter, float fYCenter, GL gl, GLU glu, double dTimePassed) {
	// PartialDisc pdCurrentSelectedElement = radialHierarchy.getCurrentSelectedElement();
	//
	// if (!bAnimationStarted) {
	// initAnimation(fXCenter, fYCenter, pdCurrentSelectedElement);
	// bAnimationStarted = true;
	// radialHierarchy.setAnimationActive(true);
	// }
	//
	// moveValues(dTimePassed);
	//
	// gl.glPushMatrix();
	// gl.glLoadIdentity();
	// gl.glTranslatef(fXCenter, fYCenter, 0);
	//
	// dsFixedColor.setFillColor(mvCurrentSelectedColorR.getMovementValue(), mvCurrentSelectedColorG
	// .getMovementValue(), mvCurrentSelectedColorB.getMovementValue(), 1);
	//
	// pdCurrentSelectedElement.drawHierarchyAngular(gl, glu, mvCurrentWidth.getMovementValue(),
	// iTargetDepth, mvCurrentStartAngle.getMovementValue(), mvCurrentAngle.getMovementValue(),
	// mvCurrentInnerRadius.getMovementValue());
	//
	// if (haveMovementValuesReachedTargets()) {
	// bAnimationStarted = false;
	// }
	//
	// if (!bAnimationStarted) {
	// ADrawingState dsNext =
	// drawingController.getDrawingState(EDrawingStateType.DRAWING_STATE_FULL_HIERARCHY);
	//
	// drawingController.setDrawingState(dsNext);
	// radialHierarchy.setAnimationActive(false);
	// PartialDisc pdNewRootElement = pdCurrentSelectedElement.getParent();
	// radialHierarchy.setCurrentRootElement(pdNewRootElement);
	// radialHierarchy.setCurrentSelectedElement(pdNewRootElement);
	//
	// navigationHistory.addNewHistoryEntry(dsNext, pdNewRootElement, pdNewRootElement, radialHierarchy
	// .getMaxDisplayedHierarchyDepth());
	// radialHierarchy.setNewSelection(ESelectionType.SELECTION, pdNewRootElement, pdNewRootElement);
	// radialHierarchy.setDisplayListDirty();
	// }
	// gl.glPopMatrix();
	// }
	//
	// /**
	// * Initializes the animation, particularly initializes all movement values needed for the animation.
	// *
	// * @param fXCenter
	// * X coordinate of the hierarchy's center.
	// * @param fYCenter
	// * Y coordinate of the hierarchy's center.
	// * @param pdCurrentSelectedElement
	// * Currently selected partial disc.
	// */
	// private void initAnimation(float fXCenter, float fYCenter, PartialDisc pdCurrentSelectedElement) {
	//
	// float fCurrentAngle = pdCurrentSelectedElement.getCurrentAngle();
	// float fCurrentInnerRadius = pdCurrentSelectedElement.getCurrentInnerRadius();
	// float fCurrentStartAngle = pdCurrentSelectedElement.getCurrentStartAngle();
	// float fCurrentWidth = pdCurrentSelectedElement.getCurrentWidth();
	//
	// PartialDisc pdNewRootElement = pdCurrentSelectedElement.getParent();
	//
	// int iDisplayedHierarchyDepth =
	// Math.min(radialHierarchy.getMaxDisplayedHierarchyDepth(), pdNewRootElement.getHierarchyDepth());
	//
	// float fTargetWidth =
	// Math.min(fXCenter * RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE, fYCenter
	// * RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE)
	// / (float) iDisplayedHierarchyDepth;
	//
	// pdNewRootElement.simulateDrawHierarchyFull(fTargetWidth, iDisplayedHierarchyDepth);
	//
	// iTargetDepth = pdCurrentSelectedElement.getCurrentDepth();
	// float fTargetAngle = pdCurrentSelectedElement.getCurrentAngle();
	// float fSimulatedStartAngle = pdCurrentSelectedElement.getCurrentStartAngle();
	// float fTargetInnerRadius = pdCurrentSelectedElement.getCurrentInnerRadius();
	//
	// float fCurrentMidAngle = fCurrentStartAngle + (fCurrentAngle / 2.0f);
	// while (fCurrentMidAngle > 360) {
	// fCurrentMidAngle -= 360;
	// }
	// while (fCurrentMidAngle < 0) {
	// fCurrentMidAngle += 360;
	// }
	//
	// float fSimulatedMidAngle = fSimulatedStartAngle + (fTargetAngle / 2.0f);
	// while (fSimulatedMidAngle > 360) {
	// fSimulatedMidAngle -= 360;
	// }
	// while (fSimulatedMidAngle < 0) {
	// fSimulatedMidAngle += 360;
	// }
	// float fDeltaStartAngle = fCurrentMidAngle - fSimulatedMidAngle;
	//
	// pdNewRootElement.setCurrentStartAngle(pdNewRootElement.getCurrentStartAngle() + fDeltaStartAngle);
	// float fTargetStartAngle = fCurrentMidAngle - (fTargetAngle / 2.0f);
	//
	// while (fTargetStartAngle < fCurrentStartAngle) {
	// fTargetStartAngle += 360;
	// }
	//
	// float fArRGB[];
	// if (DrawingStrategyManager.get().getDefaultDrawingStrategy().getDrawingStrategyType() ==
	// EPDDrawingStrategyType.RAINBOW_COLOR) {
	// ColorMapping cmRainbow = ColorMappingManager.get().getColorMapping(EColorMappingType.RAINBOW);
	// fArRGB = cmRainbow.getColor(fCurrentMidAngle / 360);
	// }
	// else {
	// ColorMapping cmExpression =
	// ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);
	// fArRGB = cmExpression.getColor(pdCurrentSelectedElement.getAverageExpressionValue());
	// }
	//
	// alMovementValues.clear();
	//
	// mvCurrentAngle = createNewMovementValue(fCurrentAngle, fTargetAngle, fAnimationDuration);
	// mvCurrentStartAngle =
	// createNewMovementValue(fCurrentStartAngle, fTargetStartAngle, fAnimationDuration);
	// mvCurrentInnerRadius =
	// createNewMovementValue(fCurrentInnerRadius, fTargetInnerRadius, fAnimationDuration);
	// mvCurrentWidth = createNewMovementValue(fCurrentWidth, fTargetWidth, fAnimationDuration);
	// mvCurrentSelectedColorR =
	// createNewMovementValue(RadialHierarchyRenderStyle.PARTIAL_DISC_ROOT_COLOR[0], fArRGB[0],
	// fAnimationDuration);
	// mvCurrentSelectedColorG =
	// createNewMovementValue(RadialHierarchyRenderStyle.PARTIAL_DISC_ROOT_COLOR[1], fArRGB[1],
	// fAnimationDuration);
	// mvCurrentSelectedColorB =
	// createNewMovementValue(RadialHierarchyRenderStyle.PARTIAL_DISC_ROOT_COLOR[2], fArRGB[2],
	// fAnimationDuration);
	//
	// dsFixedColor =
	// (PDDrawingStrategyFixedColor) DrawingStrategyManager.get().getDrawingStrategy(
	// EPDDrawingStrategyType.FIXED_COLOR);
	//
	// pdCurrentSelectedElement.setPDDrawingStrategy(dsFixedColor);
	// }

	// =============================ALL IN ONE =====================================

//	 @Override
//	 public void draw(float fXCenter, float fYCenter, GL gl, GLU glu, double dTimePassed) {
//	 PartialDisc pdCurrentSelectedElement = radialHierarchy.getCurrentSelectedElement();
//	
//	 if (!bAnimationStarted) {
//	 initAnimation(fXCenter, fYCenter, pdCurrentSelectedElement);
//	 bAnimationStarted = true;
//	 radialHierarchy.setAnimationActive(true);
//	 }
//	
//	 moveValues(dTimePassed);
//	
//	 gl.glPushMatrix();
//	 gl.glLoadIdentity();
//	 gl.glTranslatef(fXCenter, fYCenter, 0);
//	
//	 dsFixedColor.setFillColor(mvCurrentSelectedColorR.getMovementValue(), mvCurrentSelectedColorG
//	 .getMovementValue(), mvCurrentSelectedColorB.getMovementValue(), 1);
//	
//	 pdCurrentSelectedElement.drawHierarchyAngular(gl, glu, mvCurrentWidth.getMovementValue(),
//	 iTargetDepth, mvCurrentStartAngle.getMovementValue(), mvCurrentAngle.getMovementValue(),
//	 mvCurrentInnerRadius.getMovementValue());
//	
//	 if (haveMovementValuesReachedTargets()) {
//	 bAnimationStarted = false;
//	 }
//	
//	 if (!bAnimationStarted) {
//	 ADrawingState dsNext =
//	 drawingController.getDrawingState(EDrawingStateType.DRAWING_STATE_FULL_HIERARCHY);
//	
//	 drawingController.setDrawingState(dsNext);
//	 radialHierarchy.setAnimationActive(false);
//	 PartialDisc pdNewRootElement = pdCurrentSelectedElement.getParent();
//	 radialHierarchy.setCurrentRootElement(pdNewRootElement);
//	 radialHierarchy.setCurrentSelectedElement(pdNewRootElement);
//	
//	 navigationHistory.addNewHistoryEntry(dsNext, pdNewRootElement, pdNewRootElement, radialHierarchy
//	 .getMaxDisplayedHierarchyDepth());
//	 radialHierarchy.setNewSelection(ESelectionType.SELECTION, pdNewRootElement, pdNewRootElement);
//	 radialHierarchy.setDisplayListDirty();
//	 }
//	 gl.glPopMatrix();
//	 }
//	
//	 /**
//	 * Initializes the animation, particularly initializes all movement values needed for the animation.
//	 *
//	 * @param fXCenter
//	 * X coordinate of the hierarchy's center.
//	 * @param fYCenter
//	 * Y coordinate of the hierarchy's center.
//	 * @param pdCurrentSelectedElement
//	 * Currently selected partial disc.
//	 */
//	 private void initAnimation(float fXCenter, float fYCenter, PartialDisc pdCurrentSelectedElement) {
//	
//	 float fCurrentAngle = pdCurrentSelectedElement.getCurrentAngle();
//	 float fCurrentInnerRadius = pdCurrentSelectedElement.getCurrentInnerRadius();
//	 float fCurrentStartAngle = pdCurrentSelectedElement.getCurrentStartAngle();
//	 float fCurrentWidth = pdCurrentSelectedElement.getCurrentWidth();
//	
//	 PartialDisc pdNewRootElement = pdCurrentSelectedElement.getParent();
//	
//	 int iDisplayedHierarchyDepth =
//	 Math.min(radialHierarchy.getMaxDisplayedHierarchyDepth(), pdNewRootElement.getHierarchyDepth());
//	
//	 float fTargetWidth =
//	 Math.min(fXCenter * RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE, fYCenter
//	 * RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE)
//	 / (float) iDisplayedHierarchyDepth;
//	
//	 pdNewRootElement.setCurrentStartAngle(0);
//	 pdNewRootElement.simulateDrawHierarchyFull(fTargetWidth, iDisplayedHierarchyDepth);
//	
//	 iTargetDepth = pdCurrentSelectedElement.getCurrentDepth();
//	 float fTargetAngle = pdCurrentSelectedElement.getCurrentAngle();
//	 float fSimulatedStartAngle = pdCurrentSelectedElement.getCurrentStartAngle();
//	 float fTargetInnerRadius = pdCurrentSelectedElement.getCurrentInnerRadius();
//	
//	 float fCurrentMidAngle = fCurrentStartAngle + (fCurrentAngle / 2.0f);
//	 while (fCurrentMidAngle > 360) {
//	 fCurrentMidAngle -= 360;
//	 }
//	 while (fCurrentMidAngle < 0) {
//	 fCurrentMidAngle += 360;
//	 }
//	
//	 float fArRGB[];
//	 if (DrawingStrategyManager.get().getDefaultDrawingStrategy().getDrawingStrategyType() ==
//	 EPDDrawingStrategyType.RAINBOW_COLOR) {
//	 ColorMapping cmRainbow = ColorMappingManager.get().getColorMapping(EColorMappingType.RAINBOW);
//	 fArRGB = cmRainbow.getColor(fCurrentMidAngle / 360);
//	 }
//	 else {
//	 ColorMapping cmExpression =
//	 ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);
//	 fArRGB = cmExpression.getColor(pdCurrentSelectedElement.getAverageExpressionValue());
//	 }
//	
//	 alMovementValues.clear();
//	
//	 mvCurrentAngle = createNewMovementValue(fCurrentAngle, fTargetAngle, fAnimationDuration);
//	 mvCurrentStartAngle =
//	 createNewMovementValue(fCurrentStartAngle, fSimulatedStartAngle, fAnimationDuration);
//	 mvCurrentInnerRadius =
//	 createNewMovementValue(fCurrentInnerRadius, fTargetInnerRadius, fAnimationDuration);
//	 mvCurrentWidth = createNewMovementValue(fCurrentWidth, fTargetWidth, fAnimationDuration);
//	 mvCurrentSelectedColorR =
//	 createNewMovementValue(RadialHierarchyRenderStyle.PARTIAL_DISC_ROOT_COLOR[0], fArRGB[0],
//	 fAnimationDuration);
//	 mvCurrentSelectedColorG =
//	 createNewMovementValue(RadialHierarchyRenderStyle.PARTIAL_DISC_ROOT_COLOR[1], fArRGB[1],
//	 fAnimationDuration);
//	 mvCurrentSelectedColorB =
//	 createNewMovementValue(RadialHierarchyRenderStyle.PARTIAL_DISC_ROOT_COLOR[2], fArRGB[2],
//	 fAnimationDuration);
//	
//	 dsFixedColor =
//	 (PDDrawingStrategyFixedColor) DrawingStrategyManager.get().getDrawingStrategy(
//	 EPDDrawingStrategyType.FIXED_COLOR);
//	
//	 pdCurrentSelectedElement.setPDDrawingStrategy(dsFixedColor);
//	 }

	// ========================== ROTATION SECOND ========================================0000

	@Override
	public void draw(float fXCenter, float fYCenter, GL gl, GLU glu, double dTimePassed) {
		PartialDisc pdCurrentSelectedElement = radialHierarchy.getCurrentSelectedElement();

		if (!bAnimationStarted) {
			initAnimationFirstPart(fXCenter, fYCenter, pdCurrentSelectedElement);
			bAnimationStarted = true;
			radialHierarchy.setAnimationActive(true);
			iAnimationPart = 1;
		}

		moveValues(dTimePassed);

		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glTranslatef(fXCenter, fYCenter, 0);

		if (iAnimationPart == 1) {
			dsFixedColor.setFillColor(mvCurrentSelectedColorR.getMovementValue(), mvCurrentSelectedColorG
				.getMovementValue(), mvCurrentSelectedColorB.getMovementValue(), 1);

			pdCurrentSelectedElement.drawHierarchyAngular(gl, glu, mvCurrentWidth.getMovementValue(),
				iTargetDepth, mvCurrentStartAngle.getMovementValue(), mvCurrentAngle.getMovementValue(),
				mvCurrentInnerRadius.getMovementValue());
		}
		else {
			pdCurrentSelectedElement.drawHierarchyAngular(gl, glu,
				pdCurrentSelectedElement.getCurrentWidth(), iTargetDepth, mvCurrentStartAngle
					.getMovementValue(), pdCurrentSelectedElement.getCurrentAngle(), pdCurrentSelectedElement
					.getCurrentInnerRadius());
		}

		if (haveMovementValuesReachedTargets()) {
			iAnimationPart++;
			if (iAnimationPart == 2) {
				initAnimationSecondPart(fXCenter, fYCenter, pdCurrentSelectedElement);
			}
			if (iAnimationPart > 2) {
				bAnimationStarted = false;
			}
		}

		if (!bAnimationStarted) {
			ADrawingState dsNext =
				drawingController.getDrawingState(EDrawingStateType.DRAWING_STATE_FULL_HIERARCHY);

			drawingController.setDrawingState(dsNext);
			radialHierarchy.setAnimationActive(false);
			PartialDisc pdNewRootElement = pdCurrentSelectedElement.getParent();
			radialHierarchy.setCurrentRootElement(pdNewRootElement);
			radialHierarchy.setCurrentSelectedElement(pdNewRootElement);

			navigationHistory.addNewHistoryEntry(dsNext, pdNewRootElement, pdNewRootElement, radialHierarchy
				.getMaxDisplayedHierarchyDepth());
			radialHierarchy.setNewSelection(ESelectionType.SELECTION, pdNewRootElement, pdNewRootElement);
			radialHierarchy.setDisplayListDirty();
		}
		gl.glPopMatrix();
	}

	/**
	 * Initializes the animation, particularly initializes all movement values needed for the animation.
	 * 
	 * @param fXCenter
	 *            X coordinate of the hierarchy's center.
	 * @param fYCenter
	 *            Y coordinate of the hierarchy's center.
	 * @param pdCurrentSelectedElement
	 *            Currently selected partial disc.
	 */
	private void initAnimationFirstPart(float fXCenter, float fYCenter, PartialDisc pdCurrentSelectedElement) {

		float fCurrentAngle = pdCurrentSelectedElement.getCurrentAngle();
		float fCurrentInnerRadius = pdCurrentSelectedElement.getCurrentInnerRadius();
		float fCurrentStartAngle = pdCurrentSelectedElement.getCurrentStartAngle();
		float fCurrentWidth = pdCurrentSelectedElement.getCurrentWidth();

		PartialDisc pdNewRootElement = pdCurrentSelectedElement.getParent();

		int iDisplayedHierarchyDepth =
			Math.min(radialHierarchy.getMaxDisplayedHierarchyDepth(), pdNewRootElement.getHierarchyDepth());

		float fTargetWidth =
			Math.min(fXCenter * RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE, fYCenter
				* RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE)
				/ (float) iDisplayedHierarchyDepth;

		pdNewRootElement.simulateDrawHierarchyFull(fTargetWidth, iDisplayedHierarchyDepth);

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

		while (fTargetStartAngle < fCurrentStartAngle) {
			fTargetStartAngle += 360;
		}

		float fArRGB[];
		if (DrawingStrategyManager.get().getDefaultDrawingStrategy().getDrawingStrategyType() == EPDDrawingStrategyType.RAINBOW_COLOR) {
			ColorMapping cmRainbow = ColorMappingManager.get().getColorMapping(EColorMappingType.RAINBOW);
			fArRGB = cmRainbow.getColor(fCurrentMidAngle / 360);
		}
		else {
			ColorMapping cmExpression =
				ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);
			fArRGB = cmExpression.getColor(pdCurrentSelectedElement.getAverageExpressionValue());
		}

		alMovementValues.clear();

		mvCurrentAngle = createNewMovementValue(fCurrentAngle, fTargetAngle, fAnimationDuration);
		mvCurrentStartAngle =
			createNewMovementValue(fCurrentStartAngle, fTargetStartAngle, fAnimationDuration);
		mvCurrentInnerRadius =
			createNewMovementValue(fCurrentInnerRadius, fTargetInnerRadius, fAnimationDuration);
		mvCurrentWidth = createNewMovementValue(fCurrentWidth, fTargetWidth, fAnimationDuration);
		mvCurrentSelectedColorR =
			createNewMovementValue(RadialHierarchyRenderStyle.PARTIAL_DISC_ROOT_COLOR[0], fArRGB[0],
				fAnimationDuration);
		mvCurrentSelectedColorG =
			createNewMovementValue(RadialHierarchyRenderStyle.PARTIAL_DISC_ROOT_COLOR[1], fArRGB[1],
				fAnimationDuration);
		mvCurrentSelectedColorB =
			createNewMovementValue(RadialHierarchyRenderStyle.PARTIAL_DISC_ROOT_COLOR[2], fArRGB[2],
				fAnimationDuration);

		dsFixedColor =
			(PDDrawingStrategyFixedColor) DrawingStrategyManager.get().getDrawingStrategy(
				EPDDrawingStrategyType.FIXED_COLOR);

		pdCurrentSelectedElement.setPDDrawingStrategyChildren(DrawingStrategyManager.get()
			.getDefaultDrawingStrategy(), radialHierarchy.getMaxDisplayedHierarchyDepth());
		pdCurrentSelectedElement.setPDDrawingStrategy(dsFixedColor);

	}

	private void initAnimationSecondPart(float fXCenter, float fYCenter, PartialDisc pdCurrentSelectedElement) {
		float fCurrentStartAngle = pdCurrentSelectedElement.getCurrentStartAngle();
		PartialDisc pdNewRootElement = pdCurrentSelectedElement.getParent();

		int iDisplayedHierarchyDepth =
			Math.min(radialHierarchy.getMaxDisplayedHierarchyDepth(), pdNewRootElement.getHierarchyDepth());

		float fTargetWidth =
			Math.min(fXCenter * RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE, fYCenter
				* RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE)
				/ (float) iDisplayedHierarchyDepth;

		pdNewRootElement.setCurrentStartAngle(0);
		pdNewRootElement.simulateDrawHierarchyFull(fTargetWidth, iDisplayedHierarchyDepth);
		float fTargetStartAngle = pdCurrentSelectedElement.getCurrentStartAngle();
		
		
		alMovementValues.clear();

		mvCurrentStartAngle = createNewMovementValue(fCurrentStartAngle, fTargetStartAngle, fAnimationDuration);

	}
	
	
	
//=============================ROTATION FIRST===================================
	
	
	
//	@Override
//	public void draw(float fXCenter, float fYCenter, GL gl, GLU glu, double dTimePassed) {
//		PartialDisc pdCurrentSelectedElement = radialHierarchy.getCurrentSelectedElement();
//
//		if (!bAnimationStarted) {
//			initAnimationFirstPart(fXCenter, fYCenter, pdCurrentSelectedElement);
//			bAnimationStarted = true;
//			radialHierarchy.setAnimationActive(true);
//			iAnimationPart = 1;
//		}
//
//		moveValues(dTimePassed);
//
//		gl.glPushMatrix();
//		gl.glLoadIdentity();
//		gl.glTranslatef(fXCenter, fYCenter, 0);
//
//		if (iAnimationPart == 1) {
//			pdCurrentSelectedElement.drawHierarchyAngular(gl, glu,
//				pdCurrentSelectedElement.getCurrentWidth(), iTargetDepth, mvCurrentStartAngle
//					.getMovementValue(), pdCurrentSelectedElement.getCurrentAngle(), pdCurrentSelectedElement
//					.getCurrentInnerRadius());
//		}
//		else {
//			dsFixedColor.setFillColor(mvCurrentSelectedColorR.getMovementValue(), mvCurrentSelectedColorG
//				.getMovementValue(), mvCurrentSelectedColorB.getMovementValue(), 1);
//
//			pdCurrentSelectedElement.drawHierarchyAngular(gl, glu, mvCurrentWidth.getMovementValue(),
//				iTargetDepth, mvCurrentStartAngle.getMovementValue(), mvCurrentAngle.getMovementValue(),
//				mvCurrentInnerRadius.getMovementValue());
//		}
//
//		if (haveMovementValuesReachedTargets()) {
//			iAnimationPart++;
//			if (iAnimationPart == 2) {
//				initAnimationSecondPart(fXCenter, fYCenter, pdCurrentSelectedElement);
//			}
//			if (iAnimationPart > 2) {
//				bAnimationStarted = false;
//			}
//		}
//
//		if (!bAnimationStarted) {
//			ADrawingState dsNext =
//				drawingController.getDrawingState(EDrawingStateType.DRAWING_STATE_FULL_HIERARCHY);
//
//			drawingController.setDrawingState(dsNext);
//			radialHierarchy.setAnimationActive(false);
//			PartialDisc pdNewRootElement = pdCurrentSelectedElement.getParent();
//			radialHierarchy.setCurrentRootElement(pdNewRootElement);
//			radialHierarchy.setCurrentSelectedElement(pdNewRootElement);
//
//			navigationHistory.addNewHistoryEntry(dsNext, pdNewRootElement, pdNewRootElement, radialHierarchy
//				.getMaxDisplayedHierarchyDepth());
//			radialHierarchy.setNewSelection(ESelectionType.SELECTION, pdNewRootElement, pdNewRootElement);
//			radialHierarchy.setDisplayListDirty();
//		}
//		gl.glPopMatrix();
//	}
//
//	/**
//	 * Initializes the animation, particularly initializes all movement values needed for the animation.
//	 * 
//	 * @param fXCenter
//	 *            X coordinate of the hierarchy's center.
//	 * @param fYCenter
//	 *            Y coordinate of the hierarchy's center.
//	 * @param pdCurrentSelectedElement
//	 *            Currently selected partial disc.
//	 */
//	private void initAnimationSecondPart(float fXCenter, float fYCenter, PartialDisc pdCurrentSelectedElement) {
//
//		float fCurrentAngle = pdCurrentSelectedElement.getCurrentAngle();
//		float fCurrentInnerRadius = pdCurrentSelectedElement.getCurrentInnerRadius();
//		float fCurrentStartAngle = pdCurrentSelectedElement.getCurrentStartAngle();
//		float fCurrentWidth = pdCurrentSelectedElement.getCurrentWidth();
//
//		PartialDisc pdNewRootElement = pdCurrentSelectedElement.getParent();
//
//		int iDisplayedHierarchyDepth =
//			Math.min(radialHierarchy.getMaxDisplayedHierarchyDepth(), pdNewRootElement.getHierarchyDepth());
//
//		float fTargetWidth =
//			Math.min(fXCenter * RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE, fYCenter
//				* RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE)
//				/ (float) iDisplayedHierarchyDepth;
//
//		pdNewRootElement.simulateDrawHierarchyFull(fTargetWidth, iDisplayedHierarchyDepth);
//
//		iTargetDepth = pdCurrentSelectedElement.getCurrentDepth();
//		float fTargetAngle = pdCurrentSelectedElement.getCurrentAngle();
//		float fSimulatedStartAngle = pdCurrentSelectedElement.getCurrentStartAngle();
//		float fTargetInnerRadius = pdCurrentSelectedElement.getCurrentInnerRadius();
//
//		float fCurrentMidAngle = fCurrentStartAngle + (fCurrentAngle / 2.0f);
//		while (fCurrentMidAngle > 360) {
//			fCurrentMidAngle -= 360;
//		}
//		while (fCurrentMidAngle < 0) {
//			fCurrentMidAngle += 360;
//		}
//
//		float fSimulatedMidAngle = fSimulatedStartAngle + (fTargetAngle / 2.0f);
//		while (fSimulatedMidAngle > 360) {
//			fSimulatedMidAngle -= 360;
//		}
//		while (fSimulatedMidAngle < 0) {
//			fSimulatedMidAngle += 360;
//		}
//		float fDeltaStartAngle = fCurrentMidAngle - fSimulatedMidAngle;
//
//		pdNewRootElement.setCurrentStartAngle(pdNewRootElement.getCurrentStartAngle() + fDeltaStartAngle);
//		float fTargetStartAngle = fCurrentMidAngle - (fTargetAngle / 2.0f);
//
//		while (fTargetStartAngle < fCurrentStartAngle) {
//			fTargetStartAngle += 360;
//		}
//
//		float fArRGB[];
//		if (DrawingStrategyManager.get().getDefaultDrawingStrategy().getDrawingStrategyType() == EPDDrawingStrategyType.RAINBOW_COLOR) {
//			ColorMapping cmRainbow = ColorMappingManager.get().getColorMapping(EColorMappingType.RAINBOW);
//			fArRGB = cmRainbow.getColor(fCurrentMidAngle / 360);
//		}
//		else {
//			ColorMapping cmExpression =
//				ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);
//			fArRGB = cmExpression.getColor(pdCurrentSelectedElement.getAverageExpressionValue());
//		}
//
//		alMovementValues.clear();
//
//		mvCurrentAngle = createNewMovementValue(fCurrentAngle, fTargetAngle, fAnimationDuration);
//		mvCurrentStartAngle =
//			createNewMovementValue(fCurrentStartAngle, fTargetStartAngle, fAnimationDuration);
//		mvCurrentInnerRadius =
//			createNewMovementValue(fCurrentInnerRadius, fTargetInnerRadius, fAnimationDuration);
//		mvCurrentWidth = createNewMovementValue(fCurrentWidth, fTargetWidth, fAnimationDuration);
//		mvCurrentSelectedColorR =
//			createNewMovementValue(RadialHierarchyRenderStyle.PARTIAL_DISC_ROOT_COLOR[0], fArRGB[0],
//				fAnimationDuration);
//		mvCurrentSelectedColorG =
//			createNewMovementValue(RadialHierarchyRenderStyle.PARTIAL_DISC_ROOT_COLOR[1], fArRGB[1],
//				fAnimationDuration);
//		mvCurrentSelectedColorB =
//			createNewMovementValue(RadialHierarchyRenderStyle.PARTIAL_DISC_ROOT_COLOR[2], fArRGB[2],
//				fAnimationDuration);
//
//		dsFixedColor =
//			(PDDrawingStrategyFixedColor) DrawingStrategyManager.get().getDrawingStrategy(
//				EPDDrawingStrategyType.FIXED_COLOR);
//
//		pdCurrentSelectedElement.setPDDrawingStrategy(dsFixedColor);
//
//	}
//
//	private void initAnimationFirstPart(float fXCenter, float fYCenter, PartialDisc pdCurrentSelectedElement) {
//		float fCurrentStartAngle = pdCurrentSelectedElement.getCurrentStartAngle();
//		PartialDisc pdNewRootElement = pdCurrentSelectedElement.getParent();
//
//		int iDisplayedHierarchyDepth =
//			Math.min(radialHierarchy.getMaxDisplayedHierarchyDepth(), pdNewRootElement.getHierarchyDepth());
//
//		float fTargetWidth =
//			Math.min(fXCenter * RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE, fYCenter
//				* RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE)
//				/ (float) iDisplayedHierarchyDepth;
//
//		pdNewRootElement.setCurrentStartAngle(0);
//		pdNewRootElement.simulateDrawHierarchyFull(fTargetWidth, iDisplayedHierarchyDepth);
//		float fTargetAngle = pdCurrentSelectedElement.getCurrentAngle();
//		float fSimulatedStartAngle = pdCurrentSelectedElement.getCurrentStartAngle();
//		
//		float fTargetStartAngle = fSimulatedStartAngle + (fTargetAngle / 2.0f) + 180;
//		while (fTargetStartAngle > 360) {
//			fTargetStartAngle -= 360;
//		}
//		while (fTargetStartAngle < 0) {
//			fTargetStartAngle += 360;
//		}
//		
//
//		if (Math.abs((fCurrentStartAngle - fTargetStartAngle)) > Math.abs(((fTargetStartAngle - 360) - fCurrentStartAngle)))
//			fTargetStartAngle -= 360;
//		
//		alMovementValues.clear();
//
//		mvCurrentStartAngle = createNewMovementValue(fCurrentStartAngle, fTargetStartAngle, fAnimationDuration);
//
//		pdCurrentSelectedElement.setPDDrawingStrategyChildren(DrawingStrategyManager.get()
//			.getDefaultDrawingStrategy(), radialHierarchy.getMaxDisplayedHierarchyDepth());
//		
//		
//		iDisplayedHierarchyDepth =
//			Math.min(radialHierarchy.getMaxDisplayedHierarchyDepth(), pdCurrentSelectedElement.getHierarchyDepth());
//
//		fTargetWidth =
//			Math.min(fXCenter * RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE, fYCenter
//				* RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE)
//				/ (float) iDisplayedHierarchyDepth;
//		
//		pdCurrentSelectedElement.simulateDrawHierarchyFull(fTargetWidth, iDisplayedHierarchyDepth);
//		
//		dsFixedColor =
//			(PDDrawingStrategyFixedColor) DrawingStrategyManager.get().createDrawingStrategy(
//				EPDDrawingStrategyType.FIXED_COLOR);
//		dsFixedColor.setFillColor(1, 1, 1, 1);
//
//		pdCurrentSelectedElement.setPDDrawingStrategy(dsFixedColor);
//
//	}

	@Override
	public EDrawingStateType getType() {
		return EDrawingStateType.ANIMATION_PARENT_ROOT_ELEMENT;
	}
}
