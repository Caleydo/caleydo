package org.caleydo.core.view.opengl.canvas.radial;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

public abstract class DrawingStateAnimation
	extends DrawingState {

	private double fPreviousTimeStamp;
	protected boolean bAnimationStarted;
	protected float fAnimationDuration;
	protected ArrayList<MovementValue> alMovementValues;

	public DrawingStateAnimation(DrawingController drawingController, GLRadialHierarchy radialHierarchy, NavigationHistory navigationHistory) {
		
		super(drawingController, radialHierarchy, navigationHistory);
		fPreviousTimeStamp = 0;
		bAnimationStarted = false;
		alMovementValues = new ArrayList<MovementValue>();
	}

	public final void draw(float fXCenter, float fYCenter, GL gl, GLU glu) {

		double fCurrentTimeStamp = GregorianCalendar.getInstance().getTimeInMillis();

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

	public abstract void draw(float fXCenter, float fYCenter, GL gl, GLU glu, double fTimePassed);

	protected boolean haveMovementValuesReachedTargets() {

		int iNumTargetsReached = 0;

		for (MovementValue movementValue : alMovementValues) {
			if (movementValue.isTargetValueReached()) {
				iNumTargetsReached++;
			}
		}

		return (iNumTargetsReached == alMovementValues.size());
	}

	protected MovementValue createNewMovementValue(float fStartValue, float fTargetValue,
		float fMovementDuration) {

		float fSpeed = (fTargetValue - fStartValue) / fMovementDuration;
		MovementValue movementValue;
		if (fSpeed > 0)
			movementValue =
				new MovementValue(fStartValue, fTargetValue, fSpeed, MovementValue.CRITERION_GREATER_OR_EQUAL);
		else
			movementValue =
				new MovementValue(fStartValue, fTargetValue, fSpeed, MovementValue.CRITERION_SMALLER_OR_EQUAL);

		alMovementValues.add(movementValue);

		return movementValue;
	}

	protected void moveValues(double dTimePassed) {

		for (MovementValue movementValue : alMovementValues) {
			movementValue.move(dTimePassed);
		}
	}

	public float getAnimationDuration() {
		return fAnimationDuration;
	}

	public void setAnimationDuration(float fAnimationDuration) {
		this.fAnimationDuration = fAnimationDuration;
	}
	
	@Override
	public PartialDisc getSelectedElement() {
		return null;
	}

}
