package org.caleydo.view.datagraph.toolbar;

import java.util.ArrayList;
import java.util.List;
import org.caleydo.core.gui.toolbar.AToolBarContent;
import org.caleydo.core.gui.toolbar.ActionToolBarContainer;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.ToolBarContainer;
import org.caleydo.view.datagraph.GLDataViewIntegrator;

public class DataGraphToolBarContent extends AToolBarContent {

	public static final String VIEW_TITLE = "Data Graph";

	@Override
	public Class<?> getViewClass() {
		return GLDataViewIntegrator.class;
	}

	@Override
	protected List<ToolBarContainer> getToolBarContent() {
		ActionToolBarContainer container = new ActionToolBarContainer();

		// container.setImagePath(IMAGE_PATH);
		container.setTitle(VIEW_TITLE);
		List<IToolBarItem> actionList = new ArrayList<IToolBarItem>();
		container.setToolBarItems(actionList);

		IToolBarItem applySpringBasedLayoutAction = new ApplySpringBasedLayoutAction();
		IToolBarItem applyBipartiteLayoutAction = new ApplyBipartiteLayoutAction();
		IToolBarItem toolBarWidgets = new ToolBarWidgets(
				"Graph Layout");
		
		actionList.add(toolBarWidgets);
//		actionList.add(applySpringBasedLayoutAction);
//		actionList.add(applyBipartiteLayoutAction);

		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}

}
