package org.caleydo.core.view.opengl.canvas.radial;

import java.util.HashMap;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

public class DrawingController {

	public static final int DRAWING_STATE_FULL_HIERARCHY = 0;
	public static final int DRAWING_STATE_ANIM_NEW_ROOT_ELEMENT = 1;
	public static final int DRAWING_STATE_DETAIL_OUTSIDE = 2;

	private HashMap<Integer, DrawingState> drawingStates;
	private DrawingState currentDrawingState;

	public DrawingController(GLRadialHierarchy radialHierarchy) {
		drawingStates = new HashMap<Integer, DrawingState>();
		// TODO: maybe use AGLEventListener instead of GLRadialHierarchy
		currentDrawingState = new DrawingStateFullHierarchy(this, radialHierarchy);
		drawingStates.put(DRAWING_STATE_FULL_HIERARCHY, currentDrawingState);
		drawingStates.put(DRAWING_STATE_ANIM_NEW_ROOT_ELEMENT, new AnimationNewRootElement(this,
			radialHierarchy));
		drawingStates.put(DRAWING_STATE_DETAIL_OUTSIDE, new DrawingStateDetailOutside(this, radialHierarchy));
	}

	public void draw(float fXCenter, float fYCenter, GL gl, GLU glu) {
		currentDrawingState.draw(fXCenter, fYCenter, gl, glu);
	}

	public void handleMouseOver(PartialDisc pdMouseOver) {

		currentDrawingState.handleMouseOver(pdMouseOver);
	}

	public void handleClick(PartialDisc pdClicked) {

		currentDrawingState.handleClick(pdClicked);
	}
	
	public void handleDoubleClick(PartialDisc pdClicked) {

		currentDrawingState.handleDoubleClick(pdClicked);
	}

	public void setDrawingState(DrawingState drawingState) {
		currentDrawingState = drawingState;
	}
	
	public void setDrawingState(int iDrawingState) {
		DrawingState dsNext = drawingStates.get(iDrawingState);
		
		if(dsNext != null)
			currentDrawingState = dsNext;
	}

	public DrawingState getDrawingState(int iStateType) {
		return drawingStates.get(iStateType);
	}
}
