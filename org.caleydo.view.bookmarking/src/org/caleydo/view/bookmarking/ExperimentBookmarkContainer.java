package org.caleydo.view.bookmarking;

import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.manager.event.data.BookmarkEvent;
import org.caleydo.core.util.collection.UniqueList;

/**
 * A concrete implementation of ABookmarkContainer for the category
 * {@link EIDCategory#GENE}
 * 
 * @author Alexander Lex
 */
class ExperimentBookmarkContainer extends ABookmarkContainer {

	ExperimentBookmarkContainer(GLBookmarkManager manager) {
		super(manager, EIDCategory.EXPERIMENT, EIDType.EXPERIMENT_INDEX);
		bookmarkItems = new UniqueList<ABookmark>();

		selectionManager = new SelectionManager.Builder(internalIDType).build();

	}

	@Override
	<IDDataType> void handleNewBookmarkEvent(BookmarkEvent<IDDataType> event) {
		// ArrayList<Integer> ids;
		Integer id = null;
		for (IDDataType tempID : event.getBookmarks()) {
			if (tempID instanceof Integer) {
				id = (Integer) tempID;
			} else
				throw new IllegalStateException(
						"Can not handle strings for experiments");

			ExperimentBookmark bookmark = new ExperimentBookmark(textRenderer,
					id);
			bookmarkItems.add(bookmark);
			selectionManager.add(id);
		}

	}

}
