/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.radial;

import java.util.ArrayList;
import java.util.Calendar;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.view.opengl.util.animation.MovementValue;

/**
 * ADrawingStateAnimation is the base class for all animations considering the
 * radial hierarchy. It stops the time between each frame and makes the time
 * difference available to subclasses for being able to execute animations. It
 * also provides several methods for using movement values (
 * {@link MovementValue}) for making animations easier to implement. Animations
 * do not handle any user events.
 * 
 * @author Christian Partl
 */
@XmlType
@XmlSeeAlso({ AnimationNewRootElement.class, AnimationParentRootElement.class,
		AnimationPopOutDetailOutside.class, AnimationPullInDetailOutside.class })
public abstract class ADrawingStateAnimation extends ADrawingState {

	private double fPreviousTimeStamp;
	/**
	 * Determines, whether the animation has been started or not.
	 */
	protected boolean bAnimationStarted;
	/**
	 * Determines the duration of the animation.
	 */
	protected float fAnimationDuration;
	/**
	 * List of movement values which should be used to alter positions, angles
	 * etc. over time.
	 */
	protected ArrayList<MovementValue> alMovementValues;

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
	public ADrawingStateAnimation(DrawingController drawingController,
			GLRadialHierarchy radialHierarchy, NavigationHistory navigationHistory) {

		super(drawingController, radialHierarchy, navigationHistory);
		fPreviousTimeStamp = 0;
		bAnimationStarted = false;
		alMovementValues = new ArrayList<MovementValue>();
	}

	@Override
	public final void draw(float fXCenter, float fYCenter, GL2 gl, GLU glu) {

		double fCurrentTimeStamp = Calendar.getInstance().getTimeInMillis();

		if (!bAnimationStarted)
			draw(fXCenter, fYCenter, gl, glu, 0);
		else {
			double fTimePassed = (fCurrentTimeStamp - fPreviousTimeStamp) / 1000;
			draw(fXCenter, fYCenter, gl, glu, fTimePassed);
		}
		fPreviousTimeStamp = fCurrentTimeStamp;

	}

	@Override
	public final void handleSelection(PartialDisc pdSelected) {
		// do nothing
	}

	@Override
	public final void handleMouseOver(PartialDisc pdMouseOver) {
		// do nothing
	}

	@Override
	public final void handleAlternativeSelection(PartialDisc pdSelected) {
		// do nothing
	}

	/**
	 * Concrete animations use this method to draw the scene in each frame.
	 * 
	 * @param fXCenter
	 *            X coordinate of the hierarchy's center.
	 * @param fYCenter
	 *            Y coordinate of the hierarchy's center.
	 * @param gl
	 *            GL2 object that shall be used for drawing.
	 * @param glu
	 *            GLU object that shall be used for drawing.
	 * @param fTimePassed
	 *            Time difference between the current frame and the last one.
	 */
	public abstract void draw(float fXCenter, float fYCenter, GL2 gl, GLU glu,
			double fTimePassed);

	/**
	 * @return True, if all movement values of the list have reached their
	 *         target values, false otherwise.
	 */
	protected boolean haveMovementValuesReachedTargets() {

		int iNumTargetsReached = 0;

		for (MovementValue movementValue : alMovementValues) {
			if (movementValue.isTargetValueReached()) {
				iNumTargetsReached++;
			}
		}
		return (iNumTargetsReached == alMovementValues.size());
	}

	/**
	 * Creates an instance of a movement value using the specified parameters.
	 * 
	 * @param fStartValue
	 *            Start value for the movement value.
	 * @param fTargetValue
	 *            Target value for the movement value.
	 * @param fMovementDuration
	 *            The time it should take the movement value to reach the target
	 *            value.
	 * @return Instance of the newly created movement value.
	 */
	protected MovementValue createNewMovementValue(float fStartValue, float fTargetValue,
			float fMovementDuration) {

		float fSpeed = (fTargetValue - fStartValue) / fMovementDuration;
		MovementValue movementValue;
		if (fSpeed > 0)
			movementValue = new MovementValue(fStartValue, fTargetValue, fSpeed,
					MovementValue.CRITERION_GREATER_OR_EQUAL);
		else
			movementValue = new MovementValue(fStartValue, fTargetValue, fSpeed,
					MovementValue.CRITERION_SMALLER_OR_EQUAL);

		alMovementValues.add(movementValue);

		return movementValue;
	}

	/**
	 * Moves all movement values according to the time passed.
	 * 
	 * @param dTimePassed
	 *            The time that has passed between the last and the current
	 *            frame.
	 */
	protected void moveValues(double dTimePassed) {

		for (MovementValue movementValue : alMovementValues) {
			movementValue.move(dTimePassed);
		}
	}

	/**
	 * @return The duration of the animation.
	 */
	public float getAnimationDuration() {
		return fAnimationDuration;
	}

	/**
	 * Sets the duration of the animation to a specified value.
	 * 
	 * @param fAnimationDuration
	 *            Value the animation duration shall be set to.
	 */
	public void setAnimationDuration(float fAnimationDuration) {
		this.fAnimationDuration = fAnimationDuration;
	}

	@Override
	public PartialDisc getSelectedElement() {
		return null;
	}

}
