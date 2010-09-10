package org.caleydo.view.treemap.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.rcp.view.toolbar.ActionToolBarContainer;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
import org.caleydo.rcp.view.toolbar.ToolBarContainer;
import org.caleydo.rcp.view.toolbar.content.AToolBarContent;
import org.caleydo.view.treemap.GLHierarchicalTreeMap;
import org.caleydo.view.treemap.actions.ZoomInAction;
import org.caleydo.view.treemap.actions.ZoomOutAction;

/**
 * ToolBarContent implementation for scatterplot specific toolbar items.
 * 
 * @author Marc Streit
 * @author Juergen Pillhofer
 */
public class HierarchicalTreeMapToolBarContent extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/view/storagebased/parcoords/parcoords.png";

	public static final String VIEW_TITLE = "Scatterplot";

	private IToolBarItem pointSizeSlider;

	// private IToolBarItem xAxisSelector;
	// private IToolBarItem yAxisSelector;

	@Override
	public Class<?> getViewClass() {
		return GLHierarchicalTreeMap.class;
	}

	@Override
	protected List<ToolBarContainer> getToolBarContent() {
		ActionToolBarContainer container = new ActionToolBarContainer();

		container.setImagePath(IMAGE_PATH);
		container.setTitle(VIEW_TITLE);
		List<IToolBarItem> actionList = new ArrayList<IToolBarItem>();
		container.setToolBarItems(actionList);

		actionList.add(pointSizeSlider);

		// if (xAxisSelector == null) {
		// xAxisSelector = new XAxisSelector("", 0);
		// }
		// actionList.add(xAxisSelector);
		//
		// if (yAxisSelector == null) {
		// yAxisSelector = new YAxisSelector("", 0);
		// }
		// actionList.add(yAxisSelector);
		
		actionList.add(new ZoomInAction());
		actionList.add(new ZoomOutAction());

		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}



}
