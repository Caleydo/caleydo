package org.caleydo.view.scatterplot.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.view.base.swt.toolbar.content.AToolBarContent;
import org.caleydo.view.base.swt.toolbar.content.ActionToolBarContainer;
import org.caleydo.view.base.swt.toolbar.content.IToolBarItem;
import org.caleydo.view.base.swt.toolbar.content.ToolBarContainer;
import org.caleydo.view.scatterplot.GLScatterplot;
import org.caleydo.view.scatterplot.actions.ScatterplotResetSelectionAction;
import org.caleydo.view.scatterplot.actions.ScatterplotTestAction;

/**
 * ToolBarContent implementation for scatterplot specific toolbar items.
 * 
 * @author Marc Streit
 * @author Juergen Pillhofer
 */
public class ScatterplotToolBarContent
	extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/view/storagebased/parcoords/parcoords.png";

	public static final String VIEW_TITLE = "Scatterplot";

	private IToolBarItem pointSizeSlider;
	private IToolBarItem xAxisSelector;
	private IToolBarItem yAxisSelector;

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

		IToolBarItem resetSelection = new ScatterplotResetSelectionAction(targetViewID);
		actionList.add(resetSelection);

		if (pointSizeSlider == null) {
			pointSizeSlider = new PointSizeSlider("", 0);
		}
		actionList.add(pointSizeSlider);

		if (xAxisSelector == null) {
			xAxisSelector = new XAxisSelector("", 0);
		}
		actionList.add(xAxisSelector);

		if (yAxisSelector == null) {
			yAxisSelector = new YAxisSelector("", 0);
		}
		actionList.add(yAxisSelector);

		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}

}
