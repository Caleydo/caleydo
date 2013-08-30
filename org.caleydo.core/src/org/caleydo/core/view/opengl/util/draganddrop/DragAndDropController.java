/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.util.draganddrop;

import gleem.linalg.Vec2f;

import java.util.HashSet;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;

/**
 * Fixme: document!!
 *
 * @author Christian Partl
 *
 */
public class DragAndDropController {

	private Set<IDraggable> draggables;
	private IDropArea dropArea;
	private boolean isDragging;
	private boolean isDraggingFirstTime;
	private Vec2f startDraggingWindowCoords;
	private AGLView view;
	@Deprecated
	/**
	 * @deprecated using plain strings which are nowhere defined is very error prone
	 * @return
	 */
	private String draggingMode;

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

	public void setDraggingProperties(Vec2f draggingStartPosition, String draggingMode) {
		this.draggingMode = draggingMode;
		this.startDraggingWindowCoords = draggingStartPosition;
	}

	private void startDragging() {
		isDragging = true;
		isDraggingFirstTime = true;
		dropArea = null;
	}

	public void setDropArea(IDropArea dropArea) {
		if (dropArea == this.dropArea)
			return;
		if (this.dropArea != null)
			this.dropArea.handleDropAreaReplaced();
		this.dropArea = dropArea;

	}

	/**
	 * @return the dropArea, see {@link #dropArea}
	 */
	public IDropArea getDropArea() {
		return dropArea;
	}

	public void clearDraggables() {
		draggables.clear();
	}

	public void handleDragging(GL2 gl, GLMouseListener glMouseListener) {

		if (!isDragging) {
			if (glMouseListener.wasMouseDragged() && startDraggingWindowCoords != null
					&& hasDraggables()) {
				startDragging();
			}
		}

		if (isDragging) {
			Vec2f mouseWinCoords = glMouseListener.getDIPPickedPoint();

			PixelGLConverter pixelGLConverter = view.getPixelGLConverter();
			Vec2f targetWorldCoordinates = pixelGLConverter.convertMouseCoord2GL(mouseWinCoords);

			if (dropArea != null) {
				dropArea.handleDragOver(gl, draggables, targetWorldCoordinates.x(), targetWorldCoordinates.y());
			}

			for (IDraggable draggable : draggables) {
				if (isDraggingFirstTime) {

					Vec2f startDraggingWorldCoordinates = pixelGLConverter
							.convertMouseCoord2GL(startDraggingWindowCoords);
					draggable.setDraggingStartPoint(startDraggingWorldCoordinates.x(),
							startDraggingWorldCoordinates.y());
				}
				draggable.handleDragging(gl, targetWorldCoordinates.x(), targetWorldCoordinates.y());
			}

			if (glMouseListener.wasMouseReleased()) {
				isDragging = false;
				for (IDraggable draggable : draggables) {
					draggable.handleDrop(gl, targetWorldCoordinates.x(), targetWorldCoordinates.y());
				}
				if (dropArea != null) {
					dropArea.handleDrop(gl, draggables, targetWorldCoordinates.x(), targetWorldCoordinates.y(), this);

				}
				clearDraggables();
				draggingMode = null;
				startDraggingWindowCoords = null;
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

	public void setDraggingStartPosition(Vec2f startPosition) {
		startDraggingWindowCoords = startPosition;
	}

	@Deprecated
	/**
	 * @deprecated using plain strings which are nowhere defined is very error prone
	 * @return
	 */
	public String getDraggingMode() {
		return draggingMode;
	}

	@Deprecated
	/**
	 * @deprecated using plain strings which are nowhere defined is very error prone
	 * @return
	 */
	public void setDraggingMode(String draggingMode) {
		this.draggingMode = draggingMode;
	}

	/**
	 * @param draggables
	 *            setter, see {@link #draggables}
	 */
	public void setDraggables(Set<IDraggable> draggables) {
		this.draggables = draggables;
	}
}
