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
 * * This class represents the animation where the detail view is pulled in to
 * the full hierarchy. When the animation is finished the follow up drawing
 * state ({@link DrawingStateFullHierarchy}) will become active.
 * 
 * @author Christian Partl
 */
@XmlType
public class AnimationPullInDetailOutside extends ADrawingStateAnimation {

	public static final float DEFAULT_ANIMATION_DURATION = 0.3f;

	private MovementValue mvDetailViewWidth;
	private MovementValue mvOverviewWidth;
	private MovementValue mvDetailViewInnerRadius;
	private MovementValue mvDetailViewStartAngle;
	private MovementValue mvDetailViewAngle;
	private int iDisplayedDetailViewDepth;
	private int iDisplayedOverviewDepth;
	private int iAnimationPart;

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
	public AnimationPullInDetailOutside(DrawingController drawingController,
			GLRadialHierarchy radialHierarchy, NavigationHistory navigationHistory) {
		super(drawingController, radialHierarchy, navigationHistory);
		fAnimationDuration = DEFAULT_ANIMATION_DURATION;
	}

	@Override
	public void draw(float fXCenter, float fYCenter, GL2 gl, GLU glu, double dTimePassed) {
		PartialDisc pdCurrentSelectedElement = radialHierarchy
				.getCurrentSelectedElement();
		PartialDisc pdCurrentRootElement = radialHierarchy.getCurrentRootElement();

		if (!bAnimationStarted) {
			initAnimationFirstPart(fXCenter, fYCenter, pdCurrentSelectedElement,
					pdCurrentRootElement);
			iAnimationPart = 1;
			bAnimationStarted = true;
			radialHierarchy.setAnimationActive(true);
		}

		moveValues(dTimePassed);

		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glTranslatef(fXCenter, fYCenter, 0);

		DrawingStrategyManager drawingStrategyManager = radialHierarchy
				.getDrawingStrategyManager();

		pdCurrentRootElement.setPDDrawingStrategyChildren(
				drawingStrategyManager.getDefaultDrawingStrategy(),
				iDisplayedOverviewDepth);
		pdCurrentSelectedElement.setPDDrawingStrategyChildren(drawingStrategyManager
				.createDrawingStrategy(EPDDrawingStrategyType.INVISIBLE),
				iDisplayedDetailViewDepth);

		pdCurrentRootElement.drawHierarchyFull(gl, glu,
				mvOverviewWidth.getMovementValue(), iDisplayedOverviewDepth);

		pdCurrentSelectedElement.setPDDrawingStrategyChildren(
				drawingStrategyManager.getDefaultDrawingStrategy(),
				iDisplayedDetailViewDepth);

		pdCurrentSelectedElement.drawHierarchyAngular(gl, glu,
				mvDetailViewWidth.getMovementValue(), iDisplayedDetailViewDepth,
				mvDetailViewStartAngle.getMovementValue(),
				mvDetailViewAngle.getMovementValue(),
				mvDetailViewInnerRadius.getMovementValue());

		if (haveMovementValuesReachedTargets()) {
			iAnimationPart++;
			if (iAnimationPart == 2) {
				initAnimationSecondPart(fXCenter, fYCenter, pdCurrentSelectedElement,
						pdCurrentRootElement);
			}
			if (iAnimationPart > 2) {
				bAnimationStarted = false;
			}
		}

		if (!bAnimationStarted) {
			ADrawingState dsNext = drawingController
					.getDrawingState(EDrawingStateType.DRAWING_STATE_FULL_HIERARCHY);

			drawingController.setDrawingState(dsNext);
			radialHierarchy.setAnimationActive(false);
			radialHierarchy.setCurrentSelectedElement(pdCurrentRootElement);

			navigationHistory
					.addNewHistoryEntry(dsNext, pdCurrentRootElement,
							pdCurrentRootElement,
							radialHierarchy.getMaxDisplayedHierarchyDepth());
			radialHierarchy
					.setNewSelection(SelectionType.SELECTION, pdCurrentRootElement);
			radialHierarchy.setDisplayListDirty();
		}
		gl.glPopMatrix();
	}

	/**
	 * Initializes the first part of the animation which restores the original
	 * angles of the elements in the detail view. Particularly all movement
	 * values needed for this part are initialized.
	 * 
	 * @param fXCenter
	 *            X coordinate of the hierarchy's center.
	 * @param fYCenter
	 *            Y coordinate of the hierarchy's center.
	 * @param pdCurrentSelectedElement
	 *            Currently selected partial disc.
	 * @param pdCurrentRootElement
	 *            Current root partial disc.
	 */
	private void initAnimationFirstPart(float fXCenter, float fYCenter,
			PartialDisc pdCurrentSelectedElement, PartialDisc pdCurrentRootElement) {

		iDisplayedDetailViewDepth = pdCurrentSelectedElement.getCurrentDepth();
		iDisplayedOverviewDepth = pdCurrentRootElement.getCurrentDepth();

		float fCurrentSelectedElementStartAngle = pdCurrentSelectedElement
				.getCurrentStartAngle();
		float fCurrentSelectedElementAngle = pdCurrentSelectedElement.getCurrentAngle();
		float fCurrentSelectedElementInnerRadius = pdCurrentSelectedElement
				.getCurrentInnerRadius();
		float fCurrentSelectedElementWidth = pdCurrentSelectedElement.getCurrentWidth();

		float fCurrentRootWidth = pdCurrentRootElement.getCurrentWidth();

		pdCurrentRootElement.simulateDrawHierarchyFull(
				pdCurrentRootElement.getCurrentWidth(), iDisplayedOverviewDepth);

		float fCurrentSelectedElementTargetAngle = pdCurrentSelectedElement
				.getCurrentAngle();
		float fCurrentSelectedElementTargetStartAngle = pdCurrentSelectedElement
				.getCurrentStartAngle();

		if (fCurrentSelectedElementTargetStartAngle < fCurrentSelectedElementStartAngle) {
			fCurrentSelectedElementTargetStartAngle += 360;
		}

		mvDetailViewAngle = createNewMovementValue(fCurrentSelectedElementAngle,
				fCurrentSelectedElementTargetAngle, fAnimationDuration);
		mvDetailViewStartAngle = createNewMovementValue(
				fCurrentSelectedElementStartAngle,
				fCurrentSelectedElementTargetStartAngle, fAnimationDuration);
		mvDetailViewInnerRadius = createNewMovementValue(
				fCurrentSelectedElementInnerRadius, fCurrentSelectedElementInnerRadius,
				fAnimationDuration);
		mvDetailViewWidth = createNewMovementValue(fCurrentSelectedElementWidth,
				fCurrentSelectedElementWidth, fAnimationDuration);
		mvOverviewWidth = createNewMovementValue(fCurrentRootWidth, fCurrentRootWidth,
				fAnimationDuration);
	}

	/**
	 * Initializes the second part of the animation which pulls in the elements
	 * from the detail view to the full hierarchy. Particularly all movement
	 * values needed for this part are initialized.
	 * 
	 * @param fXCenter
	 *            X coordinate of the hierarchy's center.
	 * @param fYCenter
	 *            Y coordinate of the hierarchy's center.
	 * @param pdCurrentSelectedElement
	 *            Currently selected partial disc.
	 * @param pdCurrentRootElement
	 *            Current root partial disc.
	 */
	private void initAnimationSecondPart(float fXCenter, float fYCenter,
			PartialDisc pdCurrentSelectedElement, PartialDisc pdCurrentRootElement) {

		float fCurrentRootWidth = pdCurrentRootElement.getCurrentWidth();
		float fCurrentSelectedElementWidth = pdCurrentSelectedElement.getCurrentWidth();
		float fCurrentSelecedElementInnderRadius = pdCurrentSelectedElement
				.getCurrentInnerRadius();
		float fCurrentSelectedElementStartAngle = pdCurrentSelectedElement
				.getCurrentStartAngle();
		float fCurrentSelectedElementAngle = pdCurrentSelectedElement.getCurrentAngle();

		float fHierarchyOuterRadius = Math.min(fXCenter
				* RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE, fYCenter
				* RadialHierarchyRenderStyle.USED_SCREEN_PERCENTAGE);
		float fTargetWidth = fHierarchyOuterRadius / iDisplayedOverviewDepth;

		pdCurrentRootElement.simulateDrawHierarchyFull(fTargetWidth,
				iDisplayedOverviewDepth);

		float fCurrentSelectedElementTargetInnerRadius = pdCurrentSelectedElement
				.getCurrentInnerRadius();

		alMovementValues.clear();

		mvOverviewWidth = createNewMovementValue(fCurrentRootWidth, fTargetWidth,
				fAnimationDuration);
		mvDetailViewWidth = createNewMovementValue(fCurrentSelectedElementWidth,
				fTargetWidth, fAnimationDuration);
		mvDetailViewInnerRadius = createNewMovementValue(
				fCurrentSelecedElementInnderRadius,
				fCurrentSelectedElementTargetInnerRadius, fAnimationDuration);
		mvDetailViewAngle = createNewMovementValue(fCurrentSelectedElementAngle,
				fCurrentSelectedElementAngle, fAnimationDuration);
		mvDetailViewStartAngle = createNewMovementValue(
				fCurrentSelectedElementStartAngle, fCurrentSelectedElementStartAngle,
				fAnimationDuration);
	}

	@Override
	public EDrawingStateType getType() {
		return EDrawingStateType.ANIMATION_PULL_IN_DETAIL_OUTSIDE;
	}

}
