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
package org.caleydo.view.grouper.drawingstrategies;

import java.util.HashMap;

import org.caleydo.core.view.opengl.picking.PickingManager;
import org.caleydo.view.grouper.GrouperRenderStyle;
import org.caleydo.view.grouper.drawingstrategies.group.EGroupDrawingStrategyType;
import org.caleydo.view.grouper.drawingstrategies.group.GroupDrawingStrategyDragged;
import org.caleydo.view.grouper.drawingstrategies.group.GroupDrawingStrategyMouseOver;
import org.caleydo.view.grouper.drawingstrategies.group.GroupDrawingStrategyNormal;
import org.caleydo.view.grouper.drawingstrategies.group.GroupDrawingStrategySelection;
import org.caleydo.view.grouper.drawingstrategies.group.IGroupDrawingStrategy;

public class DrawingStrategyManager {

	private HashMap<EGroupDrawingStrategyType, IGroupDrawingStrategy> hashGroupDrawingStrategies;

	public DrawingStrategyManager(String dimensionPerspectiveID,
			PickingManager pickingManager, int viewID, GrouperRenderStyle renderStyle) {

		hashGroupDrawingStrategies = new HashMap<EGroupDrawingStrategyType, IGroupDrawingStrategy>();

		hashGroupDrawingStrategies.put(EGroupDrawingStrategyType.NORMAL,
				new GroupDrawingStrategyNormal(dimensionPerspectiveID, pickingManager,
						viewID, renderStyle));
		hashGroupDrawingStrategies.put(EGroupDrawingStrategyType.MOUSE_OVER,
				new GroupDrawingStrategyMouseOver(pickingManager, viewID, renderStyle));
		hashGroupDrawingStrategies.put(EGroupDrawingStrategyType.SELECTION,
				new GroupDrawingStrategySelection(pickingManager, viewID, renderStyle));
		hashGroupDrawingStrategies.put(EGroupDrawingStrategyType.DRAGGED,
				new GroupDrawingStrategyDragged(renderStyle));

	}

	public IGroupDrawingStrategy getGroupDrawingStrategy(EGroupDrawingStrategyType type) {
		return hashGroupDrawingStrategies.get(type);
	}

}
