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
package org.caleydo.view.bucket.toolbar;

import java.util.ArrayList;
import java.util.List;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.ToolBarContainer;
import org.caleydo.view.bucket.SerializedBucketView;
import org.caleydo.view.pathway.toolbar.PathwaySearchBox;
import org.caleydo.view.pathway.toolbar.PathwayToolBarMediator;
import org.caleydo.view.pathway.toolbar.actions.GeneMappingAction;

/**
 * Widget based toolbar container to display pathway related toolbar content.
 * 
 * @author Marc Streit
 */
public class PathwayToolBarContainer extends ToolBarContainer {

	/** Mediator to handle actions triggered by the contributed elements */
	PathwayToolBarMediator pathwayToolBarMediator;

	/** serialized remote rendering view to read the configuration from */
	SerializedBucketView targetViewData;

	/**
	 * Creates a the pathway selection box and add the pathway toolbar items.
	 */
	@Override
	public List<IToolBarItem> getToolBarItems() {

		List<IToolBarItem> elements = new ArrayList<IToolBarItem>();

		PathwaySearchBox pathwaySearchBox = new PathwaySearchBox("");
		pathwaySearchBox.setPathwayToolBarMediator(pathwayToolBarMediator);
		elements.add(pathwaySearchBox);

		// TextureAction textureAction = new
		// TextureAction(pathwayToolBarMediator);
		// textureAction.setTexturesEnabled(targetViewData.isPathwayTexturesEnabled());
		// elements.add(textureAction);

		GeneMappingAction geneMappingAction = new GeneMappingAction(
				pathwayToolBarMediator);
		geneMappingAction.setGeneMappingEnabled(targetViewData.isGeneMappingEnabled());
		elements.add(geneMappingAction);

		// TODO: neighborhood currently broken
		// elements.add(new NeighborhoodAction(pathwayToolBarMediator));

		return elements;
	}

	public PathwayToolBarMediator getPathwayToolBarMediator() {
		return pathwayToolBarMediator;
	}

	public void setPathwayToolBarMediator(PathwayToolBarMediator pathwayToolBarMediator) {
		this.pathwayToolBarMediator = pathwayToolBarMediator;
	}

	public SerializedBucketView getTargetViewData() {
		return targetViewData;
	}

	public void setTargetViewData(SerializedBucketView targetViewData) {
		this.targetViewData = targetViewData;
	}
}
