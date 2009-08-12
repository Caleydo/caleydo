package org.caleydo.core.view.opengl.canvas.bookmarking;

import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.manager.event.data.BookmarkEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.mapping.IDMappingManager;
import org.caleydo.core.manager.specialized.genetic.GeneticIDMappingHelper;
import org.caleydo.core.util.collection.UniqueList;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;

import com.sun.opengl.util.j2d.TextRenderer;

public class GeneBookmarkContainer
	extends ABookmarkContainer {

	protected ColorMapping colorMapping;


	public GeneBookmarkContainer(TextRenderer textRenderer) {
		super(textRenderer);
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
				davidID = GeneralManager.get().getIDMappingManager().getID(event.getIDType(), EIDType.DAVID, id);
			}			
			else
				throw new IllegalStateException("ID type unhandled");

			bookmarkItems.add(new GeneBookmark(textRenderer, davidID));
			selectionManager.add(davidID);
		}

	}

}
