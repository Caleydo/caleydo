/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.util.draganddrop;

import javax.media.opengl.GL2;

public interface IDraggable {

	public void setDraggingStartPoint(float mouseCoordinateX, float mouseCoordinateY);

	public void handleDragging(GL2 gl, float mouseCoordinateX, float mouseCoordinateY);

	public void handleDrop(GL2 gl, float mouseCoordinateX, float mouseCoordinateY);

}
