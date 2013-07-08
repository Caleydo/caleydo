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

import java.util.HashMap;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.util.clusterer.EDrawingStateType;

/**
 * The DrawingController holds instances of all drawing states and determines,
 * which drawing state is currently active. All requests concerning drawing and
 * selecting are forwarded to the currently active drawing state.
 * 
 * @author Christian Partl
 */
public class DrawingController {

	private HashMap<EDrawingStateType, ADrawingState> drawingStates;
	private ADrawingState currentDrawingState;

	/**
	 * Constructor.
	 * 
	 * @param radialHierarchy
	 *            Current instance of GLRadialHierarchy.
	 * @param navigationHistory
	 *            Navigation history that shall be used by the states.
	 */
	public DrawingController(GLRadialHierarchy radialHierarchy,
			NavigationHistory navigationHistory) {
		drawingStates = new HashMap<EDrawingStateType, ADrawingState>();
		currentDrawingState = new DrawingStateFullHierarchy(this, radialHierarchy,
				navigationHistory);
		drawingStates.put(EDrawingStateType.DRAWING_STATE_FULL_HIERARCHY,
				currentDrawingState);
		drawingStates.put(EDrawingStateType.ANIMATION_NEW_ROOT_ELEMENT,
				new AnimationNewRootElement(this, radialHierarchy, navigationHistory));
		drawingStates.put(EDrawingStateType.DRAWING_STATE_DETAIL_OUTSIDE,
				new DrawingStateDetailOutside(this, radialHierarchy, navigationHistory));
		drawingStates.put(EDrawingStateType.ANIMATION_PARENT_ROOT_ELEMENT,
				new AnimationParentRootElement(this, radialHierarchy, navigationHistory));
		drawingStates
				.put(EDrawingStateType.ANIMATION_POP_OUT_DETAIL_OUTSIDE,
						new AnimationPopOutDetailOutside(this, radialHierarchy,
								navigationHistory));
		drawingStates
				.put(EDrawingStateType.ANIMATION_PULL_IN_DETAIL_OUTSIDE,
						new AnimationPullInDetailOutside(this, radialHierarchy,
								navigationHistory));
	}

	/**
	 * Drawing request that will be forwarded to the currently active drawing
	 * state.
	 * 
	 * @param fXCenter
	 *            X coordinate of the hierarchy's center.
	 * @param fYCenter
	 *            Y coordinate of the hierarchy's center.
	 * @param gl
	 *            GL2 object that shall be used for drawing.
	 * @param glu
	 *            GLU object that shall be used for drawing.
	 */
	public void draw(float fXCenter, float fYCenter, GL2 gl, GLU glu) {
		currentDrawingState.draw(fXCenter, fYCenter, gl, glu);
	}

	/**
	 * Method for handling mouse over events. The request will be forwarded to
	 * the currently active drawing state.
	 * 
	 * @param pdMouseOver
	 *            Partial disc where the mouse over event occurred.
	 */
	public void handleMouseOver(PartialDisc pdMouseOver) {

		currentDrawingState.handleMouseOver(pdMouseOver);
	}

	/**
	 * Method for handling selection events. The request will be forwarded to
	 * the currently active drawing state.
	 * 
	 * @param pdSelected
	 *            Partial disc that has been selected.
	 */
	public void handleSelection(PartialDisc pdClicked) {

		currentDrawingState.handleSelection(pdClicked);
	}

	/**
	 * Method for handling alternative selection events. The request will be
	 * forwarded to the currently active drawing state.
	 * 
	 * @param pdSelected
	 *            Partial disc that has been selected alternatively.
	 */
	public void handleAlternativeSelection(PartialDisc pdClicked) {

		currentDrawingState.handleAlternativeSelection(pdClicked);
	}

	/**
	 * Sets the the specified drawing state as active one.
	 * 
	 * @param drawingState
	 *            Drawing state that shall become active.
	 */
	public void setDrawingState(ADrawingState drawingState) {
		currentDrawingState = drawingState;
	}

	/**
	 * Sets the the drawing state held by the drawing controller that
	 * corresponds to the specified type as active.
	 * 
	 * @param iDrawingState
	 *            Type of drawing state that should become active.
	 */
	public void setDrawingState(EDrawingStateType drawingStateType) {
		ADrawingState dsNext = drawingStates.get(drawingStateType);

		if (dsNext != null)
			currentDrawingState = dsNext;
	}

	/**
	 * Gets the drawing state with the specified type that is held by the
	 * drawing controller.
	 * 
	 * @param iStateType
	 *            Type of drawing state.
	 * @return Drawing state with the specified type that is held by the drawing
	 *         controller.
	 */
	public ADrawingState getDrawingState(EDrawingStateType drawingStateType) {
		return drawingStates.get(drawingStateType);
	}

	/**
	 * @return The currently active drawing state.
	 */
	public ADrawingState getCurrentDrawingState() {
		return currentDrawingState;
	}
}
