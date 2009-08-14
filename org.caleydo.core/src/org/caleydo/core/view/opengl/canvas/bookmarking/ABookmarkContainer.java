package org.caleydo.core.view.opengl.canvas.bookmarking;

import javax.media.opengl.GL;

import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.manager.event.data.BookmarkEvent;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.util.collection.UniqueList;
import org.caleydo.core.view.opengl.canvas.bookmarking.GLBookmarkManager.PickingIDManager;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.GLHelperFunctions;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * <p>
 * Base class for bookmark containers. A bookmark container is a container for bookmarks belonging to a
 * specific category, as specified in {@link EIDCategory}. Therefore each container has to be uniquely
 * associated with a category.
 * </p>
 * <p>
 * Bookmark containers are no independent views, but depend on {@link GLBookmarkManager} for all the public
 * interfaces to the rest of the system.
 * </p>
 * <p>
 * The bookmark container holds a list of {@link ABookmark}s which are free to some degree on how to display
 * the information, for example by using text or simple visualizations.
 * </p>
 * <p>
 * Every bookmark container holds ist own selection manager which it has to use to manage the selections of
 * its items. The selections are synchronized with the rest of the system.
 * </p>
 * <p>
 * Bookmark containers do not allow duplicate entries - every element can be present only once.
 * </p>
 * 
 * @author Alexander Lex
 */
abstract class ABookmarkContainer {

	/** The category of the container */
	EIDCategory category;
	/** The dimensions (height, widht, position, etc.) of the whole container */
	Dimensions dimensions;
	/** The name displayed as the heading in the sidebar */
	String categoryName;
	/** The list of bookmarks - each bookmark is unique, the ordering is relevant */
	UniqueList<ABookmark> bookmarkItems;
	/** Reference to the text renderer created by {@link GLBookmarkManager} */
	TextRenderer textRenderer;
	/** Reference to the internal picking id manger created by {@link GLBookmarkManager} */
	PickingIDManager pickingIDManager;

	/**
	 * The selection manager, that manages whether a particular element is selected in the bookmark list. It
	 * is a member of the abstract base class, but has to be created by the implementing instance.
	 */
	SelectionManager selectionManager;

	/**
	 * Constructor
	 * 
	 * @param category
	 *            Every cateogry in {@link EIDCategory} can have one bookmark container, therefore you need to
	 *            specify the concrete category. The category should be specified by the concrete subclass and
	 *            therefore not be part of its constructor.
	 * @param pickingIDManager
	 *            The pickingIDManager that handles the picking IDs for all the bookmarkContainers uniquely.
	 * @param textRenderer
	 *            A shared textrenderer.
	 */
	ABookmarkContainer(EIDCategory category, PickingIDManager pickingIDManager, TextRenderer textRenderer) {
		this.category = category;
		this.categoryName = category.getName();
		this.pickingIDManager = pickingIDManager;
		this.textRenderer = textRenderer;
		dimensions = new Dimensions();
	}

	/**
	 * Returns the dimensions {@link GLBookmarkManager} needs to place the containers
	 * 
	 * @return
	 */
	Dimensions getDimensions() {
		return dimensions;
	}

	/**
	 * Returns the category of the container
	 * 
	 * @return
	 */
	EIDCategory getCategory() {
		return category;
	}

	/**
	 * Renders the heading for the category and the items.
	 * 
	 * @param gl
	 */
	void render(GL gl) {

		float yOrigin = dimensions.getYOrigin();
		dimensions.setHeight(0);

		dimensions.increaseHeight(BookmarkRenderStyle.CONTAINER_HEADING_SIZE);
		yOrigin -= BookmarkRenderStyle.CONTAINER_HEADING_SIZE;
		
		RenderingHelpers.renderText(gl, textRenderer, categoryName, dimensions.getXOrigin()
			+ BookmarkRenderStyle.SIDE_SPACING, yOrigin, GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR);

		for (ABookmark item : bookmarkItems) {

			item.getDimensions().setOrigins(0, yOrigin);
			yOrigin -= item.getDimensions().getHeight();
		
			if (selectionManager.checkStatus(ESelectionType.MOUSE_OVER, item.getID()))
				GLHelperFunctions.drawPointAt(gl, item.getDimensions().getXOrigin(), yOrigin, 0);
			int pickingID = pickingIDManager.getPickingID(this, item.getID());
			gl.glPushName(pickingID);
			item.render(gl);
			gl.glPopName();
			dimensions.increaseHeight(item.getDimensions().getHeight());
		}
	}

	/**
	 * Handles the picking events and triggers selection events
	 * 
	 * @param pickingMode
	 *            for example mouse-over or clicked
	 * @param privateID
	 *            the id specified when calling {@link PickingIDManager#getPickingID(ABookmarkContainer, int)}
	 *            Internal to the specific BookmarkContainer
	 */
	void handleEvents(EPickingMode pickingMode, Integer privateID) {
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
		GeneralManager.get().getEventPublisher().triggerEvent(event);

	}

	/**
	 * Handles new bookmarks. Uses the information in the event.
	 * 
	 * @param <IDDataType>
	 *            The data type of the id, typically Integer or String
	 * @param event
	 *            The event containing the information about the new bookmark to be added.
	 */
	abstract <IDDataType> void handleNewBookmarkEvent(BookmarkEvent<IDDataType> event);

	/**
	 * Handles updates to the selections coming from external sources
	 * 
	 * @param selectionDelta
	 *            the information about the updates
	 */
	void handleSelectionUpdate(ISelectionDelta selectionDelta) {
		selectionManager.setDelta(selectionDelta);
	}

	/**
	 * Handles updates of the selection manager triggered by external sources.
	 * 
	 * @param selectionCommand
	 *            the information what to do with the selection manager
	 */
	void handleSelectionCommand(SelectionCommand selectionCommand) {
		selectionManager.executeSelectionCommand(selectionCommand);
	}
}
