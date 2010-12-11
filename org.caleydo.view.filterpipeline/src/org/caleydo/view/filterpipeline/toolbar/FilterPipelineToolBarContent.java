package org.caleydo.view.filterpipeline.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.rcp.view.toolbar.ActionToolBarContainer;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
import org.caleydo.rcp.view.toolbar.ToolBarContainer;
import org.caleydo.rcp.view.toolbar.action.storagebased.ResetViewAction;
import org.caleydo.rcp.view.toolbar.content.AToolBarContent;
import org.caleydo.view.filterpipeline.GLFilterPipeline;

/**
 * Tool bar content.
 * 
 * @author Thomas Geymayer
 */
public class FilterPipelineToolBarContent extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/icon.png";

	public static final String VIEW_TITLE = "Filter Pipeline";

	@Override
	public Class<?> getViewClass() {
		return GLFilterPipeline.class;
	}

	@Override
	protected List<ToolBarContainer> getToolBarContent() {
		ActionToolBarContainer container = new ActionToolBarContainer();

		container.setImagePath(IMAGE_PATH);
		container.setTitle(VIEW_TITLE);
		List<IToolBarItem> actionList = new ArrayList<IToolBarItem>();
		container.setToolBarItems(actionList);

		actionList.add(new SelectFilterTypeWidget());

		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}

}
