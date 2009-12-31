package org.caleydo.core.view.opengl.canvas.grouper.draganddrop;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;

public class DragAndDropController {

	private Set<IDraggable> setDraggables;
	private IDropArea dropArea;
	boolean bDragging;
	boolean bDraggingFirstTime;
	float fArDraggingStartMouseCoordinates[];
	AGLEventListener view;

	public DragAndDropController(AGLEventListener view) {
		setDraggables = new HashSet<IDraggable>();
		bDragging = false;
		bDraggingFirstTime = false;
		fArDraggingStartMouseCoordinates = new float[2];
		this.view = view;
	}

	public void addDraggable(IDraggable draggable) {
		if (draggable != null)
			setDraggables.add(draggable);
	}

	public void removeDraggable(IDraggable draggable) {
		setDraggables.remove(draggable);
	}

	public void startDragging() {
		bDragging = true;
		bDraggingFirstTime = true;
		dropArea = null;
	}

	public void setDropArea(IDropArea dropArea) {
		this.dropArea = dropArea;
	}

	public void clearDraggables() {
		setDraggables.clear();
	}

	public void handleDragging(GL gl, GLMouseListener glMouseListener) {

		if (bDragging) {
			Point mouseWinCoords = glMouseListener.getPickedPoint();

			float[] fArTargetWorldCoordinates =
				GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, mouseWinCoords.x,
					mouseWinCoords.y);

			if (dropArea != null) {
				dropArea.handleDragOver(gl, setDraggables, fArTargetWorldCoordinates[0],
					fArTargetWorldCoordinates[1]);
			}

			for (IDraggable draggable : setDraggables) {
				if (bDraggingFirstTime) {
					draggable.setDraggingStartPoint(fArTargetWorldCoordinates[0],
						fArTargetWorldCoordinates[1]);
				}
				draggable.handleDragging(gl, fArTargetWorldCoordinates[0], fArTargetWorldCoordinates[1]);
			}

			if (glMouseListener.wasMouseReleased()) {
				bDragging = false;
				if (dropArea != null) {
					dropArea.handleDrop(gl, setDraggables, fArTargetWorldCoordinates[0],
						fArTargetWorldCoordinates[1], this);

				}
				view.setDisplayListDirty();
			}

			bDraggingFirstTime = false;
		}
	}

	public boolean isDragging() {
		return bDragging;
	}

	public Set<IDraggable> getDraggables() {
		return setDraggables;
	}

	public float[] getDraggingStartMouseCoordinates() {
		return fArDraggingStartMouseCoordinates;
	}

}
