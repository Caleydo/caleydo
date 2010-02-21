package org.caleydo.view.heatmap.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.rcp.view.toolbar.ActionToolBarContainer;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
import org.caleydo.rcp.view.toolbar.ToolBarContainer;
import org.caleydo.rcp.view.toolbar.action.storagebased.PropagateSelectionsAction;
import org.caleydo.rcp.view.toolbar.action.storagebased.ResetViewAction;
import org.caleydo.rcp.view.toolbar.content.AToolBarContent;
import org.caleydo.view.heatmap.GLHeatMap;

/**
 * ToolBarContent implementation for heatmap specific toolbar items.
 * 
 * @author Werner Puff
 */
public class HeatMapToolBarContent extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/view/storagebased/heatmap/heatmap.png";

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

		int targetViewID = getTargetViewData().getViewID();

		if (renderType == STANDARD_RENDERING) {
			IToolBarItem resetViewAction = new ResetViewAction(targetViewID);
			actionList.add(resetViewAction);
			IToolBarItem propagateSelectionAction = new PropagateSelectionsAction(
					targetViewID);
			actionList.add(propagateSelectionAction);
		}

		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}

}
