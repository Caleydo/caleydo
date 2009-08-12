package org.caleydo.core.view.opengl.canvas.bookmarking;

import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.manager.event.data.BookmarkEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.collection.UniqueList;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.view.opengl.canvas.bookmarking.GLBookmarkManager.PickingIDManager;

import com.sun.opengl.util.j2d.TextRenderer;

public class GeneBookmarkContainer
	extends ABookmarkContainer {

	protected ColorMapping colorMapping;

	public GeneBookmarkContainer(PickingIDManager pickingIDManager, TextRenderer textRenderer) {
		super(EIDCategory.GENE, pickingIDManager, textRenderer);
		bookmarkItems = new UniqueList<ABookmark>();

		for (ABookmark item : bookmarkItems) {
			dimensions.setHeight(dimensions.getHeight() + item.getDimensions().getHeight());
			if (item.getDimensions().getWidth() > dimensions.getWidth())
				dimensions.setWidth(item.getDimensions().getWidth());
		}

		colorMapping = ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);

		selectionManager = new SelectionManager.Builder(EIDType.DAVID).build();

	}

	@Override
	public <IDDataType> void handleNewBookmarkEvent(BookmarkEvent<IDDataType> event) {
		// ArrayList<Integer> ids;
		int davidID;

		for (IDDataType id : event.getBookmarks()) {

			if (event.getIDType().getCategory() == EIDCategory.GENE) {
				davidID =
					GeneralManager.get().getIDMappingManager().getID(event.getIDType(), EIDType.DAVID, id);
			}
			else
				throw new IllegalStateException("ID type unhandled");
			GeneBookmark bookmark = new GeneBookmark(textRenderer, davidID);
			bookmarkItems.add(bookmark);
			selectionManager.add(davidID);
		}

	}

}
