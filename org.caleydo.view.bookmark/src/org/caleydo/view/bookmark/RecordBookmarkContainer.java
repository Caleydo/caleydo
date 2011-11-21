package org.caleydo.view.bookmark;

import java.util.Set;

import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.event.data.BookmarkEvent;
import org.caleydo.core.util.collection.UniqueList;

/**
 * A concrete implementation of ABookmarkContainer for the category
 * {@link EIDCategory#GENE}
 * 
 * @author Alexander Lex
 */
class RecordBookmarkContainer extends ABookmarkContainer<RecordSelectionManager> {

	private IDCategory category;
	private IDType idType;

	RecordBookmarkContainer(GLBookmarkView manager, IDCategory category, IDType idType) {
		super(manager, category, manager.getDataDomain().getRecordIDCategory().getPrimaryMappingType());
		bookmarkItems = new UniqueList<ABookmark>();
		this.idType = idType;
		this.category = category;

		selectionManager = new RecordSelectionManager(manager.getDataDomain()
				.getRecordIDMappingManager(), idType);

	}

	@Override
	<IDDataType> void handleNewBookmarkEvent(BookmarkEvent<IDDataType> event) {
		// ArrayList<Integer> ids;
		Set<Integer> convertedIDs;

		for (IDDataType id : event.getBookmarks()) {

			if (event.getIDType().getIDCategory() == category) {
				convertedIDs = manager.getDataDomain().getRecordIDMappingManager()
						.getIDAsSet(event.getIDType(), idType, id);
				if (convertedIDs == null || convertedIDs.size() == 0)
					continue;
			} else
				throw new IllegalStateException("ID type: " + idType + " unhandled");

			RecordBookmark bookmark = new RecordBookmark(manager, this, idType,
					convertedIDs.iterator().next(), manager.getMinSizeTextRenderer());
			if (bookmarkItems.add(bookmark))
				containerLayout.append(bookmark.getLayout());
			// selectionManager.add(davidID);
		}
		updateContainerSize();

	}

}
