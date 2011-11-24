package org.caleydo.view.datagraph.datacontainer;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.view.datagraph.GLDataGraph;
import org.caleydo.view.datagraph.node.IDataGraphNode;

public abstract class ADataContainerRenderer extends LayoutRenderer {

	public final static String DIMENSION_GROUP_PICKING_TYPE = "org.caleydo.view.datagraph.dimensiongroup";

	protected IDataGraphNode node;
	protected GLDataGraph view;
	protected DragAndDropController dragAndDropController;
	protected Map<Integer, Pair<Point2D, Point2D>> bottomDimensionGroupPositions;
	protected Map<Integer, Pair<Point2D, Point2D>> topDimensionGroupPositions;
	protected List<Pair<String, Integer>> pickingIDsToBePushed;
	protected boolean isUpsideDown = false;
	protected boolean arePickingListenersRegistered = false;

	public ADataContainerRenderer(IDataGraphNode node, GLDataGraph view,
			DragAndDropController dragAndDropController) {
		this.node = node;
		this.view = view;
		this.dragAndDropController = dragAndDropController;
		bottomDimensionGroupPositions = new HashMap<Integer, Pair<Point2D, Point2D>>();
		topDimensionGroupPositions = new HashMap<Integer, Pair<Point2D, Point2D>>();
	}

	public abstract void setDataContainers(List<DataContainer> dataContainers);

	public Pair<Point2D, Point2D> getBottomAnchorPointsOfDataContainer(
			DataContainer dataContainer) {
		return bottomDimensionGroupPositions.get(dataContainer.getID());
	}

	public Pair<Point2D, Point2D> getTopAnchorPointsOfDataContainer(
			DataContainer dataContainer) {
		return topDimensionGroupPositions.get(dataContainer.getID());
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

	public void setPickingIDsToBePushed(
			List<Pair<String, Integer>> pickingIDsToBePushed) {
		this.pickingIDsToBePushed = pickingIDsToBePushed;
	}

	protected abstract Collection<DimensionGroupRenderer> getDimensionGroupRenderers();

}
