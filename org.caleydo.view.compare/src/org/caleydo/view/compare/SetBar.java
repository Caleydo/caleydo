package org.caleydo.view.compare;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.AGLGUIElement;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;

import com.sun.opengl.util.j2d.TextRenderer;

public class SetBar extends AGLGUIElement {

	private ArrayList<ISet> sets;
	private ArrayList<SetBarItem> items;
	private Vec3f position;
	private float height;
	private float width;
	private PickingManager pickingManager;
	private TextRenderer textRenderer;
	private DragAndDropController dragAndDropController;
	private GLMouseListener glMouseListener;
	private SetBarItem currentMouseOverItem;
	private int viewID;
	private AGLView view;

	public SetBar(int viewID, PickingManager pickingManager,
			TextRenderer textRenderer,
			DragAndDropController dragAndDropController,
			GLMouseListener glMouseListener, AGLView view) {
		this.viewID = viewID;
		this.pickingManager = pickingManager;
		this.textRenderer = textRenderer;
		this.dragAndDropController = dragAndDropController;
		this.glMouseListener = glMouseListener;
		this.view = view;
		items = new ArrayList<SetBarItem>();
		sets = new ArrayList<ISet>();
		setMinSize(60);
	}

	public void render(GL gl) {
		for (SetBarItem item : items) {
			item.render(gl);
		}
	}

	public Vec3f getPosition() {
		return position;
	}

	public void setPosition(Vec3f position) {
		this.position = position;

		float currentPositionX = position.x();
		for (SetBarItem item : items) {
			item.setPosition(new Vec3f(currentPositionX, position.y(), position
					.z()));
			currentPositionX += item.getWidth();
		}
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(GL gl, float height) {
		this.height = getScaledSizeOf(gl, height, false);
		for (SetBarItem item : items) {
			item.setHeight(this.height);
		}
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
		float itemWidth = width / (float) items.size();
		float currentPositionX = position.x();
		for (SetBarItem item : items) {
			item.setWidth(itemWidth);
			item.setPosition(new Vec3f(currentPositionX, position.y(), position
					.z()));
			currentPositionX += item.getWidth();
		}
	}

	public void setSets(ArrayList<ISet> sets) {
		this.sets.clear();
		this.sets.addAll(sets);
		items.clear();
		currentMouseOverItem = null;

		float itemWidth = width / (float) sets.size();
		int itemID = 0;
		float currentPositionX = position.x();

		for (ISet set : sets) {
			SetBarItem item = new SetBarItem(itemID, viewID, pickingManager,
					textRenderer, this);
			item.setSet(set);
			item.setHeight(height);
			item.setWidth(itemWidth);
			item.setPosition(new Vec3f(currentPositionX, position.y(), position
					.z()));
			items.add(item);
			currentPositionX += item.getWidth();
			itemID++;
		}
	}

	public void handleSetBarItemSelection(int itemID, EPickingMode pickingMode) {

		SetBarItem item = items.get(itemID);

		if (item == null)
			return;

		switch (pickingMode) {
		case CLICKED:
			dragAndDropController.clearDraggables();
			dragAndDropController.setDraggingStartPosition(glMouseListener);
			dragAndDropController.addDraggable(item);
			dragAndDropController.startDragging();
			break;
		case MOUSE_OVER:
			if (item == currentMouseOverItem)
				return;

			item.setSelectionStatus(SetBarItem.SELECTION_STATUS_MOUSE_OVER);
			if (currentMouseOverItem != null)
				currentMouseOverItem
						.setSelectionStatus(SetBarItem.SELECTION_STATUS_NORMAL);
			currentMouseOverItem = item;
			view.setDisplayListDirty();

			break;
		case RIGHT_CLICKED:
			break;
		case DRAGGED:
			if (dragAndDropController.hasDraggables()) {
				if (!dragAndDropController.isDragging()) {

				}
				dragAndDropController.setDropArea(item);
			}
			break;
		}
	}
	
	public void moveItem(SetBarItem itemToMove, int newIndex) {
		if(itemToMove.getID() < newIndex)
			newIndex--;
		items.remove(itemToMove.getID());
		
		items.add(newIndex, itemToMove);
		
		int itemID = 0;
		for(SetBarItem item : items) {
			item.setID(itemID);
			itemID++;
		}
		
		view.setDisplayListDirty();
	}
}
