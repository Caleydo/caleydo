package org.caleydo.view.bookmarking;

import org.caleydo.core.data.mapping.IDCategory;
import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.manager.ISetBasedDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.event.data.BookmarkEvent;
import org.caleydo.core.manager.general.GeneralManager;
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

		colorMapping = ColorMappingManager.get().getColorMapping(
				EColorMappingType.GENE_EXPRESSION);

		selectionManager = ((ISetBasedDataDomain) DataDomainManager.getInstance()
				.getDataDomain("org.caleydo.datadomain.genetic"))
				.getContentSelectionManager();

	}

	@Override
	<IDDataType> void handleNewBookmarkEvent(BookmarkEvent<IDDataType> event) {
		// ArrayList<Integer> ids;
		Integer convertedID;

		for (IDDataType id : event.getBookmarks()) {

			if (event.getIDType().getIDCategory() == category) {
				convertedID = GeneralManager.get().getIDMappingManager()
						.getID(event.getIDType(), idType, id);
				if (convertedID == null)
					continue;
			} else
				throw new IllegalStateException("ID type unhandled");
			ContentBookmark bookmark = new ContentBookmark(manager, idType, convertedID,
					textRenderer);
			bookmarkItems.add(bookmark);
			// selectionManager.add(davidID);
		}

	}

}
