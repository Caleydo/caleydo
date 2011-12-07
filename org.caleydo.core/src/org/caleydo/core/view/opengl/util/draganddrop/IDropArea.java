package org.caleydo.core.view.opengl.util.draganddrop;

import java.util.Set;
import javax.media.opengl.GL2;

public interface IDropArea
{

	public void handleDragOver(GL2 gl, Set<IDraggable> draggables, float mouseCoordinateX,
			float mouseCoordinateY);

	public void handleDrop(GL2 gl, Set<IDraggable> draggables, float mouseCoordinateX,
			float mouseCoordinateY, DragAndDropController dragAndDropController);

	/**
	 * Called if a different {@link IDropArea} replaces the current one.
	 */
	public void handleDropAreaReplaced();
}
