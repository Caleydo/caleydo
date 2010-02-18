package org.caleydo.view.grouper.draganddrop;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;

public class DragAndDropController {

	private Set<IDraggable> setDraggables;
	private IDropArea dropArea;
	private boolean bDragging;
	private boolean bDraggingFirstTime;
	private Point startDraggingWindowCoords;
	private AGLView view;

	public DragAndDropController(AGLView view) {
		setDraggables = new HashSet<IDraggable>();
		bDragging = false;
		bDraggingFirstTime = false;
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

			float[] fArTargetWorldCoordinates = GLCoordinateUtils
					.convertWindowCoordinatesToWorldCoordinates(gl,
							mouseWinCoords.x, mouseWinCoords.y);

			if (dropArea != null) {
				dropArea.handleDragOver(gl, setDraggables,
						fArTargetWorldCoordinates[0],
						fArTargetWorldCoordinates[1]);
			}

			for (IDraggable draggable : setDraggables) {
				if (bDraggingFirstTime) {
					float[] fArStartDraggingWorldCoordinates = GLCoordinateUtils
							.convertWindowCoordinatesToWorldCoordinates(gl,
									startDraggingWindowCoords.x,
									startDraggingWindowCoords.y);
					draggable.setDraggingStartPoint(
							fArStartDraggingWorldCoordinates[0],
							fArStartDraggingWorldCoordinates[1]);
				}
				draggable.handleDragging(gl, fArTargetWorldCoordinates[0],
						fArTargetWorldCoordinates[1]);
			}

			if (glMouseListener.wasMouseReleased()) {
				bDragging = false;
				if (dropArea != null) {
					dropArea.handleDrop(gl, setDraggables,
							fArTargetWorldCoordinates[0],
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

	public boolean hasDraggables() {
		return !setDraggables.isEmpty();
	}

	public boolean containsDraggable(IDraggable draggable) {
		return setDraggables.contains(draggable);
	}

	public void setDraggingStartPosition(GLMouseListener glMouseListener) {
		startDraggingWindowCoords = glMouseListener.getPickedPoint();
	}
}
