package org.caleydo.view.visbricks.toolbar;

import java.util.ArrayList;
import java.util.List;
import org.caleydo.core.gui.toolbar.AToolBarContent;
import org.caleydo.core.gui.toolbar.ActionToolBarContainer;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.ToolBarContainer;
import org.caleydo.view.visbricks.GLVisBricks;

/**
 * Tool bar content.
 * 
 * @author Marc Streit
 */
public class VisBricksToolBarContent extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/icon.png";

	public static final String VIEW_TITLE = "VisBricks";

	@Override
	public Class<?> getViewClass() {
		return GLVisBricks.class;
	}

	@Override
	protected List<ToolBarContainer> getToolBarContent() {
		ActionToolBarContainer container = new ActionToolBarContainer();

		container.setImagePath(IMAGE_PATH);
		container.setTitle(VIEW_TITLE);
		List<IToolBarItem> actionList = new ArrayList<IToolBarItem>();
		container.setToolBarItems(actionList);

		IToolBarItem trendHighlightModeAction = new ConnectionsModeGUI(
				"Trend Highlight Mode");
		actionList.add(trendHighlightModeAction);

		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}

}
