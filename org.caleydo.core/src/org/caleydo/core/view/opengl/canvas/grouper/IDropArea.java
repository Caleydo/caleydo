package org.caleydo.core.view.opengl.canvas.grouper;

import java.util.ArrayList;

import javax.media.opengl.GL;

public interface IDropArea {

	public void handleDragOver(GL gl, ArrayList<IDraggable> alDraggables, float fMouseCoordinateX,
		float fMouseCoordinateY);
	
	public void handleDrop(ArrayList<IDraggable> alDraggables, float fMouseCoordinateX,
		float fMouseCoordinateY);
}
