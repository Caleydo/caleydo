/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.bookmark;

import java.util.Iterator;

import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.event.data.BookmarkEvent;
import org.caleydo.core.event.data.RemoveBookmarkEvent;
import org.caleydo.core.event.data.SelectionUpdateEvent;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.collection.UniqueList;
import org.caleydo.core.view.contextmenu.item.RemoveBookmarkItem;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.ILayoutedElement;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.view.bookmark.GLBookmarkView.PickingIDManager;

/**
 * <p>
 * Base class for bookmark containers. A bookmark container is a container for
 * bookmarks belonging to a specific category, as specified in
 * {@link EIDCategory}. Therefore each container has to be uniquely associated
 * with a category.
 * </p>
 * <p>
 * Bookmark containers are no independent views, but depend on
 * {@link GLBookmarkView} for all the public interfaces to the rest of the
 * system.
 * </p>
 * <p>
 * The bookmark container holds a list of {@link ABookmark}s which are free to
 * some degree on how to display the information, for example by using text or
 * simple visualizations.
 * </p>
 * <p>
 * Every bookmark container holds its own selection manager which it has to use
 * to manage the selections of its items. The selections are synchronized with
 * the rest of the system.
 * </p>
 * <p>
 * Bookmark containers do not allow duplicate entries - every element can be
 * present only once.
 * </p>
 *
 * @author Alexander Lex
 */
abstract class ABookmarkContainer
		implements ILayoutedElement {

	/** The category of the container */
	IDCategory category;
	/** The type the container uses to internally store the data */
	IDType internalIDType;
	/**
	 * The containerDimensions (height, width, position, etc.) of the whole
	 * container
	 */
	// Dimensions containerDimensions;

	Column containerLayout;
	/** The name displayed as the heading in the sidebar */
	String categoryName;
	/**
	 * The list of bookmarks - each bookmark is unique, the ordering is relevant
	 */
	UniqueList<ABookmark> bookmarkItems;

	/**
	 * Reference to the internal picking id manger created by
	 * {@link GLBookmarkView}
	 */
	PickingIDManager pickingIDManager;

	/**
	 * The selection manager, that manages whether a particular element is
	 * selected in the bookmark list. It is a member of the abstract base class,
	 * but has to be created by the implementing instance.
	 */
	SelectionManager selectionManager;

	/**
	 * The creating and managing instance of this class. We need access to it
	 * here, because it provides all the view-specific facilities such as
	 * context menu etc.
	 */
	GLBookmarkView manager;

	/**
	 * Constructor
	 *
	 * @param manager
	 *            The gl view managing the container.
	 * @param category
	 *            Every category in {@link EIDCategory} can have one bookmark
	 *            container, therefore you need to specify the concrete
	 *            category. The category should be specified by the concrete
	 *            subclass and therefore not be part of its constructor.
	 * @param internalIDType
	 *            the id type the container uses to internally store the
	 *            bookmarks
	 */
	ABookmarkContainer(GLBookmarkView manager, IDCategory category, IDType internalIDType) {
		this.internalIDType = internalIDType;
		this.manager = manager;
		this.category = category;
		this.categoryName = category.getCategoryName();
		this.pickingIDManager = manager.getBookmarkPickingIDManager();
		this.containerLayout = new Column("typeBookmarkColumn");

		containerLayout.setYDynamic(true);
		containerLayout.setRatioSizeX(1);
		containerLayout.setBottomUp(false);
		ContainerHeading heading = new ContainerHeading(manager);
		heading.setCaption(category.getCategoryName());
		containerLayout.append(heading.getLayout());
	}

	@Override
	public ElementLayout getLayout() {
		return containerLayout;
	}

	/**
	 * Returns the containerDimensions {@link GLBookmarkView} needs to place the
	 * containers
	 *
	 * @return
	 */
	// Dimensions getDimensions() {
	// return containerDimensions;
	// }

	/**
	 * Returns the category of the container
	 *
	 * @return
	 */
	IDCategory getCategory() {
		return category;
	}

	/**
	 * Renders the heading for the category and the items.
	 *
	 * @param gl
	 */
	void render(GL2 gl) {

	}

	/**
	 * Handles the picking events and triggers selection events
	 *
	 * @param pickingMode
	 *            for example mouse-over or clicked
	 * @param externalID
	 *            the id specified when calling
	 *            {@link PickingIDManager#getPickingID(ABookmarkContainer, int)}
	 *            Internal to the specific BookmarkContainer
	 */
	void handleEvents(PickingType ePickingType, PickingMode pickingMode,
			Integer externalID, final Pick pick) {
		SelectionType selectionType;

		switch (ePickingType) {

		case BOOKMARK_ELEMENT:

			switch (pickingMode) {
			case CLICKED:
				selectionType = SelectionType.SELECTION;

				break;
			case MOUSE_OVER:
				selectionType = SelectionType.MOUSE_OVER;

				break;
			case RIGHT_CLICKED:
				selectionType = SelectionType.SELECTION;

				RemoveBookmarkItem menuItem = new RemoveBookmarkItem("Remove",
						internalIDType, externalID);
				manager.getContextMenuCreator().addContextMenuItem(menuItem);

				break;

			default:
				return;
			}

			if (selectionType == SelectionType.SELECTION) {

			}
			selectionManager.clearSelection(selectionType);
			selectionManager.addToType(selectionType, externalID);

			SelectionDelta selectionDelta = selectionManager.getDelta();
			SelectionUpdateEvent event = new SelectionUpdateEvent();
			event.setSender(this);
			event.setEventSpace(manager.getDataDomain().getDataDomainID());
			event.setSelectionDelta(selectionDelta);
			GeneralManager.get().getEventPublisher().triggerEvent(event);
			break;

		case BOOKMARK_CONTAINER_HEADING:

			break;
		default:
			break;
		}

	}

	/**
	 * Handles new bookmarks. Uses the information in the event.
	 *
	 * @param <IDDataType>
	 *            The data type of the id, typically Integer or String
	 * @param event
	 *            The event containing the information about the new bookmark to
	 *            be added.
	 */
	abstract <IDDataType> void handleNewBookmarkEvent(BookmarkEvent<IDDataType> event);

	/**
	 * Handles the removal of bookmarks by using the informatinon in the event.
	 *
	 * @param <IDDataType>
	 *            The data type of the id, typically Integer or String
	 * @param event
	 *            The event containing the information about the bookmark to be
	 *            removed.
	 */
	<IDDataType> void handleRemoveBookmarkEvent(RemoveBookmarkEvent<IDDataType> event) {
		Integer id = null;
		for (IDDataType tempID : event.getBookmarks()) {
			if (tempID instanceof Integer) {
				id = (Integer) tempID;
			} else
				throw new IllegalStateException("Can not handle strings for experiments");

			Iterator<ABookmark> iterator = bookmarkItems.iterator();

			while (iterator.hasNext()) {
				if (iterator.next().getID() == id) {
					iterator.remove();
					selectionManager.remove(id);
				}
			}

		}
		updateContainerSize();
	}

	/**
	 * Handles updates to the selections coming from external sources
	 *
	 * @param selectionDelta
	 *            the information about the updates
	 */
	void handleSelectionUpdate(SelectionDelta selectionDelta) {
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

	void updateContainerSize() {
		// containerDimensions.setHeight(0.5f);
		// containerDimensions.setHeight(0);
		// containerDimensions.increaseHeight(BookmarkRenderStyle.CONTAINER_HEADING_SIZE);
		//
		// for (ABookmark bookmark : bookmarkItems) {
		// containerDimensions.increaseHeight(bookmark.getDimensions().getHeight()
		// * 2);
		// }

	}
}
