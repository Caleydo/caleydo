package org.caleydo.view.bookmarking;

import java.util.Set;

import org.caleydo.core.data.mapping.IDCategory;
import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.data.BookmarkEvent;
import org.caleydo.core.util.collection.UniqueList;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;

/**
 * A concrete implementation of ABookmarkContainer for the category
 * {@link EIDCategory#GENE}
 * 
 * @author Alexander Lex
 */
class ContentBookmarkContainer extends ABookmarkContainer<ContentSelectionManager> {

	ColorMapping colorMapping;
	IDCategory category;
	IDType idType;

	ContentBookmarkContainer(GLBookmarkManager manager, IDCategory category, IDType idType) {
		super(manager, category, manager.getDataDomain().getPrimaryContentMappingType());
		bookmarkItems = new UniqueList<ABookmark>();
		this.idType = idType;
		this.category = category;

		colorMapping = ColorMappingManager.get().getColorMapping(
				EColorMappingType.GENE_EXPRESSION);

		selectionManager = manager.getDataDomain().getContentSelectionManager();

	}

	@Override
	<IDDataType> void handleNewBookmarkEvent(BookmarkEvent<IDDataType> event) {
		// ArrayList<Integer> ids;
		Set<Integer> convertedIDs;

		for (IDDataType id : event.getBookmarks()) {

			if (event.getIDType().getIDCategory() == category) {
				convertedIDs = GeneralManager.get().getIDMappingManager()
						.getIDAsSet(event.getIDType(), idType, id);
				if (convertedIDs == null || convertedIDs.size() == 0)
					continue;
			} else
				throw new IllegalStateException("ID type: " + idType + " unhandled");

			ContentBookmark bookmark = new ContentBookmark(manager, idType, convertedIDs
					.iterator().next(), textRenderer);
			bookmarkItems.add(bookmark);
			// selectionManager.add(davidID);
		}

	}

}
