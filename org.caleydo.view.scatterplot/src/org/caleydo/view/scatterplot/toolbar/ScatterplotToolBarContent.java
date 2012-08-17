/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.scatterplot.toolbar;

import java.util.ArrayList;
import java.util.List;
import org.caleydo.core.gui.toolbar.AToolBarContent;
import org.caleydo.core.gui.toolbar.ActionToolBarContainer;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.ToolBarContainer;
import org.caleydo.view.scatterplot.GLScatterPlot;
import org.caleydo.view.scatterplot.actions.Toggle2AxisModeAction;
import org.caleydo.view.scatterplot.actions.ToggleColorModeAction;
import org.caleydo.view.scatterplot.actions.ToggleMainViewZoomAction;
import org.caleydo.view.scatterplot.actions.ToggleMatrixViewAction;
import org.caleydo.view.scatterplot.actions.ToggleMatrixZoomAction;
import org.caleydo.view.scatterplot.actions.TogglePointTypeAction;

/**
 * ToolBarContent implementation for scatterplot specific toolbar items.
 * 
 * @author Marc Streit
 * @author Juergen Pillhofer
 */
public class ScatterplotToolBarContent extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/view/tablebased/parcoords/parcoords.png";

	public static final String VIEW_TITLE = "Scatterplot";

	private IToolBarItem pointSizeSlider;

	// private IToolBarItem xAxisSelector;
	// private IToolBarItem yAxisSelector;

	@Override
	public Class<?> getViewClass() {
		return GLScatterPlot.class;
	}

	@Override
	protected List<ToolBarContainer> getToolBarContent() {
		ActionToolBarContainer container = new ActionToolBarContainer();

		container.setImagePath(IMAGE_PATH);
		container.setTitle(VIEW_TITLE);
		List<IToolBarItem> actionList = new ArrayList<IToolBarItem>();
		container.setToolBarItems(actionList);

		IToolBarItem testAction = new TogglePointTypeAction();
		actionList.add(testAction);

		IToolBarItem toggleMatrix = new ToggleMatrixViewAction();
		actionList.add(toggleMatrix);

		IToolBarItem toggleColor = new ToggleColorModeAction();
		actionList.add(toggleColor);

		IToolBarItem toggleMatrixZoom = new ToggleMatrixZoomAction();
		actionList.add(toggleMatrixZoom);

		IToolBarItem toggleMainViewZoom = new ToggleMainViewZoomAction();
		actionList.add(toggleMainViewZoom);

		IToolBarItem toggle2AxisMode = new Toggle2AxisModeAction();
		actionList.add(toggle2AxisMode);

		if (pointSizeSlider == null) {
			pointSizeSlider = new PointSizeSlider("", 0);
		}
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

		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		return list;
	}

}
