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
package org.caleydo.view.pathway.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.gui.toolbar.AToolBarContent;
import org.caleydo.core.gui.toolbar.ToolBarContainer;
import org.caleydo.core.serialize.ASerializedSingleTablePerspectiveBasedView;
import org.caleydo.view.pathway.GLPathway;

/**
 * ToolBarContent implementation for bucket specific toolbar items.
 * 
 * @author Werner Puff
 */
public class PathwayToolBarContent extends AToolBarContent {

	public static final String PATHWAY_IMAGE_PATH = "resources/icons/view/pathway/pathway.png";
	public static final String PATHWAY_VIEW_TITLE = "Pathways";

	PathwayToolBarMediator mediator;
	
	/**
	 * Default Constructor
	 */
	public PathwayToolBarContent() {
	}

	@Override
	public Class<?> getViewClass() {
		return GLPathway.class;
	}

	@Override
	protected List<ToolBarContainer> getToolBarContent() {
		ArrayList<ToolBarContainer> list = new ArrayList<ToolBarContainer>();
		list.add(createPathwayContainer());

		return list;
	}

	/**
	 * Creates and returns icons for pathway related toolbar box FIXME: pathway
	 * buttons do not work this way at the moment, because the related commands
	 * need a pathway-view-id, not a bucket id. instead of commands an event
	 * should be dispatched where all pathways are listening, too.
	 * 
	 * @return pathway related toolbar box
	 */
	private ToolBarContainer createPathwayContainer() {

		PathwayToolBarContainer container = new PathwayToolBarContainer();

		container.setImagePath(PATHWAY_IMAGE_PATH);
		container.setTitle(PATHWAY_VIEW_TITLE);

		container.setPathwayToolBarMediator(new PathwayToolBarMediator(
				((ASerializedSingleTablePerspectiveBasedView) targetViewData).getDataDomainID()));
		container.setTargetViewData(targetViewData);

		return container;
	}
}
