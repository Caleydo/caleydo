package org.caleydo.view.grouper.draganddrop;

import java.util.Set;

import javax.media.opengl.GL;

public interface IDropArea {

	public void handleDragOver(GL gl, Set<IDraggable> setDraggables,
			float fMouseCoordinateX, float fMouseCoordinateY);

	public void handleDrop(GL gl, Set<IDraggable> setDraggables,
			float fMouseCoordinateX, float fMouseCoordinateY,
			DragAndDropController dragAndDropController);
}
