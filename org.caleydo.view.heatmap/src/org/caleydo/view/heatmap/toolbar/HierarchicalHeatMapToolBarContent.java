package org.caleydo.view.heatmap.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.rcp.view.toolbar.ActionToolBarContainer;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
import org.caleydo.rcp.view.toolbar.ToolBarContainer;
import org.caleydo.rcp.view.toolbar.content.AToolBarContent;
import org.caleydo.view.heatmap.hierarchical.GLHierarchicalHeatMap;

/**
 * THIS IS DEAD AT THE MOMENT ToolBarContent implementation for heatmap specific
 * toolbar items.
 * 
 * @author Werner Puff
 */
public class HierarchicalHeatMapToolBarContent extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/view/storagebased/heatmap/heatmap.png";

	public static final String VIEW_TITLE = "Hierarchical Heat Map";

	@Override
	public Class<?> getViewClass() {
		return GLHierarchicalHeatMap.class;
	}

	@Override
	protected List<ToolBarContainer> getToolBarContent() {
		ActionToolBarContainer container = new ActionToolBarContainer();

		container.setImagePath(IMAGE_PATH);
		container.setTitle(VIEW_TITLE);
		List<IToolBarItem> actionList = new ArrayList<IToolBarItem>();
		container.setToolBarItems(actionList);

		// int targetViewID = getTargetViewData().getViewID();

		// IToolBarItem startClustering = new
		// StartClusteringDialogAction(targetViewID);
		// actionList.add(startClustering);

		// IToolBarItem mergeGroup = new MergeClasses(targetViewID);
		// actionList.add(mergeGroup);

		// after release 1.2 this should be enabled by default
		// IToolBarItem activateGroup = new ActivateGroupHandling(targetViewID);
		// actionList.add(activateGroup);

		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}

}
