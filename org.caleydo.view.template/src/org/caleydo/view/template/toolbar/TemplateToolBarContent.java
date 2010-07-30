package org.caleydo.view.template.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.rcp.view.toolbar.ActionToolBarContainer;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
import org.caleydo.rcp.view.toolbar.ToolBarContainer;
import org.caleydo.rcp.view.toolbar.content.AToolBarContent;
import org.caleydo.view.template.GLTemplate;

/**
 * Tool bar content.
 * 
 * @author <INSERT YOUR NAME>
 */
public class TemplateToolBarContent extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/icon.png";

	public static final String VIEW_TITLE = "Template";

	@Override
	public Class<?> getViewClass() {
		return GLTemplate.class;
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
