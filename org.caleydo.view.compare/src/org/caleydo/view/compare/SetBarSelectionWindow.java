package org.caleydo.view.compare;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;

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
	private PickingManager pickingManager;
	private ArrayList<SetBarItem> items;
	private SetBar setBar;
	private SetBarItem itemAtDraggingPosition;
	private int draggingIndexOffset;
	private float height;
	private float positionY;

	public SetBarSelectionWindow(int id, int viewID, SetBar setBar,
			ArrayList<SetBarItem> items, PickingManager pickingManager) {
		this.id = id;
		this.viewID = viewID;
		this.setBar = setBar;
		this.items = items;
		this.pickingManager = pickingManager;
		numSelectedItems = 0;
		lowestItemIndex = -1;
		maxSelectedItems = 0;
		minSelectedItems = 0;
	}

	public void render(GL gl) {

		if (numSelectedItems <= 0)
			return;

		SetBarItem lowestItem = items.get(lowestItemIndex);
		SetBarItem highestItem = items.get(lowestItemIndex + numSelectedItems
				- 1);

		Vec3f lowestItemPosition = lowestItem.getPosition();
		Vec3f highestItemPosition = highestItem.getPosition();
		float width = highestItemPosition.x() + highestItem.getWidth()
				- lowestItemPosition.x();

		gl.glPushName(pickingManager.getPickingID(viewID,
				EPickingType.COMPARE_SET_BAR_SELECTION_WINDOW_SELECTION, id));

		gl.glPushAttrib(GL.GL_LINE_BIT | GL.GL_COLOR_BUFFER_BIT);

		gl.glLineWidth(6.0f);

		gl.glColor4f(1, 0, 1, 1);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(lowestItemPosition.x(), positionY,
				lowestItemPosition.z() + 0.1f);
		gl.glVertex3f(lowestItemPosition.x() + width, positionY,
				lowestItemPosition.z() + 0.1f);
		gl.glVertex3f(lowestItemPosition.x() + width, positionY
				+ height, lowestItemPosition.z() + 0.1f);
		gl.glVertex3f(lowestItemPosition.x(), positionY + height,
				lowestItemPosition.z() + 0.1f);
		gl.glEnd();

		gl.glPopAttrib();

		gl.glPopName();
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
		SetBarItem currentItem = getItemFromXCoordinate(mouseCoordinateX);
		if (currentItem == null || currentItem == itemAtDraggingPosition)
			return;

		int currentItemIndex = currentItem.getID();
		int itemAtDraggingPositionIndex = itemAtDraggingPosition.getID();
		int newLowestItemIndex = lowestItemIndex
				+ (currentItemIndex - itemAtDraggingPositionIndex);

		if (newLowestItemIndex < 0)
			newLowestItemIndex = 0;

		if (newLowestItemIndex + numSelectedItems > items.size())
			newLowestItemIndex = items.size() - numSelectedItems;
		
		if(newLowestItemIndex != lowestItemIndex) {
			lowestItemIndex = newLowestItemIndex;
			itemAtDraggingPosition = items.get(lowestItemIndex
					+ draggingIndexOffset);
			
			setBar.updateSelectedItems(getSelectedItems());
		}
	}

	@Override
	public void setDraggingStartPoint(float mouseCoordinateX,
			float mouseCoordinateY) {
		itemAtDraggingPosition = getItemFromXCoordinate(mouseCoordinateX);
		draggingIndexOffset = itemAtDraggingPosition.getID() - lowestItemIndex;
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
		
		for(int i = lowestItemIndex; i < lowestItemIndex + numSelectedItems; i++) {
			selectedItems.add(items.get(i));
		}
		return selectedItems;
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
}
