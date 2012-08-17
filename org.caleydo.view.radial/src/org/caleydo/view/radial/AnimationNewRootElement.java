/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.radial;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.clusterer.EDrawingStateType;
import org.caleydo.core.util.clusterer.EPDDrawingStrategyType;
import org.caleydo.core.view.opengl.util.animation.MovementValue;

/**
 * This class represents the animation for a selected partial disc that becomes
 * the new root element. When the animation is finished the follow up drawing
 * state ({@link DrawingStateFullHierarchy}) will become active.
 * 
 * @author Christian Partl
 */
@XmlType
public class AnimationNewRootElement extends ADrawingStateAnimation {

	public static final float DEFAULT_ANIMATION_DURATION = 0.35f;

	private static final float TARGET_ROOT_ANGLE = 360.0f;
	private static final float TARGET_ROOT_INNER_RADIUS = 0.0f;

	private MovementValue mvCurrentRootAngle;
	private MovementValue mvCurrentRootStartAngle;
	private MovementValue mvCurrentRootInnerRadius;
	private MovementValue mvCurrentWidth;
	private MovementValue mvCurrentRootColorR;
	private MovementValue mvCurrentRootColorG;
	private MovementValue mvCurrentRootColorB;

	private int fTargetDepth;

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
	public AnimationNewRootElement(DrawingController drawingController,
			GLRadialHierarchy radialHierarchy, NavigationHistory navigationHistory) {

		super(drawingController, radialHierarchy, navigationHistory);
		fAnimationDuration = DEFAULT_ANIMATION_DURATION;
	}

	// @Override
	// public void draw(float fXCenter, float fYCenter, GL2 gl, GLU glu, double
	// dTimePassed) {
	//
	// PartialDisc pdCurrentSelectedElement =
	// radialHierarchy.getCurrentSelectedElement();
	//
	// if (!bAnimationStarted) {
	// initAnimation(fXCenter, fYCenter, pdCurrentSelectedElement);
	// bAnimationStarted = true;
	// radialHierarchy.setAnimationActive(true);
	// }
	//
	// moveValues(dTimePassed);
	//
	// dsFixedColor.setFillColor(mvCurrentRootColorR.getMovementValue(),
	// mvCurrentRootColorG
	// .getMovementValue(), mvCurrentRootColorB.getMovementValue(), 1);
	//
	// gl.glPushMatrix();
	// gl.glLoadIdentity();
	// gl.glTranslatef(fXCenter, fYCenter, 0);
	//
	// pdCurrentSelectedElement.drawHierarchyAngular(gl, glu,
	// mvCurrentWidth.getMovementValue(),
	// fTargetDepth, mvCurrentRootStartAngle.getMovementValue(),
	// mvCurrentRootAngle.getMovementValue(),
	// mvCurrentRootInnerRadius.getMovementValue());
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
	// radialHierarchy.setCurrentRootElement(pdCurrentSelectedElement);
	//
	// navigationHistory.addNewHistoryEntry(dsNext, pdCurrentSelectedElement,
	// pdCurrentSelectedElement,
	// radialHierarchy.getMaxDisplayedHierarchyDepth());
	// radialHierarchy.setNewSelection(SelectionType.SELECTION,
	// pdCurrentSelectedElement,
	// pdCurrentSelectedElement);
	// radialHierarchy.setDisplayListDirty();
	//
	// }
	// gl.glPopMatrix();
	// }
	//
	// /**
	// * Initializes the animation, particularly initializes all movement values
	// needed for the animation.
	// *
	// * @param fXCenter
	// * X coordinate of the hierarchy's center.
	// * @param fYCenter
	// * Y coordinate of the hierarchy's center.
	// * @param pdCurrentSelectedElement
	// * Currently selected partial disc.
	// */
	// private void initAnimation(float fXCenter, float fYCenter, PartialDisc
	// pdCurrentSelectedElement) {
	//
	// float fCurrentSelectedAngle = pdCurrentSelectedElement.getCurrentAngle();
	// float fCurrentSelectedInnerRadius =
	// pdCurrentSelectedElement.getCurrentInnerRadius();
	// float fCurrentSelectedStartAngle =
	// pdCurrentSelectedElement.getCurrentStartAngle();
	// float fCurrentWidth = pdCurrentSelectedElement.getCurrentWidth();
	//
	// fTargetDepth =
	// Math.min(radialHierarchy.getMaxDisplayedHierarchyDepth(),
	// pdCurrentSelectedElement
	// .getHierarchyDepth());
	// float fTargetWidth =
	// Math.min(fXCenter * RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE,
	// fYCenter
	// * RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE)
	// / fTargetDepth;
	//
	// float fMidAngle = fCurrentSelectedStartAngle + (fCurrentSelectedAngle /
	// 2.0f);
	// fTargetDepth = pdCurrentSelectedElement.getCurrentDepth();
	//
	// while (fMidAngle > 360) {
	// fMidAngle -= 360;
	// }
	// while (fMidAngle < 0) {
	// fMidAngle += 360;
	// }
	// float fAngleToAdd = (fMidAngle > fCurrentSelectedStartAngle) ? -180 :
	// 180;
	// float fSelectedTargetStartAngle = fMidAngle + fAngleToAdd;
	//
	// float fArRGB[];
	// if
	// (DrawingStrategyManager.get().getDefaultDrawingStrategy().getDrawingStrategyType()
	// ==
	// EPDDrawingStrategyType.RAINBOW_COLOR) {
	// ColorMapping cmRainbow =
	// ColorMappingManager.get().getColorMapping(EColorMappingType.RAINBOW);
	// fArRGB = cmRainbow.getColor(fMidAngle / 360);
	// }
	// else {
	// ColorMapping cmExpression =
	// ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);
	// fArRGB =
	// cmExpression.getColor(pdCurrentSelectedElement.getAverageExpressionValue());
	// }
	//
	// alMovementValues.clear();
	//
	// mvCurrentRootAngle =
	// createNewMovementValue(fCurrentSelectedAngle, TARGET_ROOT_ANGLE,
	// fAnimationDuration);
	// mvCurrentRootStartAngle =
	// createNewMovementValue(fCurrentSelectedStartAngle,
	// fSelectedTargetStartAngle, fAnimationDuration);
	// mvCurrentRootInnerRadius =
	// createNewMovementValue(fCurrentSelectedInnerRadius,
	// TARGET_ROOT_INNER_RADIUS, fAnimationDuration);
	// mvCurrentWidth = createNewMovementValue(fCurrentWidth, fTargetWidth,
	// fAnimationDuration);
	// mvCurrentRootColorR =
	// createNewMovementValue(fArRGB[0],
	// RadialHierarchyRenderStyle.PARTIAL_DISC_ROOT_COLOR[0],
	// fAnimationDuration);
	// mvCurrentRootColorG =
	// createNewMovementValue(fArRGB[1],
	// RadialHierarchyRenderStyle.PARTIAL_DISC_ROOT_COLOR[1],
	// fAnimationDuration);
	// mvCurrentRootColorB =
	// createNewMovementValue(fArRGB[2],
	// RadialHierarchyRenderStyle.PARTIAL_DISC_ROOT_COLOR[2],
	// fAnimationDuration);
	//
	// dsFixedColor =
	// (PDDrawingStrategyFixedColor)
	// DrawingStrategyManager.get().getDrawingStrategy(
	// EPDDrawingStrategyType.FIXED_COLOR);
	//
	// pdCurrentSelectedElement.setPDDrawingStrategy(dsFixedColor);
	// }

	// ============================= ALL IN ONE
	// ========================================

	@Override
	public void draw(float fXCenter, float fYCenter, GL2 gl, GLU glu, double dTimePassed) {

		PartialDisc pdCurrentSelectedElement = radialHierarchy
				.getCurrentSelectedElement();

		if (!bAnimationStarted) {
			initAnimation(fXCenter, fYCenter, pdCurrentSelectedElement);
			bAnimationStarted = true;
			radialHierarchy.setAnimationActive(true);
		}

		moveValues(dTimePassed);

		dsFixedColor.setFillColor(mvCurrentRootColorR.getMovementValue(),
				mvCurrentRootColorG.getMovementValue(),
				mvCurrentRootColorB.getMovementValue(), 1);

		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glTranslatef(fXCenter, fYCenter, 0);

		pdCurrentSelectedElement.drawHierarchyAngular(gl, glu,
				mvCurrentWidth.getMovementValue(), fTargetDepth,
				mvCurrentRootStartAngle.getMovementValue(),
				mvCurrentRootAngle.getMovementValue(),
				mvCurrentRootInnerRadius.getMovementValue());

		if (haveMovementValuesReachedTargets()) {
			bAnimationStarted = false;
		}

		if (!bAnimationStarted) {
			ADrawingState dsNext = drawingController
					.getDrawingState(EDrawingStateType.DRAWING_STATE_FULL_HIERARCHY);

			drawingController.setDrawingState(dsNext);
			radialHierarchy.setAnimationActive(false);
			radialHierarchy.setCurrentRootElement(pdCurrentSelectedElement);

			navigationHistory.addNewHistoryEntry(dsNext, pdCurrentSelectedElement,
					pdCurrentSelectedElement,
					radialHierarchy.getMaxDisplayedHierarchyDepth());
			radialHierarchy.setNewSelection(SelectionType.SELECTION,
					pdCurrentSelectedElement);
			radialHierarchy.setDisplayListDirty();

		}
		gl.glPopMatrix();
	}

	/**
	 * Initializes the animation, particularly initializes all movement values
	 * needed for the animation.
	 * 
	 * @param fXCenter
	 *            X coordinate of the hierarchy's center.
	 * @param fYCenter
	 *            Y coordinate of the hierarchy's center.
	 * @param pdCurrentSelectedElement
	 *            Currently selected partial disc.
	 */
	private void initAnimation(float fXCenter, float fYCenter,
			PartialDisc pdCurrentSelectedElement) {

		float fCurrentSelectedAngle = pdCurrentSelectedElement.getCurrentAngle();
		float fCurrentSelectedInnerRadius = pdCurrentSelectedElement
				.getCurrentInnerRadius();
		float fCurrentSelectedStartAngle = pdCurrentSelectedElement
				.getCurrentStartAngle();
		float fCurrentWidth = pdCurrentSelectedElement.getCurrentWidth();

		fTargetDepth = Math.min(radialHierarchy.getMaxDisplayedHierarchyDepth(),
				pdCurrentSelectedElement.getDepth());
		float fTargetWidth = Math.min(fXCenter
				* RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE, fYCenter
				* RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE)
				/ fTargetDepth;

		float fMidAngle = fCurrentSelectedStartAngle + (fCurrentSelectedAngle / 2.0f);
		fTargetDepth = pdCurrentSelectedElement.getCurrentDepth();

		while (fMidAngle > 360) {
			fMidAngle -= 360;
		}
		while (fMidAngle < 0) {
			fMidAngle += 360;
		}

		float fArRGB[] = radialHierarchy.getDrawingStrategyManager()
				.getDefaultDrawingStrategy().getColor(pdCurrentSelectedElement);
		// if
		// (radialHierarchy.getDrawingStrategyManager().getDefaultDrawingStrategy().getDrawingStrategyType()
		// == EPDDrawingStrategyType.RAINBOW_COLOR) {
		// ColorMapping cmRainbow =
		// ColorMappingManager.get().getColorMapping(EColorMappingType.RAINBOW);
		// fArRGB = cmRainbow.getColor(fMidAngle / 360);
		// }
		// else {
		// ColorMapping cmExpression =
		// ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);
		// IHierarchyData<?> hierarchyData =
		// pdCurrentSelectedElement.getHierarchyData();
		// ClusterNode clusterNode = null;
		// if (hierarchyData instanceof ClusterNode) {
		// clusterNode = (ClusterNode) hierarchyData;
		// fArRGB =
		// cmExpression.getColor(clusterNode.getAverageExpressionValue());
		// }
		// else {
		// fArRGB = new float[] { 1.0f, 1.0f, 1.0f };
		// }
		// }

		alMovementValues.clear();

		fAnimationDuration = 0.2f + ((TARGET_ROOT_ANGLE - fCurrentSelectedAngle) * 0.5f / TARGET_ROOT_ANGLE);

		mvCurrentRootAngle = createNewMovementValue(fCurrentSelectedAngle,
				TARGET_ROOT_ANGLE, fAnimationDuration);

		mvCurrentRootStartAngle = createNewMovementValue(fCurrentSelectedStartAngle, 0,
				fAnimationDuration);
		mvCurrentRootInnerRadius = createNewMovementValue(fCurrentSelectedInnerRadius,
				TARGET_ROOT_INNER_RADIUS, fAnimationDuration);
		mvCurrentWidth = createNewMovementValue(fCurrentWidth, fTargetWidth,
				fAnimationDuration);
		mvCurrentRootColorR = createNewMovementValue(fArRGB[0],
				RadialHierarchyRenderStyle.PARTIAL_DISC_ROOT_COLOR[0], fAnimationDuration);
		mvCurrentRootColorG = createNewMovementValue(fArRGB[1],
				RadialHierarchyRenderStyle.PARTIAL_DISC_ROOT_COLOR[1], fAnimationDuration);
		mvCurrentRootColorB = createNewMovementValue(fArRGB[2],
				RadialHierarchyRenderStyle.PARTIAL_DISC_ROOT_COLOR[2], fAnimationDuration);

		dsFixedColor = (PDDrawingStrategyFixedColor) radialHierarchy
				.getDrawingStrategyManager().createDrawingStrategy(
						EPDDrawingStrategyType.FIXED_COLOR);

		pdCurrentSelectedElement.setPDDrawingStrategy(dsFixedColor);
	}

	// ============================= ROTATION FIRST
	// ========================================

	// @Override
	// public void draw(float fXCenter, float fYCenter, GL2 gl, GLU glu, double
	// dTimePassed) {
	//
	// PartialDisc pdCurrentSelectedElement =
	// radialHierarchy.getCurrentSelectedElement();
	//
	// if (!bAnimationStarted) {
	// initAnimationFirstPart(fXCenter, fYCenter, pdCurrentSelectedElement);
	// bAnimationStarted = true;
	// radialHierarchy.setAnimationActive(true);
	// iAnimationPart = 1;
	// pdCurrentSelectedElement.setPDDrawingStrategyChildren(DrawingStrategyManager.get()
	// .getDefaultDrawingStrategy(),
	// radialHierarchy.getMaxDisplayedHierarchyDepth());
	// }
	//
	// moveValues(dTimePassed);
	//
	// if (iAnimationPart == 2) {
	// dsFixedColor.setFillColor(mvCurrentRootColorR.getMovementValue(),
	// mvCurrentRootColorG
	// .getMovementValue(), mvCurrentRootColorB.getMovementValue(), 1);
	// }
	//
	// gl.glPushMatrix();
	// gl.glLoadIdentity();
	// gl.glTranslatef(fXCenter, fYCenter, 0);
	//
	// if (iAnimationPart == 1) {
	// pdCurrentSelectedElement.drawHierarchyAngular(gl, glu,
	// pdCurrentSelectedElement.getCurrentWidth(), fTargetDepth,
	// mvCurrentRootStartAngle
	// .getMovementValue(), pdCurrentSelectedElement.getCurrentAngle(),
	// pdCurrentSelectedElement
	// .getCurrentInnerRadius());
	// }
	// else {
	// pdCurrentSelectedElement.drawHierarchyAngular(gl, glu,
	// mvCurrentWidth.getMovementValue(),
	// fTargetDepth, mvCurrentRootStartAngle.getMovementValue(),
	// mvCurrentRootAngle
	// .getMovementValue(), mvCurrentRootInnerRadius.getMovementValue());
	// }
	//
	// if (haveMovementValuesReachedTargets()) {
	// iAnimationPart++;
	// if (iAnimationPart == 2) {
	// initAnimationSecondPart(fXCenter, fYCenter, pdCurrentSelectedElement);
	// }
	// if (iAnimationPart > 2) {
	// bAnimationStarted = false;
	// }
	// }
	//
	// if (!bAnimationStarted) {
	// ADrawingState dsNext =
	// drawingController.getDrawingState(EDrawingStateType.DRAWING_STATE_FULL_HIERARCHY);
	//
	// drawingController.setDrawingState(dsNext);
	// radialHierarchy.setAnimationActive(false);
	// radialHierarchy.setCurrentRootElement(pdCurrentSelectedElement);
	//
	// navigationHistory.addNewHistoryEntry(dsNext, pdCurrentSelectedElement,
	// pdCurrentSelectedElement,
	// radialHierarchy.getMaxDisplayedHierarchyDepth());
	// radialHierarchy.setNewSelection(SelectionType.SELECTION,
	// pdCurrentSelectedElement,
	// pdCurrentSelectedElement);
	// radialHierarchy.setDisplayListDirty();
	//
	// }
	// gl.glPopMatrix();
	// }
	//
	// /**
	// * Initializes the animation, particularly initializes all movement values
	// needed for the animation.
	// *
	// * @param fXCenter
	// * X coordinate of the hierarchy's center.
	// * @param fYCenter
	// * Y coordinate of the hierarchy's center.
	// * @param pdCurrentSelectedElement
	// * Currently selected partial disc.
	// */
	// private void initAnimationFirstPart(float fXCenter, float fYCenter,
	// PartialDisc
	// pdCurrentSelectedElement) {
	//
	// float fCurrentSelectedAngle = pdCurrentSelectedElement.getCurrentAngle();
	// float fCurrentSelectedStartAngle =
	// pdCurrentSelectedElement.getCurrentStartAngle();
	// fTargetDepth = pdCurrentSelectedElement.getCurrentDepth();
	//
	// float fTargetStartAngle = 180 - (fCurrentSelectedAngle / 2.0f);
	// float fTargetStartAngleAlt = 180 - (fCurrentSelectedAngle / 2.0f) + 360;
	//
	// if ((fCurrentSelectedStartAngle - fTargetStartAngle) >
	// (fTargetStartAngleAlt -
	// fCurrentSelectedStartAngle))
	// fTargetStartAngle = fTargetStartAngleAlt;
	//
	// alMovementValues.clear();
	//
	// mvCurrentRootStartAngle =
	// createNewMovementValue(fCurrentSelectedStartAngle, fTargetStartAngle,
	// fAnimationDuration);
	// }
	//
	// /**
	// * Initializes the animation, particularly initializes all movement values
	// needed for the animation.
	// *
	// * @param fXCenter
	// * X coordinate of the hierarchy's center.
	// * @param fYCenter
	// * Y coordinate of the hierarchy's center.
	// * @param pdCurrentSelectedElement
	// * Currently selected partial disc.
	// */
	// private void initAnimationSecondPart(float fXCenter, float fYCenter,
	// PartialDisc
	// pdCurrentSelectedElement) {
	//
	// float fCurrentSelectedAngle = pdCurrentSelectedElement.getCurrentAngle();
	// float fCurrentSelectedInnerRadius =
	// pdCurrentSelectedElement.getCurrentInnerRadius();
	// float fCurrentSelectedStartAngle =
	// pdCurrentSelectedElement.getCurrentStartAngle();
	// float fCurrentWidth = pdCurrentSelectedElement.getCurrentWidth();
	//
	// fTargetDepth =
	// Math.min(radialHierarchy.getMaxDisplayedHierarchyDepth(),
	// pdCurrentSelectedElement
	// .getHierarchyDepth());
	// float fTargetWidth =
	// Math.min(fXCenter * RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE,
	// fYCenter
	// * RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE)
	// / fTargetDepth;
	//
	// float fMidAngle = fCurrentSelectedStartAngle + (fCurrentSelectedAngle /
	// 2.0f);
	// fTargetDepth = pdCurrentSelectedElement.getCurrentDepth();
	//
	// while (fMidAngle > 360) {
	// fMidAngle -= 360;
	// }
	// while (fMidAngle < 0) {
	// fMidAngle += 360;
	// }
	//
	// float fArRGB[];
	// if
	// (DrawingStrategyManager.get().getDefaultDrawingStrategy().getDrawingStrategyType()
	// ==
	// EPDDrawingStrategyType.RAINBOW_COLOR) {
	// ColorMapping cmRainbow =
	// ColorMappingManager.get().getColorMapping(EColorMappingType.RAINBOW);
	// fArRGB = cmRainbow.getColor(fMidAngle / 360);
	// }
	// else {
	// ColorMapping cmExpression =
	// ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);
	// fArRGB =
	// cmExpression.getColor(pdCurrentSelectedElement.getAverageExpressionValue());
	// }
	//
	// if (fCurrentSelectedStartAngle >= 360)
	// fCurrentSelectedStartAngle -= 360;
	//
	// alMovementValues.clear();
	//
	// mvCurrentRootAngle =
	// createNewMovementValue(fCurrentSelectedAngle, TARGET_ROOT_ANGLE,
	// fAnimationDuration);
	// mvCurrentRootStartAngle =
	// createNewMovementValue(fCurrentSelectedStartAngle, 0,
	// fAnimationDuration);
	// mvCurrentRootInnerRadius =
	// createNewMovementValue(fCurrentSelectedInnerRadius,
	// TARGET_ROOT_INNER_RADIUS, fAnimationDuration);
	// mvCurrentWidth = createNewMovementValue(fCurrentWidth, fTargetWidth,
	// fAnimationDuration);
	// mvCurrentRootColorR =
	// createNewMovementValue(fArRGB[0],
	// RadialHierarchyRenderStyle.PARTIAL_DISC_ROOT_COLOR[0],
	// fAnimationDuration);
	// mvCurrentRootColorG =
	// createNewMovementValue(fArRGB[1],
	// RadialHierarchyRenderStyle.PARTIAL_DISC_ROOT_COLOR[1],
	// fAnimationDuration);
	// mvCurrentRootColorB =
	// createNewMovementValue(fArRGB[2],
	// RadialHierarchyRenderStyle.PARTIAL_DISC_ROOT_COLOR[2],
	// fAnimationDuration);
	//
	// dsFixedColor =
	// (PDDrawingStrategyFixedColor)
	// DrawingStrategyManager.get().createDrawingStrategy(
	// EPDDrawingStrategyType.FIXED_COLOR);
	//
	// pdCurrentSelectedElement.setPDDrawingStrategy(dsFixedColor);
	// }

	// ============================ ROTATION SECOND
	// =============================

	// @Override
	// public void draw(float fXCenter, float fYCenter, GL2 gl, GLU glu, double
	// dTimePassed) {
	//
	// PartialDisc pdCurrentSelectedElement =
	// radialHierarchy.getCurrentSelectedElement();
	//
	// if (!bAnimationStarted) {
	// initAnimationFirstPart(fXCenter, fYCenter, pdCurrentSelectedElement);
	// bAnimationStarted = true;
	// radialHierarchy.setAnimationActive(true);
	// iAnimationPart = 1;
	// pdCurrentSelectedElement.setPDDrawingStrategyChildren(DrawingStrategyManager.get()
	// .getDefaultDrawingStrategy(),
	// radialHierarchy.getMaxDisplayedHierarchyDepth());
	// }
	//
	// moveValues(dTimePassed);
	//
	// if (iAnimationPart == 1) {
	// dsFixedColor.setFillColor(mvCurrentRootColorR.getMovementValue(),
	// mvCurrentRootColorG
	// .getMovementValue(), mvCurrentRootColorB.getMovementValue(), 1);
	// }
	//
	// gl.glPushMatrix();
	// gl.glLoadIdentity();
	// gl.glTranslatef(fXCenter, fYCenter, 0);
	//
	// if (iAnimationPart == 1) {
	// pdCurrentSelectedElement.drawHierarchyAngular(gl, glu,
	// mvCurrentWidth.getMovementValue(),
	// fTargetDepth, mvCurrentRootStartAngle.getMovementValue(),
	// mvCurrentRootAngle
	// .getMovementValue(), mvCurrentRootInnerRadius.getMovementValue());
	//
	// }
	// else {
	// pdCurrentSelectedElement.drawHierarchyAngular(gl, glu,
	// pdCurrentSelectedElement.getCurrentWidth(), fTargetDepth,
	// mvCurrentRootStartAngle
	// .getMovementValue(), pdCurrentSelectedElement.getCurrentAngle(),
	// pdCurrentSelectedElement
	// .getCurrentInnerRadius());
	// }
	//
	// if (haveMovementValuesReachedTargets()) {
	// iAnimationPart++;
	// if (iAnimationPart == 2) {
	// initAnimationSecondPart(fXCenter, fYCenter, pdCurrentSelectedElement);
	// }
	// if (iAnimationPart > 2) {
	// bAnimationStarted = false;
	// }
	// }
	//
	// if (!bAnimationStarted) {
	// ADrawingState dsNext =
	// drawingController.getDrawingState(EDrawingStateType.DRAWING_STATE_FULL_HIERARCHY);
	//
	// drawingController.setDrawingState(dsNext);
	// radialHierarchy.setAnimationActive(false);
	// radialHierarchy.setCurrentRootElement(pdCurrentSelectedElement);
	//
	// navigationHistory.addNewHistoryEntry(dsNext, pdCurrentSelectedElement,
	// pdCurrentSelectedElement,
	// radialHierarchy.getMaxDisplayedHierarchyDepth());
	// radialHierarchy.setNewSelection(SelectionType.SELECTION,
	// pdCurrentSelectedElement,
	// pdCurrentSelectedElement);
	// radialHierarchy.setDisplayListDirty();
	//
	// }
	// gl.glPopMatrix();
	// }
	//
	// /**
	// * Initializes the animation, particularly initializes all movement values
	// needed for the animation.
	// *
	// * @param fXCenter
	// * X coordinate of the hierarchy's center.
	// * @param fYCenter
	// * Y coordinate of the hierarchy's center.
	// * @param pdCurrentSelectedElement
	// * Currently selected partial disc.
	// */
	// private void initAnimationSecondPart(float fXCenter, float fYCenter,
	// PartialDisc
	// pdCurrentSelectedElement) {
	//
	// float fCurrentSelectedStartAngle =
	// pdCurrentSelectedElement.getCurrentStartAngle();
	// fTargetDepth = pdCurrentSelectedElement.getCurrentDepth();
	//
	// float fTargetStartAngle = 0;
	//
	// if(fCurrentSelectedStartAngle > 360 - fCurrentSelectedStartAngle)
	// fTargetStartAngle = 360;
	//
	// alMovementValues.clear();
	//
	// mvCurrentRootStartAngle =
	// createNewMovementValue(fCurrentSelectedStartAngle, fTargetStartAngle,
	// fAnimationDuration);
	// }
	//
	// /**
	// * Initializes the animation, particularly initializes all movement values
	// needed for the animation.
	// *
	// * @param fXCenter
	// * X coordinate of the hierarchy's center.
	// * @param fYCenter
	// * Y coordinate of the hierarchy's center.
	// * @param pdCurrentSelectedElement
	// * Currently selected partial disc.
	// */
	// private void initAnimationFirstPart(float fXCenter, float fYCenter,
	// PartialDisc
	// pdCurrentSelectedElement) {
	//
	// float fCurrentSelectedAngle = pdCurrentSelectedElement.getCurrentAngle();
	// float fCurrentSelectedInnerRadius =
	// pdCurrentSelectedElement.getCurrentInnerRadius();
	// float fCurrentSelectedStartAngle =
	// pdCurrentSelectedElement.getCurrentStartAngle();
	// float fCurrentWidth = pdCurrentSelectedElement.getCurrentWidth();
	//
	// fTargetDepth =
	// Math.min(radialHierarchy.getMaxDisplayedHierarchyDepth(),
	// pdCurrentSelectedElement
	// .getHierarchyDepth());
	// float fTargetWidth =
	// Math.min(fXCenter * RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE,
	// fYCenter
	// * RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE)
	// / fTargetDepth;
	//
	// float fMidAngle = fCurrentSelectedStartAngle + (fCurrentSelectedAngle /
	// 2.0f);
	// fTargetDepth = pdCurrentSelectedElement.getCurrentDepth();
	//
	// while (fMidAngle > 360) {
	// fMidAngle -= 360;
	// }
	// while (fMidAngle < 0) {
	// fMidAngle += 360;
	// }
	// float fAngleToAdd = (fMidAngle > fCurrentSelectedStartAngle) ? -180 :
	// 180;
	// float fSelectedTargetStartAngle = fMidAngle + fAngleToAdd;
	//
	// float fArRGB[];
	// if
	// (DrawingStrategyManager.get().getDefaultDrawingStrategy().getDrawingStrategyType()
	// ==
	// EPDDrawingStrategyType.RAINBOW_COLOR) {
	// ColorMapping cmRainbow =
	// ColorMappingManager.get().getColorMapping(EColorMappingType.RAINBOW);
	// fArRGB = cmRainbow.getColor(fMidAngle / 360);
	// }
	// else {
	// ColorMapping cmExpression =
	// ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);
	// fArRGB =
	// cmExpression.getColor(pdCurrentSelectedElement.getAverageExpressionValue());
	// }
	//
	// alMovementValues.clear();
	//
	// mvCurrentRootAngle =
	// createNewMovementValue(fCurrentSelectedAngle, TARGET_ROOT_ANGLE,
	// fAnimationDuration);
	// mvCurrentRootStartAngle =
	// createNewMovementValue(fCurrentSelectedStartAngle,
	// fSelectedTargetStartAngle, fAnimationDuration);
	// mvCurrentRootInnerRadius =
	// createNewMovementValue(fCurrentSelectedInnerRadius,
	// TARGET_ROOT_INNER_RADIUS, fAnimationDuration);
	// mvCurrentWidth = createNewMovementValue(fCurrentWidth, fTargetWidth,
	// fAnimationDuration);
	// mvCurrentRootColorR =
	// createNewMovementValue(fArRGB[0],
	// RadialHierarchyRenderStyle.PARTIAL_DISC_ROOT_COLOR[0],
	// fAnimationDuration);
	// mvCurrentRootColorG =
	// createNewMovementValue(fArRGB[1],
	// RadialHierarchyRenderStyle.PARTIAL_DISC_ROOT_COLOR[1],
	// fAnimationDuration);
	// mvCurrentRootColorB =
	// createNewMovementValue(fArRGB[2],
	// RadialHierarchyRenderStyle.PARTIAL_DISC_ROOT_COLOR[2],
	// fAnimationDuration);
	//
	// dsFixedColor =
	// (PDDrawingStrategyFixedColor)
	// DrawingStrategyManager.get().getDrawingStrategy(
	// EPDDrawingStrategyType.FIXED_COLOR);
	//
	// pdCurrentSelectedElement.setPDDrawingStrategy(dsFixedColor);
	// }

	@Override
	public EDrawingStateType getType() {
		return EDrawingStateType.ANIMATION_NEW_ROOT_ELEMENT;
	}
}
