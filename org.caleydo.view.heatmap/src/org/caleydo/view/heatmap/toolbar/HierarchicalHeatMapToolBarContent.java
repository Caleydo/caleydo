package org.caleydo.view.heatmap.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.gui.toolbar.AToolBarContent;
import org.caleydo.core.gui.toolbar.ActionToolBarContainer;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.ToolBarContainer;
import org.caleydo.core.gui.toolbar.action.TakeSnapshotAction;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.view.heatmap.hierarchical.GLHierarchicalHeatMap;

/**
 * ToolBarContent implementation for heatmap specific toolbar items.
 * 
 * @author Werner Puff
 * @author Marc Streit
 */
public class HierarchicalHeatMapToolBarContent extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/view/tablebased/heatmap/heatmap.png";

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

		AGLView view = GeneralManager.get().getViewManager().getGLView(targetViewData.getViewID());
		actionList.add(new TakeSnapshotAction(view.getParentComposite()));

		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}

}
