package org.caleydo.core.view.opengl.canvas.grouper;

import java.awt.Point;
import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;

public class DragAndDropController {

	private ArrayList<IDraggable> alDraggables;
	private IDropArea dropArea;
	boolean bDragging;
	boolean bDraggingFirstTime;

	public DragAndDropController() {
		alDraggables = new ArrayList<IDraggable>();
		bDragging = false;
		bDraggingFirstTime = false;
	}

	public void addDraggable(IDraggable draggable) {
		if (draggable != null)
			alDraggables.add(draggable);
	}

	public void startDragging() {
		bDragging = true;
		bDraggingFirstTime = true;
	}

	public void setDropArea(IDropArea dropArea) {
		this.dropArea = dropArea;
	}

	public void clearDraggables() {
		alDraggables.clear();
	}

	public void handleDragging(GL gl, GLMouseListener glMouseListener) {

		if (bDragging) {
			Point mouseWinCoords = glMouseListener.getPickedPoint();

			float[] fArTargetWorldCoordinates =
				GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, mouseWinCoords.x,
					mouseWinCoords.y);

			if (dropArea != null) {
				dropArea.handleDragOver(gl, alDraggables, fArTargetWorldCoordinates[0],
					fArTargetWorldCoordinates[1]);
			}
			
			for (IDraggable draggable : alDraggables) {
				if (bDraggingFirstTime) {
					draggable.setDraggingStartPoint(fArTargetWorldCoordinates[0],
						fArTargetWorldCoordinates[1]);
				}
				draggable.handleDragging(gl, fArTargetWorldCoordinates[0], fArTargetWorldCoordinates[1]);
			}
			
			if (glMouseListener.wasMouseReleased()) {
				bDragging = false;
				if (dropArea != null) {
					dropArea.handleDrop(alDraggables, fArTargetWorldCoordinates[0],
						fArTargetWorldCoordinates[1]);
				}
			}

			bDraggingFirstTime = false;
		}
	}

	public boolean isDragging() {
		return bDragging;
	}

}
