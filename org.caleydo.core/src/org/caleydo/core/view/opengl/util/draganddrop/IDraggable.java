package org.caleydo.core.view.opengl.util.draganddrop;

import javax.media.opengl.GL;

public interface IDraggable {

	public void setDraggingStartPoint(float mouseCoordinateX,
			float mouseCoordinateY);

	public void handleDragging(GL gl, float mouseCoordinateX,
			float mouseCoordinateY);
	
	public void handleDrop(GL gl, float mouseCoordinateX,
		float mouseCoordinateY);

}
