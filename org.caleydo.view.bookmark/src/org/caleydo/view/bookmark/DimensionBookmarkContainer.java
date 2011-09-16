package org.caleydo.view.bookmark;

import org.caleydo.core.data.selection.DimensionSelectionManager;
import org.caleydo.core.event.data.BookmarkEvent;
import org.caleydo.core.util.collection.UniqueList;

/**
 * A concrete implementation of ABookmarkContainer for the category
 * {@link EIDCategory#GENE}
 * 
 * @author Alexander Lex
 */
class DimensionBookmarkContainer extends ABookmarkContainer<DimensionSelectionManager> {

	DimensionBookmarkContainer(GLBookmarkView manager) {
		super(manager, manager.getDataDomain().getDimensionIDCategory(), manager
				.getDataDomain().getPrimaryDimensionMappingType());
		bookmarkItems = new UniqueList<ABookmark>();

		selectionManager = manager.getDataDomain().getDimensionSelectionManager();

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

			DimensionBookmark bookmark = new DimensionBookmark(manager, this, internalIDType,
					id, manager.getMinSizeTextRenderer());
			if (bookmarkItems.add(bookmark))
				containerLayout.append(bookmark.getLayout());
			// selectionManager.add(id);
		}
		updateContainerSize();
	}
}
