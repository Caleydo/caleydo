package org.caleydo.view.visbricks20.toolbar;

import java.util.ArrayList;
import java.util.List;
import org.caleydo.core.gui.toolbar.AToolBarContent;
import org.caleydo.core.gui.toolbar.ActionToolBarContainer;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.ToolBarContainer;
import org.caleydo.view.visbricks20.GLVisBricks20;

/**
 * Tool bar content.
 * 
 * @author <INSERT YOUR NAME>
 */
public class VisBricks20ToolBarContent extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/icon.png";

	public static final String VIEW_TITLE = "LayoutTemplate";

	@Override
	public Class<?> getViewClass() {
		return GLVisBricks20.class;
	}

	@Override
	protected List<ToolBarContainer> getToolBarContent() {
		ActionToolBarContainer container = new ActionToolBarContainer();

		container.setImagePath(IMAGE_PATH);
		container.setTitle(VIEW_TITLE);
		List<IToolBarItem> actionList = new ArrayList<IToolBarItem>();
		container.setToolBarItems(actionList);

		// ADD YOUR TOOLBAR CONTENT HERE
		// actionList.add();

		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}

}
