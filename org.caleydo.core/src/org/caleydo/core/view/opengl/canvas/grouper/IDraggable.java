package org.caleydo.core.view.opengl.canvas.grouper;

public interface IDraggable {
	
	public void setDraggingStartPoint(float fMouseCoordinateX, float fMouseCoordinateY);

	public void handleDragging(float fMouseCoordinateX, float fMouseCoordinateY);
	
}
