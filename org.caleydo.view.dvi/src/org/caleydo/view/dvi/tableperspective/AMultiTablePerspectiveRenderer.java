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
package org.caleydo.view.dvi.tableperspective;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.node.IDVINode;

public abstract class AMultiTablePerspectiveRenderer extends LayoutRenderer {

	protected IDVINode node;
	protected GLDataViewIntegrator view;
	protected DragAndDropController dragAndDropController;
	protected Map<Integer, Pair<Point2D, Point2D>> bottomDimensionGroupPositions;
	protected Map<Integer, Pair<Point2D, Point2D>> topDimensionGroupPositions;
	protected List<Pair<String, Integer>> pickingIDsToBePushed;
	protected boolean isUpsideDown = false;
	protected boolean arePickingListenersRegistered = false;

	public AMultiTablePerspectiveRenderer(IDVINode node, GLDataViewIntegrator view,
			DragAndDropController dragAndDropController) {
		this.node = node;
		this.view = view;
		this.dragAndDropController = dragAndDropController;
		bottomDimensionGroupPositions = new HashMap<Integer, Pair<Point2D, Point2D>>();
		topDimensionGroupPositions = new HashMap<Integer, Pair<Point2D, Point2D>>();
	}

	public abstract void setTablePerspectives(List<TablePerspective> tablePerspectives);

	public Pair<Point2D, Point2D> getBottomAnchorPointsOfTablePerspective(
			TablePerspective tablePerspective) {
		return bottomDimensionGroupPositions.get(tablePerspective.getID());
	}

	public Pair<Point2D, Point2D> getTopAnchorPointsOfTablePerspective(
			TablePerspective tablePerspective) {
		return topDimensionGroupPositions.get(tablePerspective.getID());
	}

	public void registerPickingListeners() {
		if (arePickingListenersRegistered)
			return;

		createPickingListeners();

		arePickingListenersRegistered = true;
	}

	protected abstract void createPickingListeners();

	public void unregisterPickingListeners() {
		removePickingListeners();
		arePickingListenersRegistered = false;
	}

	protected abstract void removePickingListeners();

	public void destroy() {
		unregisterPickingListeners();
	}

	public abstract void setUpsideDown(boolean isUpsideDown);

	public boolean isUpsideDown() {
		return isUpsideDown;
	}

	public List<Pair<String, Integer>> getPickingIDsToBePushed() {
		return pickingIDsToBePushed;
	}

	public void setPickingIDsToBePushed(List<Pair<String, Integer>> pickingIDsToBePushed) {
		this.pickingIDsToBePushed = pickingIDsToBePushed;
	}

	protected abstract Collection<TablePerspectiveRenderer> getDimensionGroupRenderers();

	public boolean arePickingListenersRegistered() {
		return arePickingListenersRegistered;
	}

}
