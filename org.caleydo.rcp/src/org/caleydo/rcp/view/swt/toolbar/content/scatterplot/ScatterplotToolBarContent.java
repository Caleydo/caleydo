package org.caleydo.rcp.view.swt.toolbar.content.scatterplot;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.view.opengl.canvas.storagebased.scatterplot.GLScatterplot;
import org.caleydo.rcp.action.toolbar.view.storagebased.scatterplot.ScatterplotTestAction;
import org.caleydo.rcp.view.swt.toolbar.content.AToolBarContent;
import org.caleydo.rcp.view.swt.toolbar.content.ActionToolBarContainer;
import org.caleydo.rcp.view.swt.toolbar.content.IToolBarItem;
import org.caleydo.rcp.view.swt.toolbar.content.ToolBarContainer;
import org.caleydo.rcp.view.swt.toolbar.content.radial.DepthSlider;

/**
 * ToolBarContent implementation for scatterplot specific toolbar items.
 * 
 * @author Marc Streit
 */
public class ScatterplotToolBarContent
	extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/view/storagebased/parcoords/parcoords.png";

	public static final String VIEW_TITLE = "Scatterplot";
	
	private IToolBarItem testSlider;

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

		if (testSlider == null) {
			testSlider = new PointSizeSlider("", 0);
		}
		actionList.add(testSlider);
		
		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}

}
