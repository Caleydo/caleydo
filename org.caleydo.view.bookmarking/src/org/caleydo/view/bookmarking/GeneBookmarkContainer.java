package org.caleydo.view.bookmarking;

import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.mapping.EIDType;
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
class GeneBookmarkContainer extends ABookmarkContainer<ContentSelectionManager> {

	ColorMapping colorMapping;

	GeneBookmarkContainer(GLBookmarkManager manager) {
		super(manager, EIDCategory.GENE, EIDType.DAVID);
		bookmarkItems = new UniqueList<ABookmark>();

		colorMapping = ColorMappingManager.get().getColorMapping(
				EColorMappingType.GENE_EXPRESSION);

		selectionManager = ((ISetBasedDataDomain) DataDomainManager.getInstance()
				.getDataDomain("org.caleydo.datadomain.genetic"))
				.getContentSelectionManager();

	}

	@Override
	<IDDataType> void handleNewBookmarkEvent(BookmarkEvent<IDDataType> event) {
		// ArrayList<Integer> ids;
		Integer davidID;

		for (IDDataType id : event.getBookmarks()) {

			if (event.getIDType().getCategory() == EIDCategory.GENE) {
				davidID = GeneralManager.get().getIDMappingManager().getID(
						event.getIDType(), EIDType.DAVID, id);
				if (davidID == null)
					continue;
			} else
				throw new IllegalStateException("ID type unhandled");
			GeneBookmark bookmark = new GeneBookmark(textRenderer, davidID);
			bookmarkItems.add(bookmark);
			// selectionManager.add(davidID);
		}

	}

}
