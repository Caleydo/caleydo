package org.caleydo.core.view.opengl.util.draganddrop;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;

public class DragAndDropController {

	private Set<IDraggable> draggables;
	private IDropArea dropArea;
	private boolean isDragging;
	private boolean isDraggingFirstTime;
	private Point startDraggingWindowCoords;
	private AGLView view;

	public DragAndDropController(AGLView view) {
		draggables = new HashSet<IDraggable>();
		isDragging = false;
		isDraggingFirstTime = false;
		this.view = view;
	}

	public void addDraggable(IDraggable draggable) {
		if (draggable != null)
			draggables.add(draggable);
	}

	public void removeDraggable(IDraggable draggable) {
		draggables.remove(draggable);
	}

	public void startDragging() {
		isDragging = true;
		isDraggingFirstTime = true;
		dropArea = null;
	}

	public void setDropArea(IDropArea dropArea) {
		this.dropArea = dropArea;
	}

	public void clearDraggables() {
		draggables.clear();
	}

	public void handleDragging(GL gl, GLMouseListener glMouseListener) {

		if (isDragging) {
			Point mouseWinCoords = glMouseListener.getPickedPoint();

			float[] fArTargetWorldCoordinates = GLCoordinateUtils
					.convertWindowCoordinatesToWorldCoordinates(gl,
							mouseWinCoords.x, mouseWinCoords.y);

			if (dropArea != null) {
				dropArea.handleDragOver(gl, draggables,
						fArTargetWorldCoordinates[0],
						fArTargetWorldCoordinates[1]);
			}

			for (IDraggable draggable : draggables) {
				if (isDraggingFirstTime) {
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
				isDragging = false;
				for (IDraggable draggable : draggables) {
					draggable.handleDrop(gl, fArTargetWorldCoordinates[0],
							fArTargetWorldCoordinates[1]);
				}
				if (dropArea != null) {
					dropArea.handleDrop(gl, draggables,
							fArTargetWorldCoordinates[0],
							fArTargetWorldCoordinates[1], this);

				}
				view.setDisplayListDirty();
			}

			isDraggingFirstTime = false;
		}
	}

	public boolean isDragging() {
		return isDragging;
	}

	public Set<IDraggable> getDraggables() {
		return draggables;
	}

	public boolean hasDraggables() {
		return !draggables.isEmpty();
	}

	public boolean containsDraggable(IDraggable draggable) {
		return draggables.contains(draggable);
	}

	public void setDraggingStartPosition(Point startPosition) {
		startDraggingWindowCoords = startPosition;
	}
}
