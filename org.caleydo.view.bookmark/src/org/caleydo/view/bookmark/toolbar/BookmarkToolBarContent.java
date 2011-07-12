package org.caleydo.view.bookmark.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.gui.toolbar.ActionToolBarContainer;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.ToolBarContainer;
import org.caleydo.core.gui.toolbar.content.AToolBarContent;
import org.caleydo.view.bookmark.GLBookmarkView;

/**
 * ToolBarContent implementation for parcoords specific toolbar items.
 * 
 * @author Werner Puff
 */
public class BookmarkToolBarContent extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/view/storagebased/parcoords/parcoords.png";

	public static final String VIEW_TITLE = "Parallel Coordinates";

	@Override
	public Class<?> getViewClass() {
		return GLBookmarkView.class;
	}

	@Override
	protected List<ToolBarContainer> getToolBarContent() {
		ActionToolBarContainer container = new ActionToolBarContainer();

		container.setImagePath(IMAGE_PATH);
		container.setTitle(VIEW_TITLE);
		List<IToolBarItem> actionList = new ArrayList<IToolBarItem>();
		container.setToolBarItems(actionList);

		IToolBarItem exportDataItem = new ExportDataAction();
		actionList.add(exportDataItem);

		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}

}
