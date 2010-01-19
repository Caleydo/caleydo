package org.caleydo.view.grouper.draganddrop;

import javax.media.opengl.GL;

public interface IDraggable {

	public void setDraggingStartPoint(float fMouseCoordinateX,
			float fMouseCoordinateY);

	public void handleDragging(GL gl, float fMouseCoordinateX,
			float fMouseCoordinateY);

}
