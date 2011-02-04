package org.caleydo.view.bookmark;

import org.caleydo.core.data.selection.StorageSelectionManager;
import org.caleydo.core.manager.event.data.BookmarkEvent;
import org.caleydo.core.util.collection.UniqueList;

/**
 * A concrete implementation of ABookmarkContainer for the category
 * {@link EIDCategory#GENE}
 * 
 * @author Alexander Lex
 */
class StorageBookmarkContainer extends ABookmarkContainer<StorageSelectionManager> {

	StorageBookmarkContainer(GLBookmarkView manager) {
		super(manager, manager.getDataDomain().getStorageIDCategory(), manager
				.getDataDomain().getPrimaryStorageMappingType());
		bookmarkItems = new UniqueList<ABookmark>();

		selectionManager = manager.getDataDomain().getStorageSelectionManager();

	}

	@Override
	<IDDataType> void handleNewBookmarkEvent(BookmarkEvent<IDDataType> event) {
		// ArrayList<Integer> ids;
		Integer id = null;
		for (IDDataType tempID : event.getBookmarks()) {
			if (tempID instanceof Integer) {
				id = (Integer) tempID;
			} else
				throw new IllegalStateException("Can not handle strings for experiments");

			StorageBookmark bookmark = new StorageBookmark(manager, this, internalIDType,
					id, manager.getTextRenderer());
			if (bookmarkItems.add(bookmark))
				containerLayout.appendElement(bookmark.getElementLayout());
			// selectionManager.add(id);
		}
		updateContainerSize();
	}
}
