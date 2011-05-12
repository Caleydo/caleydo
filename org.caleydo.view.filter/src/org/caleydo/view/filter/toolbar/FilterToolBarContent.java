package org.caleydo.view.filter.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.gui.toolbar.ActionToolBarContainer;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.ToolBarContainer;
import org.caleydo.core.gui.toolbar.action.UseRandomSamplingAction;
import org.caleydo.core.gui.toolbar.content.AToolBarContent;
import org.caleydo.view.filter.RcpFilterView;

/**
 * Tool bar content.
 * 
 * @author Marc Streit
 */
public class FilterToolBarContent extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/icon.png";
	public static final String VIEW_TITLE = "Filter";

	@Override
	public Class<?> getViewClass() {
		return RcpFilterView.class;
	}

	@Override
	protected List<ToolBarContainer> getToolBarContent() {
		ActionToolBarContainer container = new ActionToolBarContainer();

		container.setImagePath(IMAGE_PATH);
		container.setTitle(VIEW_TITLE);
		List<IToolBarItem> actionList = new ArrayList<IToolBarItem>();
		container.setToolBarItems(actionList);

		// ADD YOUR TOOLBAR CONTENT HERE
		actionList.add(new UseRandomSamplingAction());
		
		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}

}
