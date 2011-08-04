package org.caleydo.view.heatmap.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.gui.toolbar.AToolBarContent;
import org.caleydo.core.gui.toolbar.ActionToolBarContainer;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.ToolBarContainer;
import org.caleydo.core.gui.toolbar.action.PropagateSelectionsAction;
import org.caleydo.core.gui.toolbar.action.ResetViewAction;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

/**
 * ToolBarContent implementation for heatmap specific toolbar items.
 * 
 * @author Werner Puff
 */
public class HeatMapToolBarContent extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/view/tablebased/heatmap/heatmap.png";

	public static final String VIEW_TITLE = "Heat Map";

	@Override
	public Class<?> getViewClass() {
		return GLHeatMap.class;
	}

	@Override
	protected List<ToolBarContainer> getToolBarContent() {
		ActionToolBarContainer container = new ActionToolBarContainer();

		container.setImagePath(IMAGE_PATH);
		container.setTitle(VIEW_TITLE);
		List<IToolBarItem> actionList = new ArrayList<IToolBarItem>();
		container.setToolBarItems(actionList);

		if (renderType == STANDARD_RENDERING) {
			IToolBarItem resetViewAction = new ResetViewAction();
			actionList.add(resetViewAction);
			IToolBarItem propagateSelectionAction = new PropagateSelectionsAction();
			actionList.add(propagateSelectionAction);
		}

		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}

}
