package org.caleydo.view.datagraph;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.data.container.ADimensionGroupData;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.view.datagraph.node.IDataGraphNode;

public abstract class ADataContainerRenderer extends LayoutRenderer {

	protected final static String DIMENSION_GROUP_PICKING_TYPE = "org.caleydo.view.datagraph.dimensiongroup";

	protected IDataGraphNode node;
	protected AGLView view;
	protected DragAndDropController dragAndDropController;
	protected Map<Integer, Pair<Point2D, Point2D>> dimensionGroupPositions;

	public ADataContainerRenderer(IDataGraphNode node, AGLView view,
			DragAndDropController dragAndDropController) {
		this.node = node;
		this.view = view;
		this.dragAndDropController = dragAndDropController;
		dimensionGroupPositions = new HashMap<Integer, Pair<Point2D, Point2D>>();
	}

	public abstract void setDimensionGroups(
			List<ADimensionGroupData> dimensionGroupDatas);

	public Pair<Point2D, Point2D> getAnchorPointsOfDimensionGroup(
			ADimensionGroupData dimensionGroupData) {
		return dimensionGroupPositions.get(dimensionGroupData.getID());
	}
	
	public abstract void destroy();

}
