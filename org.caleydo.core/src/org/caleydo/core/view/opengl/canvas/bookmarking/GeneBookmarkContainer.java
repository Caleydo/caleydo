package org.caleydo.core.view.opengl.canvas.bookmarking;

import java.util.ArrayList;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.manager.event.data.BookmarkEvent;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.item.BookmarkItem;

public class GeneBookmarkContainer
	extends ABookmarkContainer {

	protected ColorMapping colorMapping;

	public GeneBookmarkContainer() {
		bookmarkItems = new ArrayList<ABookmark>();

		for (ABookmark item : bookmarkItems) {
			dimensions.setHeight(dimensions.getHeight() + item.getDimensions().getHeight());
			if (item.getDimensions().getWidth() > dimensions.getWidth())
				dimensions.setWidth(item.getDimensions().getWidth());
		}

		colorMapping = ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);

	}

	@Override
	public <IDDataType> void handleNewBookmarkEvent(BookmarkEvent<IDDataType> event) {
		// ArrayList<Integer> ids;
//		if (event.getIDType() == EIDType.REFSEQ_MRNA_INT) {
//			e
//		}

		for (IDDataType id : event.getBookmarks()) {
			bookmarkItems.add(new GeneBookmark((Integer) id));
		}

	}

}
