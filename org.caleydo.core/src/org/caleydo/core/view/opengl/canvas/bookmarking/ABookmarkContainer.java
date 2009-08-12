package org.caleydo.core.view.opengl.canvas.bookmarking;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.data.BookmarkEvent;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.collection.UniqueList;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.util.GLHelperFunctions;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * @author Alexander Lex
 */
public abstract class ABookmarkContainer {

	protected Dimensions dimensions;

	/**
	 * The list of bookmarks - each bookmark is unique, the ordering is relevant
	 */
	protected UniqueList<ABookmark> bookmarkItems;
	protected TextRenderer textRenderer;

	/**
	 * The selection manager, that manages whether a particular element is selected in the bookmark list. It
	 * is a member of the abstract base class, but has to be created by the implementing instance.
	 */
	SelectionManager selectionManager;

	/**
	 * The queue which holds the events
	 */
	private BlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>> queue;

	public ABookmarkContainer(TextRenderer textRenderer) {
		this.textRenderer = textRenderer;
		dimensions = new Dimensions();
		queue = new LinkedBlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>>();
	}

	public Dimensions getDimensions() {
		return dimensions;
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

			item.render(gl);
		}
	}

	public abstract <IDDataType> void handleNewBookmarkEvent(BookmarkEvent<IDDataType> event);

	/**
	 * This method should be called every display cycle when it is save to change the state of the object. It
	 * processes all the previously submitted events.
	 */
	// public final void processEvents() {
	// Pair<AEventListener<? extends IListenerOwner>, AEvent> pair;
	// while (queue.peek() != null) {
	// pair = queue.poll();
	// pair.getFirst().handleEvent(pair.getSecond());
	// }
	// }

	// @Override
	// public void handleContentTriggerSelectionCommand(EIDType type, SelectionCommand selectionCommand) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void handleStorageTriggerSelectionCommand(EIDType type, SelectionCommand selectionCommand) {
	// // TODO Auto-generated method stub
	//
	// }
	//

	public void handleSelectionUpdate(ISelectionDelta selectionDelta) {
		selectionManager.setDelta(selectionDelta);
	}
	//
	// @Override
	// public void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event) {
	// queue.add(new Pair<AEventListener<? extends IListenerOwner>, AEvent>(listener, event));
	// }

}
