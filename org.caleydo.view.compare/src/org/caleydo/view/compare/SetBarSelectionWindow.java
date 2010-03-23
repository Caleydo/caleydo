package org.caleydo.view.compare;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

public class SetBarSelectionWindow implements IDraggable {

	// private Vec3f position;
	// private float height;
	// private float width;
	private int numSelectedItems;
	private int maxSelectedItems;
	private int minSelectedItems;
	private int lowestItemIndex;
	private int viewID;
	private int id;
	private EPickingType draggingSelection;
	private PickingManager pickingManager;
	private TextureManager textureManager;
	private ArrayList<SetBarItem> items;
	private SetBar setBar;
	private SetBarItem itemAtDraggingPosition;
	private DragAndDropController dragAndDropController;
	private int draggingIndexOffset;
	private float height;
	private float positionY;
	private int numSelectedItemsDragging;
	private int lowestItemIndexDragging;
	private boolean dragged;

	public SetBarSelectionWindow(int id, int viewID, SetBar setBar,
			ArrayList<SetBarItem> items, PickingManager pickingManager,
			TextureManager textureManager,
			DragAndDropController dragAndDropController) {
		this.id = id;
		this.viewID = viewID;
		this.setBar = setBar;
		this.items = items;
		this.pickingManager = pickingManager;
		this.textureManager = textureManager;
		this.dragAndDropController = dragAndDropController;
		numSelectedItems = 0;
		lowestItemIndex = 0;
		maxSelectedItems = 0;
		minSelectedItems = 0;
		numSelectedItemsDragging = -1;
		lowestItemIndexDragging = -1;
		dragged = false;
	}

	public void render(GL gl) {
		renderWindow(gl, numSelectedItems, lowestItemIndex, 1, true, -0.1f);
	}

	private void renderWindow(GL gl, int numSelectedItems, int lowestItemIndex,
			float alpha, boolean isPickable, float zOffset) {
		if (numSelectedItems <= 0)
			return;

		SetBarItem lowestItem = items.get(lowestItemIndex);
		SetBarItem highestItem = items.get(lowestItemIndex + numSelectedItems
				- 1);
		Vec3f lowestItemPosition = lowestItem.getPosition();
		Vec3f highestItemPosition = highestItem.getPosition();
		float arrowWidth = 0.08f * lowestItem.getWidth();
		float rightArrowPositionX = highestItemPosition.x()
				+ highestItem.getWidth() - arrowWidth;
		float width = highestItemPosition.x() + highestItem.getWidth()
				- lowestItemPosition.x() - 2.0f * arrowWidth;

		if (isPickable) {
			gl.glPushName(pickingManager.getPickingID(viewID,
					EPickingType.COMPARE_SELECTION_WINDOW_ARROW_LEFT_SELECTION,
					id));

			renderArrow(gl, lowestItemPosition, arrowWidth, alpha, true,
					zOffset);

			gl.glPopName();

			gl
					.glPushName(pickingManager
							.getPickingID(
									viewID,
									EPickingType.COMPARE_SELECTION_WINDOW_ARROW_RIGHT_SELECTION,
									id));

			renderArrow(gl, new Vec3f(rightArrowPositionX, lowestItemPosition
					.y(), lowestItemPosition.z()), arrowWidth, alpha, false,
					zOffset);

			gl.glPopName();

			gl.glPushName(pickingManager.getPickingID(viewID,
					EPickingType.COMPARE_SELECTION_WINDOW_SELECTION, id));

			renderBody(gl, lowestItemPosition, arrowWidth, width, alpha,
					zOffset);

			gl.glPopName();

		} else {

			renderArrow(gl, lowestItemPosition, arrowWidth, alpha, true,
					zOffset);
			renderArrow(gl, new Vec3f(rightArrowPositionX, lowestItemPosition
					.y(), lowestItemPosition.z()), arrowWidth, alpha, false,
					zOffset);
			renderBody(gl, lowestItemPosition, arrowWidth, width, alpha,
					zOffset);
		}
	}

	private void renderArrow(GL gl, Vec3f position, float arrowWidth,
			float alpha, boolean isLeft, float zOffset) {

		Vec3f lowerLeftCorner = new Vec3f(position.x(), positionY, position.z()
				+ zOffset);
		Vec3f lowerRightCorner = new Vec3f(position.x() + arrowWidth,
				positionY, position.z() + zOffset);
		Vec3f upperRightCorner = new Vec3f(position.x() + arrowWidth, positionY
				+ height, position.z() + zOffset);
		Vec3f upperLeftCorner = new Vec3f(position.x(), positionY + height,
				position.z() + zOffset);

		if (isLeft) {
			textureManager.renderTexture(gl, EIconTextures.HEAT_MAP_ARROW,
					lowerRightCorner, upperRightCorner, upperLeftCorner,
					lowerLeftCorner, 1, 1, 1, alpha);
		} else {
			textureManager.renderTexture(gl, EIconTextures.HEAT_MAP_ARROW,
					upperLeftCorner, lowerLeftCorner, lowerRightCorner,
					upperRightCorner, 1, 1, 1, alpha);
		}

	}

	private void renderBody(GL gl, Vec3f lowestItemPosition, float arrowWidth,
			float width, float alpha, float zOffset) {
		gl.glPushAttrib(GL.GL_LINE_BIT | GL.GL_COLOR_BUFFER_BIT);

		// gl.glLineWidth(6.0f);

		float[] selectionColor = SelectionType.SELECTION.getColor();

		gl.glColor4f(selectionColor[0], selectionColor[1], selectionColor[2],
				alpha);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(lowestItemPosition.x() + arrowWidth, positionY,
				lowestItemPosition.z() + zOffset);
		gl.glVertex3f(lowestItemPosition.x() + arrowWidth + width, positionY,
				lowestItemPosition.z() + zOffset);
		gl.glVertex3f(lowestItemPosition.x() + arrowWidth + width, positionY
				+ height, lowestItemPosition.z() + zOffset);
		gl.glVertex3f(lowestItemPosition.x() + arrowWidth, positionY + height,
				lowestItemPosition.z() + zOffset);
		gl.glEnd();

		gl.glPopAttrib();
	}

	public int getNumSelectedItems() {
		return numSelectedItems;
	}

	public void setNumSelectedItems(int numSelectedItems) {
		this.numSelectedItems = numSelectedItems;
	}

	public int getMaxSelectedItems() {
		return maxSelectedItems;
	}

	public void setMaxSelectedItems(int maxSelectedItems) {
		this.maxSelectedItems = maxSelectedItems;
	}

	public int getMinSelectedItems() {
		return minSelectedItems;
	}

	public void setMinSelectedItems(int minSelectedItems) {
		this.minSelectedItems = minSelectedItems;
	}

	public int getLowestItemIndex() {
		return lowestItemIndex;
	}

	public void setLowestItemIndex(int lowestItemIndex) {
		this.lowestItemIndex = lowestItemIndex;
	}

	@Override
	public void handleDragging(GL gl, float mouseCoordinateX,
			float mouseCoordinateY) {

		switch (draggingSelection) {
		case COMPARE_SELECTION_WINDOW_SELECTION:
			handleWindowDragging(gl, mouseCoordinateX, mouseCoordinateY);
			break;
		case COMPARE_SELECTION_WINDOW_ARROW_LEFT_SELECTION:
			handleArrowLeftDragging(gl, mouseCoordinateX, mouseCoordinateY);
			break;
		case COMPARE_SELECTION_WINDOW_ARROW_RIGHT_SELECTION:
			handleArrowRightDragging(gl, mouseCoordinateX, mouseCoordinateY);
			break;
		}

		if (dragged) {
			renderWindow(gl, numSelectedItemsDragging, lowestItemIndexDragging,
					0.5f, false, -0.05f);
		}
	}

	private void handleArrowLeftDragging(GL gl, float mouseCoordinateX,
			float mouseCoordinateY) {

		SetBarItem currentItem = getItemFromXCoordinate(mouseCoordinateX);
		if (currentItem == null)
			return;

		int currentItemIndex = currentItem.getID();
		int newNumSelectedItems = lowestItemIndex + numSelectedItems
				- currentItemIndex;

		if ((newNumSelectedItems >= minSelectedItems)
				&& (newNumSelectedItems <= maxSelectedItems)) {
			dragged = true;
			numSelectedItemsDragging = newNumSelectedItems;
			lowestItemIndexDragging = currentItemIndex;
		}
	}

	private void handleArrowRightDragging(GL gl, float mouseCoordinateX,
			float mouseCoordinateY) {

		SetBarItem currentItem = getItemFromXCoordinate(mouseCoordinateX);
		if (currentItem == null)
			return;

		int currentItemIndex = currentItem.getID();
		int newNumSelectedItems = currentItemIndex - lowestItemIndex + 1;

		if ((newNumSelectedItems >= minSelectedItems)
				&& (newNumSelectedItems <= maxSelectedItems)) {
			dragged = true;
			numSelectedItemsDragging = newNumSelectedItems;
			lowestItemIndexDragging = lowestItemIndex;
		}
	}

	private void handleWindowDragging(GL gl, float mouseCoordinateX,
			float mouseCoordinateY) {
		SetBarItem currentItem = getItemFromXCoordinate(mouseCoordinateX);
		if (currentItem == null)
			return;
		int currentItemIndex = currentItem.getID();
		int itemAtDraggingPositionIndex = itemAtDraggingPosition.getID();
		int newLowestItemIndex = lowestItemIndex
				+ (currentItemIndex - itemAtDraggingPositionIndex);

		if (newLowestItemIndex < 0)
			newLowestItemIndex = 0;

		if (newLowestItemIndex + numSelectedItems > items.size())
			newLowestItemIndex = items.size() - numSelectedItems;

		dragged = true;
		itemAtDraggingPosition = items.get(lowestItemIndex
				+ draggingIndexOffset);
		numSelectedItemsDragging = numSelectedItems;
		lowestItemIndexDragging = newLowestItemIndex;

	}

	@Override
	public void setDraggingStartPoint(float mouseCoordinateX,
			float mouseCoordinateY) {

		switch (draggingSelection) {
		case COMPARE_SELECTION_WINDOW_SELECTION:
			itemAtDraggingPosition = getItemFromXCoordinate(mouseCoordinateX);
			draggingIndexOffset = itemAtDraggingPosition.getID()
					- lowestItemIndex;
			break;
		}

	}

	private SetBarItem getItemFromXCoordinate(float xCoordinate) {
		for (SetBarItem item : items) {
			if (xCoordinate >= item.getPosition().x()
					&& xCoordinate <= item.getPosition().x() + item.getWidth())
				return item;
		}
		return null;
	}

	public ArrayList<SetBarItem> getSelectedItems() {
		ArrayList<SetBarItem> selectedItems = new ArrayList<SetBarItem>();

		for (int i = lowestItemIndex; i < lowestItemIndex + numSelectedItems; i++) {
			selectedItems.add(items.get(i));
		}
		return selectedItems;
	}

	public void adjustWindowSizeCentered(int windowSize) {

		if (windowSize >= items.size()) {
			numSelectedItems = items.size();
			lowestItemIndex = 0;
			return;
		}

		int windowIndexOffsetLower = (int) Math.floor((double) windowSize / 2.0
				- (double) numSelectedItems / 2.0);

		lowestItemIndex -= windowIndexOffsetLower;
		numSelectedItems = windowSize;

		if (lowestItemIndex < 0) {
			lowestItemIndex = 0;
		}

		if (lowestItemIndex + numSelectedItems > items.size()) {
			lowestItemIndex = items.size() - numSelectedItems;
		}
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getPositionY() {
		return positionY;
	}

	public void setPositionY(float positionY) {
		this.positionY = positionY;
	}

	public void handleSelection(int externalID, EPickingType pickingType,
			EPickingMode pickingMode, Pick pick) {

		if (pickingMode == EPickingMode.CLICKED) {
			draggingSelection = pickingType;
			dragAndDropController.clearDraggables();
			dragAndDropController.setDraggingStartPosition(pick
					.getPickedPoint());
			dragAndDropController.addDraggable(this);
			dragAndDropController.startDragging();

		}
	}

	@Override
	public void handleDrop(GL gl, float mouseCoordinateX, float mouseCoordinateY) {
		if (dragged) {
			if (lowestItemIndex != lowestItemIndexDragging
					|| numSelectedItems != numSelectedItemsDragging) {
				lowestItemIndex = lowestItemIndexDragging;
				numSelectedItems = numSelectedItemsDragging;

				setBar.updateSelectedItems(getSelectedItems());
			}
			dragged = false;
		}

	}
}
