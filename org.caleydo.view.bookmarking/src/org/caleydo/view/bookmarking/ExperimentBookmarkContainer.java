package org.caleydo.view.bookmarking;

import org.caleydo.core.data.selection.StorageSelectionManager;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.ISetBasedDataDomain;
import org.caleydo.core.manager.event.data.BookmarkEvent;
import org.caleydo.core.util.collection.UniqueList;

/**
 * A concrete implementation of ABookmarkContainer for the category
 * {@link EIDCategory#GENE}
 * 
 * @author Alexander Lex
 */
class ExperimentBookmarkContainer extends ABookmarkContainer<StorageSelectionManager> {

	ExperimentBookmarkContainer(GLBookmarkManager manager) {
		super(manager, manager.getDataDomain().getStorageIDCategory(), manager
				.getDataDomain().getPrimaryStorageMappingType());
		bookmarkItems = new UniqueList<ABookmark>();

		selectionManager = ((ISetBasedDataDomain) DataDomainManager.getInstance()
				.getDataDomain("org.caleydo.datadomain.genetic"))
				.getStorageSelectionManager();

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
					textRenderer);
			bookmarkItems.add(bookmark);
			// selectionManager.add(id);
		}

	}
}
