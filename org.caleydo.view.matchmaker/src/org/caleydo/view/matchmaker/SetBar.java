package org.caleydo.view.matchmaker;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingManager;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.AGLGUIElement;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.matchmaker.contextmenu.AdjustPValueItem;
import org.caleydo.view.matchmaker.contextmenu.ClusterTableItem;
import org.caleydo.view.matchmaker.contextmenu.DuplicateTableBarElementItem;
import org.caleydo.view.matchmaker.state.ACompareViewState;

import com.jogamp.opengl.util.awt.TextRenderer;

public class SetBar extends AGLGUIElement {

	private static final float ITEM_HEIGHT_PROTION = 0.7f;
	private static final float SELECTION_WINDOW_HEIGHT_PROTION = 0.8f;

	// private ArrayList<DataTable> sets;
	private ArrayList<SetBarItem> items;
	private Vec3f position;
	private float height;
	private float width;
	private PickingManager pickingManager;
	private TextRenderer textRenderer;
	// private TextureManager textureManager;
	private DragAndDropController dragAndDropController;
	// private GLMouseListener glMouseListener;
	private SetBarItem currentMouseOverItem;
	private int viewID;
	private AGLView view;
	private SetBarSelectionWindow selectionWindow;
	private ACompareViewState viewState;

	public SetBar(int viewID, PickingManager pickingManager, TextRenderer textRenderer,
			DragAndDropController dragAndDropController, GLMouseListener glMouseListener,
			AGLView view, TextureManager textureManager) {
		this.viewID = viewID;
		this.pickingManager = pickingManager;
		this.textRenderer = textRenderer;
		this.dragAndDropController = dragAndDropController;
		// this.glMouseListener = glMouseListener;
		this.view = view;
		// this.textureManager = textureManager;
		items = new ArrayList<SetBarItem>();
		selectionWindow = new SetBarSelectionWindow(0, viewID, this, items,
				pickingManager, textureManager, dragAndDropController);
		// sets = new ArrayList<DataTable>();
		setMinSize(60);
	}

	public void render(GL2 gl) {
		selectionWindow.render(gl);
		for (SetBarItem item : items) {
			item.render(gl);
		}

	}

	public Vec3f getPosition() {
		return position;
	}

	public void setPosition(Vec3f position) {
		this.position = position;

		updateItemProperties();
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(GL2 gl, float height) {
		this.height = getScaledSizeOf(gl, height, false);
		updateItemProperties();
		selectionWindow.setHeight(this.height * SELECTION_WINDOW_HEIGHT_PROTION);
		selectionWindow.setPositionY(position.y()
				+ ((1 - SELECTION_WINDOW_HEIGHT_PROTION) * this.height) / 2.0f);
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
		updateItemProperties();
	}

	public void setTables(ArrayList<DataTable> sets) {
		// this.sets.clear();
		// this.sets.addAll(sets);
		items.clear();
		currentMouseOverItem = null;

		float itemWidth = width / (float) sets.size();
		int itemID = 0;
		float currentPositionX = position.x();

		for (DataTable set : sets) {
			SetBarItem item = new SetBarItem(itemID, viewID, pickingManager,
					textRenderer, this);
			item.setTable(set);
			item.setHeight(height * ITEM_HEIGHT_PROTION);
			item.setWidth(itemWidth);
			item.setPosition(new Vec3f(currentPositionX, position.y()
					+ ((1 - ITEM_HEIGHT_PROTION) * this.height) / 2.0f, position.z()));
			items.add(item);
			currentPositionX += item.getWidth();
			itemID++;
		}

		if (viewState != null) {
			selectionWindow.setLowestItemIndex(0);
			selectionWindow.setNumSelectedItems(Math.min(viewState.getNumSetsInFocus(),
					items.size()));
			selectionWindow.setMaxSelectedItems(viewState.getMaxSetsInFocus());
			selectionWindow.setMinSelectedItems(viewState.getMinSetsInFocus());

			updateSelectedItems(selectionWindow.getSelectedItems());
		}
	}

	public void handleSetBarItemSelection(int itemID, PickingMode pickingMode, Pick pick) {

		SetBarItem item = items.get(itemID);

		if (item == null)
			return;

		switch (pickingMode) {
		case CLICKED:
			dragAndDropController.clearDraggables();
			dragAndDropController.setDraggingStartPosition(pick.getPickedPoint());
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
			viewState.setTableBarDisplayListDirty();

			break;
		case RIGHT_CLICKED:

			view.getContextMenuCreator().addContextMenuItem(
					new DuplicateTableBarElementItem(itemID));

			ArrayList<DataTable> tables = new ArrayList<DataTable>();
			tables.add(items.get(itemID).getTable());
			view.getContextMenuCreator().addContextMenuItem(new ClusterTableItem(tables));

			view.getContextMenuCreator().addContextMenuItem(new AdjustPValueItem());

			break;
		case DRAGGED:
			if (dragAndDropController.hasDraggables()) {
				dragAndDropController.setDropArea(item);
			}
			break;
		}
	}

	public void handleSetBarSelectionWindowSelection(int externalID,
			PickingType pickingType, PickingMode pickingMode, Pick pick) {

		selectionWindow.handleSelection(externalID, pickingType, pickingMode, pick);
	}

	public void moveItem(SetBarItem itemToMove, int newIndex) {
		if (itemToMove.getID() < newIndex)
			newIndex--;

		ArrayList<SetBarItem> oldSelection = selectionWindow.getSelectedItems();

		items.remove(itemToMove.getID());

		items.add(newIndex, itemToMove);

		updateItemProperties();

		ArrayList<SetBarItem> newSelection = selectionWindow.getSelectedItems();

		if (isNewSelection(oldSelection, newSelection)) {
			updateSelectedItems(newSelection);
		}

		viewState.setTableBarDisplayListDirty();
	}

	public void handleDuplicateSetBarItem(int itemID) {
		// FIXME: Is it necessary to clone the set?

		SetBarItem item = items.get(itemID);

		SetBarItem clone = new SetBarItem(itemID, viewID, pickingManager, textRenderer,
				this);
		clone.setTable(item.getTable());

		ArrayList<SetBarItem> oldSelection = selectionWindow.getSelectedItems();

		items.add(itemID, clone);

		updateItemProperties();

		ArrayList<SetBarItem> newSelection = selectionWindow.getSelectedItems();

		if (isNewSelection(oldSelection, newSelection)) {
			updateSelectedItems(newSelection);
		}

		viewState.setTableBarDisplayListDirty();
	}

	private void updateItemProperties() {

		int itemID = 0;
		float itemWidth = width / (float) items.size();
		float currentPositionX = position.x();
		for (SetBarItem item : items) {
			item.tableID(itemID);
			item.setHeight(height * ITEM_HEIGHT_PROTION);
			item.setWidth(itemWidth);
			item.setPosition(new Vec3f(currentPositionX, position.y()
					+ ((1 - ITEM_HEIGHT_PROTION) * this.height) / 2.0f, position.z()));
			itemID++;
			currentPositionX += itemWidth;
		}
	}

	public void updateSelectedItems(ArrayList<SetBarItem> itemsInFocus) {
		ArrayList<DataTable> setsInFocus = new ArrayList<DataTable>();

		for (SetBarItem item : itemsInFocus) {
			setsInFocus.add(item.getTable());
		}
		viewState.setTablesInFocus(setsInFocus);
	}

	public ACompareViewState getViewState() {
		return viewState;
	}

	public void setViewState(ACompareViewState viewState) {
		this.viewState = viewState;
	}

	public void setMaxSelectedItems(int maxSelectedItems) {
		selectionWindow.setMaxSelectedItems(maxSelectedItems);
	}

	public void setMinSelectedItems(int minSelectedItems) {
		selectionWindow.setMinSelectedItems(minSelectedItems);
	}

	private boolean isNewSelection(ArrayList<SetBarItem> oldSelection,
			ArrayList<SetBarItem> newSelection) {
		if (oldSelection.size() != newSelection.size())
			return true;

		for (SetBarItem oldSelectionItem : oldSelection) {
			for (SetBarItem newSelectionItem : newSelection) {
				if (oldSelectionItem != newSelectionItem)
					return true;
			}
		}

		return false;
	}

	public void increaseLowestItemIndex(int offset) {
		selectionWindow.setLowestItemIndex(selectionWindow.getLowestItemIndex() + offset);
	}

	public void setWindowSize(int windowSize) {
		selectionWindow.setNumSelectedItems(windowSize);
	}

	public void adjustSelectionWindowSizeCentered(int windowSize) {
		selectionWindow.adjustWindowSizeCentered(windowSize);
	}

	public ArrayList<DataTable> getTablesInFocus() {
		ArrayList<DataTable> setsInFocus = new ArrayList<DataTable>();

		for (SetBarItem item : selectionWindow.getSelectedItems()) {
			setsInFocus.add(item.getTable());
		}

		return setsInFocus;
	}
}
