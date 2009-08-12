package org.caleydo.core.view.opengl.canvas.bookmarking;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.media.opengl.GL;

import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.data.BookmarkEvent;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.collection.UniqueList;
import org.caleydo.core.view.opengl.canvas.bookmarking.GLBookmarkManager.PickingIDManager;
import org.caleydo.core.view.opengl.util.GLHelperFunctions;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * @author Alexander Lex
 */
public abstract class ABookmarkContainer {

	protected Dimensions dimensions;
	protected EIDCategory category;

	/**
	 * The list of bookmarks - each bookmark is unique, the ordering is relevant
	 */
	protected UniqueList<ABookmark> bookmarkItems;
	protected TextRenderer textRenderer;

	protected PickingIDManager pickingIDManager;

	/**
	 * The selection manager, that manages whether a particular element is selected in the bookmark list. It
	 * is a member of the abstract base class, but has to be created by the implementing instance.
	 */
	SelectionManager selectionManager;

	/**
	 * The queue which holds the events
	 */
	private BlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>> queue;

	public ABookmarkContainer(EIDCategory category, PickingIDManager pickingIDManager,
		TextRenderer textRenderer) {
		this.category = category;
		this.pickingIDManager = pickingIDManager;
		this.textRenderer = textRenderer;
		dimensions = new Dimensions();
		queue = new LinkedBlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>>();
	}

	public Dimensions getDimensions() {
		return dimensions;
	}

	public EIDCategory getCategory() {
		return category;
	}

	public void render(GL gl) {

		// processEvents();
		float yOrigin = dimensions.getYOrigin();
		for (ABookmark item : bookmarkItems) {

			item.getDimensions().setOrigins(0, yOrigin);
			yOrigin -= item.getDimensions().getHeight();

			if (selectionManager.checkStatus(ESelectionType.MOUSE_OVER, item.getID()))
				GLHelperFunctions.drawPointAt(gl, item.getDimensions().getXOrigin(), item.getDimensions()
					.getYOrigin(), 0);
			int pickingID = pickingIDManager.getPickingID(this, item.getID());
			gl.glPushName(pickingID);
			item.render(gl);
			gl.glPopName();
		}
	}

	public void handleEvents(EPickingMode pickingMode, Integer privateID) {
		ESelectionType selectionType;
		switch (pickingMode) {
			case CLICKED:
				selectionType = ESelectionType.SELECTION;
				break;
			case MOUSE_OVER:
				selectionType = ESelectionType.MOUSE_OVER;
				break;
			default:
				return;
		}
		selectionManager.clearSelection(selectionType);
		selectionManager.addToType(selectionType, privateID);
		
		SelectionCommand command = new SelectionCommand(ESelectionCommandType.CLEAR, selectionType);
		SelectionCommandEvent commandEvent = new SelectionCommandEvent();
		commandEvent.setSender(this);
		commandEvent.setCategory(category);
		commandEvent.setSelectionCommand(command);
		GeneralManager.get().getEventPublisher().triggerEvent(commandEvent);

		ISelectionDelta selectionDelta = selectionManager.getDelta();
		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setSender(this);
		event.setSelectionDelta((SelectionDelta) selectionDelta);
		event.setInfo("waaa");
		GeneralManager.get().getEventPublisher().triggerEvent(event);

	}

	public abstract <IDDataType> void handleNewBookmarkEvent(BookmarkEvent<IDDataType> event);
	

	public void handleSelectionUpdate(ISelectionDelta selectionDelta) {
		selectionManager.setDelta(selectionDelta);
	}
	
	public void handleSelectionCommand(SelectionCommand selectionCommand)
	{
		selectionManager.executeSelectionCommand(selectionCommand);
	}
}
