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
package org.caleydo.view.dvi.toolbar;

import java.util.ArrayList;
import java.util.List;
import org.caleydo.core.gui.toolbar.AToolBarContent;
import org.caleydo.core.gui.toolbar.ActionToolBarContainer;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.OpenOnlineHelpAction;
import org.caleydo.core.gui.toolbar.ToolBarContainer;
import org.caleydo.view.dvi.GLDataViewIntegrator;

public class DVIToolBarContent extends AToolBarContent {

	public static final String VIEW_TITLE = "Data Graph";

	@Override
	public Class<?> getViewClass() {
		return GLDataViewIntegrator.class;
	}

	@Override
	protected List<ToolBarContainer> getToolBarContent() {
		ActionToolBarContainer container = new ActionToolBarContainer();

		// container.setImagePath(IMAGE_PATH);
		container.setTitle(VIEW_TITLE);
		List<IToolBarItem> actionList = new ArrayList<IToolBarItem>();
		container.setToolBarItems(actionList);

		// IToolBarItem applySpringBasedLayoutAction = new
		// ApplySpringBasedLayoutAction();
		// IToolBarItem applyBipartiteLayoutAction = new
		// ApplyBipartiteLayoutAction();
		IToolBarItem toolBarWidgets = new ToolBarWidgets("Graph Layout");

		actionList.add(toolBarWidgets);
		// actionList.add(applySpringBasedLayoutAction);
		// actionList.add(applyBipartiteLayoutAction);

		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(container);

		container = new ActionToolBarContainer();
		actionList = new ArrayList<IToolBarItem>();
		container.setToolBarItems(actionList);
		actionList
				.add(new OpenOnlineHelpAction(
						"http://www.icg.tugraz.at/project/caleydo/help/caleydo-2.0/setting-up-visualizations-data-view-integrator",
						true));

		list.add(container);

		return list;
	}

}
