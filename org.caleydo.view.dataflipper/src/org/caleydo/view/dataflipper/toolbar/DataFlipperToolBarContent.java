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
package org.caleydo.view.dataflipper.toolbar;

import java.util.ArrayList;
import java.util.List;
import org.caleydo.core.gui.toolbar.AToolBarContent;
import org.caleydo.core.gui.toolbar.ToolBarContainer;
import org.caleydo.view.dataflipper.GLDataFlipper;

/**
 * ToolBarContent implementation for data flipper specific toolbar items.
 * 
 * @author Werner Puff
 * @author Marc Streit
 */
public class DataFlipperToolBarContent extends AToolBarContent {

	public static final String IMAGE_PATH = "resources/icons/view/remote/remote.png";
	public static final String VIEW_TITLE = "Bucket";

	DataFlipperToolBarMediator mediator;

	@Override
	public Class<?> getViewClass() {
		return GLDataFlipper.class;
	}

	@Override
	protected List<ToolBarContainer> getToolBarContent() {
		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		// list.add(createBucketContainer());

		return list;
	}

	// /**
	// * Creates and returns icons for data flipper related toolbar box
	// *
	// * @return bucket related toolbar box
	// */
	// private ToolBarContainer createBucketContainer() {
	// mediator = new DataFlipperToolBarMediator();
	// mediator.setToolBarContent(this);
	// SerializedDataFlipperView serializedView = (SerializedDataFlipperView)
	// getTargetViewData();
	// ActionToolBarContainer container = new ActionToolBarContainer();
	//
	// container.setImagePath(IMAGE_PATH);
	// container.setTitle(VIEW_TITLE);
	// List<IToolBarItem> actionList = new ArrayList<IToolBarItem>();
	// container.setToolBarItems(actionList);
	//
	// return container;
	// }

	@Override
	public void dispose() {
		if (mediator != null) {
			mediator.dispose();
			mediator = null;
		}
	}
}
