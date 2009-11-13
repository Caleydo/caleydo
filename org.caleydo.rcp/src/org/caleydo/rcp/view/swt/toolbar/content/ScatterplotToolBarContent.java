package org.caleydo.rcp.view.swt.toolbar.content;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.view.opengl.canvas.storagebased.GLScatterplot;
import org.caleydo.rcp.action.toolbar.view.storagebased.scatterplot.ScatterplotTestAction;

/**
 * ToolBarContent implementation for scatterplot specific toolbar items.
 * 
 * @author Marc Streit
 */
public class ScatterplotToolBarContent
	extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/view/storagebased/parcoords/parcoords.png";

	public static final String VIEW_TITLE = "Scatterplot";

	@Override
	public Class<?> getViewClass() {
		return GLScatterplot.class;
	}

	@Override
	protected List<ToolBarContainer> getToolBarContent() {
		ActionToolBarContainer container = new ActionToolBarContainer();

		container.setImagePath(IMAGE_PATH);
		container.setTitle(VIEW_TITLE);
		List<IToolBarItem> actionList = new ArrayList<IToolBarItem>();
		container.setToolBarItems(actionList);

		int targetViewID = getTargetViewData().getViewID();

		IToolBarItem testAction = new ScatterplotTestAction(targetViewID);
		actionList.add(testAction);

		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}

}
