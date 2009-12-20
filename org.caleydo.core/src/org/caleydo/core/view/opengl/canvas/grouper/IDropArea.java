package org.caleydo.core.view.opengl.canvas.grouper;

import java.util.ArrayList;

public interface IDropArea {

	public void handleDragOver(ArrayList<IDraggable> alDraggables, float fMouseCoordinateX,
		float fMouseCoordinateY);
	
	public void handleDrop(ArrayList<IDraggable> alDraggables, float fMouseCoordinateX,
		float fMouseCoordinateY);
}
