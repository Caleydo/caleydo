package org.caleydo.core.view.opengl.canvas.grouper;

import java.util.Set;

import javax.media.opengl.GL;

public interface IDropArea {

	public void handleDragOver(GL gl, Set<IDraggable> alDraggables, float fMouseCoordinateX,
		float fMouseCoordinateY);
	
	public void handleDrop(Set<IDraggable> alDraggables, float fMouseCoordinateX,
		float fMouseCoordinateY);
}
