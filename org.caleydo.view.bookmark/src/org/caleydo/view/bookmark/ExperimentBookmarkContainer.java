package org.caleydo.view.bookmark;

import org.caleydo.core.data.selection.StorageSelectionManager;
import org.caleydo.core.manager.event.data.BookmarkEvent;
import org.caleydo.core.util.collection.UniqueList;
import org.caleydo.core.view.opengl.layout.Row;

/**
 * A concrete implementation of ABookmarkContainer for the category
 * {@link EIDCategory#GENE}
 * 
 * @author Alexander Lex
 */
class ExperimentBookmarkContainer extends ABookmarkContainer<StorageSelectionManager> {

	ExperimentBookmarkContainer(GLBookmarkView manager) {
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

			StorageBookmark bookmark = new StorageBookmark(manager, internalIDType, id,
					manager.getTextRenderer());
			bookmarkItems.add(bookmark);
			layoutRow.appendElement(bookmark.getElementLayout());
			// selectionManager.add(id);
		}
		updateContainerSize();
	}
}
