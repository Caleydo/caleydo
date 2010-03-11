package org.caleydo.core.view.opengl.util.draganddrop;

import java.util.Set;

import javax.media.opengl.GL;

public interface IDropArea {

	public void handleDragOver(GL gl, Set<IDraggable> draggables,
			float mouseCoordinateX, float mouseCoordinateY);

	public void handleDrop(GL gl, Set<IDraggable> draggables,
			float mouseCoordinateX, float mouseCoordinateY,
			DragAndDropController dragAndDropController);
}
