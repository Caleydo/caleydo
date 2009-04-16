package org.caleydo.rcp.views.swt.toolbar.content;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.view.opengl.canvas.storagebased.GLHierarchicalHeatMap;
import org.caleydo.rcp.action.toolbar.view.storagebased.ActivateGroupHandling;
import org.caleydo.rcp.action.toolbar.view.storagebased.ClearSelectionsAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.MergeClasses;
import org.caleydo.rcp.action.toolbar.view.storagebased.StartClusteringAction;

/**
 * ToolBarContent implementation for heatmap specific toolbar items.  
 * @author Werner Puff
 */
public class HierarchicalHeatMapToolBarContent
	extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/view/storagebased/heatmap/heatmap.png";

	public static final String VIEW_TITLE = "Full Heat Map";

	@Override
	public Class<?> getViewClass() {
		return GLHierarchicalHeatMap.class;
	}
	
	@Override
	public List<ToolBarContainer> getDefaultToolBar() {
		ActionToolBarContainer container = new ActionToolBarContainer();

		container.setImagePath(IMAGE_PATH);
		container.setTitle(VIEW_TITLE);
		List<IToolBarItem> actionList = new ArrayList<IToolBarItem>();
		container.setToolBarItems(actionList);

		IToolBarItem startClustering = new StartClusteringAction(targetViewID);
		actionList.add(startClustering);
		IToolBarItem clearSelectionsAction = new ClearSelectionsAction(targetViewID);
		actionList.add(clearSelectionsAction);

		IToolBarItem mergeGroup = new MergeClasses(targetViewID);
		actionList.add(mergeGroup);
		
		IToolBarItem activateGroup = new ActivateGroupHandling(targetViewID);
		actionList.add(activateGroup);
		
		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}

}
