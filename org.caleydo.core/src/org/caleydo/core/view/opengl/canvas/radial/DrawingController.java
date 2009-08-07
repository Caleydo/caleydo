package org.caleydo.core.view.opengl.canvas.radial;

import java.util.HashMap;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

/**
 * The DrawingController holds instances of all drawing states and determines, which drawing state is
 * currently active. All requests concerning drawing and selecting are forwarded to the currently active
 * drawing state.
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
	public DrawingController(GLRadialHierarchy radialHierarchy, NavigationHistory navigationHistory) {
		drawingStates = new HashMap<EDrawingStateType, ADrawingState>();
		currentDrawingState = new DrawingStateFullHierarchy(this, radialHierarchy, navigationHistory);
		drawingStates.put(EDrawingStateType.DRAWING_STATE_FULL_HIERARCHY, currentDrawingState);
		drawingStates.put(EDrawingStateType.ANIMATION_NEW_ROOT_ELEMENT, new AnimationNewRootElement(this,
			radialHierarchy, navigationHistory));
		drawingStates.put(EDrawingStateType.DRAWING_STATE_DETAIL_OUTSIDE, new DrawingStateDetailOutside(this,
			radialHierarchy, navigationHistory));
		drawingStates.put(EDrawingStateType.ANIMATION_PARENT_ROOT_ELEMENT, new AnimationParentRootElement(
			this, radialHierarchy, navigationHistory));
		drawingStates.put(EDrawingStateType.ANIMATION_POP_OUT_DETAIL_OUTSIDE,
			new AnimationPopOutDetailOutside(this, radialHierarchy, navigationHistory));
		drawingStates.put(EDrawingStateType.ANIMATION_PULL_IN_DETAIL_OUTSIDE,
			new AnimationPullInDetailOutside(this, radialHierarchy, navigationHistory));
	}

	/**
	 * Drawing request that will be forwarded to the currently active drawing state.
	 * 
	 * @param fXCenter
	 *            X coordinate of the hierarchy's center.
	 * @param fYCenter
	 *            Y coordinate of the hierarchy's center.
	 * @param gl
	 *            GL object that shall be used for drawing.
	 * @param glu
	 *            GLU object that shall be used for drawing.
	 */
	public void draw(float fXCenter, float fYCenter, GL gl, GLU glu) {
		currentDrawingState.draw(fXCenter, fYCenter, gl, glu);
	}

	/**
	 * Method for handling mouse over events. The request will be forwarded to the currently active drawing
	 * state.
	 * 
	 * @param pdMouseOver
	 *            Partial disc where the mouse over event occurred.
	 * @param broadcastSelection
	 *            Determines if the selected element shall be broadcasted via event system.
	 */
	public void handleMouseOver(PartialDisc pdMouseOver, boolean broadcastSelection) {

		currentDrawingState.handleMouseOver(pdMouseOver, broadcastSelection);
	}

	/**
	 * Method for handling selection events. The request will be forwarded to the currently active drawing
	 * state.
	 * 
	 * @param pdSelected
	 *            Partial disc that has been selected.
	 * @param broadcastSelection
	 *            Determines if the selected element shall be broadcasted via event system.
	 */
	public void handleSelection(PartialDisc pdClicked, boolean broadcastSelection) {

		currentDrawingState.handleSelection(pdClicked, broadcastSelection);
	}

	/**
	 * Method for handling alternative selection events. The request will be forwarded to the currently active
	 * drawing state.
	 * 
	 * @param pdSelected
	 *            Partial disc that has been selected alternatively.
	 * @param broadcastSelection
	 *            Determines if the selected element shall be broadcasted via event system.
	 */
	public void handleAlternativeSelection(PartialDisc pdClicked, boolean broadcastSelection) {

		currentDrawingState.handleAlternativeSelection(pdClicked, broadcastSelection);
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
	 * Sets the the drawing state held by the drawing controller that corresponds to the specified type as
	 * active.
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
	 * Gets the drawing state with the specified type that is held by the drawing controller.
	 * 
	 * @param iStateType
	 *            Type of drawing state.
	 * @return Drawing state with the specified type that is held by the drawing controller.
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
